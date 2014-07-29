package us.nsakt.dynamicdatabase.daos;

import com.google.common.collect.Lists;
import org.bson.types.ObjectId;
import org.bukkit.entity.Player;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.dao.BasicDAO;
import us.nsakt.dynamicdatabase.MongoExecutionService;
import us.nsakt.dynamicdatabase.documents.ClusterDocument;
import us.nsakt.dynamicdatabase.documents.ServerDocument;
import us.nsakt.dynamicdatabase.tasks.core.QueryActionTask;
import us.nsakt.dynamicdatabase.tasks.core.base.DBCallback;

import java.util.List;

/**
 * A dynamic way to interact with all servers.
 * This is the proper place to put find methods.
 *
 * @author NathanTheBook
 */
public class Servers extends BasicDAO<ServerDocument, ObjectId> {
    /**
     * Default constructor for an instance of the DAO.
     * DAO needs to be initiated before any finder methods can be ran.
     *
     * @param datastore The datastore that the documents are stored in.
     */
    public Servers(Datastore datastore) {
        super(datastore);
    }

    /**
     * Get all servers a player can see, regardless of cluster.
     * NOTE: This does not check if the server is online.
     * <p/>
     * ----------| CALLBACK INFORMATION |----------
     * The only object that is passed to the callback if a list of ServerDocuments.
     *
     * @param player   Player to perform the permissions check on.
     * @param plugin   Plugin that assigned the server permissions (for the permission nodes).
     * @param callback Action to perform on completion of the server query.
     */
    public void getAllServersPlayerCanSee(final Player player, final String plugin, final DBCallback callback) {
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
        MongoExecutionService.getExecutorService().submit(task);
    }

    /**
     * Get all servers that are public, regardless of cluster.
     * NOTE: This does not check if the server is online.
     * <p/>
     * ----------| CALLBACK INFORMATION |----------
     * The only object that is passed to the callback if a list of ServerDocuments.
     *
     * @param callback Action to perform on completion of the server query.
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
        MongoExecutionService.getExecutorService().submit(task);
    }

    /**
     * Get all servers that are public
     * NOTE: This does not check if the server is online.
     * <p/>
     * ----------| CALLBACK INFORMATION |----------
     * The only object that is passed to the callback if a list of ServerDocuments.
     *
     * @param cluster  Specific cluster the servers are in.
     * @param callback Action to perform on completion of the server query.
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
        MongoExecutionService.getExecutorService().submit(task);
    }
}
