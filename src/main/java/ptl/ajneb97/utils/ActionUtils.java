package ptl.ajneb97.utils;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import ptl.ajneb97.PlayerTimeLimit;
import ptl.ajneb97.api.PlayerTimeLimitAPI;
import ptl.ajneb97.libs.title.TitleAPI;
import ptl.ajneb97.managers.MessagesManager;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ActionUtils {

    public static void executeAction(Player player, String actionText, PlayerTimeLimit plugin){
        int indexFirst = actionText.indexOf(" ");
        String actionType = actionText.substring(0,indexFirst).replace(":","");
        String actionLine = actionText.substring(indexFirst+1);
        actionLine = OtherUtils.replaceGlobalVariables(actionLine,player,plugin);

        switch (actionType) {
            case "message" -> ActionUtils.message(player, actionLine);
            case "centered_message" -> ActionUtils.centeredMessage(player, actionLine);
            case "console_command" -> ActionUtils.consoleCommand(actionLine);
            case "player_command" -> ActionUtils.playerCommand(player, actionLine);
            case "playsound" -> ActionUtils.playSound(player, actionLine);
            case "title" -> ActionUtils.title(player, actionLine);
        }
    }

    public static void playSound(Player player, String soundLine){
        String[] sep = soundLine.split(";");
        Sound sound;
        float volume;
        float pitch;
        try {
            sound = getSoundByName(sep[0]);
            volume = Float.parseFloat(sep[1]);
            pitch = Float.parseFloat(sep[2]);
        }catch(Exception e ) {
            Bukkit.getConsoleSender().sendMessage(PlayerTimeLimit.prefix+MessagesManager.getLegacyColoredMessage("&7Sound Name: &c"+sep[0]+" &7is not valid. Change it in the config!"));
            return;
        }

        player.playSound(player.getLocation(), sound, volume, pitch);
    }

    private static Sound getSoundByName(String name){
        try {
            Class<?> soundTypeClass = Class.forName("org.bukkit.Sound");
            Method valueOf = soundTypeClass.getMethod("valueOf", String.class);
            return (Sound) valueOf.invoke(null,name);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static void consoleCommand(String actionLine){
        ConsoleCommandSender sender = Bukkit.getConsoleSender();
        Bukkit.dispatchCommand(sender, actionLine);
    }

    public static void playerCommand(Player player, String actionLine){
        player.performCommand(actionLine);
    }

    public static void message(Player player,String actionLine){
        if(PlayerTimeLimitAPI.getPlugin().getConfigsManager().getMainConfigManager().isUseMiniMessage()){
            MiniMessageUtils.message(player,actionLine);
        }else{
            player.sendMessage(MessagesManager.getLegacyColoredMessage(actionLine));
        }
    }

    public static void centeredMessage(Player player,String actionLine){
        if(PlayerTimeLimitAPI.getPlugin().getConfigsManager().getMainConfigManager().isUseMiniMessage()){
            MiniMessageUtils.centeredMessage(player,actionLine);
        }else{
            actionLine = MessagesManager.getLegacyColoredMessage(actionLine);
            player.sendMessage(MessagesManager.getCenteredMessage(actionLine));
        }
    }

    public static void title(Player player,String actionLine){
        String[] sep = actionLine.split(";");
        int fadeIn = Integer.parseInt(sep[0]);
        int stay = Integer.parseInt(sep[1]);
        int fadeOut = Integer.parseInt(sep[2]);

        String title = sep[3];
        String subtitle = sep[4];
        if(title.equals("none")) {
            title = "";
        }
        if(subtitle.equals("none")) {
            subtitle = "";
        }
        TitleAPI.sendTitle(player,fadeIn,stay,fadeOut,title,subtitle);
    }

    public static void kick(Player player,String actionLine){
        if(PlayerTimeLimitAPI.getPlugin().getConfigsManager().getMainConfigManager().isUseMiniMessage()) {
            MiniMessageUtils.kick(player,actionLine);
        }else{
            player.kickPlayer(MessagesManager.getLegacyColoredMessage(actionLine));
        }
    }
}
