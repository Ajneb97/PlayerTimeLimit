package ptl.ajneb97;


import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import ptl.ajneb97.api.ExpansionPlayerTimeLimit;
import ptl.ajneb97.api.PlayerTimeLimitAPI;
import ptl.ajneb97.configs.ConfigsManager;
import ptl.ajneb97.listeners.PlayerListener;
import ptl.ajneb97.managers.MensajesManager;
import ptl.ajneb97.managers.PlayerManager;
import ptl.ajneb97.managers.ServerManager;
import ptl.ajneb97.tasks.DataSaveTask;
import ptl.ajneb97.tasks.PlayerTimeTask;
import ptl.ajneb97.tasks.ServerTimeResetTask;



public class PlayerTimeLimit extends JavaPlugin {
  
	PluginDescriptionFile pdfFile = getDescription();
	public String version = pdfFile.getVersion();
	public String latestversion;
	
	public String rutaConfig;
	
	private PlayerManager playerManager;
	private ConfigsManager configsManager;
	private MensajesManager mensajesManager;
	private ServerManager serverManager;
	
	private DataSaveTask dataSaveTask;
	
	public static String nombrePlugin = ChatColor.translateAlternateColorCodes('&', "&8[&bPlayerTime&cLimit&8] ");
	
	public void onEnable(){
	   this.playerManager = new PlayerManager(this);
	   this.serverManager = new ServerManager(this);
	   registerEvents();
	   registerCommands();
	   registerConfig();
	   this.configsManager = new ConfigsManager(this);
	   this.configsManager.configurar();
	   
	   serverManager.executeDataTime();
	   
	   PlayerTimeTask timeTask = new PlayerTimeTask(this);
	   timeTask.start();
	   ServerTimeResetTask serverTask = new ServerTimeResetTask(this);
	   serverTask.start();
	   
	   recargarDataSaveTask();
	   checkMessagesUpdate();
	   
	   PlayerTimeLimitAPI api = new PlayerTimeLimitAPI(this);
	   if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null){
		   new ExpansionPlayerTimeLimit(this).register();
	   }
	   
	   Bukkit.getConsoleSender().sendMessage(nombrePlugin+ChatColor.YELLOW + "Has been enabled! " + ChatColor.WHITE + "Version: " + version);
	   Bukkit.getConsoleSender().sendMessage(nombrePlugin+ChatColor.YELLOW + "Thanks for using my plugin!  " + ChatColor.WHITE + "~Ajneb97");
	   
