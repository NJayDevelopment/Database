package us.nsakt.dynamicdatabase.daos;

import com.google.common.collect.Lists;
import org.bson.types.ObjectId;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.dao.BasicDAO;
import us.nsakt.dynamicdatabase.documents.ClusterDocument;
import us.nsakt.dynamicdatabase.documents.ServerDocument;

import java.util.List;

public class Servers extends BasicDAO<ServerDocument, ObjectId> {

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
    public List<ServerDocument> getAllServersPlayerCanSee(Player player, Plugin plugin) {
        List<ServerDocument> result = Lists.newArrayList();
        for (ServerDocument document : getDatastore().find(ServerDocument.class).asList()) {
            if (document.canSee(player, plugin)) result.add(document);
        }
        return result;
    }

    /**
     * Get a list of all public servers.
     *
     * @return A list of all public servers.
     */
    public List<ServerDocument> getAllPublicServers() {
        List<ServerDocument> result = Lists.newArrayList();
        for (ServerDocument document : getDatastore().find(ServerDocument.class).asList()) {
            if (document.isPublic()) result.add(document);
        }
        return result;
    }

    /**
     * Get a list of all public servers.
     *
     * @return A list of all public servers.
     */
    public List<ServerDocument> getAllPublicServers(ClusterDocument cluster) {
        List<ServerDocument> result = Lists.newArrayList();
        for (ServerDocument document : getDatastore().find(ServerDocument.class).field(ServerDocument.MongoFields.CLUSTER.fieldName).equal(cluster).asList()) {
            if (document.isPublic()) result.add(document);
        }
        return result;
    }
}
