package ptl.ajneb97.managers.dependencies;

import org.bukkit.Bukkit;
import ptl.ajneb97.PlayerTimeLimit;

public class DependencyManager {

    private PlayerTimeLimit plugin;

    private boolean isPlaceholderAPI;
    private boolean isPaper;

    public DependencyManager(PlayerTimeLimit plugin){
        this.plugin = plugin;

        if(Bukkit.getServer().getPluginManager().getPlugin("PlaceholderAPI") != null){
            isPlaceholderAPI = true;
        }
        try{
            Class.forName("com.destroystokyo.paper.ParticleBuilder");
            isPaper = true;
        }catch(Exception e){

        }
    }

    public boolean isPlaceholderAPI() {
        return isPlaceholderAPI;
    }

    public boolean isPaper() {
        return isPaper;
    }
}
