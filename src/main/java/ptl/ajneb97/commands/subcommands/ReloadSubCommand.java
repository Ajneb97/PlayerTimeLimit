package ptl.ajneb97.commands.subcommands;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import ptl.ajneb97.PlayerTimeLimit;
import ptl.ajneb97.api.PlayerTimeLimitAPI;
import ptl.ajneb97.commands.SubCommand;
import ptl.ajneb97.managers.MessagesManager;
import ptl.ajneb97.utils.PlayerUtils;

import java.util.List;

public class ReloadSubCommand implements SubCommand {

    @Override
    public String getName() {
        return "reload";
    }

    @Override
    public boolean hasPermission(CommandSender sender){
        return PlayerUtils.isPlayerTimeLimitAdmin(sender);
    }

    @Override
    public void execute(CommandSender sender, String[] args, MessagesManager msgManager, FileConfiguration messagesConfig) {
        // /ptl reload
        if(!hasPermission(sender)){
            msgManager.sendMessage(sender,messagesConfig.getString("noPermissions"),true);
            return;
        }

        if(!PlayerTimeLimitAPI.getPlugin().getConfigsManager().reload()){
            sender.sendMessage(PlayerTimeLimit.prefix+MessagesManager.getLegacyColoredMessage("&cThere was an error reloading the config, check the console."));
            return;
        }
        msgManager.sendMessage(sender,messagesConfig.getString("configReloaded"),true);
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return null;
    }
}
