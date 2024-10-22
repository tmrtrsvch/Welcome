package ch.tmrtrsv.welcome.tabcompleters;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WelcomeTabCompleter implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (command.getName().equalsIgnoreCase("welcome")) {
            if (args.length == 1) {
                List<String> completions = new ArrayList<>();

                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    if (player.hasPermission("welcome.reload")) {
                        completions.add("reload");
                    }
                } else {
                    completions.add("reload");
                }
                return completions;
            }
        }
        return Collections.emptyList();
    }
}