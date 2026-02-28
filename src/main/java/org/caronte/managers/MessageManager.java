package org.caronte.managers;

import org.caronte.Main;
import org.caronte.utils.ColorUtil;
import org.caronte.utils.CenteredMessageUtil;

import java.util.List;
import java.util.stream.Collectors;

public class MessageManager {

    private final Main plugin;

    public MessageManager(Main plugin) {
        this.plugin = plugin;
    }

    /**
     * Obtiene un mensaje desde config.yml usando un path.
     */
    public String getMessage(String path) {
        String message = plugin.getConfig().getString(path);

        if (message == null) {
            return "";
        }

        return format(message);
    }

    /**
     * Obtiene una lista de mensajes desde config.yml.
     */
    public List<String> getStringList(String path) {
        List<String> list = plugin.getConfig().getStringList(path);

        return list.stream()
                .map(this::format)
                .collect(Collectors.toList());
    }

    /**
     * Formatea un string dinámico (no path del config).
     */
    public String getMessageFromString(String text) {
        return format(text);
    }

    /**
     * Pipeline completo de formato:
     * 1. Detecta <center>
     * 2. Aplica colores HEX y &
     * 3. Centra si es necesario
     */
    private String format(String text) {

        if (text == null) return "";

        boolean centered = text.contains("<center>");
        text = text.replace("<center>", "");

        // Primero aplicar colores
        text = ColorUtil.color(text);

        // Luego centrar si corresponde
        if (centered) {
            text = CenteredMessageUtil.centerMessage(text);
        }

        return text;
    }

    /**
     * Recarga el config.yml
     */
    public void reload() {
        plugin.reloadConfig();
    }
}