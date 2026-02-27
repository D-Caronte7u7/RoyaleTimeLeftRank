package org.caronte.services;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.types.InheritanceNode;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class TimeRankService {

    private final LuckPerms luckPerms;

    public TimeRankService(LuckPerms luckPerms) {
        this.luckPerms = luckPerms;
    }

    public CompletableFuture<List<RankData>> getRemainingRanks(UUID uuid) {

        return luckPerms.getUserManager().loadUser(uuid).thenApply(user -> {

            if (user == null) return List.of();

            long now = System.currentTimeMillis();

            return user.getNodes().stream()
                    .filter(node -> node instanceof InheritanceNode)
                    .map(node -> (InheritanceNode) node)
                    .filter(InheritanceNode::hasExpiry)
                    .filter(node -> node.getExpiry().toEpochMilli() > now)
                    .sorted(Comparator.comparing(node -> node.getExpiry().toEpochMilli()))
                    .map(node -> {

                        long expiry = node.getExpiry().toEpochMilli();
                        long remaining = expiry - now;
                        String groupName = node.getGroupName();

                        Group group = luckPerms.getGroupManager().getGroup(groupName);

                        String prefix = "";
                        if (group != null && group.getCachedData() != null) {
                            prefix = group.getCachedData().getMetaData().getPrefix();
                            if (prefix == null) prefix = "";
                        }

                        return new RankData(groupName, prefix, remaining);
                    })
                    .collect(Collectors.toList());
        });
    }

    public static class RankData {

        private final String rankName;
        private final String prefix;
        private final long remaining;

        public RankData(String rankName, String prefix, long remaining) {
            this.rankName = rankName;
            this.prefix = prefix;
            this.remaining = remaining;
        }

        public String getRankName() {
            return rankName;
        }

        public String getPrefix() {
            return prefix;
        }

        public long getRemaining() {
            return remaining;
        }
    }
}