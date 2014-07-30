package us.nsakt.dynamicdatabase.tasks;

import org.bukkit.entity.Player;
import us.nsakt.dynamicdatabase.MongoExecutionService;
import us.nsakt.dynamicdatabase.daos.DAOGetter;
import us.nsakt.dynamicdatabase.daos.Servers;
import us.nsakt.dynamicdatabase.documents.ServerDocument;
import us.nsakt.dynamicdatabase.documents.UserDocument;
import us.nsakt.dynamicdatabase.tasks.core.SaveTask;
import us.nsakt.dynamicdatabase.tasks.core.base.DBCallback;

import java.util.UUID;

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
        serverDocument.getOnlinePlayers().add(new DAOGetter().getUsers().getUserFromPlayer(player));
        if (player.hasPermission("dynamicdb.staff.staff")) serverDocument.getOnlineStaff().add(new DAOGetter().getUsers().getUserFromPlayer(player));
    }

    /**
     * Remove a player from a server's online players list.
     *
     * @param serverDocument Server the player is leaving
     * @param player         Player that is leaving the server
     */
    public static void removePlayerFromOnline(final ServerDocument serverDocument, final Player player) {
        serverDocument.getOnlinePlayers().remove(new DAOGetter().getUsers().getUserFromPlayer(player));
        if (player.hasPermission("dynamicdb.staff.staff")) serverDocument.getOnlineStaff().remove(new DAOGetter().getUsers().getUserFromPlayer(player));
    }
}
