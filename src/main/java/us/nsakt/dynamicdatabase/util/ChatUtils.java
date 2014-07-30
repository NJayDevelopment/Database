package us.nsakt.dynamicdatabase.util;

import com.sk89q.minecraft.util.commands.ChatColor;

/**
 * Random utilities for creating chat messages,
 *
 * @author NathanTheBook
 */
public class ChatUtils {
    /**
     * Generate a prefix with the specified text.
     *
     * @param text Text to put inside the prefix block
     * @return A nicely formatted prefix string with the supplied text
     */
    public static String generatePrefix(String text) {
        StringBuilder message = new StringBuilder();
        message.append(ChatColor.GOLD).append("[").append(ChatColor.BLUE).append(text).append(ChatColor.RESET).append(ChatColor.GOLD).append("]").append(ChatColor.RESET);
        return message.toString();
    }
}
