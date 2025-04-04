package org.txtox8729.vshulker.listeners;

import com.earth2me.essentials.Essentials;
import org.bukkit.*;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.*;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.txtox8729.vshulker.VShulker;
import org.txtox8729.vshulker.utils.ConfigUtil;

import java.util.*;

public class SHListeners implements Listener {

    private final Map<UUID, Inventory> openShulkers = new HashMap<>();
    private final Map<UUID, ShulkerInfo> shulkerInfo = new HashMap<>();
    private static final NamespacedKey SHULKER_OPEN_KEY = new NamespacedKey(VShulker.getInstance(), "shulker-open");

    private static class ShulkerInfo {
        private final ItemStack item;
        private final int slot;

        public ShulkerInfo(ItemStack item, int slot) {
            this.item = item;
            this.slot = slot;
        }

        public ItemStack getItem() {
            return item;
        }

        public int getSlot() {
            return slot;
        }
    }

    private boolean isVanished(Player player) {
        Essentials essentials = VShulker.getInstance().getEssentials();
        return essentials != null && essentials.getUser(player.getUniqueId()).isVanished();
    }

    private boolean isGodMode(Player player) {
        Essentials essentials = VShulker.getInstance().getEssentials();
        return essentials != null && essentials.getUser(player.getUniqueId()).isGodModeEnabled();
    }

    @EventHandler
    public void onPickupAttempt(PlayerAttemptPickupItemEvent e) {
        Player p = e.getPlayer();
        ItemStack item = e.getItem().getItemStack();

        if (ConfigUtil.disableAutoPickupInVanish && isVanished(p)) {
            e.setCancelled(true);
            return;
        }
        if (ConfigUtil.disableAutoPickupInGodMode && isGodMode(p)) {
            e.setCancelled(true);
            return;
        }

        if (p.getInventory().firstEmpty() != -1) return;
        if (isShulkerBox(item)) return;

        if (tryAddToShulker(p, item)) {
            e.setCancelled(true);
            e.getItem().remove();
            playPickupSound(p);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player p)) return;

