package ptl.ajneb97.configs;

import ptl.ajneb97.PlayerTimeLimit;

public class ConfigsManager {

	private PlayerConfigsManager playerConfigsManager;
	private MensajesConfigManager mensajesConfigManager;
	private MainConfigManager mainConfigManager;
	
	public ConfigsManager(PlayerTimeLimit plugin) {
		this.mainConfigManager = new MainConfigManager(plugin);
		this.playerConfigsManager = new PlayerConfigsManager(plugin);
		this.mensajesConfigManager = new MensajesConfigManager(plugin);
	}
	
	public void configurar() {
		this.mainConfigManager.configurar();
		this.playerConfigsManager.configurar();
		this.mensajesConfigManager.configurar();
	}

	public MensajesConfigManager getMensajesConfigManager() {
		return mensajesConfigManager;
	}

	public PlayerConfigsManager getPlayerConfigsManager() {
		return playerConfigsManager;
	}

	public MainConfigManager getMainConfigManager() {
		return mainConfigManager;
	}

	
}
