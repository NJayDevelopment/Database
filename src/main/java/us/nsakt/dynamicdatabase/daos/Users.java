package us.nsakt.dynamicdatabase.daos;

import org.bson.types.ObjectId;
import org.bukkit.entity.Player;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.dao.BasicDAO;
import us.nsakt.dynamicdatabase.documents.UserDocument;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

/**
 * Data access object to represent the users datastore.
 */
public class Users extends BasicDAO<UserDocument, ObjectId> {

    /**
     * Constructor
     *
     * @param document  Document class to represent
     * @param datastore Datastore that contains the objects
     */
    public Users(Class<UserDocument> document, Datastore datastore) {
        super(document, datastore);
    }

    /**
     * Get all users who have logged in with that username
     *
     * @param username Username to look for
     * @param limit    Limit of how many should be returned
     * @return all users who have logged in with that username
     */
    public List<UserDocument> getAllMatchingUsers(String username, @Nullable Integer limit) {
        return getDatastore().find(UserDocument.class).field(UserDocument.MongoFields.USERNAMES.fieldName).contains(username).limit((limit != null) ? limit : 10000000).asList();
    }

    /**
     * See if a user is in the database
     *
     * @param uuid UUID to look for
     * @return If the user exists
     */
    public boolean exists(UUID uuid) {
        return getDatastore().find(UserDocument.class).field(UserDocument.MongoFields.UUID.fieldName).equal(uuid).get() != null;
    }

    /**
     * Get a UserDocument from a Bukkit player.
     *
     * @param player Player to get the document for
     * @return a UserDocument from the Bukkit player.
     */
    public UserDocument getUserFromPlayer(Player player) {
        UUID uuid = player.getUniqueId();
        return getDatastore().find(UserDocument.class).field(UserDocument.MongoFields.UUID.fieldName).equal(uuid).get();
    }
}
