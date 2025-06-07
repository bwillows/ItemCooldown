package bwillows.itemcooldown.listeners;

import bwillows.itemcooldown.ItemCooldown;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityResurrectEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class onEntityResurrect implements Listener {
    ItemCooldown plugin;
    int totem_of_undying_cooldown;

    public Map<UUID, Long> lastDeathTime = new HashMap<>();
    public Map<UUID, Long> totemConsumeTime = new HashMap<>();

    public onEntityResurrect(ItemCooldown plugin) {
        this.plugin = plugin;
        totem_of_undying_cooldown = plugin.getConfig().getInt("cooldowns.TOTEM_OF_UNDYING", 180);
    }

    @EventHandler
    public void onEntityResurrect(EntityResurrectEvent event) {
        if(!(event.getEntity() instanceof Player))
            return;

        Player player = (Player) event.getEntity();

        if(player.hasPermission("itemcooldown.bypass-cooldown"))
            return;

        if(plugin.isOnCooldown(player, ItemCooldown.CooldownType.TOTEM)) {
            event.setCancelled(true);
            int remaining = plugin.getRemainingCooldown(player, ItemCooldown.CooldownType.TOTEM);
            String timeFormatted = plugin.utils.formatTime(remaining);
            String message = plugin.getConfig().getString("messages.totem_of_undying").replace("{time}", timeFormatted);
            message = ChatColor.translateAlternateColorCodes('&', message);  // Support for color codes
            player.sendMessage(message);
            return;
        }

        plugin.startTotemCooldown(player, totem_of_undying_cooldown);
    }
}
