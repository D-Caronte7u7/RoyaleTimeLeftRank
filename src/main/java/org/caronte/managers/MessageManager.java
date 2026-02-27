package org.caronte.managers;

import org.bukkit.ChatColor;
import org.caronte.Main;

import java.util.List;
import java.util.stream.Collectors;

public class MessageManager {

    private final Main plugin;

    public MessageManager(Main plugin) {
        this.plugin = plugin;
    }

    public String getMessage(String path) {
        String message = plugin.getConfig().getString(path);

        if (message == null) {
            return "";
        }

        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public List<String> getStringList(String path) {
        List<String> list = plugin.getConfig().getStringList(path);

        return list.stream()
                .map(line -> ChatColor.translateAlternateColorCodes('&', line))
                .collect(Collectors.toList());
    }

    public void reload() {
        plugin.reloadConfig();
    }
}