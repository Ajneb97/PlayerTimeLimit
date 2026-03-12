package ptl.ajneb97;


import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import ptl.ajneb97.api.ExpansionPlayerTimeLimit;
import ptl.ajneb97.api.PlayerTimeLimitAPI;
import ptl.ajneb97.commands.CommandManager;
import ptl.ajneb97.configs.ConfigsManager;
import ptl.ajneb97.database.DatabaseManager;
import ptl.ajneb97.listeners.PlayerListener;
import ptl.ajneb97.managers.*;
import ptl.ajneb97.managers.dependencies.DependencyManager;
import ptl.ajneb97.managers.dependencies.Metrics;
import ptl.ajneb97.model.internal.UpdateCheckerResult;
import ptl.ajneb97.tasks.*;
import ptl.ajneb97.utils.ServerVersion;


public class PlayerTimeLimit extends JavaPlugin {
  
	PluginDescriptionFile pdfFile = getDescription();
	public String version = pdfFile.getVersion();
	public static String prefix;
	public static ServerVersion serverVersion;
	
	private PlayerDataManager playerDataManager;
	private ConfigsManager configsManager;
	private MessagesManager messagesManager;
	private PlayerTimeManager playerTimeManager;
	private DependencyManager dependencyManager;
	private TimeResetManager timeResetManager;
	private DatabaseManager databaseManager;
	
	private PlayerDataSaveTask playerDataSaveTask;
	private PlayerTimeTask playerTimeTask;
	private PlayerUpdateLimitTask playerUpdateLimitTask;
	private PlayerInfoBarsTask playerInfoBarsTask;
	private ServerTimeResetTask serverTimeResetTask;
	private UpdateCheckerManager updateCheckerManager;

	
	public void onEnable(){
		setVersion();
		setPrefix();
	   	this.playerDataManager = new PlayerDataManager(this);
		this.playerTimeManager = new PlayerTimeManager(this);
		this.dependencyManager = new DependencyManager(this);
		this.timeResetManager = new TimeResetManager(this);
		this.configsManager = new ConfigsManager(this);
		this.configsManager.configure();

		registerEvents();
		registerCommands();

		playerTimeTask = new PlayerTimeTask(this);
		playerTimeTask.start();
		playerUpdateLimitTask = new PlayerUpdateLimitTask(this);
		playerUpdateLimitTask.start();
		playerInfoBarsTask = new PlayerInfoBarsTask(this);
		playerInfoBarsTask.start();
		serverTimeResetTask = new ServerTimeResetTask(this);
		serverTimeResetTask.start();
		reloadPlayerDataSaveTask();

		this.databaseManager = new DatabaseManager(this);
		databaseManager.configure();

		timeResetManager.initialize();

		PlayerTimeLimitAPI api = new PlayerTimeLimitAPI(this);
		if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null){
			   new ExpansionPlayerTimeLimit(this).register();
		}
		Metrics metrics = new Metrics(this,30074);

		Bukkit.getConsoleSender().sendMessage(prefix+MessagesManager.getLegacyColoredMessage("&eHas been enabled! &fVersion: "+version));
		Bukkit.getConsoleSender().sendMessage(prefix+MessagesManager.getLegacyColoredMessage("&eThanks for using my plugin!   &f~Ajneb97"));

		updateCheckerManager = new UpdateCheckerManager(version);
		updateMessage(updateCheckerManager.check());
	}
	  
	public void onDisable(){
		databaseManager.closeConnection();

		Bukkit.getConsoleSender().sendMessage(prefix+MessagesManager.getLegacyColoredMessage("&eHas been disabled! &fVersion: "+version));
	}

	public void setPrefix(){
		prefix = MessagesManager.getLegacyColoredMessage("&8[&bPlayerTime&cLimit&8] ");
	}

	public void setVersion(){
		String packageName = Bukkit.getServer().getClass().getPackage().getName();
		String bukkitVersion = Bukkit.getServer().getBukkitVersion().split("-")[0];
        switch (bukkitVersion) {
            case "1.20.5", "1.20.6" -> serverVersion = ServerVersion.v1_20_R4;
            case "1.21", "1.21.1" -> serverVersion = ServerVersion.v1_21_R1;
            case "1.21.2", "1.21.3" -> serverVersion = ServerVersion.v1_21_R2;
            case "1.21.4" -> serverVersion = ServerVersion.v1_21_R3;
            case "1.21.5" -> serverVersion = ServerVersion.v1_21_R4;
            case "1.21.6", "1.21.7", "1.21.8" -> serverVersion = ServerVersion.v1_21_R5;
            case "1.21.9", "1.21.10" -> serverVersion = ServerVersion.v1_21_R6;
            case "1.21.11" -> serverVersion = ServerVersion.v1_21_R7;
            default -> {
                try {
                    serverVersion = ServerVersion.valueOf(packageName.replace("org.bukkit.craftbukkit.", ""));
                } catch (Exception e) {
                    serverVersion = ServerVersion.v1_21_R7;
                }
            }
        }
	}

	public void registerCommands(){
		this.getCommand("playertimelimit").setExecutor(new CommandManager(this));
	}
	
	public void registerEvents(){
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(new PlayerListener(this), this);
	}

	public void reloadPlayerDataSaveTask() {
		if(playerDataSaveTask != null) {
			playerDataSaveTask.end();
		}
		playerDataSaveTask = new PlayerDataSaveTask(this);
		playerDataSaveTask.start(configsManager.getMainConfigManager().getPlayerDataSave());
	}

	public MessagesManager getMessagesManager() {
		return messagesManager;
	}

	public void setMessagesManager(MessagesManager messagesManager) {
		this.messagesManager = messagesManager;
	}

	public FileConfiguration getMessagesConfig() {
		return this.configsManager.getMessagesConfigManager().getConfigFile().getConfig();
	}

	public ConfigsManager getConfigsManager() {
		return configsManager;
	}

	public PlayerDataManager getPlayerDataManager() {
		return playerDataManager;
	}

	public PlayerTimeManager getPlayerTimeManager() {
		return playerTimeManager;
	}

	public DependencyManager getDependencyManager() {
		return dependencyManager;
	}

	public TimeResetManager getTimeResetManager() {
		return timeResetManager;
	}

	public DatabaseManager getDatabaseManager() {
		return databaseManager;
	}

	public void updateMessage(UpdateCheckerResult result){
		if(!result.isError()){
			String latestVersion = result.getLatestVersion();
			if(latestVersion != null){
				Bukkit.getConsoleSender().sendMessage(MessagesManager.getLegacyColoredMessage("&cThere is a new version available. &e(&7"+latestVersion+"&e)"));
				Bukkit.getConsoleSender().sendMessage(MessagesManager.getLegacyColoredMessage("&cYou can download it at: &fhttps://modrinth.com/plugin/player-time-limit"));
			}
		}else{
			Bukkit.getConsoleSender().sendMessage(MessagesManager.getLegacyColoredMessage(prefix+" &cError while checking update."));
		}
	}

	public UpdateCheckerManager getUpdateCheckerManager() {
		return updateCheckerManager;
	}
}
