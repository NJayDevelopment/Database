package net.njay.dynamicdatabase.util;

import com.sk89q.minecraft.util.commands.ChatColor;
import net.njay.dynamicdatabase.daos.DAOService;
import net.njay.dynamicdatabase.documents.PunishmentDocument;
import net.njay.dynamicdatabase.documents.ServerDocument;

/**
 * Created by Austin on 8/10/14.
 */
public class BroadcastUtils {
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

    public static String generateCrossServerPrefix(ServerDocument serverDocument) {
        StringBuilder message = new StringBuilder();
        message.append(generatePrefix(serverDocument.getName()));
        return message.toString();
    }


    public static String generateAdminChatMessage(String player, String chatMessage) {
        StringBuilder message = new StringBuilder();
        message.append(generatePrefix("A"));
        message.append(" ");
        message.append(player).append(ChatColor.RESET);
        message.append(": ");
        message.append(chatMessage);
        return message.toString();
    }

    public static String generatePunishmentMessage(PunishmentDocument punishment) {
        StringBuilder message = new StringBuilder();
        message.append(punishment.getPunisher() == null ? ChatColor.GOLD + "Console" : DAOService.getUsers().getUserFromUuid(punishment.getPunisher()).getLastUsername());
        message.append(ChatColor.RESET);
        message.append(" ");
        message.append(ChatColor.DARK_BLUE);
        message.append(punishment.getType().past);
        message.append(ChatColor.RESET);
        message.append(" ");
        message.append(DAOService.getUsers().getUserFromUuid(punishment.getPunished()).getLastUsername());
        message.append(ChatColor.RESET);
        message.append(" ");
        message.append(ChatColor.DARK_BLUE);
        message.append("for");
        message.append(ChatColor.RESET);
        message.append(" ");
        message.append(ChatColor.RED);
        message.append(punishment.getReason());
        return message.toString();
    }
}
