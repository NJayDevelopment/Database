package us.nsakt.dynamicdatabase.documents;

import org.mongodb.morphia.annotations.Entity;
import us.nsakt.dynamicdatabase.util.Visibility;

/**
 * Class to represent a "cluster" of documents.
 */
@Entity("clusters")
public class ClusterDocument extends Document {
    private String name;
    private Visibility visibility;

    /**
     * Get the cluster's name
     *
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * Set the cluster's name
     *
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get the cluster's visibility
     *
     * @return
     */
    public Visibility getVisibility() {
        return visibility;
    }

    /**
     * Set the cluster's visibility
     *
     * @param visibility
     */
    public void setVisibility(Visibility visibility) {
        this.visibility = visibility;
    }

    @Override
    public String toString() {
        return "Clusters{" +
                "name='" + getName() + '\'' +
                ", visibility=" + getVisibility() +
                '}';
    }

    @Override
    public ClusterDocument getCluster() {
        return this;
    }

    public enum MongoFields {
        id("_id"),
        NAME("name"),
        VISIBILITY("visibility");

        public String fieldName;

        MongoFields(String fieldName) {
            this.fieldName = fieldName;
        }
    }
}
