package org.caronte.utils;

import org.bukkit.ChatColor;

public class CenteredMessageUtil {

    private static final int CENTER_PX = 154;

    public static String centerMessage(String message) {

        if (message == null || message.isEmpty())
            return "";

        int messagePxSize = 0;
        boolean previousCode = false;
        boolean isBold = false;

        char[] chars = message.toCharArray();

        for (int i = 0; i < chars.length; i++) {

            char c = chars[i];

            // Detecta código de color
            if (c == ChatColor.COLOR_CHAR) {
                previousCode = true;
                continue;
            }

            if (previousCode) {
                previousCode = false;

                // Detectar HEX moderno (§x§R§R§G§G§B§B)
                if (c == 'x' || c == 'X') {
                    // Saltar los siguientes 12 caracteres (§R§R§G§G§B§B)
                    i += 12;
                    continue;
                }

                isBold = (c == 'l' || c == 'L');
                continue;
            }

            DefaultFontInfo dFI = DefaultFontInfo.getDefaultFontInfo(c);

            messagePxSize += isBold
                    ? dFI.getBoldLength()
                    : dFI.getLength();

            messagePxSize++;
        }

        int halvedMessageSize = messagePxSize / 2;
        int toCompensate = CENTER_PX - halvedMessageSize;
        int spaceLength = DefaultFontInfo.SPACE.getLength() + 1;

        int compensated = 0;
        StringBuilder sb = new StringBuilder();

        while (compensated < toCompensate) {
            sb.append(" ");
            compensated += spaceLength;
        }

        return sb + message;
    }
}