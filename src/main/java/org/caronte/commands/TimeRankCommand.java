package org.caronte.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.caronte.Main;
import org.caronte.managers.MessageManager;
import org.caronte.services.TimeRankService;
import org.caronte.services.TimeRankService.RankData;
import org.caronte.utils.CenteredMessageUtil;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class TimeRankCommand implements CommandExecutor {

    private final TimeRankService service;
    private final MessageManager messageManager;

    public TimeRankCommand(TimeRankService service, MessageManager messageManager) {
        this.service = service;
        this.messageManager = messageManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        // Reload
        if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {

            if (!sender.hasPermission("royaletimeleftrank.reload")) {
                sender.sendMessage(messageManager.getMessage("messages.no-permission"));
                return true;
            }

            messageManager.reload();
            sender.sendMessage(messageManager.getMessage("messages.reload-success"));
            return true;
        }

        Player target;

        // Consultar otro jugador
        if (args.length == 1) {

            if (!sender.hasPermission("royaletimeleftrank.others")) {
                sender.sendMessage(messageManager.getMessage("messages.no-permission"));
                return true;
            }

            target = Bukkit.getPlayer(args[0]);

            if (target == null) {
                sender.sendMessage(messageManager.getMessage("messages.player-not-found"));
                return true;
            }

        } else {

            if (!(sender instanceof Player)) {
                sender.sendMessage(messageManager.getMessage("messages.console-specify"));
                return true;
            }

            target = (Player) sender;
        }

        Player finalTarget = target;

        service.getRemainingRanks(target.getUniqueId()).thenAccept(ranks -> {

            Bukkit.getScheduler().runTask(Main.getInstance(), () -> {

                if (ranks.isEmpty()) {
                    sender.sendMessage(messageManager.getMessage("messages.no-temp-rank"));
                    return;
                }

                for (String line : messageManager.getStringList("header")) {
                    sendFormattedLine(sender, line, finalTarget.getName(), null);
                }

                String rawFormat = Main.getInstance().getConfig().getString("rank-format");

                for (RankData rank : ranks) {

                    long millis = rank.getRemaining();

                    long days = TimeUnit.MILLISECONDS.toDays(millis);
                    long hours = TimeUnit.MILLISECONDS.toHours(millis) % 24;
                    long minutes = TimeUnit.MILLISECONDS.toMinutes(millis) % 60;

                    String line = rawFormat
                            .replace("%player%", finalTarget.getName())
                            .replace("%rank%", rank.getRankName())
                            .replace("%prefix%", rank.getPrefix() == null ? "" : rank.getPrefix())
                            .replace("%days%", String.valueOf(days))
                            .replace("%hours%", String.valueOf(hours))
                            .replace("%minutes%", String.valueOf(minutes));

                    line = messageManager.getMessageFromString(line);

                    sender.sendMessage(line);
                }

                // =========================
                // FOOTER
                // =========================
                for (String line : messageManager.getStringList("footer")) {
                    sendFormattedLine(sender, line, finalTarget.getName(), null);
                }

            });

        });

        return true;
    }

    private void sendFormattedLine(CommandSender sender, String line, String playerName, RankData rank) {

        boolean centered = false;

        if (line.startsWith("<center>")) {
            centered = true;
            line = line.replace("<center>", "");
        }

        line = line.replace("%player%", playerName);

        line = ChatColor.translateAlternateColorCodes('&', line);

        if (centered) {
            line = CenteredMessageUtil.centerMessage(line);
        }

        sender.sendMessage(line);
    }
}