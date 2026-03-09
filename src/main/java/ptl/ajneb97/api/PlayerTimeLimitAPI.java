package ptl.ajneb97.api;

import org.bukkit.entity.Player;

import ptl.ajneb97.PlayerTimeLimit;

public class PlayerTimeLimitAPI {

	private static PlayerTimeLimit plugin;
	public PlayerTimeLimitAPI(PlayerTimeLimit plugin) {
		this.plugin = plugin;
	}
	
	public static String getTimeLeft(Player player) {
		return plugin.getPlayerDataManager().getTimeLeft(player);
	}
	
	public static String getTotalTime(Player player) {
		return plugin.getPlayerDataManager().getTotalTime(player);
	}

	public static String getTimeUntilNextReset() {
		return plugin.getTimeResetManager().getTimeUntilNextReset();
	}

	public static PlayerTimeLimit getPlugin() {
		return plugin;
	}
}
