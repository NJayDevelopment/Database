package us.nsakt.dynamicdatabase.documents;

import com.google.common.collect.Maps;
import org.bukkit.permissions.Permission;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Property;
import org.mongodb.morphia.annotations.Reference;
import us.nsakt.dynamicdatabase.MongoExecutionService;
import us.nsakt.dynamicdatabase.tasks.core.QueryActionTask;
import us.nsakt.dynamicdatabase.tasks.core.base.DBCallback;

import java.util.HashMap;
import java.util.List;

/**
 * Class to represent a "group" document in the database.
 * DOCUMENT DESCRIPTION: This document is meant to serve as an easy way to group players together and apply special properties to them.
 *
 * @author NathanTheBook
 */
@Entity("groups")
public class GroupDocument extends Document {
    private String name;
    private String flair;
    @Property("flair_color")
    private String flairColor;
    private int priority;
    @Reference
    private List<UserDocument> members;
    @Property("mc_permissions")
    private List<String> mcPermissions;
    @Reference
    private ClusterDocument cluster;

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

    public List<UserDocument> getMembers() {
        return members;
    }

    public void setMembers(List<UserDocument> members) {
        this.members = members;
    }

    public List<String> getMcPermissions() {
        return mcPermissions;
    }

    public void setMcPermissions(List<String> mcPermissions) {
        this.mcPermissions = mcPermissions;
    }

    public ClusterDocument getCluster() {
        return cluster;
    }

    public void setCluster(ClusterDocument cluster) {
        this.cluster = cluster;
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
                '}';
    }

    /**
     * Get a nicely formatted HashMap of the group's permissions.
     */
    public HashMap<Permission, Boolean> getGroupPermissions() {
        HashMap<Permission, Boolean> formattedPermissions = Maps.newHashMap();
        List<String> stringPerms = this.getMcPermissions();
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
    public void getLowerGroups(final Datastore datastore, final DBCallback callback) {
        final GroupDocument parent = this;
        QueryActionTask task = new QueryActionTask(datastore, datastore.createQuery(this.getClass())) {
            @Override
            public void run() {
                getQuery().field(GroupDocument.MongoFields.PRIORITY.fieldName).lessThanOrEq(parent.getPriority());
                getQuery().field(MongoFields.CLUSTER.fieldName).equal(getCluster());
                callback.call(getQuery().asList());
            }
        };
        MongoExecutionService.getExecutorService().submit(task);
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
        CLUSTER("cluster");

        public String fieldName;

        MongoFields(String fieldName) {
            this.fieldName = fieldName;
        }
    }
}
