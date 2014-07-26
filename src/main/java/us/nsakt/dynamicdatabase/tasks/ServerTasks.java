package us.nsakt.dynamicdatabase.tasks;

import us.nsakt.dynamicdatabase.QueryExecutor;
import us.nsakt.dynamicdatabase.daos.DAOGetter;
import us.nsakt.dynamicdatabase.daos.Servers;
import us.nsakt.dynamicdatabase.documents.ServerDocument;
import us.nsakt.dynamicdatabase.tasks.core.SaveTask;

import java.util.UUID;

/**
 * Different tasks for working with servers.
 */
public class ServerTasks {


    /**
     * Get the document's relative data access object.
     *
     * @return the document's relative data access object.
     */
    private static Servers getDao() {
        return new DAOGetter().getServers();
    }

    /**
     * Adds a player to the online players list.
     *
     * @param serverDocument Server to add the player to
     * @param player         Player to be added
     */
    public static void addPlayerToOnline(final ServerDocument serverDocument, final UUID player) {
        SaveTask task = new SaveTask(getDao().getDatastore(), serverDocument) {
            @Override
            public void run() {
                serverDocument.getOnlinePlayers().add(player);
            }
        };
        QueryExecutor.getExecutorService().submit(task);
    }

    /**
     * Remove a player from the online players list
     *
     * @param serverDocument Server the player is leaving
     * @param player         Player to be removed
     */
    public static void removePlayerFromOnline(final ServerDocument serverDocument, final UUID player) {
        SaveTask task = new SaveTask(getDao().getDatastore(), serverDocument) {
            @Override
            public void run() {
                serverDocument.getOnlinePlayers().remove(player);
            }
        };
        QueryExecutor.getExecutorService().submit(task);
    }
}
