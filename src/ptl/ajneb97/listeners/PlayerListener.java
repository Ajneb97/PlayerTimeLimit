package ptl.ajneb97.listeners;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerQuitEvent;

import ptl.ajneb97.PlayerTimeLimit;
import ptl.ajneb97.configs.others.TimeLimit;
import ptl.ajneb97.managers.MensajesManager;
import ptl.ajneb97.managers.PlayerManager;
import ptl.ajneb97.model.TimeLimitPlayer;

public class PlayerListener implements Listener{

	private PlayerTimeLimit plugin;
	public PlayerListener(PlayerTimeLimit plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onPreJoin(PlayerLoginEvent event) {
		Player player = event.getPlayer();
		String uuid = player.getUniqueId().toString();
		PlayerManager playerManager = plugin.getPlayerManager();
		TimeLimitPlayer p = playerManager.getPlayerByUUID(uuid);
		if(p != null) {
			int currentTime = p.getCurrentTime();
			int timeLimit = playerManager.getTimeLimitPlayer(player);
			if(currentTime >= timeLimit && timeLimit != 0) {
				FileConfiguration messages = plugin.getMessages();
				List<String> msg = messages.getStringList("joinErrorMessage");
				String finalMessage = "";
				for(String line : msg) {
					finalMessage = finalMessage+line+"\n";
				}
				finalMessage = MensajesManager.getMensajeColor(finalMessage);
				event.disallow(Result.KICK_OTHER, finalMessage);
			}
		}
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		
		PlayerManager playerManager = plugin.getPlayerManager();
		TimeLimitPlayer p = playerManager.getPlayerByUUID(player.getUniqueId().toString());
		if(p == null) {
			p = playerManager.createPlayer(player);
		}
		p.setPlayer(player);
		p.setName(player.getName());
		
		FileConfiguration config = plugin.getConfig();
		if(config.getString("update_notification").equals("true")) {
			if(player.isOp() && !(plugin.version.equals(plugin.latestversion))){
				player.sendMessage(plugin.nombrePlugin + ChatColor.RED +" There is a new version available. "+ChatColor.YELLOW+
		  				  "("+ChatColor.GRAY+plugin.latestversion+ChatColor.YELLOW+")");
				player.sendMessage(ChatColor.RED+"You can download it at: "+ChatColor.GREEN+"https://www.spigotmc.org/resources/96577/");			 
			}
		}
	}
	
	@EventHandler
	public void onLeave(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		
		PlayerManager playerManager = plugin.getPlayerManager();
		TimeLimitPlayer p = playerManager.getPlayerByUUID(player.getUniqueId().toString());
		if(p != null) {
			p.setPlayer(null);
			p.eliminarBossBar();
		}
	}
}
