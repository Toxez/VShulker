package org.txtox8729.vshulker;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import org.txtox8729.vshulker.commands.ReloadCommand;
import org.txtox8729.vshulker.commands.VTabCompleter;
import org.txtox8729.vshulker.listeners.ContainerListener;
import org.txtox8729.vshulker.listeners.LimitListener;
import org.txtox8729.vshulker.listeners.SHListeners;
import org.txtox8729.vshulker.utils.ConfigUtil;

public final class VShulker extends JavaPlugin {
    private static VShulker instance;

    @Override
    public void onEnable() {
        instance = this;
        ConfigUtil.init(this);

        Bukkit.getPluginManager().registerEvents(new SHListeners(), this);
        Bukkit.getPluginManager().registerEvents(new ContainerListener(), this);

        LimitListener limitListener = new LimitListener(this);
        Bukkit.getPluginManager().registerEvents(limitListener, this);

        this.getCommand("vshulker").setExecutor(new ReloadCommand());
        this.getCommand("vshulker").setTabCompleter(new VTabCompleter());

        getLogger().info(ChatColor.GREEN + "Плагин VShulker успешно включен! версия: 1.1");
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

    public static VShulker getInstance() {
        return instance;
    }
}
