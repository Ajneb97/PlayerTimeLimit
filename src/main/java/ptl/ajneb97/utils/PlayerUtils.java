package ptl.ajneb97.utils;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import ptl.ajneb97.api.PlayerTimeLimitAPI;

import java.util.UUID;

public class PlayerUtils {
    public static boolean isPlayerTimeLimitAdmin(CommandSender sender){
        return sender.hasPermission("playertimelimit.admin");
    }

    public static UUID getOfflineUUIDByPlayerName(String playerName){
        OfflinePlayer offlinePlayer = null;
        if(PlayerTimeLimitAPI.getPlugin().getDependencyManager().isPaper()){
            offlinePlayer = Bukkit.getOfflinePlayerIfCached(playerName);
        }

        if(offlinePlayer == null){
            offlinePlayer = Bukkit.getOfflinePlayer(playerName);
        }

        if(offlinePlayer.hasPlayedBefore()){
            return offlinePlayer.getUniqueId();
        }

        return null;
    }
}
