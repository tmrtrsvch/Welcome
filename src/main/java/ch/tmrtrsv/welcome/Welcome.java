package ch.tmrtrsv.welcome;

import ch.tmrtrsv.welcome.commands.ReloadCommand;
import ch.tmrtrsv.welcome.tabcompleters.WelcomeTabCompleter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import ch.tmrtrsv.welcome.utils.Utils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;

public class Welcome extends JavaPlugin implements Listener {

    private File dataFile;
    private FileConfiguration dataConfig;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        createDataFile();
        loadDataConfig();
        sendCredit();

        this.getCommand("welcome").setExecutor(new ReloadCommand(this));
        this.getCommand("welcome").setTabCompleter(new WelcomeTabCompleter());

        Bukkit.getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        sendCredit();
    }

    private void sendCredit() {
        getLogger().info(Utils.color(""));
        getLogger().info(Utils.color("&f &#FBF408W&#FBF207e&#FCEF07l&#FCED06c&#FCEA05o&#FDE804m&#FDE504e &#FEE002v&#FEDE012&#FFDB01.&#FFD9000"));
        getLogger().info(Utils.color("&f Автор: &#FB3908Т&#FC2B06и&#FD1D04м&#FE0E02у&#FF0000р"));
        getLogger().info(Utils.color("&f Телеграм: &#008DFF@&#0086FFt&#007FFFm&#0078FFr&#0071FFt&#006BFFr&#0064FFs&#005DFFv&#0056FFc&#004FFFh"));
        getLogger().info(Utils.color(""));
    }

    private void createDataFile() {
        dataFile = new File(getDataFolder(), "data.yml");
        if (!dataFile.exists()) {
            dataFile.getParentFile().mkdirs();
            saveResource("data.yml", false);
        }
    }

    public void loadDataConfig() {
        dataConfig = YamlConfiguration.loadConfiguration(dataFile);
    }

    private void saveDataConfig() {
        try {
            dataConfig.save(dataFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String playerName = player.getName();

        if (!dataConfig.contains("joined-players." + playerName)) {
            handleFirstJoin(player);
            dataConfig.set("joined-players." + playerName, true);
            saveDataConfig();
        } else {
            handleRegularJoin(player);
        }
    }

    private void handleFirstJoin(Player player) {
        ConfigurationSection firstJoin = getConfig().getConfigurationSection("settings.first-join");
        if (firstJoin == null) return;

        sendTitleAndSubtitle(player, firstJoin);
        sendChatMessages(player, firstJoin.getStringList("message"));
        playSound(player, firstJoin.getString("sound"));
        giveItems(player, firstJoin.getConfigurationSection("items"));
        executeCommands(player, firstJoin.getStringList("commands"));
    }

    private void handleRegularJoin(Player player) {
        ConfigurationSection join = getConfig().getConfigurationSection("settings.join");
        if (join == null) return;

        sendTitleAndSubtitle(player, join);
        sendChatMessages(player, join.getStringList("message"));
        playSound(player, join.getString("sound"));
        giveItems(player, join.getConfigurationSection("items"));
        executeCommands(player, join.getStringList("commands"));
    }

    private void sendTitleAndSubtitle(Player player, ConfigurationSection section) {
        String title = Utils.color(section.getString("title").replace("%player%", player.getName()));
        String subtitle = Utils.color(section.getString("subtitle").replace("%player%", player.getName()));
        player.sendTitle(title, subtitle, 10, 70, 20);
    }

    private void sendChatMessages(Player player, List<String> messages) {
        for (String message : messages) {
            player.sendMessage(Utils.color(message.replace("%player%", player.getName())));
        }
    }

    private void playSound(Player player, String soundName) {
        if (soundName != null && !soundName.isEmpty()) {
            try {
                Sound sound = Sound.valueOf(soundName);
                player.playSound(player.getLocation(), sound, 1.0f, 1.0f);
            } catch (IllegalArgumentException e) {
                getLogger().warning("Недопустимый звук: " + soundName);
            }
        }
    }

    private void giveItems(Player player, ConfigurationSection itemsSection) {
        if (itemsSection == null) return;

        Set<String> itemKeys = itemsSection.getKeys(false);
        for (String key : itemKeys) {
            ConfigurationSection item = itemsSection.getConfigurationSection(key);
            if (item != null) {
                String itemName = Utils.color(item.getString("name"));
                List<String> itemLore = item.getStringList("lore");
                int amount = item.getInt("amount");

                giveItem(player, key, amount, itemName, itemLore);
            }
        }
    }

    private void giveItem(Player player, String materialName, int amount, String itemName, List<String> itemLore) {
        Material material = Material.getMaterial(materialName.toUpperCase());
        if (material != null) {
            ItemStack itemStack = new ItemStack(material, amount);
            ItemMeta itemMeta = itemStack.getItemMeta();
            if (itemMeta != null) {
                itemMeta.setDisplayName(Utils.color(itemName));
                itemMeta.setLore(Utils.color(itemLore));
                itemStack.setItemMeta(itemMeta);
            }
            player.getInventory().addItem(itemStack);
        } else {
            getLogger().warning("Недопустимый ID предмета: " + materialName);
        }
    }

    private void executeCommands(Player player, List<String> commands) {
        for (String command : commands) {
            String parsedCommand = command.replace("%player%", player.getName());
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), parsedCommand);
        }
    }
}
