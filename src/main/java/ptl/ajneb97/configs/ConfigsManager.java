package ptl.ajneb97.configs;

import ptl.ajneb97.PlayerTimeLimit;

public class ConfigsManager {

	private PlayerTimeLimit plugin;
	private MainConfigManager mainConfigManager;
	private MessagesConfigManager messagesConfigManager;
	private DataConfigManager dataConfigManager;
	
	public ConfigsManager(PlayerTimeLimit plugin) {
		this.mainConfigManager = new MainConfigManager(plugin);
		this.messagesConfigManager = new MessagesConfigManager(plugin);
		this.dataConfigManager = new DataConfigManager(plugin);
		this.plugin = plugin;
	}
	
	public void configure() {
		this.mainConfigManager.configure();
		this.messagesConfigManager.configure();
		this.dataConfigManager.configure();
	}

	public MainConfigManager getMainConfigManager() {
		return mainConfigManager;
	}

	public MessagesConfigManager getMessagesConfigManager() {
		return messagesConfigManager;
	}

	public DataConfigManager getDataConfigManager() {
		return dataConfigManager;
	}

	public boolean reload(){
		if(!messagesConfigManager.reloadConfig()){
			return false;
		}
		if(!mainConfigManager.reloadConfig()){
			return false;
		}

		plugin.reloadPlayerDataSaveTask();
		plugin.getTimeResetManager().configReload();
		plugin.getPlayerDataManager().configReload();

		return true;
	}
}
