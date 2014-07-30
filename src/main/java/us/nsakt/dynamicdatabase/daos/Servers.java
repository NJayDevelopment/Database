package us.nsakt.dynamicdatabase.daos;

import com.google.common.collect.Lists;
import org.bson.types.ObjectId;
import org.bukkit.entity.Player;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.dao.BasicDAO;
import us.nsakt.dynamicdatabase.Debug;
import us.nsakt.dynamicdatabase.documents.ClusterDocument;
import us.nsakt.dynamicdatabase.documents.ServerDocument;
import us.nsakt.dynamicdatabase.util.Visibility;

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
     * @param player   Player to perform the permissions check on.
     * @param plugin   Plugin that assigned the server permissions (for the permission nodes).
     */
    public List<ServerDocument> getAllServersPlayerCanSee(final Player player, final String plugin) {
        List<ServerDocument> result = Lists.newArrayList();
        for (ServerDocument document : getDatastore().find(ServerDocument.class).asList()) {
            if (document.canSee(player, plugin)) result.add(document);
        }
        return result;
    }

    /**
     * Get all servers a player can see, regardless of cluster.
     * NOTE: This does not check if the server is online.
     * @param player   Player to perform the permissions check on.
     * @param plugin   Plugin that assigned the server permissions (for the permission nodes).
     */
    public List<ServerDocument> getAllServersPlayerCanSee(final Player player, final String plugin, final ClusterDocument cluster) {
        List<ServerDocument> result = Lists.newArrayList();
        for (ServerDocument document : getDatastore().find(ServerDocument.class).field(ServerDocument.MongoFields.CLUSTER.fieldName).equal(cluster).asList()) {
            if (document.canSee(player, plugin)) result.add(document);
        }
        return result;
    }

    /**
     * Get all servers that are public, regardless of cluster.
     * NOTE: This does not check if the server is online.
     */
    public List<ServerDocument> getAllPublicServers() {
        List<ServerDocument> result = Lists.newArrayList();
        for (ServerDocument document : getDatastore().find(ServerDocument.class).asList()) {
            Debug.log(Debug.LogLevel.INFO, document.toString());
            if (document.getVisibility().equals(Visibility.PUBLIC)) result.add(document);
        }
        return result;
    }

    /**
     * Get all servers that are public
     * NOTE: This does not check if the server is online.
     *
     * @param cluster  Specific cluster the servers are in.
     */
    public List<ServerDocument> getAllPublicServers(final ClusterDocument cluster) {
        List<ServerDocument> result = Lists.newArrayList();
        for (ServerDocument document : getDatastore().find(ServerDocument.class).field(ServerDocument.MongoFields.CLUSTER.fieldName).equal(cluster.getObjectId()).asList()) {
            if (document.getVisibility().equals(Visibility.PUBLIC)) result.add(document);
        }
        return result;
    }
}
