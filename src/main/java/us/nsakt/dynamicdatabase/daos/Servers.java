package us.nsakt.dynamicdatabase.daos;

import com.google.common.collect.Lists;
import org.bson.types.ObjectId;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.dao.BasicDAO;
import us.nsakt.dynamicdatabase.QueryExecutor;
import us.nsakt.dynamicdatabase.documents.ClusterDocument;
import us.nsakt.dynamicdatabase.documents.ServerDocument;
import us.nsakt.dynamicdatabase.tasks.core.QueryActionTask;
import us.nsakt.dynamicdatabase.tasks.core.base.DBCallback;

import java.util.List;

/**
 * Data access object to represent the servers datastore.
 */
public class Servers extends BasicDAO<ServerDocument, ObjectId> {

    /**
     * Constructor
     *
     * @param document  Document class to represent
     * @param datastore Datastore that contains the objects
     */
    public Servers(Class<ServerDocument> document, Datastore datastore) {
        super(document, datastore);
    }

    /**
     * Get all servers a player can see.
     *
     * @param player Player to check against
     * @param plugin Plugin that the player has permissions assigned to
     * @return All servers the player can see.
     */
    public void getAllServersPlayerCanSee(final Player player, final Plugin plugin, final DBCallback callback) {
        QueryActionTask task = new QueryActionTask(getDatastore(), getDatastore().createQuery(getEntityClazz())) {
            @Override
            public void run() {
                List<ServerDocument> result = Lists.newArrayList();
                for (ServerDocument document : getDatastore().find(ServerDocument.class).asList()) {
                    if (document.canSee(player, plugin)) result.add(document);
                }
                callback.call(result);
            }
        };
        QueryExecutor.getExecutorService().submit(task);
    }

    /**
     * Get a list of all public servers.
     *
     * @return A list of all public servers.
     */
    public void getAllPublicServers(final DBCallback callback) {
        QueryActionTask task = new QueryActionTask(getDatastore(), getDatastore().createQuery(getEntityClazz())) {
            @Override
            public void run() {
                List<ServerDocument> result = Lists.newArrayList();
                for (ServerDocument document : getDatastore().find(ServerDocument.class).asList()) {
                    if (document.isPublic()) result.add(document);
                }
                callback.call(result);
            }
        };
        QueryExecutor.getExecutorService().submit(task);
    }

    /**
     * Get a list of all public servers.
     *
     * @return A list of all public servers.
     */
    public void getAllPublicServers(final ClusterDocument cluster, final DBCallback callback) {
        QueryActionTask task = new QueryActionTask(getDatastore(), getDatastore().createQuery(getEntityClazz())) {
            @Override
            public void run() {
                List<ServerDocument> result = Lists.newArrayList();
                for (ServerDocument document : getDatastore().find(ServerDocument.class).field(ServerDocument.MongoFields.CLUSTER.fieldName).equal(cluster).asList()) {
                    if (document.isPublic()) result.add(document);
                }
                callback.call(result);
            }
        };
        QueryExecutor.getExecutorService().submit(task);
    }
}
