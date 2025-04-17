package org.txtox8729.vshulkers.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.txtox8729.vshulkers.utils.ConfigUtil;

public class ReloadCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (!player.hasPermission("vshulker.admin") && !player.isOp()) {
                player.sendMessage(ConfigUtil.noPermissionMessage);
                return true;
            }
        }

        if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            ConfigUtil.plugin.reloadPlugin();
            sender.sendMessage(ConfigUtil.reloadSuccessMessage);
            return true;
        } else {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (player.hasPermission("vshulker.admin") || player.isOp()) {
                    sender.sendMessage(ConfigUtil.usageMessage);
                } else {
                    sender.sendMessage(ConfigUtil.noPermissionMessage);
                }
            } else {
                sender.sendMessage(ConfigUtil.usageMessage);
            }
            return true;
        }
    }
}