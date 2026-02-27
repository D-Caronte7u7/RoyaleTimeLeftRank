package org.caronte.commands;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.caronte.Main;
import org.caronte.managers.MessageManager;
import org.caronte.services.TimeRankService;
import org.caronte.utils.TimeFormatter;

import java.util.Optional;
import java.util.UUID;

public class TimeRankCommand implements CommandExecutor {

    private final TimeRankService service;
    private final MessageManager messages;

    public TimeRankCommand(TimeRankService service, MessageManager messages) {
        this.service = service;
        this.messages = messages;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender,
                             @NotNull Command command,
                             @NotNull String label,
                             @NotNull String[] args) {

        // ===============================
        // RELOAD
        // ===============================

        if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {

            if (!sender.hasPermission("royaletimeleftrank.reload")) {
                sender.sendMessage(messages.get("no-permission"));
                return true;
            }

            messages.reload();
            sender.sendMessage(messages.get("reload-success"));
            return true;
        }

        // ===============================
        // VALIDACIÓN CONSOLA
        // ===============================

        if (!(sender instanceof Player) && args.length == 0) {
            sender.sendMessage(messages.get("console-specify"));
            return true;
        }

        OfflinePlayer target;

        if (args.length >= 1) {

            if (!sender.hasPermission("royaletimeleftrank.others")) {
                sender.sendMessage(messages.get("no-permission"));
                return true;
            }

            target = Bukkit.getOfflinePlayer(args[0]);

            if (target == null || target.getUniqueId() == null) {
                sender.sendMessage(messages.get("player-not-found"));
                return true;
            }

        } else {
            target = (Player) sender;
        }

        UUID uuid = target.getUniqueId();

        service.getRemainingTime(uuid).thenAccept(result -> {

            if (result.isEmpty()) {
                sender.sendMessage(messages.get("no-temp-rank"));
                return;
            }

            TimeRankService.RankData data = result.get();

            long remaining = data.getRemaining();

            long days = TimeFormatter.getDays(remaining);
            long hours = TimeFormatter.getHours(remaining);
            long minutes = TimeFormatter.getMinutes(remaining);

            for (String line : Main.getInstance().getConfig().getStringList("time-left-message")) {

                String formatted = line
                        .replace("%player%", target.getName())
                        .replace("%rank%", data.getRankName())
                        .replace("%prefix%", data.getPrefix())
                        .replace("%days%", String.valueOf(days))
                        .replace("%hours%", String.valueOf(hours))
                        .replace("%minutes%", String.valueOf(minutes));

                if (formatted.startsWith("<center>")) {
                    formatted = formatted.replace("<center>", "");
                    formatted = org.caronte.utils.CenteredMessageUtil.centered(formatted);
                } else {
                    formatted = org.bukkit.ChatColor.translateAlternateColorCodes('&', formatted);
                }

                sender.sendMessage(formatted);
            }
        });

        return true;
    }
}