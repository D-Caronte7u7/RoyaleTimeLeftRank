package org.caronte.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.caronte.managers.ExpiringRankManager;

public class ExpiringRankListener implements Listener {

    private final ExpiringRankManager manager;

    public ExpiringRankListener(ExpiringRankManager manager) {
        this.manager = manager;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        manager.checkPlayer(event.getPlayer());
    }
}