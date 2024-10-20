package ch.tmrtrsv.welcome.commands;

import ch.tmrtrsv.welcome.Welcome;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;

public class ReloadCommand implements CommandExecutor {

    private final Welcome plugin;

    public ReloadCommand(Welcome plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (player.hasPermission("welcome.reload")) {
                plugin.reloadConfig();
                plugin.loadConfigValues();
                player.sendMessage(ChatColor.GREEN + "Конфигурация плагина успешно перезагружена!");
            } else {
                player.sendMessage(ChatColor.RED + "У вас нет прав для выполнения этой команды.");
            }
        } else {
            plugin.reloadConfig();
            plugin.loadConfigValues();
            sender.sendMessage(ChatColor.GREEN + "Конфигурация плагина успешно перезагружена!");
        }
        return true;
    }
}