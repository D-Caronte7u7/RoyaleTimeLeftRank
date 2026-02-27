package org.caronte.managers;

import org.caronte.Main;
import org.caronte.utils.ColorUtil;

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

        return ColorUtil.color(message);
    }

    public List<String> getStringList(String path) {
        List<String> list = plugin.getConfig().getStringList(path);

        return list.stream()
                .map(ColorUtil::color)
                .collect(Collectors.toList());
    }

    public void reload() {
        plugin.reloadConfig();
    }
}