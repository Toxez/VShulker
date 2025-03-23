package org.txtox8729.vshulker.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.txtox8729.vshulker.VShulker;

public class ReloadCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (!player.hasPermission("vshulker.admin") && !player.isOp()) {
                player.sendMessage(VShulker.noPermissionMessage);
                return true;
            }
        }

        if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            ((VShulker) VShulker.plugin).reloadPlugin();
            sender.sendMessage(VShulker.reloadSuccessMessage);
            return true;
        } else {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (player.hasPermission("vshulker.admin") || player.isOp()) {
                    sender.sendMessage(VShulker.usageMessage);
                } else {
                    sender.sendMessage(VShulker.noPermissionMessage);
                }
            } else {
                sender.sendMessage(VShulker.usageMessage);
            }
            return true;
        }
    }
}