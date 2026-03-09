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

import java.util.ArrayList;
import java.util.List;

public class ResetTimeSubCommand implements SubCommand {

    @Override
    public String getName() {
        return "resettime";
    }

    @Override
    public boolean hasPermission(CommandSender sender){
        return PlayerUtils.isPlayerTimeLimitAdmin(sender);
    }

    @Override
    public void execute(CommandSender sender, String[] args, MessagesManager msgManager, FileConfiguration messagesConfig) {
        // /ptl resettime <player>/*
        if(!hasPermission(sender)){
            msgManager.sendMessage(sender,messagesConfig.getString("noPermissions"),true);
            return;
        }

        if(args.length <= 1){
            msgManager.sendMessage(sender,messagesConfig.getString("commandResetTimeError"),true);
            return;
        }

        String playerName = args[1];
        PlayerDataManager playerDataManager = PlayerTimeLimitAPI.getPlugin().getPlayerDataManager();
        if(playerName.equals("*")){
            playerDataManager.resetTimeForAllPlayers(messagesConfig,result -> {
                msgManager.sendMessage(sender,result,true);
            });
        }else{
            playerDataManager.resetTimeForPlayer(playerName,messagesConfig,result -> {
                msgManager.sendMessage(sender,result,true);
            });
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (!hasPermission(sender)) {
            return null;
        }

        if (args.length == 2) {
            List<String> completions = new ArrayList<>();
            for (Player online : Bukkit.getOnlinePlayers()) {
                completions.add(online.getName());
            }
            completions.add("*");

            return completions.stream()
                    .filter(name -> name.toLowerCase().startsWith(args[1].toLowerCase()))
                    .toList();
        }

        return null;
    }
}
