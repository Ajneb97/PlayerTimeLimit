package ptl.ajneb97.tasks;

import org.bukkit.scheduler.BukkitRunnable;
import ptl.ajneb97.PlayerTimeLimit;

public class PlayerDataSaveTask {

	private PlayerTimeLimit plugin;
	private boolean end;
	public PlayerDataSaveTask(PlayerTimeLimit plugin) {
		this.plugin = plugin;
		this.end = false;
	}
	
	public void end() {
		end = true;
	}
	
	public void start(int seconds) {
		long ticks = seconds* 20L;
		
		new BukkitRunnable() {
			@Override
			public void run() {
				if(end) {
					this.cancel();
				}else {
					execute();
				}
			}
			
		}.runTaskTimerAsynchronously(plugin, ticks, ticks);
	}
	
	public void execute() {
		plugin.getDatabaseManager().savePlayers(true);
	}
}
