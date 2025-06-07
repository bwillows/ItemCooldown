package bwillows.itemcooldown;

import bwillows.itemcooldown.listeners.onEntityResurrect;
import bwillows.itemcooldown.listeners.onPlayerConsumeItem;
import bwillows.itemcooldown.listeners.onPlayerUseItem;
import bwillows.itemcooldown.listeners.projectileLaunchEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityResurrectEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ItemCooldown extends JavaPlugin implements Listener {

    ItemCooldown plugin;
    public FileConfiguration config;

    public Utils utils;


    public enum CooldownType {
        ENDER_PEARL,
        GOLDEN_APPLE,
        ENCHANTED_GOLDEN_APPLE,
        TOTEM
    }

    private class Cooldowns {
        Long enderpearl;
        Long goldenApple;
        Long enchantedGoldenApple;
        Long totem;
    }

    private Map<UUID, Cooldowns> cooldownsMap = new HashMap<>();

    @Override
    public void onEnable() {
        this.plugin = this;
        utils = new Utils();
        File pluginFolder = new File(getDataFolder().getParent(), getDescription().getName());
        if (!pluginFolder.exists()) {
            pluginFolder.mkdirs();  // Creates the folder if it doesn't exist
        }

        // Save default config if it doesn't exist
        saveDefaultConfig();

        config = this.getConfig();

        Bukkit.getPluginManager().registerEvents(new onPlayerConsumeItem(plugin), this);
        Bukkit.getPluginManager().registerEvents(new onPlayerUseItem(plugin), this);
        Bukkit.getPluginManager().registerEvents(new projectileLaunchEvent(plugin), this);
        Bukkit.getPluginManager().registerEvents(this, this);

        if(utils.isAtLeastMinecraft_1_11()) {
            Bukkit.getPluginManager().registerEvents(new onEntityResurrect(plugin), this);
        }
    }

    @Override
    public void onDisable() {
        // Clean up when plugin is disabled
    }


    public void startCooldown(Player player, CooldownType type, int seconds) {
        Cooldowns cooldowns = cooldownsMap.computeIfAbsent(player.getUniqueId(), id -> new Cooldowns());

        long expiryTime = System.currentTimeMillis() + (seconds * 1000L);

        // Set the cooldown timestamp
        switch (type) {
            case ENDER_PEARL:
                cooldowns.enderpearl = expiryTime;
                break;
            case GOLDEN_APPLE:
                cooldowns.goldenApple = expiryTime;
                break;
            case ENCHANTED_GOLDEN_APPLE:
                cooldowns.enchantedGoldenApple = expiryTime;
                break;
            case TOTEM:
                cooldowns.totem = expiryTime;
                break;
        }

        // Schedule task to clear the cooldown after the time passes
        new BukkitRunnable() {
            @Override
            public void run() {
                Cooldowns cd = cooldownsMap.get(player.getUniqueId());
                if (cd == null) return;

                switch (type) {
                    case ENDER_PEARL:
                        if (cd.enderpearl != null && System.currentTimeMillis() >= cd.enderpearl) {
                            cd.enderpearl = null;
                        }
                        break;
                    case GOLDEN_APPLE:
                        if (cd.goldenApple != null && System.currentTimeMillis() >= cd.goldenApple) {
                            cd.goldenApple = null;
                        }
                        break;
                    case ENCHANTED_GOLDEN_APPLE:
                        if (cd.enchantedGoldenApple != null && System.currentTimeMillis() >= cd.enchantedGoldenApple) {
                            cd.enchantedGoldenApple = null;
                        }
                        break;
                    case TOTEM:
                        if (cd.totem != null && System.currentTimeMillis() >= cd.totem) {
                            cd.totem = null;
                        }
                        break;
                }
            }
        }.runTaskLater(plugin, seconds * 20L); // 20 ticks = 1 second
    }

    public boolean isOnCooldown(Player player, CooldownType type) {
        Cooldowns cd = cooldownsMap.get(player.getUniqueId());
        if (cd == null) return false;

        long now = System.currentTimeMillis();

        switch (type) {
            case ENDER_PEARL:
                return cd.enderpearl != null && now < cd.enderpearl;
            case GOLDEN_APPLE:
                return cd.goldenApple != null && now < cd.goldenApple;
            case ENCHANTED_GOLDEN_APPLE:
                return cd.enchantedGoldenApple != null && now < cd.enchantedGoldenApple;
            case TOTEM:
                return cd.totem != null && now < cd.totem;
            default:
                return false;
        }
    }

    public int getRemainingCooldown(Player player, CooldownType type) {
        Cooldowns cd = cooldownsMap.get(player.getUniqueId());
        if (cd == null) return 0;

        long now = System.currentTimeMillis();
        long expiresAt = 0;

        switch (type) {
            case ENDER_PEARL: expiresAt = cd.enderpearl != null ? cd.enderpearl : 0; break;
            case GOLDEN_APPLE: expiresAt = cd.goldenApple != null ? cd.goldenApple : 0; break;
            case ENCHANTED_GOLDEN_APPLE: expiresAt = cd.enchantedGoldenApple != null ? cd.enchantedGoldenApple : 0; break;
            case TOTEM: expiresAt = cd.totem != null ? cd.totem : 0; break;
        }

        return (int) Math.max(0, (expiresAt - now) / 1000);
    }

    private final Map<UUID, BukkitTask> totemCooldownTasks = new HashMap<>();

    public void startTotemCooldown(Player player, int seconds) {
        UUID uuid = player.getUniqueId();

        // Set cooldown
        cooldownsMap.computeIfAbsent(uuid, id -> new Cooldowns()).totem = System.currentTimeMillis() + (seconds * 1000L);

        // Cancel existing task if any
        if (totemCooldownTasks.containsKey(uuid)) {
            totemCooldownTasks.get(uuid).cancel();
        }

        // Schedule a new task
        BukkitTask task = new BukkitRunnable() {
            @Override
            public void run() {
                Cooldowns cd = cooldownsMap.get(uuid);
                if (cd != null) cd.totem = null;
            }
        }.runTaskLater(plugin, seconds * 20L);

        totemCooldownTasks.put(uuid, task);
    }

    public void resetTotemCooldown(Player player) {
        UUID uuid = player.getUniqueId();

        // Clear timestamp
        Cooldowns cd = cooldownsMap.get(uuid);
        if (cd != null) cd.totem = null;

        // Cancel task
        if (totemCooldownTasks.containsKey(uuid)) {
            totemCooldownTasks.get(uuid).cancel();
            totemCooldownTasks.remove(uuid);
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        resetTotemCooldown(player);
    }
}