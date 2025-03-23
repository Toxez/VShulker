package org.txtox8729.vshulker;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.txtox8729.vshulker.commands.ReloadCommand;
import org.txtox8729.vshulker.commands.VTabCompleter;
import org.txtox8729.vshulker.listeners.ContainerListener;
import org.txtox8729.vshulker.listeners.LimitListener;
import org.txtox8729.vshulker.listeners.SHListeners;
import org.txtox8729.vshulker.utils.HexUtil;

import java.util.List;

public final class VShulker extends JavaPlugin {
    public static JavaPlugin plugin;
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

    private static VShulker instance;

    @Override
    public void onEnable() {
        instance = this;
        plugin = this;

        Bukkit.getPluginManager().registerEvents(new SHListeners(), this);
        Bukkit.getPluginManager().registerEvents(new ContainerListener(), this);

        LimitListener limitListener = new LimitListener(this);
        Bukkit.getPluginManager().registerEvents(limitListener, this);

        this.getCommand("vshulker").setExecutor(new ReloadCommand());
        this.getCommand("vshulker").setTabCompleter(new VTabCompleter());

        this.configInit();

        getLogger().info(ChatColor.GREEN + "Плагин VShulker успешно включен!");
        getLogger().info(ChatColor.GREEN + "Автор: Tox_8729");
    }

    @Override
    public void onDisable() {
        getLogger().info(ChatColor.RED + "Плагин VShulker успешно выключен.");
        getLogger().info(ChatColor.RED + "Автор: Tox_8729");
    }

    //---------------------------------------------------------------------------------------------
    private void configInit() {
        this.saveDefaultConfig();
        FileConfiguration config = this.getConfig();

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
                Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Ошибка конфига: звук '" + invalidSound + "' не найден!");
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
        //------------------------------------------------------------------------------------------

        shulkerOpenEnabled = config.getBoolean("shulkerOpen.enabled", true);

        // Сообщения ----------------------------------------------------------------------------------------
        noPermissionMessage = HexUtil.translate(config.getString("messages.no-permission-message", "&7[&#D21919✘&7] &7У вас &#D21919нет прав &7на выполнение этой команды!"));
        reloadSuccessMessage = HexUtil.translate(config.getString("messages.reload-success-message", "&7[&#32CD32✔&7] &7Конфигурация &#32CD32успешно &7перезагружена!"));
        usageMessage = HexUtil.translate(config.getString("messages.usage-message", "&7[&#DBA544★&7] &fИспользование: &#DBA544/vshulker reload"));
        noShulkerInContainerMessage = HexUtil.translate(config.getString("messages.no-shulker-in-container", "&7[&#D21919✘&7] &7Вы &#D21919не можете &7положить сюда шалкер!"));
        limitShulkerReachedMessage = HexUtil.translate(config.getString("messages.limit-shulker-reached", "&7[&#D21919✘&7] &7Вы &#D21919не можете &7хранить более &6%limit% &7шалкеров в инвентаре!"));
        limitShulkerDroppedMessage = HexUtil.translate(config.getString("messages.limit-shulker-dropped", "&7[&#D21919✘&7] &7У вас было выброшено &6%dropped% &7шалкеров, так как лимит &6%limit% &7был превышен!"));
        // ---------------------------------------------------------------------------------------

        //----------------------------------------------------------------------------
        shulkerLimit = config.getInt("settings.limit-shulker-boxes", 3);
        allowedItems = config.getStringList("shulker-auto");
        //------------------------------------------------------------------------------------------------
    }

    public void reloadPlugin() {
        this.reloadConfig();
        this.configInit();
    }

    public static VShulker getInstance() {
        return instance;
    }
}