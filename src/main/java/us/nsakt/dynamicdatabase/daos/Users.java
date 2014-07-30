package us.nsakt.dynamicdatabase.daos;

import org.bson.types.ObjectId;
import org.bukkit.entity.Player;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.dao.BasicDAO;
import org.mongodb.morphia.query.Query;
import us.nsakt.dynamicdatabase.MongoExecutionService;
import us.nsakt.dynamicdatabase.documents.UserDocument;
import us.nsakt.dynamicdatabase.tasks.core.QueryActionTask;
import us.nsakt.dynamicdatabase.tasks.core.base.DBCallback;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

/**
 * A dynamic way to interact with all users.
 * This is the proper place to put find methods.
 *
 * @author NathanTheBook
 */
public class Users extends BasicDAO<UserDocument, ObjectId> {
    /**
     * Default constructor for an instance of the DAO.
     * DAO needs to be initiated before any finder methods can be ran.
     *
     * @param datastore The datastore that the documents are stored in.
     */
    public Users(Datastore datastore) {
        super(datastore);
    }

    /**
     * Find all users that have used the supplied username.
     *
     * @param username Username to search for
     * @param limit    Optional limit on the number of results
     */
    public List<UserDocument> getAllMatchingUsers(final String username, final @Nullable Integer limit) {
        Query<UserDocument> query = getDatastore().createQuery(UserDocument.class);
        query.field(UserDocument.MongoFields.USERNAMES.fieldName).contains(username);
        query.limit((limit != null) ? limit : Integer.MAX_VALUE);
        return query.asList();
    }

    /**
     * Check if a user exists.
     *
     * @param uuid UUID to search for in databse
     * @return If the UUID is in the database
     */
    public boolean exists(UUID uuid) {
        return getDatastore().find(UserDocument.class).field(UserDocument.MongoFields.UUID.fieldName).equal(uuid).get() != null;
    }

    /**
     * Convert a Bukkit player to a UserDocument, and run a task on them.
     *
     * @param player   Player to convert to UserDocument
     */
    public UserDocument getUserFromPlayer(final Player player) {
        final UUID uuid = player.getUniqueId();
        Query<UserDocument> query = getDatastore().createQuery(UserDocument.class);
        query.field(UserDocument.MongoFields.UUID.fieldName).equal(uuid);
        return query.get();
    }
}
