package org.caronte.utils;

import net.md_5.bungee.api.ChatColor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ColorUtil {

    // Detecta formato &#FFFFFF
    private static final Pattern HEX_PATTERN = Pattern.compile("&#([A-Fa-f0-9]{6})");

    // Detecta formato #FFFFFF
    private static final Pattern PURE_HEX_PATTERN = Pattern.compile("#([A-Fa-f0-9]{6})");

    public static String color(String message) {

        if (message == null) return null;

        // Soporte para &#FFFFFF
        Matcher matcher = HEX_PATTERN.matcher(message);
        StringBuffer buffer = new StringBuffer();

        while (matcher.find()) {
            String hexCode = matcher.group(1);
            matcher.appendReplacement(buffer, ChatColor.of("#" + hexCode).toString());
        }

        matcher.appendTail(buffer);

        // Soporte para #FFFFFF directo
        matcher = PURE_HEX_PATTERN.matcher(buffer.toString());
        buffer = new StringBuffer();

        while (matcher.find()) {
            String hexCode = matcher.group(1);
            matcher.appendReplacement(buffer, ChatColor.of("#" + hexCode).toString());
        }

        matcher.appendTail(buffer);

        // Soporte para & colores normales
        return ChatColor.translateAlternateColorCodes('&', buffer.toString());
    }
}