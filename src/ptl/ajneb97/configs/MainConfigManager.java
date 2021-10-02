package ptl.ajneb97.configs;


import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import ptl.ajneb97.PlayerTimeLimit;
import ptl.ajneb97.configs.others.TimeLimit;

public class MainConfigManager {

	private PlayerTimeLimit plugin;
	private ArrayList<TimeLimit> timeLimits;
	private boolean actionBar;
	private boolean bossBar;
	private String bossBarColor;
	private String bossBarStyle;
	private String resetTime;
	
	public MainConfigManager(PlayerTimeLimit plugin) {
		this.plugin = plugin;
	}
	public void configurar() {
		FileConfiguration config = plugin.getConfig();

		timeLimits = new ArrayList<TimeLimit>();
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

}
