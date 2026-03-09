package ptl.ajneb97.tasks;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import ptl.ajneb97.PlayerTimeLimit;
import ptl.ajneb97.configs.MainConfigManager;
import ptl.ajneb97.managers.PlayerDataManager;
import ptl.ajneb97.managers.PlayerTimeManager;

public class PlayerInfoBarsTask {

	private PlayerTimeLimit plugin;
	public PlayerInfoBarsTask(PlayerTimeLimit plugin) {
		this.plugin = plugin;
	}
	
	public void start() {
		new BukkitRunnable() {
			@Override
			public void run() {
				execute();
			}
			
		}.runTaskTimerAsynchronously(plugin, 1L, 20L);
	}
	
	public void execute() {
		MainConfigManager mainConfigManager = plugin.getConfigsManager().getMainConfigManager();
		PlayerTimeManager playerTimeManager = plugin.getPlayerTimeManager();
		PlayerDataManager playerDataManager = plugin.getPlayerDataManager();
		for(Player player : Bukkit.getOnlinePlayers()){
			playerTimeManager.sendInfoBars(player,playerDataManager,mainConfigManager);
		}
	}

}
