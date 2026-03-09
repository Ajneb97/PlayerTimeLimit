package ptl.ajneb97.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import ptl.ajneb97.managers.MessagesManager;

import java.util.List;

public interface SubCommand {
    String getName();
    void execute(CommandSender sender, String[] args, MessagesManager msgManager, FileConfiguration messagesConfig);
    List<String> tabComplete(CommandSender sender, String[] args);
    boolean hasPermission(CommandSender sender);
}
