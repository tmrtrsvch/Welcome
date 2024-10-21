package ch.tmrtrsv.welcome;

import ch.tmrtrsv.welcome.commands.ReloadCommand;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import ch.tmrtrsv.welcome.utils.Utils;

import java.util.List;
import java.util.Set;

public class Welcome extends JavaPlugin implements Listener {

    private boolean titleEnabled;
    private String title;
    private boolean subtitleEnabled;
    private String subtitle;
    private boolean chatLinesEnabled;
    private List<String> chatLines;
    private boolean soundEnabled;
    private String soundName;
    private ConfigurationSection items;
    private List<String> joinCommands;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        loadConfigValues();
        sendCredit();

        this.getCommand("welcome").setExecutor(new ReloadCommand(this));

        Bukkit.getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        sendCredit();
    }

    private void sendCredit() {
        getLogger().info(Utils.color(""));
        getLogger().info(Utils.color("&f &#f7f707Welcome v1.1"));
        getLogger().info(Utils.color("&f Автор: &#FB3908Т&#FC2B06и&#FD1D04м&#FE0E02у&#FF0000р"));
        getLogger().info(Utils.color("&f Телеграм: &#008DFF@&#0086FFt&#007FFFm&#0078FFr&#0071FFt&#006BFFr&#0064FFs&#005DFFv&#0056FFc&#004FFFh"));
        getLogger().info(Utils.color(""));
    }

    public void loadConfigValues() {
        titleEnabled = getConfig().getBoolean("title.enabled");
        title = Utils.color(getConfig().getString("title.message"));
        subtitleEnabled = getConfig().getBoolean("subtitle.enabled");
        subtitle = Utils.color(getConfig().getString("subtitle.message"));
        chatLinesEnabled = getConfig().getBoolean("chatLines.enabled");
        chatLines = getConfig().getStringList("chatLines.messages");
        soundEnabled = getConfig().getBoolean("sound.enabled");
        soundName = getConfig().getString("sound.soundName");
        items = getConfig().getConfigurationSection("items");
        joinCommands = getConfig().getStringList("commands");
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (titleEnabled || subtitleEnabled) {
            sendTitle(player, title, subtitle);
        }

        if (chatLinesEnabled) {
            sendChatMessage(player, chatLines);
        }

        if (items != null) {
            giveItems(player);
        }

        if (soundEnabled && soundName != null && !soundName.isEmpty()) {
            try {
                Sound sound = Sound.valueOf(soundName);
                player.playSound(player.getLocation(), sound, 1.0f, 1.0f);
            } catch (IllegalArgumentException e) {
                getLogger().warning("Недопустимый звук: " + soundName);
            }
        }

        executeJoinCommands(player);
    }

    private void sendTitle(Player player, String title, String subtitle) {
        player.sendTitle(title.replace("%player%", player.getName()), subtitle.replace("%player%", player.getName()), 10, 70, 20);
    }

    private void sendChatMessage(Player player, List<String> lines) {
        for (String line : lines) {
            String formattedLine = Utils.color(line.replace("%player%", player.getName()));
            player.sendMessage(formattedLine);
        }
    }

    private void giveItems(Player player) {
        Set<String> itemKeys = items.getKeys(false);

        for (String key : itemKeys) {
            ConfigurationSection itemSection = items.getConfigurationSection(key);
            if (itemSection != null) {
                String itemName = Utils.color(itemSection.getString("name"));
                List<String> itemLore = itemSection.getStringList("lore");
                int amount = itemSection.getInt("amount");

                giveItem(player, key, amount, itemName, itemLore);
            }
        }
    }

    private void giveItem(Player player, String itemId, int amount, String itemName, List<String> itemLore) {
        Material material = Material.getMaterial(itemId.toUpperCase());
        if (material != null) {
            ItemStack item = new ItemStack(material, amount);
            ItemMeta itemMeta = item.getItemMeta();
            if (itemMeta != null) {
                itemMeta.setDisplayName(Utils.color(itemName.replace("%player%", player.getName())));
                itemMeta.setLore(Utils.color(itemLore));
                item.setItemMeta(itemMeta);
            }
            PlayerInventory inventory = player.getInventory();
            inventory.addItem(item);
        } else {
            getLogger().warning("Недопустимый ID предмета: " + itemId);
        }
    }

    private void executeJoinCommands(Player player) {
        for (String command : joinCommands) {
            String parsedCommand = command.replace("%player%", player.getName());
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), parsedCommand);
        }
    }
}