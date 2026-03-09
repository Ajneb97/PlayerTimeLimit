package ptl.ajneb97.configs;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.bukkit.configuration.file.FileConfiguration;
import ptl.ajneb97.PlayerTimeLimit;
import ptl.ajneb97.configs.model.CommonConfig;
import ptl.ajneb97.managers.MessagesManager;

public class MessagesConfigManager {

	private PlayerTimeLimit plugin;
	private CommonConfig configFile;
	
	public MessagesConfigManager(PlayerTimeLimit plugin) {
		this.plugin = plugin;
		this.configFile = new CommonConfig("messages.yml",plugin,null,false);
		configFile.registerConfig();
		checkUpdates();
	}

	public void configure() {
		FileConfiguration config = configFile.getConfig();

		MessagesManager messagesManager = new MessagesManager();
		messagesManager.setPrefix(config.getString("prefix"));
		messagesManager.setActionBarMessage(config.getString("actionBarMessage"));
		messagesManager.setBossBarMessage(config.getString("bossBarMessage"));
		messagesManager.setTimeSeconds(config.getString("timeSeconds"));
		messagesManager.setTimeMinutes(config.getString("timeMinutes"));
		messagesManager.setTimeHours(config.getString("timeHours"));
		messagesManager.setTimeDays(config.getString("timeDays"));
		messagesManager.setTimeInfinite(config.getString("timeInfinite"));

		plugin.setMessagesManager(messagesManager);
	}

	public CommonConfig getConfigFile() {
		return configFile;
	}

	public boolean reloadConfig(){
		if(!configFile.reloadConfig()){
			return false;
		}
		configure();
		return true;
	}

	public void checkUpdates(){
		Path pathConfig = Paths.get(configFile.getRoute());
		try{
			String text = new String(Files.readAllBytes(pathConfig));
			FileConfiguration config = configFile.getConfig();

		}catch(IOException e){
			e.printStackTrace();
		}
	}
}
