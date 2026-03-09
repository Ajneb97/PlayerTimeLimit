package ptl.ajneb97.commands.subcommands;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import ptl.ajneb97.api.PlayerTimeLimitAPI;
import ptl.ajneb97.commands.PlayerSubCommand;
import ptl.ajneb97.managers.MessagesManager;
import java.util.List;

public class MessageSubCommand extends PlayerSubCommand {

    @Override
    public String getName() {
        return "message";
    }

    @Override
    public boolean hasPermission(CommandSender sender){
        return sender.hasPermission("playertimelimit.commands.message");
    }

    @Override
    protected void executePlayer(Player player, String[] args, MessagesManager msgManager, FileConfiguration messagesConfig) {
        // /ptl message
        if(!hasPermission(player)){
            msgManager.sendMessage(player,messagesConfig.getString("noPermissions"),true);
            return;
        }

        String result = PlayerTimeLimitAPI.getPlugin().getPlayerDataManager().setMessageEnabled(player);
        if(result != null){
            msgManager.sendMessage(player,messagesConfig.getString(result),true);
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return null;
    }
}
