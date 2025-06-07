package bwillows.itemcooldown.listeners;

import bwillows.itemcooldown.ItemCooldown;
import org.bukkit.ChatColor;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;

public class projectileLaunchEvent implements Listener {
    ItemCooldown plugin;
    int enderpearl_cooldown;

    public projectileLaunchEvent(ItemCooldown plugin) {
        this.plugin = plugin;
        enderpearl_cooldown = plugin.getConfig().getInt("cooldowns.ENDER_PEARL", 10);
    }

    @EventHandler
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        if (!(event.getEntity() instanceof EnderPearl)) return;

        Projectile projectile = event.getEntity();
        if (!(projectile.getShooter() instanceof Player)) return;

        Player player = (Player) projectile.getShooter();

        if(player.hasPermission("itemcooldown.bypass-cooldown"))
            return;

        if(!plugin.isOnCooldown(player, ItemCooldown.CooldownType.ENDER_PEARL)) {
            plugin.startCooldown(player, ItemCooldown.CooldownType.ENDER_PEARL, enderpearl_cooldown);
            return;
        } else if (plugin.isOnCooldown(player, ItemCooldown.CooldownType.ENDER_PEARL)) {
            event.setCancelled(true);
            int remaining = plugin.getRemainingCooldown(player, ItemCooldown.CooldownType.ENDER_PEARL);
            String timeFormatted = plugin.utils.formatTime(remaining);
            String message = plugin.getConfig().getString("messages.ender_pearl").replace("{time}", timeFormatted);
            message = ChatColor.translateAlternateColorCodes('&', message);  // Support for color codes
            player.sendMessage(message);
            return;
        }
    }
}
