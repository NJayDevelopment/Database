package us.nsakt.dynamicdatabase.daos;

import org.bson.types.ObjectId;
import org.bukkit.entity.Player;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.dao.BasicDAO;
import us.nsakt.dynamicdatabase.MongoExecutionService;
import us.nsakt.dynamicdatabase.documents.UserDocument;
import us.nsakt.dynamicdatabase.tasks.core.QueryActionTask;
import us.nsakt.dynamicdatabase.tasks.core.base.DBCallback;

import javax.annotation.Nullable;
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
     * Find all users that have used the supplied username, and run a task on them.
     * <p/>
     * ----------| CALLBACK INFORMATION |----------
     * The only object that is passed to the callback is a list of UserDocuments.
     *
     * @param username Username to search for
     * @param limit    Optional limit on the number of results
     * @param callback Action to run when the query is completed
     */
    public void getAllMatchingUsers(final String username, final @Nullable Integer limit, final DBCallback callback) {
        QueryActionTask task = new QueryActionTask(getDatastore(), getDatastore().createQuery(getEntityClazz())) {
            @Override
            public void run() {
                getQuery().field(UserDocument.MongoFields.USERNAMES.fieldName).contains(username);
                getQuery().limit((limit != null) ? limit : Integer.MAX_VALUE);
                callback.call(getQuery().asList());
            }
        };
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
     * <p/>
     * ----------| CALLBACK INFORMATION |----------
     * The only object that is passed to the callback is a UserDocument.
     *
     * @param player   Player to convert to UserDocument
     * @param callback Action to run when the query is completed
     */
    public void getUserFromPlayer(final Player player, final DBCallback callback) {
        final UUID uuid = player.getUniqueId();
        QueryActionTask task = new QueryActionTask(getDatastore(), getDatastore().createQuery(getEntityClazz())) {
            @Override
            public void run() {
                getQuery().field(UserDocument.MongoFields.UUID.fieldName).equal(uuid);
                callback.call(getQuery().get());
            }
        };
        MongoExecutionService.getExecutorService().submit(task);
    }
}
