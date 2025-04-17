package org.txtox8729.vshulkers;

import com.earth2me.essentials.Essentials;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import org.txtox8729.vshulkers.commands.ReloadCommand;
import org.txtox8729.vshulkers.commands.VTabCompleter;
import org.txtox8729.vshulkers.listeners.ContainerListener;
import org.txtox8729.vshulkers.listeners.LimitListener;
import org.txtox8729.vshulkers.listeners.SHListeners;
import org.txtox8729.vshulkers.utils.ConfigUtil;

public final class VShulkers extends JavaPlugin {
    private static VShulkers instance;
    private Essentials essentials;

    @Override
    public void onEnable() {
        instance = this;
        ConfigUtil.init(this);

        if (Bukkit.getPluginManager().getPlugin("Essentials") != null) {
            essentials = (Essentials) Bukkit.getPluginManager().getPlugin("Essentials");
        } else {
        }

        Bukkit.getPluginManager().registerEvents(new SHListeners(), this);
        Bukkit.getPluginManager().registerEvents(new ContainerListener(), this);

        LimitListener limitListener = new LimitListener(this);
        Bukkit.getPluginManager().registerEvents(limitListener, this);

        this.getCommand("vshulker").setExecutor(new ReloadCommand());
        this.getCommand("vshulker").setTabCompleter(new VTabCompleter());

        getLogger().info(ChatColor.GREEN + "Плагин VShulker успешно включен! версия: 1.3");
        getLogger().info(ChatColor.GREEN + "Автор: Tox_8729");
    }

    @Override
    public void onDisable() {
        getLogger().info(ChatColor.RED + "Плагин VShulker успешно выключен.");
        getLogger().info(ChatColor.RED + "Автор: Tox_8729");
    }

    public void reloadPlugin() {
        this.reloadConfig();
        ConfigUtil.init(this);
    }

    public static VShulkers getInstance() {
        return instance;
    }

    public Essentials getEssentials() {
        return essentials;
    }
}