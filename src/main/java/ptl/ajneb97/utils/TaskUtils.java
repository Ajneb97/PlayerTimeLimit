package ptl.ajneb97.utils;

import org.bukkit.scheduler.BukkitRunnable;
import ptl.ajneb97.PlayerTimeLimit;

public class TaskUtils {
    public static void runAsync(Runnable task, PlayerTimeLimit plugin){
        new BukkitRunnable() {
            @Override
            public void run() {
                task.run();
            }
        }.runTaskAsynchronously(plugin);
    }

    public static void runSync(Runnable task, PlayerTimeLimit plugin){
        new BukkitRunnable() {
            @Override
            public void run() {
                task.run();
            }
        }.runTask(plugin);
    }
}
