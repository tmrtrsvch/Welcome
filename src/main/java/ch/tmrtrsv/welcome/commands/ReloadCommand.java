package ch.tmrtrsv.welcome.commands;

import ch.tmrtrsv.welcome.Welcome;
import ch.tmrtrsv.welcome.utils.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ReloadCommand implements CommandExecutor {

    private final Welcome plugin;

    public ReloadCommand(Welcome plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (args.length == 0) {
            sender.sendMessage(Utils.color(plugin.getConfig().getString("messages.reload-usage")));
            return true;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (player.hasPermission("welcome.reload")) {
                    plugin.reloadConfig();
                    plugin.loadDataConfig();
                    player.sendMessage(Utils.color(plugin.getConfig().getString("messages.config-reloaded")));
                } else {
                    player.sendMessage(Utils.color(plugin.getConfig().getString("messages.no-perm")));
                }
            } else {
                plugin.reloadConfig();
                plugin.loadDataConfig();
                sender.sendMessage(Utils.color(plugin.getConfig().getString("messages.config-reloaded")));
            }
        }
        return true;
    }
}