package us.nsakt.dynamicdatabase.tasks;

import org.bukkit.entity.Player;
import us.nsakt.dynamicdatabase.daos.DAOGetter;
import us.nsakt.dynamicdatabase.daos.Servers;
import us.nsakt.dynamicdatabase.documents.ServerDocument;

/**
 * Basic Utility class to perform action related to server documents.
 *
 * @author NathanTheBook
 */
public class ServerTasks {
    private static Servers getDao() {
        return new DAOGetter().getServers();
    }

    /**
     * Add a player to a server's online players list.
     *
     * @param serverDocument Server the player is joining
     * @param player         Player that is joining the server
     */
    public static void addPlayerToOnline(final ServerDocument serverDocument, final Player player) {
        serverDocument.getOnlinePlayers().add(player.getUniqueId());
        if (player.hasPermission("dynamicdb.staff.staff")) serverDocument.getOnlineStaff().add(player.getUniqueId());
    }

    /**
     * Remove a player from a server's online players list.
     *
     * @param serverDocument Server the player is leaving
     * @param player         Player that is leaving the server
     */
    public static void removePlayerFromOnline(final ServerDocument serverDocument, final Player player) {
        serverDocument.getOnlinePlayers().remove(player.getUniqueId());
        if (player.hasPermission("dynamicdb.staff.staff")) serverDocument.getOnlineStaff().remove(player.getUniqueId());
    }
}
