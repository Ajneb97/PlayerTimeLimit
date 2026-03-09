package ptl.ajneb97.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import ptl.ajneb97.managers.MessagesManager;

public abstract class PlayerSubCommand implements SubCommand{

    @Override
    public final void execute(CommandSender sender, String[] args, MessagesManager msgManager, FileConfiguration messagesConfig) {
        if (!(sender instanceof Player player)) {
            msgManager.sendMessage(sender,messagesConfig.getString("onlyPlayerCommand"),true);
            return;
        }

        executePlayer(player, args, msgManager, messagesConfig);
    }

    protected abstract void executePlayer(Player player, String[] args, MessagesManager msgManager, FileConfiguration messagesConfig);
}
