package ptl.ajneb97.utils;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;
import ptl.ajneb97.PlayerTimeLimit;

public class OtherUtils {

    public static String replaceGlobalVariables(String text, Player player, PlayerTimeLimit plugin) {
        if(player == null){
            return text;
        }
        text = text.replace("%player%",player.getName());
        if(plugin.getDependencyManager().isPlaceholderAPI()) {
            text = PlaceholderAPI.setPlaceholders(player, text);
        }

        return text;
    }
}
