package ptl.ajneb97.configs;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;

import ptl.ajneb97.PlayerTimeLimit;
import ptl.ajneb97.model.TimeLimitPlayer;

public class PlayerConfigsManager {
	
	private ArrayList<PlayerConfig> configPlayers;
	private PlayerTimeLimit plugin;
	
	public PlayerConfigsManager(PlayerTimeLimit plugin) {
		this.plugin = plugin;
		this.configPlayers = new ArrayList<PlayerConfig>();
	}
	
	public void configurar() {
		createPlayersFolder();
		registerPlayers();
		cargarJugadores();
	}
	
	public void createPlayersFolder(){
		File folder;
        try {
            folder = new File(plugin.getDataFolder() + File.separator + "players");
            if(!folder.exists()){
                folder.mkdirs();
            }
        } catch(SecurityException e) {
            folder = null;
        }
	}
	
	public void savePlayers() {
		for(int i=0;i<configPlayers.size();i++) {
			configPlayers.get(i).savePlayerConfig();
		}
	}
	
	public void registerPlayers(){
		String path = plugin.getDataFolder() + File.separator + "players";
		File folder = new File(path);
		File[] listOfFiles = folder.listFiles();
		for (int i=0;i<listOfFiles.length;i++) {
			if(listOfFiles[i].isFile()) {
		        String pathName = listOfFiles[i].getName();
		        PlayerConfig config = new PlayerConfig(pathName,plugin);
		        config.registerPlayerConfig();
		        configPlayers.add(config);
		    }
		}
	}
	
	public ArrayList<PlayerConfig> getConfigPlayers(){
		return this.configPlayers;
	}
	
	public boolean archivoYaRegistrado(String pathName) {
		for(int i=0;i<configPlayers.size();i++) {
			if(configPlayers.get(i).getPath().equals(pathName)) {
				return true;
			}
		}
		return false;
	}
	
	public PlayerConfig getPlayerConfig(String pathName) {
		for(int i=0;i<configPlayers.size();i++) {
			if(configPlayers.get(i).getPath().equals(pathName)) {
				return configPlayers.get(i);
			}
		}
		return null;
	}
	
	public ArrayList<PlayerConfig> getPlayerConfigs() {
		return this.configPlayers;
	}
	
	public boolean registerPlayer(String pathName) {
		if(!archivoYaRegistrado(pathName)) {
			PlayerConfig config = new PlayerConfig(pathName,plugin);
	        config.registerPlayerConfig();
	        configPlayers.add(config);
	        return true;
		}else {
			return false;
		}
	}
	
	public void removerConfigPlayer(String path) {
		for(int i=0;i<configPlayers.size();i++) {
			if(configPlayers.get(i).getPath().equals(path)) {
				configPlayers.remove(i);
			}
		}
	}
	
	public void cargarJugadores() {
		ArrayList<TimeLimitPlayer> jugadores = new ArrayList<TimeLimitPlayer>();
		
		for(PlayerConfig playerConfig : configPlayers) {
			FileConfiguration players = playerConfig.getConfig();
			String name = players.getString("name");
			String uuid = playerConfig.getPath().replace(".yml", "");
			
			TimeLimitPlayer p = new TimeLimitPlayer(uuid,name);
			
			p.setCurrentTime(players.getInt("current_time"));
			p.setTotalTime(players.getInt("total_time"));
			p.setMessageEnabled(players.getBoolean("messages"));
			
			jugadores.add(p);
		}
		plugin.getPlayerManager().setPlayers(jugadores);
	}
	
	public void guardarJugadores() {
		for(TimeLimitPlayer player : plugin.getPlayerManager().getPlayers()) {
			String jugador = player.getName();
			PlayerConfig playerConfig = getPlayerConfig(player.getUuid()+".yml");
			if(playerConfig == null) {
				registerPlayer(player.getUuid()+".yml");
				playerConfig = getPlayerConfig(player.getUuid()+".yml");
			}
			FileConfiguration players = playerConfig.getConfig();
			
			players.set("name", jugador);
			players.set("current_time", player.getCurrentTime());
			players.set("total_time", player.getTotalTime());
			players.set("messages", player.isMessageEnabled());
		}
		savePlayers();
	}
}
