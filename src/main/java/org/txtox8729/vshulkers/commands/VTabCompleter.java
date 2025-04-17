package org.txtox8729.vshulkers.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class VTabCompleter implements TabCompleter {

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (player.hasPermission("vshulker.admin") || player.isOp()) {
                    List<String> completions = new ArrayList<>();
                    completions.add("reload");
                    return completions;
                }
            } else {
                List<String> completions = new ArrayList<>();
                completions.add("reload");
                return completions;
            }
        }
        return Collections.emptyList();
    }
}