package us.nsakt.dynamicdatabase.tasks;

import org.bukkit.Bukkit;
import us.nsakt.dynamicdatabase.DynamicDatabasePlugin;
import us.nsakt.dynamicdatabase.daos.Servers;
import us.nsakt.dynamicdatabase.documents.ServerDocument;
import us.nsakt.dynamicdatabase.tasks.runners.ServerTask;

import java.util.UUID;

/**
 * Different tasks for working with servers.
 */
public class ServerTasks {

    Servers servers = new Servers(ServerDocument.class, DynamicDatabasePlugin.getInstance().getDatastores().get(ServerDocument.class));

    /**
     * Get the document's relative data access object.
     *
     * @return the document's relative data access object.
     */
    private Servers getDao() {
        return servers;
    }

    /**
     * Adds a player to the online players list.
     *
     * @param serverDocument Server to add the player to
     * @param player         Player to be added
     */
    public void addPlayerToOnline(final ServerDocument serverDocument, final UUID player) {
        ServerTask task = new ServerTask(getDao().getDatastore(), serverDocument) {
            @Override
            public void run() {
                serverDocument.getOnlinePlayers().add(player);
            }
        };
        Bukkit.getScheduler().runTaskAsynchronously(DynamicDatabasePlugin.getInstance(), task);
    }

    /**
     * Remove a player from the online players list
     *
     * @param serverDocument Server the player is leaving
     * @param player         Player to be removed
     */
    public void removePlayerFromOnline(final ServerDocument serverDocument, final UUID player) {
        ServerTask task = new ServerTask(getDao().getDatastore(), serverDocument) {
            @Override
            public void run() {
                serverDocument.getOnlinePlayers().remove(player);
            }
        };
        Bukkit.getScheduler().runTaskAsynchronously(DynamicDatabasePlugin.getInstance(), task);
    }
}
