package us.nsakt.dynamicdatabase.daos;

import com.google.common.collect.Maps;
import org.bson.types.ObjectId;
import org.bukkit.permissions.Permission;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.dao.BasicDAO;
import org.mongodb.morphia.query.Query;
import us.nsakt.dynamicdatabase.documents.GroupDocument;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * A dynamic way to interact with all groups.
 * This is the proper place to put find methods.
 *
 * @author NathanTheBook
 */
public class Groups extends BasicDAO<GroupDocument, ObjectId> {
    /**
     * Default constructor for an instance of the DAO.
     * DAO needs to be initiated before any finder methods can be ran.
     *
     * @param datastore The datastore that the documents are stored in.
     */
    public Groups(Datastore datastore) {
        super(datastore);
    }

    /**
     * Find all groups a UUID is in, then run a task on the resulting list of groups.
     *
     * @param uuid     UUID to get group information from.
     */
    public List<GroupDocument> getAllGroups(final UUID uuid) {
        Query<GroupDocument> query = getDatastore().createQuery(GroupDocument.class);
        query.field(GroupDocument.MongoFields.MEMBERS.fieldName).contains(uuid.toString());
        query.order(GroupDocument.MongoFields.PRIORITY.fieldName);
        return query.asList();
    }

    /**
     * Find all groups a UUID is in,, get the permissions from those groups, then run a task on the resulting list of permissions.
     *
     * @param uuid UUID to get permission information from.
     */
    public HashMap<Permission, Boolean> getAllPermissions(final UUID uuid) {
        final HashMap<Permission, Boolean> result = Maps.newHashMap();

        List<GroupDocument> groupDocuments = getAllGroups(uuid);
        for (GroupDocument document : groupDocuments) {
            result.putAll(document.getGroupPermissions());
        }
        return result;
    }
}