package us.nsakt.dynamicdatabase.documents;

import org.mongodb.morphia.annotations.Entity;

/**
 * Class to represent a "cluster" of documents.
 */
@Entity("clusters")
public class ClusterDocument extends Document {
    private String name;
    private String visibility;

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
    public String getVisibility() {
        return visibility;
    }

    /**
     * Set the cluster's visibility
     *
     * @param visibility
     */
    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    @Override
    public String toString() {
        return "Cluster{" +
                "name='" + getName() + '\'' +
                ", visibility=" + getVisibility() +
                '}';
    }

    @Override
    public ClusterDocument getCluster() {
        return this;
    }

    protected enum MongoFields {
        id("_id"),
        NAME("name"),
        VISIBILITY("visibility");

        public String fieldName;

        MongoFields(String fieldName) {
            this.fieldName = fieldName;
        }
    }
}
