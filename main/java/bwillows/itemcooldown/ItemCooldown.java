package bwillows.itemcooldown;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ItemCooldown extends JavaPlugin implements Listener {

    private Map<Player, Long> enderPearlCooldowns = new HashMap<>();
    private Map<Player, Long> goldenAppleCooldowns = new HashMap<>();
    private Map<Player, Long> enchantedGoldenAppleCooldowns = new HashMap<>();
    public FileConfiguration config;

    @Override
    public void onEnable() {
        File pluginFolder = new File(getDataFolder().getParent(), getDescription().getName());
        if (!pluginFolder.exists()) {
            pluginFolder.mkdirs();  // Creates the folder if it doesn't exist
        }

        // Save default config if it doesn't exist
        saveDefaultConfig();

        config = this.getConfig();

        Bukkit.getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        // Clean up when plugin is disabled
    }

    @EventHandler
    public void onPlayerUseItem(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        // Check if the player has bypass permission
        if (player.hasPermission("itemcooldown.bypass-cooldown")) {
            return; // Skip cooldown check if the player has permission
        }

        // Check for ender pearl
        if (event.getItem() != null && event.getItem().getType() == Material.ENDER_PEARL) {
            long cooldownTime = config.getInt("cooldowns.ender_pearl") * 1000;
            if (isCooldownActive(player, enderPearlCooldowns, cooldownTime)) {
                long remainingTime = (cooldownTime - (System.currentTimeMillis() - enderPearlCooldowns.get(player))) / 1000;
                String message = config.getString("messages.ender_pearl").replace("{time}", String.valueOf(remainingTime));
                message = ChatColor.translateAlternateColorCodes('&', message);  // Support for color codes
                event.setCancelled(true);
                player.sendMessage(message);
            } else {
                startCooldown(player, enderPearlCooldowns, cooldownTime);
            }
        }

        // Check for golden apple
        if (event.getItem() != null && event.getItem().getType() == Material.GOLDEN_APPLE) {
            long cooldownTime = config.getInt("cooldowns.golden_apple") * 1000;
            if (isCooldownActive(player, goldenAppleCooldowns, cooldownTime)) {
                long remainingTime = (cooldownTime - (System.currentTimeMillis() - goldenAppleCooldowns.get(player))) / 1000;
                String message = config.getString("messages.golden_apple").replace("{time}", String.valueOf(remainingTime));
                message = ChatColor.translateAlternateColorCodes('&', message);  // Support for color codes
                event.setCancelled(true);
                player.sendMessage(message);
            } else {
                startCooldown(player, goldenAppleCooldowns, cooldownTime);
            }
        }

        // Check for enchanted golden apple
        if (event.getItem() != null && event.getItem().getType() == Material.GOLDEN_APPLE && event.getItem().getDurability() == 1) { // Enchanted Golden Apple
            long cooldownTime = config.getInt("cooldowns.enchanted_golden_apple") * 1000;
            if (isCooldownActive(player, enchantedGoldenAppleCooldowns, cooldownTime)) {
                long remainingTime = (cooldownTime - (System.currentTimeMillis() - enchantedGoldenAppleCooldowns.get(player))) / 1000;
                String message = config.getString("messages.enchanted_golden_apple").replace("{time}", String.valueOf(remainingTime));
                message = ChatColor.translateAlternateColorCodes('&', message);  // Support for color codes
                event.setCancelled(true);
                player.sendMessage(message);
            } else {
                startCooldown(player, enchantedGoldenAppleCooldowns, cooldownTime);
            }
        }
    }

    @EventHandler
    public void onPlayerConsumeItem(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();

        // Check if the player has bypass permission
        if (player.hasPermission("itemcooldown.bypass-cooldown")) {
            return; // Skip cooldown check if the player has permission
        }

        // Handle cooldown for golden apple when consumed
        if (event.getItem().getType() == Material.GOLDEN_APPLE && event.getItem().getDurability() == 0) {
            long cooldownTime = config.getInt("cooldowns.golden_apple") * 1000;
            if (isCooldownActive(player, goldenAppleCooldowns, cooldownTime)) {
                long remainingTime = (cooldownTime - (System.currentTimeMillis() - goldenAppleCooldowns.get(player))) / 1000;
                String message = config.getString("messages.golden_apple").replace("{time}", String.valueOf(remainingTime));
                message = ChatColor.translateAlternateColorCodes('&', message);  // Support for color codes
                event.setCancelled(true);
                player.sendMessage(message);
            } else {
                startCooldown(player, goldenAppleCooldowns, cooldownTime);
            }
        }

        // Handle cooldown for enchanted golden apple when consumed
        if (event.getItem().getType() == Material.GOLDEN_APPLE && event.getItem().getDurability() == 1) { // Enchanted Golden Apple
            long cooldownTime = config.getInt("cooldowns.enchanted_golden_apple") * 1000;
            if (isCooldownActive(player, enchantedGoldenAppleCooldowns, cooldownTime)) {
                long remainingTime = (cooldownTime - (System.currentTimeMillis() - enchantedGoldenAppleCooldowns.get(player))) / 1000;
                String message = config.getString("messages.enchanted_golden_apple").replace("{time}", String.valueOf(remainingTime));
                message = ChatColor.translateAlternateColorCodes('&', message);  // Support for color codes
                event.setCancelled(true);
                player.sendMessage(message);
            } else {
                startCooldown(player, enchantedGoldenAppleCooldowns, cooldownTime);
            }
        }
    }

    private boolean isCooldownActive(Player player, Map<Player, Long> cooldownMap, long cooldownTime) {
        if (cooldownMap.containsKey(player)) {
            long lastUsed = cooldownMap.get(player);
            if (System.currentTimeMillis() - lastUsed < cooldownTime) {
                return true; // Cooldown is still active
            }
        }
        return false; // No cooldown or cooldown has expired
    }

    private void startCooldown(Player player, Map<Player, Long> cooldownMap, long cooldownTime) {
        cooldownMap.put(player, System.currentTimeMillis());
        // Start a delayed task to clear the cooldown after the duration
        new BukkitRunnable() {
            @Override
            public void run() {
                cooldownMap.remove(player);
            }
        }.runTaskLater(this, cooldownTime / 50); // Convert milliseconds to ticks
    }
}