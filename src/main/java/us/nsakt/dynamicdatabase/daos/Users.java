package us.nsakt.dynamicdatabase.daos;

import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.dao.BasicDAO;
import us.nsakt.dynamicdatabase.documents.UserDocument;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class Users extends BasicDAO<UserDocument, ObjectId> {

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
}
