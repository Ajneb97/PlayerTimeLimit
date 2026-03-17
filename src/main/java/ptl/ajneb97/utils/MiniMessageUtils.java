package ptl.ajneb97.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.title.Title;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import ptl.ajneb97.managers.MessagesManager;

public class MiniMessageUtils {

    public static void messagePrefix(CommandSender sender, String message, boolean isPrefix, String prefix){
        if(isPrefix){
            sender.sendMessage(MiniMessage.miniMessage().deserialize(prefix+message));
        }else{
            sender.sendMessage(MiniMessage.miniMessage().deserialize(message));
        }
    }

    public static void title(Player player, String title, String subtitle, Integer fadeIn, Integer stay, Integer fadeOut){
        player.showTitle(Title.title(
                MiniMessage.miniMessage().deserialize(title),MiniMessage.miniMessage().deserialize(subtitle),
                fadeIn,stay,fadeOut
        ));
    }

    public static void actionbar(Player player, String message){
        player.sendActionBar(MiniMessage.miniMessage().deserialize(message));
    }

    public static void message(Player player,String message){
        player.sendMessage(MiniMessage.miniMessage().deserialize(message));
    }

    public static void centeredMessage(Player player,String message){
        MiniMessage mm = MiniMessage.miniMessage();
        Component component = mm.deserialize(message);
        String centeredTextLegacy = MessagesManager.getCenteredMessage(LegacyComponentSerializer.legacySection().serialize(component)); // to legacy
        Component centeredTextMiniMessage = LegacyComponentSerializer.legacySection().deserialize(centeredTextLegacy); // to minimessage
        player.sendMessage(centeredTextMiniMessage);
    }

    public static void kick(Player player,String message){
        player.kick(MiniMessage.miniMessage().deserialize(message));
    }

    public static void preLoginKickMessage(AsyncPlayerPreLoginEvent preJoinEvent, String message){
        preJoinEvent.kickMessage(MiniMessage.miniMessage().deserialize(message));
    }
}
