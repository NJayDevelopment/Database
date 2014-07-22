package us.nsakt.dynamicdatabase.documents;

import java.util.List;
import java.util.UUID;

/**
 * Class to represent a group of players with a set of permissions and other attributes.
 */
public class GroupDocument extends Document {
    private String name, flair, flairColor;
    private int priority;
    private List<UUID> members;
    private List<String> mc_permissions;
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
    public List<String> getMc_permissions() {
        return mc_permissions;
    }

    /**
     * Set the group's minecraft permissions
     *
     * @param mc_permissions
     */
    public void setMc_permissions(List<String> mc_permissions) {
        this.mc_permissions = mc_permissions;
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
                "name='" + name + '\'' +
                ", flair='" + flair + '\'' +
                ", flairColor='" + flairColor + '\'' +
                ", priority=" + priority +
                ", members=" + members +
                ", mc_permissions=" + mc_permissions +
                ", cluster=" + cluster +
                '}';
    }
}
