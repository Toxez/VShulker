package org.txtox8729.vshulkers.utils;

import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.txtox8729.vshulkers.VShulkers;

import java.util.List;

public class ConfigUtil {
    public static VShulkers plugin;

    public static Sound pickupSound;
    public static float pickupSoundPitch;
    public static float pickupSoundVolume;
    public static boolean pickupSoundEnabled;

    public static Sound denySound;
    public static float denySoundPitch;
    public static float denySoundVolume;
    public static boolean denySoundEnabled;

    public static boolean shulkerOpenEnabled;
    public static String noPermissionMessage;
    public static String reloadSuccessMessage;
    public static String usageMessage;
    public static String noShulkerInContainerMessage;
    public static String limitShulkerReachedMessage;
    public static String limitShulkerDroppedMessage;
    public static int shulkerLimit;
    public static List<String> allowedItems;

    public static boolean disableShulkerOpenInVanish;
    public static boolean disableAutoPickupInVanish;
    public static boolean disableShulkerOpenInGodMode;
    public static boolean disableAutoPickupInGodMode;

    public static String vanishShulkerOpenDeniedMessage;
    public static String godShulkerOpenDeniedMessage;

    public static void init(VShulkers pluginInstance) {
        plugin = pluginInstance;
        plugin.saveDefaultConfig();
        FileConfiguration config = plugin.getConfig();

        String defaultPickupSoundName = "ENTITY_ITEM_PICKUP";
        float defaultPickupPitch = 1.2F;
        float defaultPickupVolume = 0.4F;
        boolean defaultPickupSoundEnabled = true;

        ConfigurationSection pickupSoundSec = config.getConfigurationSection("pickupSound");
        if (pickupSoundSec != null) {
            try {
                String soundName = pickupSoundSec.getString("sound", defaultPickupSoundName).toUpperCase();
                pickupSound = Sound.valueOf(soundName);
            } catch (IllegalArgumentException e) {
                String invalidSound = pickupSoundSec.getString("sound");
                plugin.getLogger().warning("Ошибка конфига: звук '" + invalidSound + "' не найден!");
                pickupSound = Sound.valueOf(defaultPickupSoundName);
            }

            pickupSoundPitch = (float) pickupSoundSec.getDouble("pitch", defaultPickupPitch);
            pickupSoundVolume = (float) pickupSoundSec.getDouble("volume", defaultPickupVolume);
            pickupSoundEnabled = pickupSoundSec.getBoolean("enabled", defaultPickupSoundEnabled);
        } else {
            pickupSound = Sound.valueOf(defaultPickupSoundName);
            pickupSoundPitch = defaultPickupPitch;
            pickupSoundVolume = defaultPickupVolume;
            pickupSoundEnabled = defaultPickupSoundEnabled;
        }

        String defaultDenySoundName = "BLOCK_NOTE_BLOCK_BASS";
        float defaultDenyPitch = 0.5F;
        float defaultDenyVolume = 1.0F;
        boolean defaultDenySoundEnabled = true;

        ConfigurationSection denySoundSec = config.getConfigurationSection("denySound");
        if (denySoundSec != null) {
            try {
                String soundName = denySoundSec.getString("sound", defaultDenySoundName).toUpperCase();
                denySound = Sound.valueOf(soundName);
            } catch (IllegalArgumentException e) {
                String invalidSound = denySoundSec.getString("sound");
                plugin.getLogger().warning("Ошибка конфига: звук '" + invalidSound + "' не найден!");
                denySound = Sound.valueOf(defaultDenySoundName);
            }

            denySoundPitch = (float) denySoundSec.getDouble("pitch", defaultDenyPitch);
            denySoundVolume = (float) denySoundSec.getDouble("volume", defaultDenyVolume);
            denySoundEnabled = denySoundSec.getBoolean("enabled", defaultDenySoundEnabled);
        } else {
            denySound = Sound.valueOf(defaultDenySoundName);
            denySoundPitch = defaultDenyPitch;
            denySoundVolume = defaultDenyVolume;
            denySoundEnabled = defaultDenySoundEnabled;
        }

        shulkerOpenEnabled = config.getBoolean("shulkerOpen.enabled", true);

        noPermissionMessage = HexUtil.translate(config.getString("messages.no-permission-message", "&7[&#D21919✘&7] &7У вас &#D21919нет прав &7на выполнение этой команды!"));
        reloadSuccessMessage = HexUtil.translate(config.getString("messages.reload-success-message", "&7[ CD32✔&7] &7Конфигурация  CD32успешно &7перезагружена!"));
        usageMessage = HexUtil.translate(config.getString("messages.usage-message", "&7[&#DBA544★&7] &fИспользование: &#DBA544/vshulker reload"));
        noShulkerInContainerMessage = HexUtil.translate(config.getString("messages.no-shulker-in-container", "&7[&#D21919✘&7] &7Вы &#D21919не можете &7положить сюда шалкер!"));
        limitShulkerReachedMessage = HexUtil.translate(config.getString("messages.limit-shulker-reached", "&7[&#D21919✘&7] &7Вы &#D21919не можете &7хранить более &6%limit% &7шалкеров в инвентаре!"));
        limitShulkerDroppedMessage = HexUtil.translate(config.getString("messages.limit-shulker-dropped", "&7[&#D21919✘&7] &7У вас было выброшено &6%dropped% &7шалкеров, так как лимит &6%limit% &7был превышен!"));

        shulkerLimit = config.getInt("settings.limit-shulker-boxes", 3);
        allowedItems = config.getStringList("shulker-auto");

        disableShulkerOpenInVanish = config.getBoolean("essentials-support.disable-shulker-open-in-vanish", true);
        disableAutoPickupInVanish = config.getBoolean("essentials-support.disable-auto-pickup-in-vanish", true);
        disableShulkerOpenInGodMode = config.getBoolean("essentials-support.disable-shulker-open-in-god-mode", true);
        disableAutoPickupInGodMode = config.getBoolean("essentials-support.disable-auto-pickup-in-god-mode", true);

        vanishShulkerOpenDeniedMessage = HexUtil.translate(config.getString("messages.vanish-shulker-open-denied", "&7[&#D21919✘&7] &7Вы &#D21919не можете &7открыть шалкер в ванише!"));
        godShulkerOpenDeniedMessage = HexUtil.translate(config.getString("messages.god-shulker-open-denied", "&7[&#D21919✘&7] &7Вы &#D21919не можете &7открыть шалкер в режиме бога!"));
    }
}