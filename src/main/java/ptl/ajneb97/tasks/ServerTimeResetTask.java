package ptl.ajneb97.tasks;

import org.bukkit.scheduler.BukkitRunnable;
import ptl.ajneb97.PlayerTimeLimit;

public class ServerTimeResetTask {

	private PlayerTimeLimit plugin;
	public ServerTimeResetTask(PlayerTimeLimit plugin) {
		this.plugin = plugin;
	}
	
	public void start() {
		new BukkitRunnable() {
			@Override
			public void run() {
				execute();
			}
			
		}.runTaskTimer(plugin, 1200L, 20L);
	}
	
	public void execute() {
		plugin.getTimeResetManager().checkAndReset();
	}
}
