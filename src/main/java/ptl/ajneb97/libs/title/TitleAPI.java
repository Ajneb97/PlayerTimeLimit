package ptl.ajneb97.libs.title;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import ptl.ajneb97.api.PlayerTimeLimitAPI;
import ptl.ajneb97.managers.MessagesManager;
import ptl.ajneb97.utils.MiniMessageUtils;


public class TitleAPI implements Listener {

    public static void sendTitle(Player player, Integer fadeIn, Integer stay, Integer fadeOut, String title, String subtitle) {
        if(title.isEmpty()) {
            title = " ";
        }
        if(subtitle.isEmpty()) {
            subtitle = " ";
        }

        if(PlayerTimeLimitAPI.getPlugin().getConfigsManager().getMainConfigManager().isUseMiniMessage()){
            MiniMessageUtils.title(player,title,subtitle);
        }else{
            player.sendTitle(MessagesManager.getLegacyColoredMessage(title), MessagesManager.getLegacyColoredMessage(subtitle), fadeIn, stay, fadeOut);
        }
    }
}