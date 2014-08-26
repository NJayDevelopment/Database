package net.njay.dynamicdatabase.documents;

import com.google.common.collect.Maps;
import org.bson.types.ObjectId;
import org.bukkit.permissions.Permission;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Property;
import net.njay.dynamicdatabase.MongoExecutionService;
import net.njay.dynamicdatabase.daos.DAOService;
import net.njay.dynamicdatabase.tasks.core.QueryActionTask;
import net.njay.dynamicdatabase.tasks.core.base.DBCallBack;

import java.util.HashMap;
import java.util.List;

/**
 * Class to represent a "group" document in the database.
 * DOCUMENT DESCRIPTION: This document is meant to serve as an easy way to group players together and apply special properties to them.
 *
 * @author NathanTheBook
 */
@Entity(value = "dndb_groups", noClassnameStored = true)
public class GroupDocument extends Document {
    private String name;
    private String flair;
    @Property("flair_color")
    private String flairColor;
    private int priority;
    private List<ObjectId> members;
    @Property("mc_permissions")
    private List<String> mcPermissions;
    private ObjectId cluster;
    @Property("give_to_new")
    private boolean giveToNew;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFlair() {
        return flair;
    }

    public void setFlair(String flair) {
        this.flair = flair;
    }

    public String getFlairColor() {
        return flairColor;
    }

    public void setFlairColor(String flairColor) {
        this.flairColor = flairColor;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public List<ObjectId> getMembers() {
        return members;
    }

    public void setMembers(List<ObjectId> members) {
        this.members = members;
    }

    public List<String> getMcPermissions() {
        return mcPermissions;
    }

    public void setMcPermissions(List<String> mcPermissions) {
        this.mcPermissions = mcPermissions;
    }

    public ClusterDocument getCluster() {
        return DAOService.getClusters().findOne(ClusterDocument.MongoFields.id.fieldName, cluster);
    }

    public void setCluster(ClusterDocument cluster) {
        this.cluster = cluster.getObjectId();
    }

    public boolean isGiveToNew() {
        return giveToNew;
    }

    public void setGiveToNew(boolean giveToNew) {
        this.giveToNew = giveToNew;
    }

    @Override
    public String toString() {
        return "GroupDocument{" +
                "name='" + name + '\'' +
                ", flair='" + flair + '\'' +
                ", flairColor='" + flairColor + '\'' +
                ", priority=" + priority +
                ", members=" + members +
                ", mcPermissions=" + mcPermissions +
                ", cluster=" + cluster +
                ", giveToNew=" + giveToNew +
                '}';
    }

    /**
     * Get a nicely formatted HashMap of the group's permissions.
     */
    public HashMap<Permission, Boolean> getGroupPermissions() {
        List<String> stringPerms = getMcPermissions();
        HashMap<Permission, Boolean> formattedPermissions = Maps.newHashMap();
        if (stringPerms == null || stringPerms.isEmpty()) return null;
        for (String permission : stringPerms) {
            if (permission.startsWith("#")) continue;
            boolean add = !permission.startsWith("-");
            formattedPermissions.put(new Permission(add ? permission : permission.substring(1)), add);
        }
        return formattedPermissions;
    }

    /**
     * Get all groups lower in priority then the current group, and perform an action on them.
     * <p/>
     * ----------| CALLBACK INFORMATION |----------
     * The only object that is passed to the callback is a list of GroupDocuments.
     * *
     *
     * @param datastore Datastore that the group is in.
     * @param callback  Action to perform when the query is completed.
     */
    public void getLowerGroups(final Datastore datastore, final DBCallBack callback) {
        final GroupDocument parent = this;
        QueryActionTask<GroupDocument> task = new QueryActionTask<GroupDocument>(datastore, datastore.createQuery(GroupDocument.class)) {
            @Override
            public void run() {
                getQuery().field(GroupDocument.MongoFields.PRIORITY.fieldName).lessThanOrEq(parent.getPriority());
                getQuery().field(MongoFields.CLUSTER.fieldName).equal(getCluster());
                callback.call(getQuery().asList());
            }
        };
        MongoExecutionService.getExecutorService().execute(task);
    }

    /**
     * An enum representation of all fields in the class for reference in Mongo operations.
     */
    public enum MongoFields {
        id("_id"),
        NAME("name"),
        FLAIR("flair"),
        FLAIR_COLOR("flair_color"),
        PRIORITY("priority"),
        MEMBERS("members"),
        MC_PERMISSIONS("mc_permissions"),
        CLUSTER("cluster"),
        GIVE_TO_NEW("give_to_new");

        public String fieldName;

        MongoFields(String fieldName) {
            this.fieldName = fieldName;
        }
    }
}
