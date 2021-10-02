package ptl.ajneb97.tasks;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import ptl.ajneb97.PlayerTimeLimit;
import ptl.ajneb97.configs.MainConfigManager;
import ptl.ajneb97.model.TimeLimitPlayer;

public class ServerTimeResetTask {

	private PlayerTimeLimit plugin;
	private DateTimeFormatter dtf;
	public ServerTimeResetTask(PlayerTimeLimit plugin) {
		this.plugin = plugin;
		this.dtf = DateTimeFormatter.ofPattern("HH:mm"); 
	}
	
	public void start() {
		new BukkitRunnable() {
			@Override
			public void run() {
				execute();
			}
			
		}.runTaskTimer(plugin, 0L, 1200L); //cada 60 segundos
	}
	
	public void execute() {
		MainConfigManager mainConfig = plugin.getConfigsManager().getMainConfigManager();
		String resetTime = mainConfig.getResetTime();
		
		LocalDateTime now = LocalDateTime.now(); 
		String currentTime = dtf.format(now);
		if(resetTime.equals(currentTime)) {
			//REINICIO DE TIEMPO
			new BukkitRunnable() {
				@Override
				public void run() {
					plugin.getPlayerManager().resetPlayers();
				}
				
			}.runTaskAsynchronously(plugin);
		}
	}
}
