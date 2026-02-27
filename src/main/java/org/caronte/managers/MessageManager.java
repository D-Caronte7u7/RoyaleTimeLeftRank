package org.caronte.managers;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.caronte.Main;

public class MessageManager {

    private final Main plugin;
    private FileConfiguration config;

    public MessageManager(Main plugin) {
        this.plugin = plugin;
        reload();
    }

    public void reload() {
        plugin.reloadConfig();
        this.config = plugin.getConfig();
    }

    public String get(String path) {
        String message = config.getString("messages." + path, "");
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}