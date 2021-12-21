package ptl.ajneb97.managers;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import ptl.ajneb97.PlayerTimeLimit;
import ptl.ajneb97.configs.MainConfigManager;
import ptl.ajneb97.configs.others.TimeLimit;
import ptl.ajneb97.model.TimeLimitPlayer;
import ptl.ajneb97.utils.BypassTimes;
import ptl.ajneb97.utils.UtilsTime;

public class PlayerManager {

	private ArrayList<TimeLimitPlayer> players;
	private PlayerTimeLimit plugin;
	
	public PlayerManager(PlayerTimeLimit plugin) {
		this.plugin = plugin;
		this.players = new ArrayList<TimeLimitPlayer>();
	}

	public ArrayList<TimeLimitPlayer> getPlayers() {
		return players;
	}

	public void setPlayers(ArrayList<TimeLimitPlayer> players) {
		this.players = players;
	}
	
	public TimeLimitPlayer getPlayerByUUID(String uuid) {
		for(TimeLimitPlayer p : players) {
			if(p.getUuid().equals(uuid)) {
				return p;
			}
		}
		return null;
	}
	
	public TimeLimitPlayer getPlayerByName(String name) {
		for(TimeLimitPlayer p : players) {
			if(p.getName().equals(name)) {
				return p;
			}
		}
		return null;
	}
	
	public TimeLimitPlayer createPlayer(Player player) {
		TimeLimitPlayer p = new TimeLimitPlayer(player.getUniqueId().toString(),player.getName());
		players.add(p);
		FileConfiguration config = plugin.getConfig();
		p.setMessageEnabled(config.getBoolean("information_message_enabled_by_default"));
		
		return p;
	}
	
	public void checkUserTime(final Player player,TimeLimitPlayer p) {
		if(hasTimeLeft(p)) {
			return;
		}
		
		//El jugador ya ha completado su tiempo
		final FileConfiguration messages = plugin.getMessages();
		new BukkitRunnable() {
			public void run() {
				MainConfigManager mainConfig = plugin.getConfigsManager().getMainConfigManager();
				if(mainConfig.isWorldWhitelistEnabled()) {
					worldWhitelistSystemKick(player);
					return;
				}
				
				List<String> msg = messages.getStringList("kickMessage");
				String finalMessage = "";
				for(String line : msg) {
					finalMessage = finalMessage+line+"\n";
				}
				finalMessage = MensajesManager.getMensajeColor(finalMessage);
				player.kickPlayer(finalMessage);
			}
		}.runTask(plugin);
	}
	
	public void resetPlayers() {
		for(TimeLimitPlayer p : players) {
			p.setCurrentTime(0);
		}
	}
	
	public boolean hasTimeLeft(TimeLimitPlayer p) {
		int currentTime = p.getCurrentTime();
		int timeLimit = getTimeLimitPlayer(p.getPlayer());
		if(currentTime < timeLimit || timeLimit == 0 || BypassTimes.isBypassNow(plugin)) {
			return true;
		}
		return false;
	}
	
	public int getTimeLimitPlayer(Player player) {
		int timeReal = 0;
		ArrayList<TimeLimit> timeLimits = plugin.getConfigsManager().getMainConfigManager().getTimeLimits();
		for(TimeLimit timeLimit : timeLimits) {
			
			String name = timeLimit.getName();
			int time = timeLimit.getTime();
			if(name.equals("default")) {
				timeReal = time;
				continue;
			}
			if(name.equals("op")) {
				if(player.isOp()) {
					return time;
				}
			}
			
			String permission = "playertimelimit.limit."+name;
			if(player.hasPermission(permission)) {
				timeReal = time;
			}
		}
		
		return timeReal;
	}
	
	public String getTimeLeft(TimeLimitPlayer p,int timeLimit) {
		MensajesManager msgManager = plugin.getMensajesManager();
		int remainingTime = timeLimit-p.getCurrentTime();
		String timeString = "";
		if(timeLimit == 0) {
			timeString = msgManager.getTimeInfinite();
		}else {
			timeString = UtilsTime.getTime(remainingTime,msgManager);
		}
		return timeString;
	}
	
	public void worldWhitelistSystemKick(Player player) {
		MainConfigManager mainConfig = plugin.getConfigsManager().getMainConfigManager();
		String coordinates = mainConfig.getWorldWhitelistTeleportCoordinates();
		try {
			String[] sep = coordinates.split(";");
			World world = Bukkit.getWorld(sep[0]);
			double x = Double.valueOf(sep[1]);
			double y = Double.valueOf(sep[2]);
			double z = Double.valueOf(sep[3]);
			float yaw = Float.valueOf(sep[4]);
			float pitch = Float.valueOf(sep[5]);
			
			player.teleport(new Location(world,x,y,z,yaw,pitch));
			
			FileConfiguration messages = plugin.getMessages();
			List<String> msg = messages.getStringList("kickMessage");
			for(String m : msg) {
				player.sendMessage(MensajesManager.getMensajeColor(m));
			}
		}catch(Exception e) {
			player.sendMessage(plugin.nombrePlugin+" &cError! Impossible to teleport &7"+player.getName()
			+" &cto this coordinates: &7"+coordinates);
		}
	}
}
