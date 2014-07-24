package us.nsakt.dynamicdatabase.daos;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.bson.types.ObjectId;
import org.bukkit.permissions.Permission;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.dao.BasicDAO;
import us.nsakt.dynamicdatabase.documents.GroupDocument;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class Groups extends BasicDAO<GroupDocument, ObjectId> {

    public Groups(Class<GroupDocument> document, Datastore datastore) {
        super(document, datastore);
    }

    /**
     * Get all groups a UUId is in
     *
     * @param uuid UUID to search for
     * @return A list of groups the UUID was found in
     */
    public List<GroupDocument> getAllGroups(UUID uuid) {
        List<GroupDocument> groupDocuments = Lists.newArrayList();
        groupDocuments = getDatastore().find(getEntityClazz()).field(GroupDocument.MongoFields.MEMBERS.fieldName).contains(uuid.toString()).order(GroupDocument.MongoFields.PRIORITY.fieldName).asList();
        return groupDocuments;
    }

    /**
     * Get all permissions a UUID has
     *
     * @param uuid UUID to search for
     * @return all permissions the UUID has
     */
    public HashMap<Permission, Boolean> getAllPermissions(UUID uuid) {
        HashMap<Permission, Boolean> result = Maps.newHashMap();
        for (GroupDocument groupDocument : getAllGroups(uuid)) {
            result.putAll(groupDocument.getGroupPermissions());
        }
        return result;
    }

    /**
     * Get a UUID's highest priority group
     *
     * @param uuid UUID to search for
     * @return a UUID's highest priority group
     */
    public GroupDocument getHighestPriorityGroup(UUID uuid) {
        return getDatastore().find(GroupDocument.class).field(GroupDocument.MongoFields.MEMBERS.fieldName).contains(uuid.toString()).order(GroupDocument.MongoFields.PRIORITY.fieldName).limit(1).get();
    }
}