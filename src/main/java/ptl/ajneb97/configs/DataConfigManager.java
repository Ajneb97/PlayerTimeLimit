package ptl.ajneb97.configs;

import org.bukkit.configuration.file.FileConfiguration;
import ptl.ajneb97.PlayerTimeLimit;
import ptl.ajneb97.configs.model.CommonConfig;

public class DataConfigManager {

    private PlayerTimeLimit plugin;
    private CommonConfig configFile;

    public DataConfigManager(PlayerTimeLimit plugin){
        this.plugin = plugin;
        this.configFile = new CommonConfig("data.yml",plugin,null,false);
        configFile.registerConfig();
    }

    public void configure() {
        FileConfiguration config = configFile.getConfig();

        if(config.contains("last_reset")){
            plugin.getTimeResetManager().setLastResetMillis(config.getLong("last_reset"));
        }
    }

    public void saveConfig(long lastResetMillis){
        FileConfiguration config = configFile.getConfig();
        config.set("last_reset",lastResetMillis);
        configFile.saveConfig();
    }

    public CommonConfig getConfigFile() {
        return configFile;
    }

}
