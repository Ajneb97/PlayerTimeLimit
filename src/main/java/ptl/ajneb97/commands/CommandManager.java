package ptl.ajneb97.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import ptl.ajneb97.PlayerTimeLimit;
import ptl.ajneb97.commands.subcommands.*;
import ptl.ajneb97.managers.MessagesManager;
import ptl.ajneb97.utils.PlayerUtils;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandManager implements CommandExecutor, TabCompleter {

    private PlayerTimeLimit plugin;
    private Map<String, SubCommand> subCommands;
    public CommandManager(PlayerTimeLimit plugin) {
        this.plugin = plugin;
        this.subCommands = new HashMap<>();

        subCommands.put("reload", new ReloadSubCommand());
        subCommands.put("addtime", new AddTimeSubCommand());
        subCommands.put("taketime", new TakeTimeSubCommand());
        subCommands.put("resettime", new ResetTimeSubCommand());
        subCommands.put("message", new MessageSubCommand());
        subCommands.put("info", new InfoSubCommand());
        subCommands.put("check", new CheckSubCommand());
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if(args.length == 0) {
            help(sender);
            return true;
        }

        SubCommand subCommand = subCommands.get(args[0].toLowerCase());
        if(subCommand == null) {
            help(sender);
            return true;
        }

        subCommand.execute(sender, args, plugin.getMessagesManager(), plugin.getMessagesConfig());
        return true;
    }

    private void help(CommandSender sender){
        if(PlayerUtils.isPlayerTimeLimitAdmin(sender)) {
            sender.sendMessage(MessagesManager.getLegacyColoredMessage("&c&m                                                                    "));
            sender.sendMessage(MessagesManager.getLegacyColoredMessage("      &b&lPlayerTime&c&lLimit &eCommands"));
            sender.sendMessage(MessagesManager.getLegacyColoredMessage(" "));
            sender.sendMessage(MessagesManager.getLegacyColoredMessage("&8- &c/ptl message &7Enables or disables the time limit information message for yourself."));
            sender.sendMessage(MessagesManager.getLegacyColoredMessage("&8- &c/ptl info &7Checks the remaining time for play times reset."));
            sender.sendMessage(MessagesManager.getLegacyColoredMessage("&8- &c/ptl check (optional)<player> &7Checks player time left and total time."));
            sender.sendMessage(MessagesManager.getLegacyColoredMessage("&8- &c/ptl resettime <player>/* &7Resets play time for a player or all players."));
            sender.sendMessage(MessagesManager.getLegacyColoredMessage("&8- &c/ptl addtime <player> <time> &7Adds play time to a player."));
            sender.sendMessage(MessagesManager.getLegacyColoredMessage("&8- &c/ptl taketime <player> <time> &7Takes play time from a player."));
            sender.sendMessage(MessagesManager.getLegacyColoredMessage("&8- &c/ptl reload &7Reloads the config."));
            sender.sendMessage(MessagesManager.getLegacyColoredMessage(" "));
            sender.sendMessage(MessagesManager.getLegacyColoredMessage("&c&m                                                                    "));
        }
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if(args.length == 1) {
            return subCommands.values().stream()
                    .distinct()
                    .filter(sub -> sub.hasPermission(sender))
                    .map(SubCommand::getName)
                    .filter(name -> name.toLowerCase().startsWith(args[0].toLowerCase()))
                    .toList();
        }

        SubCommand subCommand = subCommands.get(args[0].toLowerCase());
        if(subCommand != null) {
            return subCommand.tabComplete(sender, args);
        }

        return null;
    }
}
