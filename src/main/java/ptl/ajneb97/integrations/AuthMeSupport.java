package ptl.ajneb97.integrations;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import ptl.ajneb97.PlayerTimeLimit;
import ptl.ajneb97.configs.MainConfigManager;
import ptl.ajneb97.managers.PlayerDataManager;
import ptl.ajneb97.managers.PlayerTimeManager;

import java.lang.reflect.Method;

public class AuthMeSupport {

    private static final String AUTHME_PLUGIN_NAME = "AuthMe";
    private static final String AUTHME_API_CLASS = "fr.xephi.authme.api.v3.AuthMeApi";
    private static final String AUTHME_LOGIN_EVENT = "fr.xephi.authme.events.LoginEvent";
    private static final String AUTHME_RESTORE_SESSION_EVENT = "fr.xephi.authme.events.RestoreSessionEvent";
    private static final String AUTHME_REGISTER_EVENT = "fr.xephi.authme.events.RegisterEvent";

    private final PlayerTimeLimit plugin;

    private final boolean authMePresent;
    private Method apiGetInstanceMethod;
    private Method isAuthenticatedMethod;
    private Method isRegisteredMethod;
    private Object authMeApiInstance;
    private boolean apiLoaded;

    public AuthMeSupport(PlayerTimeLimit plugin) {
        this.plugin = plugin;
        this.authMePresent = Bukkit.getPluginManager().getPlugin(AUTHME_PLUGIN_NAME) != null;
        if (authMePresent) {
            loadApi();
        }
    }

    public boolean isReadyForTimeTracking(Player player) {
        if (!authMePresent) {
            return true;
        }
        if (player == null || !player.isOnline()) {
            return false;
        }
        if (!apiLoaded && !loadApi()) {
            return false;
        }

        try {
            boolean authenticated = (boolean) isAuthenticatedMethod.invoke(authMeApiInstance, player);
            if (authenticated) {
                return true;
            }

            boolean registered = (boolean) isRegisteredMethod.invoke(authMeApiInstance, player.getName());
            return !registered;
        } catch (ReflectiveOperationException e) {
            plugin.getLogger().warning("AuthMe API invocation failed while checking auth state: " + e.getMessage());
            return false;
        }
    }

    public void registerAuthEvents() {
        if (!authMePresent) {
            return;
        }

        registerAuthEvent(AUTHME_LOGIN_EVENT);
        registerAuthEvent(AUTHME_RESTORE_SESSION_EVENT);
        registerAuthEvent(AUTHME_REGISTER_EVENT);
    }

    private boolean loadApi() {
        try {
            Class<?> apiClass = Class.forName(AUTHME_API_CLASS);
            apiGetInstanceMethod = apiClass.getMethod("getInstance");
            isAuthenticatedMethod = apiClass.getMethod("isAuthenticated", Player.class);
            isRegisteredMethod = apiClass.getMethod("isRegistered", String.class);
            authMeApiInstance = apiGetInstanceMethod.invoke(null);
            apiLoaded = authMeApiInstance != null;
            if (!apiLoaded) {
                plugin.getLogger().warning("AuthMe API instance is null; delaying auth checks.");
            }
            return apiLoaded;
        } catch (ReflectiveOperationException e) {
            plugin.getLogger().warning("Could not load AuthMe API v3 support: " + e.getMessage());
            apiLoaded = false;
            return false;
        }
    }

    @SuppressWarnings("unchecked")
    private void registerAuthEvent(String eventClassName) {
        try {
            Class<?> rawEventClass = Class.forName(eventClassName);
            if (!Event.class.isAssignableFrom(rawEventClass)) {
                return;
            }

            Class<? extends Event> eventClass = (Class<? extends Event>) rawEventClass;
            Listener listener = new Listener() {
            };

            PluginManager pluginManager = Bukkit.getPluginManager();
            pluginManager.registerEvent(eventClass, listener, EventPriority.MONITOR, (registeredListener, event) -> {
                Player player = extractPlayer(event);
                if (player == null) {
                    return;
                }
                runSync(() -> refreshAfterAuthentication(player));
            }, plugin, true);
        } catch (ClassNotFoundException ignored) {
            // AuthMe variant may not expose all events; this is optional.
        }
    }

    private Player extractPlayer(Event event) {
        try {
            Method getPlayer = event.getClass().getMethod("getPlayer");
            Object result = getPlayer.invoke(event);
            if (result instanceof Player player) {
                return player;
            }
        } catch (ReflectiveOperationException ignored) {
            // Ignore unsupported event signatures.
        }
        return null;
    }

    private void runSync(Runnable action) {
        if (Bukkit.isPrimaryThread()) {
            action.run();
        } else {
            Bukkit.getScheduler().runTask(plugin, action);
        }
    }

    private void refreshAfterAuthentication(Player player) {
        if (player == null || !player.isOnline()) {
            return;
        }

        PlayerDataManager playerDataManager = plugin.getPlayerDataManager();
        PlayerTimeManager playerTimeManager = plugin.getPlayerTimeManager();
        MainConfigManager mainConfigManager = plugin.getConfigsManager().getMainConfigManager();

        playerDataManager.updateTimeLimit(player);
        playerTimeManager.sendInfoBars(player, playerDataManager, mainConfigManager);
    }
}
