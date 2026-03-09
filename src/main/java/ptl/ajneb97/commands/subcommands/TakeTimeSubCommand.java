package ptl.ajneb97.commands.subcommands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import ptl.ajneb97.api.PlayerTimeLimitAPI;
import ptl.ajneb97.commands.SubCommand;
import ptl.ajneb97.managers.MessagesManager;
import ptl.ajneb97.managers.PlayerDataManager;
import ptl.ajneb97.utils.PlayerUtils;
import java.util.List;

public class TakeTimeSubCommand implements SubCommand {

    @Override
    public String getName() {
        return "taketime";
    }

    @Override
    public boolean hasPermission(CommandSender sender){
        return PlayerUtils.isPlayerTimeLimitAdmin(sender);
    }

    @Override
    public void execute(CommandSender sender, String[] args, MessagesManager msgManager, FileConfiguration messagesConfig) {
        // /ptl taketime <player> <time>
        if(!hasPermission(sender)){
            msgManager.sendMessage(sender,messagesConfig.getString("noPermissions"),true);
            return;
        }

        if(args.length <= 2){
            msgManager.sendMessage(sender,messagesConfig.getString("commandTakeTimeError"),true);
            return;
        }

        String playerName = args[1];
        Player player = Bukkit.getPlayer(playerName);
        if(player == null){
            msgManager.sendMessage(sender,messagesConfig.getString("playerNotOnline"),true);
            return;
        }

        int time;
        try {
            time = Integer.parseInt(args[2]);
            if(time <= 0) {
                msgManager.sendMessage(sender,messagesConfig.getString("invalidNumber"),true);
                return;
            }
        }catch(NumberFormatException e) {
            msgManager.sendMessage(sender,messagesConfig.getString("invalidNumber"),true);
            return;
        }

        PlayerDataManager playerDataManager = PlayerTimeLimitAPI.getPlugin().getPlayerDataManager();
        playerDataManager.takePlayTime(player,time);

        msgManager.sendMessage(sender,messagesConfig.getString("commandTakeTimeCorrect")
                .replace("%time%",time+"").replace("%player%",playerName),true);
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (!hasPermission(sender)) {
            return null;
        }

        if (args.length == 2) {
            return null;
        }else if(args.length == 3){
            List<String> completions = List.of("<amount>");
            return completions.stream()
                    .filter(name -> name.toLowerCase().startsWith(args[2].toLowerCase()))
                    .toList();
        }

        return null;
    }
}
