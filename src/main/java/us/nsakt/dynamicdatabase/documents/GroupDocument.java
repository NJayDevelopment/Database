package us.nsakt.dynamicdatabase.documents;

import com.google.common.collect.Maps;
import org.bukkit.permissions.Permission;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.annotations.Property;
import org.mongodb.morphia.annotations.Reference;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Class to represent a group of players with a set of permissions and other attributes.
 */
public class GroupDocument extends Document {

    private String name;
    private String flair;

    @Property("flair_color")
    private String flairColor;

    private int priority;
    private List<UUID> members;

    @Property("mc_permissions")
    private List<String> mcPermissions;

    @Reference
    private ClusterDocument cluster;

    /**
     * Get the group's name
     *
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * Set the group's name
     *
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get the group's flair
     *
     * @return
     */
    public String getFlair() {
        return flair;
    }

    /**
     * Set the group's flair
     *
     * @param flair
     */
    public void setFlair(String flair) {
        this.flair = flair;
    }

    /**
     * Get the group's flair color
     *
     * @return
     */
    public String getFlairColor() {
        return flairColor;
    }

    /**
     * Set the group's flair color
     *
     * @param flairColor
     */
    public void setFlairColor(String flairColor) {
        this.flairColor = flairColor;
    }

    /**
     * Get the group's priority
     *
     * @return
     */
    public int getPriority() {
        return priority;
    }

    /**
     * Set the group's priority
     *
     * @param priority
     */
    public void setPriority(int priority) {
        this.priority = priority;
    }

    /**
     * Get a list of the group's members (UUID)
     *
     * @return
     */
    public List<UUID> getMembers() {
        return members;
    }

    /**
     * Set a list of the group's members (UUID)
     *
     * @param members
     */
    public void setMembers(List<UUID> members) {
        this.members = members;
    }

    /**
     * Get the group's minecraft permissions
     *
     * @return
     */
    public List<String> getMcPermissions() {
        return mcPermissions;
    }

    /**
     * Set the group's minecraft permissions
     *
     * @param mcPermissions
     */
    public void setMcPermissions(List<String> mcPermissions) {
        this.mcPermissions = mcPermissions;
    }

    /**
     * Get the group's cluster
     *
     * @return
     */
    @Override
    public ClusterDocument getCluster() {
        return cluster;
    }

    /**
     * Set the group's cluster
     *
     * @param cluster
     */
    public void setCluster(ClusterDocument cluster) {
        this.cluster = cluster;
    }

    @Override
    public String toString() {
        return "Group{" +
                "name='" + getName() + '\'' +
                ", flair='" + getFlair() + '\'' +
                ", flairColor='" + getFlairColor() + '\'' +
                ", priority=" + getPriority() +
                ", members=" + getMembers() +
                ", mcPermissions=" + getMcPermissions() +
                ", cluster=" + getCluster() +
                '}';
    }

    /**
     * Get a nicely formatted HashMap of a group's permissions
     *
     * @return a nicely formatted HashMap of a group's permissions
     */
    public HashMap<Permission, Boolean> getGroupPermissions() {
        HashMap<Permission, Boolean> formattedPermissions = Maps.newHashMap();
        List<String> stringPerms = this.getMcPermissions();
        for (String permission : stringPerms) {
            if (permission.startsWith("#")) continue; // We can have comments in the permissions field. Yay!
            boolean add = !permission.startsWith("-");
            formattedPermissions.put(new Permission(add ? permission : permission.substring(1)), add);
        }
        return formattedPermissions;
    }

    /**
     * Get all groups lower in priority than the group
     *
     * @return all groups lower in priority than the group
     */
    public List<GroupDocument> getLowerGroups(Datastore datastore) {
        return datastore.find(GroupDocument.class).field(GroupDocument.MongoFields.PRIORITY.fieldName).lessThanOrEq(this.getPriority()).asList();
    }

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
