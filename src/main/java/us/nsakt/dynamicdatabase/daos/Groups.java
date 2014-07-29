package us.nsakt.dynamicdatabase.daos;

import com.google.common.collect.Maps;
import org.bson.types.ObjectId;
import org.bukkit.permissions.Permission;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.dao.BasicDAO;
import us.nsakt.dynamicdatabase.QueryExecutor;
import us.nsakt.dynamicdatabase.documents.GroupDocument;
import us.nsakt.dynamicdatabase.tasks.core.QueryActionTask;
import us.nsakt.dynamicdatabase.tasks.core.base.DBCallback;

import java.util.HashMap;
import java.util.UUID;

/**
 * Data access object to represent the groups datastore
 */
public class Groups extends BasicDAO<GroupDocument, ObjectId> {

    /**
     * Constructor
     *
     * @param document  Document class to represent
     * @param datastore Datastore that contains the objects
     */
    public Groups(Class<GroupDocument> document, Datastore datastore) {
        super(document, datastore);
    }

    /**
     * Get all groups a UUId is in
     *
     * @param uuid UUID to search for
     * @return A list of groups the UUID was found in
     */
    public void getAllGroups(final UUID uuid, final DBCallback callback) {
        QueryActionTask task = new QueryActionTask(getDatastore(), getDatastore().createQuery(getEntityClazz())) {
            @Override
            public void run() {
                getQuery().field(GroupDocument.MongoFields.MEMBERS.fieldName).contains(uuid.toString());
                getQuery().order(GroupDocument.MongoFields.PRIORITY.fieldName);
                callback.call(getQuery().asList());
            }
        };
        QueryExecutor.getExecutorService().submit(task);
    }

    /**
     * Get all permissions a UUID has
     *
     * @param uuid UUID to search for
     * @return all permissions the UUID has
     */
    public void getAllPermissions(final UUID uuid, final DBCallback finalCallback) {
        final HashMap<Permission, Boolean> result = Maps.newHashMap();
        DBCallback callback = new DBCallback() {
            @Override
            public void call() {
            }

            @Override
            public void call(Object... objects) {
                Object o = objects[0];
                result.putAll(((GroupDocument) o).getGroupPermissions());

                finalCallback.call(result);
            }
        };
        getAllGroups(uuid, callback);
    }
}