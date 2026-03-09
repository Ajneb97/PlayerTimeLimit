package ptl.ajneb97.configs;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.configuration.file.FileConfiguration;

import ptl.ajneb97.PlayerTimeLimit;
import ptl.ajneb97.configs.model.*;

public class MainConfigManager {

	private PlayerTimeLimit plugin;
	private CommonConfig configFile;

	private boolean updateNotification;
	private Map<String,Integer> timeLimits;
	private PlayTimeResetConfig playTimeReset;
	private WorldWhitelistSystemConfig worldWhitelistSystem;
	private boolean informationMessageEnabledByDefault;
	private int playerDataSave;
	private boolean actionBar;
	private BossBarConfig bossBar;
	private boolean useMiniMessage;
	private Map<Integer,List<String>> notifications;

	public MainConfigManager(PlayerTimeLimit plugin){
		this.plugin = plugin;
		this.configFile = new CommonConfig("config.yml",plugin,null,false);
		configFile.registerConfig();

		checkUpdate();
	}

	public void configure() {
		FileConfiguration config = configFile.getConfig();

		updateNotification = config.getBoolean("update_notification");
		timeLimits = new HashMap<>();
		for(String key : config.getConfigurationSection("time_limits").getKeys(false)) {
			int time = config.getInt("time_limits."+key);
			timeLimits.put(key,time);
		}

		playTimeReset = new PlayTimeResetConfig(
				PlayTimeResetConfig.Mode.valueOf(config.getString("time_reset.mode")),
				ZoneId.of(config.getString("time_reset.timezone")),
				config.getString("time_reset.hour")
		);
		worldWhitelistSystem = new WorldWhitelistSystemConfig(
				config.getBoolean("world_whitelist_system.enabled"),
				config.getStringList("world_whitelist_system.worlds"),
				config.getString("world_whitelist_system.teleport_coordinates_on_kick")
		);

		informationMessageEnabledByDefault = config.getBoolean("information_message_enabled_by_default");
		playerDataSave = config.getInt("player_data_save");
		actionBar = config.getBoolean("action_bar");

		bossBar = new BossBarConfig(
				config.getBoolean("boss_bar.enabled"),
				config.getString("boss_bar.color"),
				config.getString("boss_bar.style")
		);

		useMiniMessage = config.getBoolean("use_minimessage");

		notifications = new HashMap<>();
		if(config.contains("notifications")) {
			for(String key : config.getConfigurationSection("notifications").getKeys(false)) {
				int seconds = Integer.parseInt(key);
				List<String> actions = config.getStringList("notifications."+key);
				notifications.put(seconds,actions);
			}
		}
	}

	public boolean reloadConfig(){
		if(!configFile.reloadConfig()){
			return false;
		}
		configure();
		return true;
	}

	public void checkUpdate(){
		Path pathConfig = Paths.get(configFile.getRoute());
		try{
			String text = new String(Files.readAllBytes(pathConfig));
			FileConfiguration config = getConfig();
		}catch(IOException e){
			e.printStackTrace();
		}
	}

	public FileConfiguration getConfig(){
		return configFile.getConfig();
	}

	public boolean isUpdateNotification() {
		return updateNotification;
	}

	public Map<String, Integer> getTimeLimits() {
		return timeLimits;
	}

	public PlayTimeResetConfig getPlayTimeReset() {
		return playTimeReset;
	}

	public WorldWhitelistSystemConfig getWorldWhitelistSystem() {
		return worldWhitelistSystem;
	}

	public boolean isInformationMessageEnabledByDefault() {
		return informationMessageEnabledByDefault;
	}

	public int getPlayerDataSave() {
		return playerDataSave;
	}

	public boolean isActionBar() {
		return actionBar;
	}

	public BossBarConfig getBossBar() {
		return bossBar;
	}

	public boolean isUseMiniMessage() {
		return useMiniMessage;
	}

	public Map<Integer, List<String>> getNotifications() {
		return notifications;
	}
}
