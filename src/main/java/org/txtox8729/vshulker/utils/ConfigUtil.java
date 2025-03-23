package org.txtox8729.vshulker.utils;

import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.txtox8729.vshulker.VShulker;

import java.util.List;

public class ConfigUtil {
    public static VShulker plugin;

    public static Sound pickupSound;
    public static float pickupSoundPitch;
    public static float pickupSoundVolume;
    public static boolean pickupSoundEnabled;
    public static boolean shulkerOpenEnabled;
    public static String noPermissionMessage;
    public static String reloadSuccessMessage;
    public static String usageMessage;
    public static String noShulkerInContainerMessage;
    public static String limitShulkerReachedMessage;
    public static String limitShulkerDroppedMessage;
    public static int shulkerLimit;
    public static List<String> allowedItems;

    public static void init(VShulker pluginInstance) {
        plugin = pluginInstance;
        plugin.saveDefaultConfig();
        FileConfiguration config = plugin.getConfig();

        // Настройки звука
        String defaultSoundName = "ENTITY_ITEM_PICKUP";
        float defaultPitch = 1.2F;
        float defaultVolume = 0.4F;
        boolean defaultSoundEnabled = true;

        ConfigurationSection pickupSoundSec = config.getConfigurationSection("pickupSound");
        if (pickupSoundSec != null) {
            try {
                String soundName = pickupSoundSec.getString("sound", defaultSoundName).toUpperCase();
                pickupSound = Sound.valueOf(soundName);
            } catch (IllegalArgumentException e) {
                String invalidSound = pickupSoundSec.getString("sound");
                plugin.getLogger().warning("Ошибка конфига: звук '" + invalidSound + "' не найден!");
                pickupSound = Sound.valueOf(defaultSoundName);
            }

            pickupSoundPitch = (float) pickupSoundSec.getDouble("pitch", defaultPitch);
            pickupSoundVolume = (float) pickupSoundSec.getDouble("volume", defaultVolume);
            pickupSoundEnabled = pickupSoundSec.getBoolean("enabled", defaultSoundEnabled);
        } else {
            pickupSound = Sound.valueOf(defaultSoundName);
            pickupSoundPitch = defaultPitch;
            pickupSoundVolume = defaultVolume;
            pickupSoundEnabled = defaultSoundEnabled;
        }

        shulkerOpenEnabled = config.getBoolean("shulkerOpen.enabled", true);

        // сообщения
        noPermissionMessage = HexUtil.translate(config.getString("messages.no-permission-message", "&7[&#D21919✘&7] &7У вас &#D21919нет прав &7на выполнение этой команды!"));
        reloadSuccessMessage = HexUtil.translate(config.getString("messages.reload-success-message", "&7[&#32CD32✔&7] &7Конфигурация &#32CD32успешно &7перезагружена!"));
        usageMessage = HexUtil.translate(config.getString("messages.usage-message", "&7[&#DBA544★&7] &fИспользование: &#DBA544/vshulker reload"));
        noShulkerInContainerMessage = HexUtil.translate(config.getString("messages.no-shulker-in-container", "&7[&#D21919✘&7] &7Вы &#D21919не можете &7положить сюда шалкер!"));
        limitShulkerReachedMessage = HexUtil.translate(config.getString("messages.limit-shulker-reached", "&7[&#D21919✘&7] &7Вы &#D21919не можете &7хранить более &6%limit% &7шалкеров в инвентаре!"));
        limitShulkerDroppedMessage = HexUtil.translate(config.getString("messages.limit-shulker-dropped", "&7[&#D21919✘&7] &7У вас было выброшено &6%dropped% &7шалкеров, так как лимит &6%limit% &7был превышен!"));

        shulkerLimit = config.getInt("settings.limit-shulker-boxes", 3);
        allowedItems = config.getStringList("shulker-auto");
    }
}
