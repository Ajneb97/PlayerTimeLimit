package ptl.ajneb97.commands.subcommands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import ptl.ajneb97.api.PlayerTimeLimitAPI;
import ptl.ajneb97.commands.PlayerSubCommand;
import ptl.ajneb97.managers.MessagesManager;
import ptl.ajneb97.managers.PlayerDataManager;
import ptl.ajneb97.model.player.PlayerData;
import java.util.List;

public class CheckSubCommand extends PlayerSubCommand {

    @Override
    public String getName() {
        return "check";
    }

    @Override
    public boolean hasPermission(CommandSender sender){
        return sender.hasPermission("playertimelimit.commands.check");
    }

    @Override
    protected void executePlayer(Player player, String[] args, MessagesManager msgManager, FileConfiguration messagesConfig) {
        // /ptl check (optional)<player>
        if(!hasPermission(player)){
            msgManager.sendMessage(player,messagesConfig.getString("noPermissions"),true);
            return;
        }

        Player playerCheck;
        PlayerDataManager playerDataManager = PlayerTimeLimitAPI.getPlugin().getPlayerDataManager();
        if(args.length >= 2){
            if(!player.hasPermission("playertimelimit.commands.check.others")){
                msgManager.sendMessage(player,messagesConfig.getString("noPermissions"),true);
                return;
            }

            playerCheck = Bukkit.getPlayer(args[1]);
            if(playerCheck == null){
                msgManager.sendMessage(player,messagesConfig.getString("playerNotOnline"),true);
                return;
            }
        }else{
            playerCheck = player;
        }

        PlayerData playerData = playerDataManager.getPlayer(playerCheck);
        if(playerData == null){
            msgManager.sendMessage(player,messagesConfig.getString("playerDoesNotExists"),true);
            return;
        }

        String timeLeft = playerDataManager.getTimeLeft(playerCheck);
        String totalTime = playerDataManager.getTotalTime(playerCheck);

        List<String> msg = messagesConfig.getStringList("checkCommandMessage");
        for(String m : msg){
            msgManager.sendMessage(player,m.replace("%player%",playerCheck.getName())
                    .replace("%time_left%",timeLeft)
                    .replace("%total_time%",totalTime),false);
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return null;
    }
}
