package org.caronte.managers;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.types.InheritanceNode;
import org.bukkit.entity.Player;
import org.caronte.Main;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

public class ExpiringRankManager {

    private final Main plugin;
    private final LuckPerms luckPerms;
    private final MessageManager messageManager;

    // 👇 ESTE debe coincidir EXACTAMENTE con el Main
    public ExpiringRankManager(Main plugin, LuckPerms luckPerms, MessageManager messageManager) {
        this.plugin = plugin;
        this.luckPerms = luckPerms;
        this.messageManager = messageManager;
    }

    public void checkPlayer(Player player) {

        if (!plugin.getConfig().getBoolean("expiring-notification.enabled")) return;

        User user = luckPerms.getUserManager().getUser(player.getUniqueId());
        if (user == null) return;

        int notifyBeforeDays = plugin.getConfig().getInt("expiring-notification.notify-before-days");

        for (InheritanceNode node : user.getNodes().stream()
                .filter(n -> n instanceof InheritanceNode)
                .map(n -> (InheritanceNode) n)
                .toList()) {

            if (!node.hasExpiry()) continue;

            Instant expiry = node.getExpiry();
            long secondsLeft = Duration.between(Instant.now(), expiry).getSeconds();

            if (secondsLeft <= 0) continue;

            long daysLeft = secondsLeft / 86400;

            if (daysLeft <= notifyBeforeDays) {

                String groupName = node.getGroupName();

                String prefix = "";
                var group = luckPerms.getGroupManager().getGroup(groupName);
                if (group != null && group.getCachedData() != null) {
                    prefix = group.getCachedData().getMetaData().getPrefix();
                    if (prefix == null) prefix = "";
                }

                sendNotification(player, groupName, prefix, secondsLeft);
            }
        }
    }

    private void sendNotification(Player player, String rank, String prefix, long secondsLeft) {

        List<String> messages = plugin.getConfig().getStringList("expiring-notification.message");

        String formattedTime = formatTime(secondsLeft);

        for (String line : messages) {
            player.sendMessage(
                    messageManager.getMessageFromString(
                            line.replace("%rank%", rank)
                                    .replace("%prefix%", prefix)
                                    .replace("%time%", formattedTime)
                    )
            );
        }
    }

    private String formatTime(long seconds) {

        long days = seconds / 86400;
        long hours = (seconds % 86400) / 3600;
        long minutes = (seconds % 3600) / 60;

        if (days > 0)
            return days + "d " + hours + "h";

        if (hours > 0)
            return hours + "h " + minutes + "m";

        return minutes + "m";
    }
}