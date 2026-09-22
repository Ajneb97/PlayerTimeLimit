package ptl.ajneb97.tasks;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import ptl.ajneb97.PlayerTimeLimit;
import ptl.ajneb97.managers.PlayerDataManager;

public class PlayerUpdateLimitTask {

    private PlayerTimeLimit plugin;
    public PlayerUpdateLimitTask(PlayerTimeLimit plugin) {
        this.plugin = plugin;
    }

    public void start() {
        new BukkitRunnable() {
            @Override
            public void run() {
                execute();
            }

        }.runTaskTimer(plugin, 0L, 60L);
    }

    public void execute() {
        PlayerDataManager playerDataManager = plugin.getPlayerDataManager();
        for(Player player : Bukkit.getOnlinePlayers()){
            if(playerDataManager.isReadyForTimeTracking(player)) {
                playerDataManager.updateTimeLimit(player);
            }
        }
    }
}