        if (e.getClick().isRightClick()
                && e.getClickedInventory() != null
                && e.getClickedInventory().getType() == InventoryType.PLAYER
                && isShulkerBox(e.getCurrentItem())) {

            if (ConfigUtil.disableShulkerOpenInVanish && isVanished(p)) {
                p.sendMessage(ConfigUtil.vanishShulkerOpenDeniedMessage);
                e.setCancelled(true);
                return;
            }
            if (ConfigUtil.disableShulkerOpenInGodMode && isGodMode(p)) {
                p.sendMessage(ConfigUtil.godShulkerOpenDeniedMessage);
                e.setCancelled(true);
                return;
            }

            if (p.getOpenInventory().getType() != InventoryType.CRAFTING) {
                e.setCancelled(true);
                return;
            }

            openShulker(p, e.getCurrentItem(), e.getSlot());
            e.setCancelled(true);
        }
    }

    private boolean tryAddToShulker(Player p, ItemStack item) {
        if (!ConfigUtil.allowedItems.contains(item.getType().toString())) {
            return false;
        }

        for (ItemStack shulker : p.getInventory()) {
            if (!isShulkerBox(shulker)) continue;

            BlockStateMeta meta = (BlockStateMeta) shulker.getItemMeta();
            ShulkerBox box = (ShulkerBox) meta.getBlockState();

            if (box.getInventory().firstEmpty() == -1) continue;

            if (box.getInventory().addItem(item).isEmpty()) {
                meta.setBlockState(box);
                shulker.setItemMeta(meta);
                p.updateInventory();
                playPickupSound(p);
                return true;
            }
        }
        return false;
    }

    private void playPickupSound(Player p) {
        if (ConfigUtil.pickupSoundEnabled && ConfigUtil.pickupSound != null) {
            p.playSound(
                    p.getLocation(),
                    ConfigUtil.pickupSound,
                    SoundCategory.PLAYERS,
                    ConfigUtil.pickupSoundVolume,
                    ConfigUtil.pickupSoundPitch
            );
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        ItemStack item = p.getInventory().getItemInMainHand();

        if (!isShulkerBox(item)) return;

        boolean isCreative = p.getGameMode() == GameMode.CREATIVE;
        boolean shulkerOpenEnabled = ConfigUtil.shulkerOpenEnabled;

        boolean shouldOpen = false;

        if (isCreative) {
            shouldOpen = e.getAction() == Action.RIGHT_CLICK_AIR
                    && !p.isSneaking();
        } else {
            shouldOpen = e.getAction() == Action.RIGHT_CLICK_AIR
                    && (shulkerOpenEnabled ? !p.isSneaking() : p.isSneaking());
        }

        if (shouldOpen) {
            if (ConfigUtil.disableShulkerOpenInVanish && isVanished(p)) {
                p.sendMessage(ConfigUtil.vanishShulkerOpenDeniedMessage);
                e.setCancelled(true);
                return;
            }
            if (ConfigUtil.disableShulkerOpenInGodMode && isGodMode(p)) {
                p.sendMessage(ConfigUtil.godShulkerOpenDeniedMessage);
                e.setCancelled(true);
                return;
            }

            if (!isCreative && p.getOpenInventory().getType() != InventoryType.CRAFTING) {
                e.setCancelled(true);
                return;
            }

            openShulker(p, item, p.getInventory().getHeldItemSlot());
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        Player p = (Player) e.getPlayer();
        UUID uuid = p.getUniqueId();

        Inventory inv = openShulkers.remove(uuid);
        ShulkerInfo info = shulkerInfo.remove(uuid);
        p.removeMetadata(SHULKER_OPEN_KEY.getKey(), VShulker.getInstance());

        if (inv != null && info != null) {
            ItemStack shulkerItem = info.getItem();
            if (shulkerItem == null || !isShulkerBox(shulkerItem)) return;

            int slot = info.getSlot();
            updateShulkerContents(inv, shulkerItem);

            if (slot >= 0 && slot < p.getInventory().getSize()) {
                p.getInventory().setItem(slot, shulkerItem);
            }
            p.updateInventory();
        }
    }

    private void openShulker(Player p, ItemStack item, int slot) {
        if (item == null || !isShulkerBox(item) || isShulkerOpen(p)) {
            return;
        }

        BlockStateMeta meta = (BlockStateMeta) item.getItemMeta();
        if (meta == null || !(meta.getBlockState() instanceof ShulkerBox)) return;

        ShulkerBox box = (ShulkerBox) meta.getBlockState();
        UUID uuid = p.getUniqueId();

        openShulkers.put(uuid, box.getInventory());
        shulkerInfo.put(uuid, new ShulkerInfo(item, slot));
        p.setMetadata(SHULKER_OPEN_KEY.getKey(), new FixedMetadataValue(VShulker.getInstance(), true));
        p.openInventory(box.getInventory());
    }

    private void updateShulkerContents(Inventory inv, ItemStack shulkerItem) {
        if (shulkerItem == null || !isShulkerBox(shulkerItem)) return;

        BlockStateMeta meta = (BlockStateMeta) shulkerItem.getItemMeta();
        if (meta == null) return;

        ShulkerBox box = (ShulkerBox) meta.getBlockState();
        box.getInventory().setContents(inv.getContents());
        meta.setBlockState(box);
        shulkerItem.setItemMeta(meta);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        UUID uuid = p.getUniqueId();

        Inventory inv = openShulkers.get(uuid);
        ShulkerInfo info = shulkerInfo.get(uuid);

        if (inv != null && info != null) {
            updateShulkerContents(inv, info.getItem());
        }

        openShulkers.remove(uuid);
        shulkerInfo.remove(uuid);
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        Player p = e.getEntity();
        UUID uuid = p.getUniqueId();

        Inventory inv = openShulkers.get(uuid);
        ShulkerInfo info = shulkerInfo.get(uuid);

        if (inv != null && info != null) {
            updateShulkerContents(inv, info.getItem());
        }

        openShulkers.remove(uuid);
        shulkerInfo.remove(uuid);
    }

    private boolean isShulkerOpen(Player p) {
        return p.hasMetadata(SHULKER_OPEN_KEY.getKey());
    }

    private boolean isShulkerBox(ItemStack item) {
        return item != null &&
                item.getType().name().endsWith("SHULKER_BOX") &&
                item.getItemMeta() instanceof BlockStateMeta &&
                ((BlockStateMeta) item.getItemMeta()).getBlockState() instanceof ShulkerBox;
    }
}