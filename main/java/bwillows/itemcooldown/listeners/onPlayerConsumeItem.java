package bwillows.itemcooldown.listeners;

import bwillows.itemcooldown.ItemCooldown;
import bwillows.itemcooldown.Utils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;

public class onPlayerConsumeItem implements Listener {
    ItemCooldown plugin;

    int golden_apple_cooldown;
    int enchanted_golden_apple_cooldown;
    public onPlayerConsumeItem(ItemCooldown plugin) {
        this.plugin = plugin;
        golden_apple_cooldown = plugin.getConfig().getInt("cooldowns.GOLDEN_APPLE", 10);
        enchanted_golden_apple_cooldown = plugin.getConfig().getInt("cooldowns.ENCHANTED_GOLDEN_APPLE", 60);
    }

    @EventHandler
    public void onPlayerConsumeItem(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        ItemStack itemStack = event.getItem();

        if(itemStack == null)
            return;

        if(player.hasPermission("itemcooldown.bypass-cooldown"))
            return;

        if(plugin.utils.isGoldenApple(itemStack)) {
            if(!plugin.isOnCooldown(player, ItemCooldown.CooldownType.GOLDEN_APPLE)) {
                plugin.startCooldown(player, ItemCooldown.CooldownType.GOLDEN_APPLE,  golden_apple_cooldown);
                return;
            } else {
                event.setCancelled(true);
                int remaining = plugin.getRemainingCooldown(player, ItemCooldown.CooldownType.GOLDEN_APPLE);
                String timeFormatted = plugin.utils.formatTime(remaining);
                String message = plugin.getConfig().getString("messages.golden_apple").replace("{time}", timeFormatted);
                message = ChatColor.translateAlternateColorCodes('&', message);  // Support for color codes
                player.sendMessage(message);
                return;
            }
        } else if (plugin.utils.isEnchantedGoldenApple(itemStack)) {
            if(!plugin.isOnCooldown(player, ItemCooldown.CooldownType.ENCHANTED_GOLDEN_APPLE)) {
                plugin.startCooldown(player, ItemCooldown.CooldownType.ENCHANTED_GOLDEN_APPLE,  enchanted_golden_apple_cooldown);
                return;
            } else {
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
}
