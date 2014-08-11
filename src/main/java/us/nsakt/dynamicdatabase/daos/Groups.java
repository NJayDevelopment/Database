package us.nsakt.dynamicdatabase.daos;

import com.google.common.collect.Maps;
import org.bson.types.ObjectId;
import org.bukkit.permissions.Permission;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.dao.BasicDAO;
import org.mongodb.morphia.query.Query;
import us.nsakt.dynamicdatabase.documents.ClusterDocument;
import us.nsakt.dynamicdatabase.documents.GroupDocument;
import us.nsakt.dynamicdatabase.documents.UserDocument;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

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
     * Find all groups a UserDocument is in.
     *
     * @param userDocument UserDocument to get group information from.
     */
    public List<GroupDocument> getAllGroups(final UserDocument userDocument) {
        Query<GroupDocument> query = getDatastore().createQuery(GroupDocument.class);
        query.field(GroupDocument.MongoFields.MEMBERS.fieldName).hasAllOf(Arrays.asList(userDocument.getObjectId()));
        query.order("-" + GroupDocument.MongoFields.PRIORITY.fieldName);
        return query.asList();
    }

    public List<GroupDocument> getAllDefaultGroups(final ClusterDocument clusterDocument) {
        Query<GroupDocument> query = getDatastore().createQuery(GroupDocument.class);
        query.field(GroupDocument.MongoFields.GIVE_TO_NEW.fieldName).equal(true);
        query.field(GroupDocument.MongoFields.CLUSTER.fieldName).equal(clusterDocument.getObjectId());
        query.order("-" + GroupDocument.MongoFields.PRIORITY.fieldName);
        return query.asList();
    }

    /**
     * Find all groups a UserDocument is in,, get the permissions from those groups
     *
     * @param userDocument UserDocument to get permission information from.
     */
    public HashMap<Permission, Boolean> getAllPermissions(final UserDocument userDocument) {
        final HashMap<Permission, Boolean> result = Maps.newHashMap();

        List<GroupDocument> groupDocuments = getAllGroups(userDocument);
        if (groupDocuments == null || groupDocuments.isEmpty()) return null;
        for (GroupDocument document : groupDocuments) {
            if (document.getGroupPermissions() == null || document.getGroupPermissions().isEmpty()) continue;
            result.putAll(document.getGroupPermissions());
        }
        return result;
    }
}