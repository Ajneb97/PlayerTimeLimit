package ptl.ajneb97.configs;


import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;

import ptl.ajneb97.PlayerTimeLimit;
import ptl.ajneb97.configs.others.Notification;
import ptl.ajneb97.configs.others.TimeLimit;

public class MainConfigManager {

	private PlayerTimeLimit plugin;
	private ArrayList<TimeLimit> timeLimits;
	private boolean actionBar;
	private boolean bossBar;
	private String bossBarColor;
	private String bossBarStyle;
	private String resetTime;
	private boolean worldWhitelistEnabled;
	private List<String> worldWhitelistWorlds;
	private String worldWhitelistTeleportCoordinates;
	private ArrayList<Notification> notifications;
	
	public MainConfigManager(PlayerTimeLimit plugin) {
		this.plugin = plugin;
	}
	public void configurar() {
		FileConfiguration config = plugin.getConfig();

		timeLimits = new ArrayList<TimeLimit>();
		notifications = new ArrayList<Notification>();
		for(String key : config.getConfigurationSection("time_limits").getKeys(false)) {
			int time = config.getInt("time_limits."+key);
			TimeLimit timeLimit = new TimeLimit(key,time);
			timeLimits.add(timeLimit);
		}
		actionBar = config.getBoolean("action_bar");
		bossBar = config.getBoolean("boss_bar.enabled");
		bossBarColor = config.getString("boss_bar.color");
		bossBarStyle = config.getString("boss_bar.style");
		resetTime = config.getString("reset_time");
		worldWhitelistEnabled = config.getBoolean("world_whitelist_system.enabled");
		worldWhitelistWorlds = config.getStringList("world_whitelist_system.worlds");
		worldWhitelistTeleportCoordinates = config.getString("world_whitelist_system.teleport_coordinates_on_kick");
		
		
		if(config.contains("notification")) {
			for(String key : config.getConfigurationSection("notification").getKeys(false)) {
				int seconds = Integer.valueOf(key);
				List<String> message = null;
				if(config.contains("notification."+key+".message")) {
					message = config.getStringList("notification."+key+".message");
				}
				
				Notification notification = new Notification(seconds,message);
				notifications.add(notification);
			}
		}
	}

	public boolean isActionBar() {
		return actionBar;
	}
	public boolean isBossBar() {
		return bossBar;
	}
	public String getBossBarColor() {
		return bossBarColor;
	}
	public String getBossBarStyle() {
		return bossBarStyle;
	}
	public String getResetTime() {
		return resetTime;
	}
	public ArrayList<TimeLimit> getTimeLimits() {
		return timeLimits;
	}
	public boolean isWorldWhitelistEnabled() {
		return worldWhitelistEnabled;
	}
	public List<String> getWorldWhitelistWorlds() {
		return worldWhitelistWorlds;
	}
	public String getWorldWhitelistTeleportCoordinates() {
		return worldWhitelistTeleportCoordinates;
	}
	public ArrayList<Notification> getNotifications() {
		return notifications;
	}
	public Notification getNotificationAtTime(int seconds) {
		for(Notification n : notifications) {
			if(n.getSeconds() == seconds) {
				return n;
			}
		}
		return null;
	}
}
