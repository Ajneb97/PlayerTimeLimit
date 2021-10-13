package ptl.ajneb97.api;

import org.bukkit.entity.Player;

import ptl.ajneb97.PlayerTimeLimit;
import ptl.ajneb97.managers.PlayerManager;
import ptl.ajneb97.model.TimeLimitPlayer;
import ptl.ajneb97.utils.UtilsTime;

public class PlayerTimeLimitAPI {

	private static PlayerTimeLimit plugin;
	public PlayerTimeLimitAPI(PlayerTimeLimit plugin) {
		this.plugin = plugin;
	}
	
	public static String getTimeLeft(Player player) {
		PlayerManager playerManager = plugin.getPlayerManager();
		TimeLimitPlayer p = playerManager.getPlayerByUUID(player.getUniqueId().toString());
		int timeLimit = playerManager.getTimeLimitPlayer(player);
		return playerManager.getTimeLeft(p, timeLimit);
	}
	
	public static String getTotalTime(Player player) {
		PlayerManager playerManager = plugin.getPlayerManager();
		TimeLimitPlayer p = playerManager.getPlayerByUUID(player.getUniqueId().toString());
		return UtilsTime.getTime(p.getTotalTime(), plugin.getMensajesManager());
	}
}
