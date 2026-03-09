package ptl.ajneb97.tasks;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import ptl.ajneb97.PlayerTimeLimit;
import ptl.ajneb97.configs.MainConfigManager;
import ptl.ajneb97.managers.PlayerTimeManager;

public class PlayerTimeTask {

	private PlayerTimeLimit plugin;
	public PlayerTimeTask(PlayerTimeLimit plugin) {
		this.plugin = plugin;
	}
	
	public void start() {
		new BukkitRunnable() {
			@Override
			public void run() {
				execute();
			}
			
		}.runTaskTimer(plugin, 0L, 20L);
	}
	
	public void execute() {
		MainConfigManager mainConfigManager = plugin.getConfigsManager().getMainConfigManager();
		PlayerTimeManager playerTimeManager = plugin.getPlayerTimeManager();
		for(Player player : Bukkit.getOnlinePlayers()){
			playerTimeManager.updateTimePlayer(player,mainConfigManager);
		}
	}

}
