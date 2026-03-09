package ptl.ajneb97.commands.subcommands;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import ptl.ajneb97.api.PlayerTimeLimitAPI;
import ptl.ajneb97.commands.PlayerSubCommand;
import ptl.ajneb97.managers.MessagesManager;
import ptl.ajneb97.managers.TimeResetManager;

import java.util.List;

public class InfoSubCommand extends PlayerSubCommand {

    @Override
    public String getName() {
        return "info";
    }

    @Override
    public boolean hasPermission(CommandSender sender){
        return sender.hasPermission("playertimelimit.commands.info");
    }

    @Override
    protected void executePlayer(Player player, String[] args, MessagesManager msgManager, FileConfiguration messagesConfig) {
        // /ptl info
        if(!hasPermission(player)){
            msgManager.sendMessage(player,messagesConfig.getString("noPermissions"),true);
            return;
        }

        TimeResetManager timeResetManager = PlayerTimeLimitAPI.getPlugin().getTimeResetManager();
        String resetTime = timeResetManager.getNextResetDateFormatted();
        String resetTimeRemaining = timeResetManager.getTimeUntilNextReset();

        List<String> msg = messagesConfig.getStringList("infoCommandMessage");
        for(String m : msg){
            msgManager.sendMessage(player,m.replace("%reset_time%",resetTime)
                    .replace("%remaining%",resetTimeRemaining),false);
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return null;
    }
}
