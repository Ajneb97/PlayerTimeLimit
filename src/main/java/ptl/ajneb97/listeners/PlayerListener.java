package ptl.ajneb97.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import ptl.ajneb97.PlayerTimeLimit;
import ptl.ajneb97.managers.MessagesManager;

public class PlayerListener implements Listener{

	private PlayerTimeLimit plugin;
	public PlayerListener(PlayerTimeLimit plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPreLogin(AsyncPlayerPreLoginEvent event) {
		plugin.getPlayerDataManager().managePreLogin(event);
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		plugin.getPlayerDataManager().manageJoin(player);

		// Update notification
		String latestVersion = plugin.getUpdateCheckerManager().getLatestVersion();
		if(player.isOp() && !(plugin.version.equals(latestVersion)) &&
				plugin.getConfigsManager().getMainConfigManager().isUpdateNotification()){
			player.sendMessage(MessagesManager.getLegacyColoredMessage(plugin.prefix+" &cThere is a new version available. &e(&7"+latestVersion+"&e)"));
			player.sendMessage(MessagesManager.getLegacyColoredMessage("&cYou can download it at: &ahttps://www.spigotmc.org/resources/96577/"));
		}
	}
	
	@EventHandler
	public void onLeave(PlayerQuitEvent event) {
		plugin.getPlayerDataManager().manageLeave(event.getPlayer());
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onTeleport(PlayerTeleportEvent event) {
		plugin.getPlayerTimeManager().manageOnTeleport(event.getPlayer(),event);
	}
}
