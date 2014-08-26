package net.njay.dynamicdatabase.tasks;

import org.bukkit.entity.Player;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import net.njay.dynamicdatabase.MongoExecutionService;
import net.njay.dynamicdatabase.daos.DAOService;
import net.njay.dynamicdatabase.daos.Servers;
import net.njay.dynamicdatabase.documents.ServerDocument;

/**
 * Basic Utility class to perform action related to server documents.
 *
 * @author NathanTheBook
 */
public class ServerTasks {
    private static Servers getDao() {
        return DAOService.getServers();
    }

    /**
     * Add a player to a server's online players list.
     *
     * @param serverDocument Server the player is joining
     * @param player         Player that is joining the server
     */
    public static void addPlayerToOnline(final ServerDocument serverDocument, final Player player) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Query<ServerDocument> query = getDao().createQuery().filter(ServerDocument.MongoFields.id.fieldName, serverDocument.getObjectId());
                UpdateOperations<ServerDocument> updates = getDao().createUpdateOperations();
                updates.add(ServerDocument.MongoFields.ONLINE_PLAYERS.fieldName, player.getUniqueId());
                if (player.hasPermission("dynamicdb.staff.staff"))
                    updates.add(ServerDocument.MongoFields.ONLINE_STAFF.fieldName, player.getUniqueId());
                getDao().update(query, updates);
            }
        };
        MongoExecutionService.getExecutorService().execute(runnable);
    }

    /**
     * Remove a player from a server's online players list.
     *
     * @param serverDocument Server the player is leaving
     * @param player         Player that is leaving the server
     */
    public static void removePlayerFromOnline(final ServerDocument serverDocument, final Player player) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Query<ServerDocument> query = getDao().createQuery().filter(ServerDocument.MongoFields.id.fieldName, serverDocument.getObjectId());
                UpdateOperations<ServerDocument> updates = getDao().createUpdateOperations();
                updates.removeAll(ServerDocument.MongoFields.ONLINE_PLAYERS.fieldName, player.getUniqueId());
                if (player.hasPermission("dynamicdb.staff.staff"))
                    updates.removeAll(ServerDocument.MongoFields.ONLINE_STAFF.fieldName, player.getUniqueId());
                getDao().update(query, updates);
            }
        };
        MongoExecutionService.getExecutorService().execute(runnable);
    }
}
