package us.nsakt.dynamicdatabase.documents;

import org.mongodb.morphia.annotations.Entity;
import us.nsakt.dynamicdatabase.util.Visibility;

/**
 * Class to represent a "cluster" of documents.
 */
@Entity("clusters")
public class Cluster extends Document {
    private String name;
    private Visibility visibility;

    /**
     * Get the cluster's name
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * Set the cluster's name
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get the cluster's visibility
     * @return
     */
    public Visibility getVisibility() {
        return visibility;
    }

    /**
     * Set the cluster's visibility
     * @param visibility
     */
    public void setVisibility(Visibility visibility) {
        this.visibility = visibility;
    }

    @Override
    public String toString() {
        return "Cluster{" +
                "name='" + name + '\'' +
                ", visibility=" + visibility +
                '}';
    }

    @Override
    public Cluster getCluster() {
        return this;
    }
}
