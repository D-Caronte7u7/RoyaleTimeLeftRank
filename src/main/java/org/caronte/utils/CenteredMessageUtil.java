package org.caronte.utils;

import org.bukkit.ChatColor;

public class CenteredMessageUtil {

    private static final int CENTER_PX = 154;

    public static String centered(String message) {

        if (message == null || message.isEmpty())
            return "";

        message = ChatColor.translateAlternateColorCodes('&', message);

        int messagePxSize = 0;
        boolean previousCode = false;
        boolean isBold = false;

        for (char c : message.toCharArray()) {

            if (c == ChatColor.COLOR_CHAR) {
                previousCode = true;
                continue;
            }

            if (previousCode) {
                previousCode = false;
                isBold = (c == 'l' || c == 'L');
                continue;
            }

            DefaultFontInfo dFI = DefaultFontInfo.getDefaultFontInfo(c);
            messagePxSize += isBold ? dFI.getBoldLength() : dFI.getLength();
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