package org.txtox8729.vshulkers.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAttemptPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.txtox8729.vshulkers.utils.ConfigUtil;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class LimitListener implements Listener {

    private final Map<UUID, Long> lastMessageTimeMap = new HashMap<>();
    private final JavaPlugin plugin;

    public LimitListener(JavaPlugin plugin) {
        this.plugin = plugin;
        startShulkerCheckTask();
    }

    private void startShulkerCheckTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                Bukkit.getOnlinePlayers().stream()
                        .filter(player -> !player.hasPermission("vshulker.limit"))
                        .forEach(player -> dropExcessShulkers(player));
            }
        }.runTaskTimer(plugin, 20L, 20L);
    }


    private void dropExcessShulkers(Player player) {
        int shulkerCount = 0, droppedCount = 0;
        ItemStack[] inventoryContents = player.getInventory().getContents();

        for (int i = 0; i < inventoryContents.length; i++) {
            ItemStack item = inventoryContents[i];
            if (isShulkerBox(item)) {
                if (++shulkerCount > ConfigUtil.shulkerLimit) {
                    player.getWorld().dropItemNaturally(player.getLocation(), item.clone());
                    inventoryContents[i] = null;
                    droppedCount++;
                }
            }
        }

        player.getInventory().setContents(inventoryContents);

        if (droppedCount > 0) {
            sendMessageWithCooldown(player, ConfigUtil.limitShulkerDroppedMessage
                    .replace("%limit%", String.valueOf(ConfigUtil.shulkerLimit))
                    .replace("%dropped%", String.valueOf(droppedCount)), true);
        }
    }

    private boolean isShulkerBox(ItemStack item) {
        return item != null && item.getType().name().contains("SHULKER_BOX");
    }

    private void sendMessageWithCooldown(Player player, String message, boolean forceMessage) {
        long currentTime = System.currentTimeMillis();
        if (forceMessage || currentTime - lastMessageTimeMap.getOrDefault(player.getUniqueId(), 0L) >= 1000L) {
            lastMessageTimeMap.put(player.getUniqueId(), currentTime);
            player.sendMessage(message);
        }
    }

    @EventHandler
    public void onPlayerAttemptPickupItem(PlayerAttemptPickupItemEvent event) {
        Player player = event.getPlayer();
        if (!player.hasPermission("vshulker.limit") && isShulkerBox(event.getItem().getItemStack())) {
            if (countShulkerBoxes(player.getInventory().getContents()) >= ConfigUtil.shulkerLimit) {
                event.setCancelled(true);
                sendMessageWithCooldown(player, ConfigUtil.limitShulkerReachedMessage
                        .replace("%limit%", String.valueOf(ConfigUtil.shulkerLimit)), false);
            }
        }
    }

    private int countShulkerBoxes(ItemStack[] contents) {
        return (int) Arrays.stream(contents)
                .filter(this::isShulkerBox)
                .count();
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        lastMessageTimeMap.remove(event.getPlayer().getUniqueId());
    }
}