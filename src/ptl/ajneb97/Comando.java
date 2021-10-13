package ptl.ajneb97;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import ptl.ajneb97.managers.MensajesManager;
import ptl.ajneb97.managers.PlayerManager;
import ptl.ajneb97.model.TimeLimitPlayer;
import ptl.ajneb97.utils.UtilsTime;

public class Comando implements CommandExecutor {

    private final PlayerTimeLimit plugin;
    private final FileConfiguration messages;

    public Comando(PlayerTimeLimit plugin) {
        this.plugin = plugin;
        this.messages = plugin.getMessages();
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        MensajesManager msgManager = plugin.getMensajesManager();
        if (!(sender instanceof Player)) {
            if (args.length >= 1) {
                if (args[0].equalsIgnoreCase("reload")) {
                    reload(sender, msgManager);
                }
            }

            return false;
        }
        Player jugador = (Player) sender;

        if (!jugador.isOp() || !jugador.hasPermission("playertimelimit.admin")) {
            msgManager.enviarMensaje(sender, messages.getString("noPermissions"), true);
            return true;
        }

        if (!(args.length >= 1)) {
            if (!jugador.isOp() || !jugador.hasPermission("playertimelimit.admin")) {
                msgManager.enviarMensaje(sender, messages.getString("noPermissions"), true);
                return true;
            }
            help(sender);
            return true;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            reload(sender, msgManager);
            return true;

        } else if (args[0].equalsIgnoreCase("message")) {
            if (!jugador.hasPermission("playertimelimit.command.message")) {
                msgManager.enviarMensaje(sender, messages.getString("noPermissions"), true);
                return true;
            }

            message(jugador, msgManager);
            return true;

        } else if (args[0].equalsIgnoreCase("info")) {
            if (!jugador.hasPermission("playertimelimit.command.info")) {
                msgManager.enviarMensaje(sender, messages.getString("noPermissions"), true);
                return true;
            }

            info(jugador);
            return true;

        } else if (args[0].equalsIgnoreCase("check")) {
            if (!jugador.hasPermission("playertimelimit.command.check")) {
                msgManager.enviarMensaje(sender, messages.getString("noPermissions"), true);
                return true;
            }

            check(args, jugador, msgManager);
            return true;

        } else {
            help(sender);
        }
        return true;

    }

    public void help(CommandSender sender) {

        sender.sendMessage(
                MensajesManager.getMensajeColor("&c&m                                                                    "),
                MensajesManager.getMensajeColor("      &b&lPlayerTime&c&lLimit &eCommands"),
                MensajesManager.getMensajeColor(" "),
                MensajesManager.getMensajeColor("&8- &c/ptl message &7Enables or disables the time limit information message for yourself."),
                MensajesManager.getMensajeColor("&8- &c/ptl info &7Checks the remaining time for playtimes reset."),
                MensajesManager.getMensajeColor("&8- &c/ptl check <player> &7Checks player time left and total time."),
                MensajesManager.getMensajeColor("&8- &c/ptl reload &7Reloads the config."),
                MensajesManager.getMensajeColor(" "),
                MensajesManager.getMensajeColor("&c&m                                                                    ")
        );
    }

    public void reload(CommandSender sender,MensajesManager msgManager) {
        plugin.recargarConfigs();
        msgManager.enviarMensaje(sender, messages.getString("commandReload"), true);
    }

    public void message(Player player, MensajesManager msgManager) {
        // /ptl message
        TimeLimitPlayer p = plugin.getPlayerManager().getPlayerByUUID(player.getUniqueId().toString());
        boolean messagesEnabled = p.isMessageEnabled();
        if (messagesEnabled) {
            msgManager.enviarMensaje(player, messages.getString("messageDisabled"), true);
        } else {
            msgManager.enviarMensaje(player, messages.getString("messageEnabled"), true);
        }
        p.setMessageEnabled(!messagesEnabled);
    }

    public void info(Player player) {
        // /ptl info
        String timeReset = plugin.getConfigsManager().getMainConfigManager().getResetTime();
        String remaining = plugin.getServerManager().getRemainingTimeForTimeReset();

        List<String> msg = messages.getStringList("infoCommandMessage");
        for (String m : msg) {
            player.sendMessage(MensajesManager.getMensajeColor(m.replace("%reset_time%", timeReset)
                    .replace("%remaining%", remaining)));
        }
    }

    public void check(String[] args, Player player, MensajesManager msgManager) {
        // /ptl check <player>
        TimeLimitPlayer p;
        if (args.length == 1) {
            p = plugin.getPlayerManager().getPlayerByUUID(player.getUniqueId().toString());
        } else {
            if (player.isOp() || player.hasPermission("playertimelimit.admin")
                    || player.hasPermission("playertimelimit.command.check.others")) {
                p = plugin.getPlayerManager().getPlayerByName(args[1]);
            } else {
                msgManager.enviarMensaje(player, messages.getString("noPermissions"), true);
                return;
            }
        }

        if (p == null) {
            msgManager.enviarMensaje(player, messages.getString("playerDoesNotExists"), true);
            return;
        }

        PlayerManager playerManager = plugin.getPlayerManager();
        int timeLimit = playerManager.getTimeLimitPlayer(player);
        String timeLeft = playerManager.getTimeLeft(p, timeLimit);
        String totalTime = UtilsTime.getTime(p.getTotalTime(), msgManager);
        List<String> msg = messages.getStringList("checkCommandMessage");
        for (String m : msg) {
            player.sendMessage(MensajesManager.getMensajeColor(m.replace("%player%", p.getName())
                    .replace("%time_left%", timeLeft).replace("%total_time%", totalTime)));
        }
    }
}
