package us.nsakt.dynamicdatabase.daos;

import com.google.common.collect.Maps;
import org.bson.types.ObjectId;
import org.bukkit.permissions.Permission;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.dao.BasicDAO;
import us.nsakt.dynamicdatabase.MongoExecutionService;
import us.nsakt.dynamicdatabase.documents.GroupDocument;
import us.nsakt.dynamicdatabase.tasks.core.QueryActionTask;
import us.nsakt.dynamicdatabase.tasks.core.base.DBCallback;

import java.util.HashMap;
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
     * <p/>
     * ----------| CALLBACK INFORMATION |----------
     * The only object that is passed to the callback is a List of GroupDocuments.
     *
     * @param uuid     UUID to get group information from.
     * @param callback Action to perform on completion of the group query.
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
        MongoExecutionService.getExecutorService().submit(task);
    }

    /**
     * Find all groups a UUID is in,, get the permissions from those groups, then run a task on the resulting list of permissions.
     * <p/>
     * ----------| CALLBACK INFORMATION |----------
     * The only object that is passed to the callback is a HashMap of <Permission, Boolean>.
     *
     * @param uuid          UUID to get permission information from.
     * @param finalCallback Action to perform on completion of the permissions query.
     */
    public void getAllPermissions(final UUID uuid, final DBCallback finalCallback) {
        final HashMap<Permission, Boolean> result = Maps.newHashMap();
        DBCallback callback = new DBCallback() {
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