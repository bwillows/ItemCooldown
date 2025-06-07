package bwillows.itemcooldown.listeners;

import bwillows.itemcooldown.ItemCooldown;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class onPlayerUseItem implements Listener {
    ItemCooldown plugin;

    int golden_apple_cooldown;
    int enchanted_golden_apple_cooldown;
    int enderpearl_cooldown;

    public onPlayerUseItem(ItemCooldown plugin) {
        this.plugin = plugin;

        golden_apple_cooldown = plugin.getConfig().getInt("cooldowns.GOLDEN_APPLE", 10);
        enchanted_golden_apple_cooldown = plugin.getConfig().getInt("cooldowns.ENCHANTED_GOLDEN_APPLE", 60);
        enderpearl_cooldown = plugin.getConfig().getInt("cooldowns.ENDER_PEARL", 10);
    }


    // on player use item
    @EventHandler
    public void onPlayerUseItem(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack itemStack = event.getItem();

        if(itemStack == null)
            return;

        if(itemStack.getType().equals(Material.ENDER_PEARL) && plugin.isOnCooldown(player, ItemCooldown.CooldownType.ENDER_PEARL)) {
            event.setCancelled(true);
            int remaining = plugin.getRemainingCooldown(player, ItemCooldown.CooldownType.ENDER_PEARL);
            String timeFormatted = plugin.utils.formatTime(remaining);
            String message = plugin.getConfig().getString("messages.ender_pearl").replace("{time}", timeFormatted);
            message = ChatColor.translateAlternateColorCodes('&', message);  // Support for color codes
            player.sendMessage(message);
            return;
        }

        if(plugin.utils.isGoldenApple(itemStack) && plugin.isOnCooldown(player, ItemCooldown.CooldownType.GOLDEN_APPLE)) {
            event.setCancelled(true);
            int remaining = plugin.getRemainingCooldown(player, ItemCooldown.CooldownType.GOLDEN_APPLE);
            String timeFormatted = plugin.utils.formatTime(remaining);
            String message = plugin.getConfig().getString("messages.golden_apple").replace("{time}", timeFormatted);
            message = ChatColor.translateAlternateColorCodes('&', message);  // Support for color codes
            player.sendMessage(message);
            return;
        }
        if(plugin.utils.isEnchantedGoldenApple(itemStack) && plugin.isOnCooldown(player, ItemCooldown.CooldownType.ENCHANTED_GOLDEN_APPLE)) {
            event.setCancelled(true);
            int remaining = plugin.getRemainingCooldown(player, ItemCooldown.CooldownType.ENCHANTED_GOLDEN_APPLE);
            String timeFormatted = plugin.utils.formatTime(remaining);
            String message = plugin.getConfig().getString("messages.enchanted_golden_apple").replace("{time}", timeFormatted);
            message = ChatColor.translateAlternateColorCodes('&', message);  // Support for color codes
            player.sendMessage(message);
            return;
        }




    }
}