	   updateChecker();
	}
	  
	public void onDisable(){
		this.configsManager.getPlayerConfigsManager().guardarJugadores();
		serverManager.saveDataTime();
		Bukkit.getConsoleSender().sendMessage(nombrePlugin+ChatColor.YELLOW + "Has been disabled! " + ChatColor.WHITE + "Version: " + version);
	}
	public void registerCommands(){
		this.getCommand("playertimelimit").setExecutor(new Comando(this));
	}
	
	public void registerEvents(){
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(new PlayerListener(this), this);
	}
	
	public void registerConfig(){	
		File config = new File(this.getDataFolder(), "config.yml");
		rutaConfig = config.getPath();
		if(!config.exists()){
			this.getConfig().options().copyDefaults(true);
			saveConfig();  
		}
	}
	
	public void recargarConfigs() {
		this.configsManager.getMensajesConfigManager().reloadMessages();
		this.configsManager.getPlayerConfigsManager().guardarJugadores();
		reloadConfig();
		this.configsManager.getMainConfigManager().configurar();
		
		recargarDataSaveTask();
	}
	
	public void recargarDataSaveTask() {
		if(dataSaveTask != null) {
			dataSaveTask.end();
		}
		dataSaveTask = new DataSaveTask(this);
		dataSaveTask.start(getConfig().getInt("data_save_time"));
	}

	public PlayerManager getPlayerManager() {
		return playerManager;
	}

	public MensajesManager getMensajesManager() {
		return mensajesManager;
	}

	public void setMensajesManager(MensajesManager mensajesManager) {
		this.mensajesManager = mensajesManager;
	}
	
	public FileConfiguration getMessages() {
		return this.configsManager.getMensajesConfigManager().getMessages();
	}

	public ConfigsManager getConfigsManager() {
		return configsManager;
	}

	public ServerManager getServerManager() {
		return serverManager;
	}
	
	public void checkMessagesUpdate(){
		  Path archivoConfig = Paths.get(rutaConfig);
		  Path archivoMessages = Paths.get(configsManager.getMensajesConfigManager().getPath());
		  try{
			  String textoConfig = new String(Files.readAllBytes(archivoConfig));
			  String textoMessages = new String(Files.readAllBytes(archivoMessages));
			  FileConfiguration messages = configsManager.getMensajesConfigManager().getMessages();
			  
			  if(!textoMessages.contains("commandResetTimeError:")){
				  messages.set("commandResetTimeError", "&cYou need to use: &7/ptl resettime <player>");
				  messages.set("commandResetTimeCorrect", "&aCurrent time has been reset for player &7%player%&a!");
				  messages.set("commandTakeTimeError", "&cYou need to use: &7/ptl taketime <player> <time>");
				  messages.set("invalidNumber", "&cYou need to use a valid number!");
				  messages.set("commandTakeTimeCorrect", "&aTaken &7%time% seconds &afrom &7%player% &atime!");
				  messages.set("playerNotOnline", "&cThat player is not online.");
				  messages.set("commandAddTimeError", "&cYou need to use: &7/ptl addtime <player> <time>");
				  messages.set("commandAddTimeCorrect", "&aAdded &7%time% seconds &ato &7%player% &atime!");
				  configsManager.getMensajesConfigManager().saveMessages();
			  }
			  if(!textoConfig.contains("world_whitelist_system:")){
				  getConfig().set("world_whitelist_system.enabled", false);
				  List<String> lista = new ArrayList<String>();
				  lista.add("world");lista.add("world_nether");lista.add("world_the_end");
				  getConfig().set("world_whitelist_system.worlds", lista);
				  getConfig().set("world_whitelist_system.teleport_coordinates_on_kick", "spawn;0;60;0;90;0");
				  saveConfig();
			  }
			  if(!textoConfig.contains("update_notification:")){
				  getConfig().set("update_notification", true);
				  saveConfig();
				  messages.set("playerDoesNotExists", "&cThat player doesn't exists.");
				  List<String> lista = new ArrayList<String>();
				  lista.add("&c&m                                          ");
				  lista.add("&7&l%player% Data:");
				  lista.add("&7Time left: &a%time_left%");
				  lista.add("&7Total played time: &a%total_time%");
				  lista.add("&c&m                                          ");
				  messages.set("checkCommandMessage", lista);
				  lista = new ArrayList<String>();
				  lista.add("&c&m                                          ");
				  lista.add("&7Exact time when playtimes will be reset:");
				  lista.add("&e%reset_time%");
				  lista.add("");
				  lista.add("&7Remaining time until reset:");
				  lista.add("&e%remaining%");
				  lista.add("&c&m                                          ");
				  messages.set("infoCommandMessage", lista);
				  configsManager.getMensajesConfigManager().saveMessages();
			  }
		  }catch(IOException e){
			  e.printStackTrace();
		  }
	}
	
	public void updateChecker(){
		try {
			HttpURLConnection con = (HttpURLConnection) new URL(
					"https://api.spigotmc.org/legacy/update.php?resource=96577").openConnection();
			int timed_out = 1250;
			con.setConnectTimeout(timed_out);
			con.setReadTimeout(timed_out);
			latestversion = new BufferedReader(new InputStreamReader(con.getInputStream())).readLine();
			if (latestversion.length() <= 7) {
				if(!version.equals(latestversion)){
					Bukkit.getConsoleSender().sendMessage(ChatColor.RED +"There is a new version available. "+ChatColor.YELLOW+
							"("+ChatColor.GRAY+latestversion+ChatColor.YELLOW+")");
					Bukkit.getConsoleSender().sendMessage(ChatColor.RED+"You can download it at: "+ChatColor.WHITE+"https://www.spigotmc.org/resources/96577/");  
				}      	  
			}
		} catch (Exception ex) {
			Bukkit.getConsoleSender().sendMessage(nombrePlugin + ChatColor.RED +"Error while checking update.");
		}
	}
}
