package us.nsakt.dynamicdatabase.tasks;

import us.nsakt.dynamicdatabase.MongoExecutionService;
import us.nsakt.dynamicdatabase.daos.DAOGetter;
import us.nsakt.dynamicdatabase.daos.Servers;
import us.nsakt.dynamicdatabase.documents.ServerDocument;
import us.nsakt.dynamicdatabase.tasks.core.SaveTask;

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
    public static void addPlayerToOnline(final ServerDocument serverDocument, final UUID player) {
        SaveTask task = new SaveTask(getDao().getDatastore(), serverDocument) {
            @Override
            public void run() {
                serverDocument.getOnlinePlayers().add(player);
            }
        };
        MongoExecutionService.getExecutorService().submit(task);
    }

    /**
     * Remove a player from a server's online players list.
     *
     * @param serverDocument Server the player is leaving
     * @param player         Player that is leaving the server
     */
    public static void removePlayerFromOnline(final ServerDocument serverDocument, final UUID player) {
        SaveTask task = new SaveTask(getDao().getDatastore(), serverDocument) {
            @Override
            public void run() {
                serverDocument.getOnlinePlayers().remove(player);
            }
        };
        MongoExecutionService.getExecutorService().submit(task);
    }
}
