package ch.tmrtrsv.welcome;

import ch.tmrtrsv.welcome.commands.ReloadCommand;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
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

public class Welcome extends JavaPlugin implements Listener {

    private boolean titleEnabled;
    private String title;
    private boolean subtitleEnabled;
    private String subtitle;
    private boolean chatLinesEnabled;
    private List<String> chatLines;
    private boolean soundEnabled;
    private String soundName;
    private boolean itemEnabled;
    private String itemId;
    private int itemAmount;
    private String itemName;
    private List<String> itemLore;

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
        getLogger().info(Utils.color("&f &#f7f707Welcome v1.0"));
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
        itemEnabled = getConfig().getBoolean("item.enabled");
        itemId = getConfig().getString("item.id");
        itemAmount = getConfig().getInt("item.amount");
        itemName = Utils.color(getConfig().getString("item.name"));
        itemLore = getConfig().getStringList("item.lore");
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

        if (itemEnabled) {
            giveItem(player, itemId, itemAmount, itemName, itemLore);
        }

        if (soundEnabled && soundName != null && !soundName.isEmpty()) {
            try {
                Sound sound = Sound.valueOf(soundName);
                player.playSound(player.getLocation(), sound, 1.0f, 1.0f);
            } catch (IllegalArgumentException e) {
                getLogger().warning("Недопустимый звук: " + soundName);
            }
        }
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

    private void giveItem(Player player, String itemId, int amount, String itemName, List<String> itemLore) {
        if (itemId != null && !itemId.isEmpty()) {
            try {
                Material material = Material.getMaterial(itemId);
                if (material != null) {
                    ItemStack item = new ItemStack(material, amount);
                    ItemMeta itemMeta = item.getItemMeta();
                    itemMeta.setDisplayName(Utils.color(itemName.replace("%player%", player.getName())));
                    itemMeta.setLore(Utils.color(itemLore));
                    item.setItemMeta(itemMeta);
                    PlayerInventory inventory = player.getInventory();
                    inventory.addItem(item);
                } else {
                    getLogger().warning("Недопустимый ID предмета: " + itemId);
                }
            } catch (NumberFormatException e) {
                getLogger().warning("Недопустимый ID предмета: " + itemId);
            }
        }
    }
}