package org.txtox8729.vshulkers.listeners;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.txtox8729.vshulkers.VShulkers;
import org.txtox8729.vshulkers.utils.ConfigUtil;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;
import java.util.stream.Collectors;

public class ContainerListener implements Listener {

    private static final Set<InventoryType> RESTRICTED_CONTAINERS = EnumSet.of(
            InventoryType.CHEST, InventoryType.BARREL, InventoryType.BREWING,
            InventoryType.DISPENSER, InventoryType.DROPPER, InventoryType.HOPPER,
            InventoryType.FURNACE, InventoryType.BLAST_FURNACE, InventoryType.SMOKER,
            InventoryType.ENDER_CHEST, InventoryType.SHULKER_BOX
    );

    private static final Set<Material> SHULKER_BOXES = Arrays.stream(Material.values())
            .filter(material -> material.name().endsWith("_SHULKER_BOX") || material == Material.SHULKER_BOX)
            .collect(Collectors.toSet());

    private static final long MESSAGE_COOLDOWN = 1000L;

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;

        Inventory topInventory = event.getView().getTopInventory();
        if (!isRestrictedContainer(topInventory.getType())) return;

        if (player.hasPermission("vshulker.bypass")) return;

        boolean isShulkerInvolved = checkForShulker(event, player);

        if (isShulkerInvolved) {
            if (isShulkerInsideContainer(event)) {
                event.setCancelled(true);
                return;
            }

            event.setCancelled(true);
            sendMessageWithCooldown(player, ConfigUtil.noShulkerInContainerMessage);
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;

        Inventory topInventory = event.getView().getTopInventory();
        if (!isRestrictedContainer(topInventory.getType())) return;

        if (player.hasPermission("vshulker.bypass")) return;

        if (event.getOldCursor() != null && isShulkerBox(event.getOldCursor())) {
            if (isShulkerInsideContainer(event)) {
                event.setCancelled(true);
                return;
            }

            event.setCancelled(true);
            sendMessageWithCooldown(player, ConfigUtil.noShulkerInContainerMessage);
        }
    }

    @EventHandler
    public void onInventoryMove(InventoryMoveItemEvent event) {
        if (isShulkerBox(event.getItem()) && isRestrictedContainer(event.getDestination().getType())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getPlayer() instanceof Player player) {
            player.removeMetadata("shulkerMessageCooldown", VShulkers.getPlugin(VShulkers.class));
        }
    }

    private boolean checkForShulker(InventoryClickEvent event, Player player) {
        if (isShulkerBox(event.getCursor()) || isShulkerBox(event.getCurrentItem())) {
            return true;
        }

        if (isShulkerBox(player.getInventory().getItemInOffHand())) {
            return true;
        }

        if (event.getAction() == InventoryAction.HOTBAR_SWAP) {
            int hotbarSlot = event.getHotbarButton();

            if (hotbarSlot >= 0 && hotbarSlot < player.getInventory().getSize()) {
                if (isShulkerBox(player.getInventory().getItem(hotbarSlot))) {
                    event.setCancelled(true);
                    return true;
                }
            }
        }

        if (event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
            if (isShulkerBox(event.getCurrentItem())) {
                return true;
            }
        }

        if (event.getClick() == ClickType.SWAP_OFFHAND) {
            if (isShulkerBox(player.getInventory().getItemInOffHand())) {
                return true;
            }
        }

        if (event.getClick() == ClickType.NUMBER_KEY) {
            int hotbarSlot = event.getHotbarButton();
            if (hotbarSlot >= 0 && hotbarSlot < player.getInventory().getSize()) {
                ItemStack itemInHotbar = player.getInventory().getItem(hotbarSlot);

                if (isShulkerBox(itemInHotbar)) {
                    event.setCancelled(true);
                    return true;
                }
            }
        }

        if (event.getAction() == InventoryAction.SWAP_WITH_CURSOR) {
            if (isShulkerBox(event.getCursor()) || isShulkerBox(event.getCurrentItem())) {
                return true;
            }
        }

        return false;
    }

    private boolean isShulkerBox(ItemStack item) {
        return item != null && SHULKER_BOXES.contains(item.getType());
    }

    private boolean isRestrictedContainer(InventoryType type) {
        return RESTRICTED_CONTAINERS.contains(type);
    }

    private boolean isShulkerInsideContainer(InventoryInteractEvent event) {
        Inventory topInventory = event.getView().getTopInventory();
        for (ItemStack item : topInventory.getContents()) {
            if (isShulkerBox(item)) {
                return true;
            }
        }
        return false;
    }

    private void sendMessageWithCooldown(Player player, String message) {
        long currentTime = System.currentTimeMillis();
        String metaKey = "shulkerMessageCooldown";

        if (player.hasMetadata(metaKey)) {
            long lastSend = player.getMetadata(metaKey).get(0).asLong();
            if (currentTime - lastSend < MESSAGE_COOLDOWN) return;
        }

        player.sendMessage(message);
        if (ConfigUtil.denySoundEnabled) {
            player.playSound(player.getLocation(), ConfigUtil.denySound,
                    ConfigUtil.denySoundVolume, ConfigUtil.denySoundPitch);
        }
        player.setMetadata(metaKey, new FixedMetadataValue(VShulkers.getPlugin(VShulkers.class), currentTime));
    }
}