package org.caronte.services;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.cacheddata.CachedMetaData;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.NodeType;
import net.luckperms.api.node.types.InheritanceNode;
import net.luckperms.api.query.QueryOptions;

import java.util.Comparator;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class TimeRankService {

    private final LuckPerms luckPerms;

    // ===============================
    // CACHE SYSTEM
    // ===============================

    private final Map<UUID, RankData> cache = new ConcurrentHashMap<>();
    private final Map<UUID, Long> cacheExpiry = new ConcurrentHashMap<>();

    private static final long CACHE_DURATION = 15_000; // 15 segundos

    public TimeRankService(LuckPerms luckPerms) {
        this.luckPerms = luckPerms;
    }

    public CompletableFuture<Optional<RankData>> getRemainingTime(UUID uuid) {

        long now = System.currentTimeMillis();

        // ===============================
        // 1️⃣ Verificar cache primero
        // ===============================

        if (cache.containsKey(uuid) && cacheExpiry.getOrDefault(uuid, 0L) > now) {
            return CompletableFuture.completedFuture(
                    Optional.of(cache.get(uuid))
            );
        }

        // ===============================
        // 2️⃣ Intentar obtener usuario desde memoria
        // ===============================

        User user = luckPerms.getUserManager().getUser(uuid);

        if (user != null) {
            return CompletableFuture.completedFuture(
                    processUser(user, now, uuid)
            );
        }

        // ===============================
        // 3️⃣ Si no está en memoria, cargar async
        // ===============================

        return luckPerms.getUserManager().loadUser(uuid)
                .thenApply(loadedUser -> processUser(loadedUser, now, uuid));
    }

    private Optional<RankData> processUser(User user, long now, UUID uuid) {

        if (user == null) return Optional.empty();

        // Buscar el rango temporal con mayor duración restante
        Optional<InheritanceNode> bestTempNode = user.getNodes(NodeType.INHERITANCE).stream()
                .filter(InheritanceNode::hasExpiry)
                .filter(node -> node.getExpiry().toEpochMilli() > now)
                .max(Comparator.comparingLong(node ->
                        node.getExpiry().toEpochMilli()
                ));

        if (bestTempNode.isEmpty()) {
            return Optional.empty();
        }

        InheritanceNode node = bestTempNode.get();

        long expiry = node.getExpiry().toEpochMilli();
        long remaining = expiry - now;

        String groupName = node.getGroupName();
        Group group = luckPerms.getGroupManager().getGroup(groupName);

        String prefix = "";

        if (group != null) {

            QueryOptions queryOptions = luckPerms.getContextManager()
                    .getQueryOptions(user)
                    .orElse(QueryOptions.defaultContextualOptions());

            CachedMetaData metaData = group.getCachedData().getMetaData(queryOptions);

            String groupPrefix = metaData.getPrefix();
            if (groupPrefix != null) {
                prefix = groupPrefix;
            }
        }

        RankData data = new RankData(groupName, prefix, remaining);

        // ===============================
        // Guardar en cache
        // ===============================

        cache.put(uuid, data);
        cacheExpiry.put(uuid, now + CACHE_DURATION);

        return Optional.of(data);
    }

    // ===============================
    // Clase de datos
    // ===============================

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