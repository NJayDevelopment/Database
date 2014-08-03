package us.nsakt.dynamicdatabase.documents;

import org.mongodb.morphia.annotations.Entity;
import us.nsakt.dynamicdatabase.util.Visibility;

/**
 * Class to represent a "cluster" document in the database.
 * DOCUMENT DESCRIPTION: This document is meant to serve as an easy way to group documents together into separate groups while using the same database.
 *
 * @author NathanTheBook
 */
@Entity("dndb_clusters")
public class ClusterDocument extends Document {
    private String name;
    private Visibility visibility;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Visibility getVisibility() {
        return visibility;
    }

    public void setVisibility(Visibility visibility) {
        this.visibility = visibility;
    }

    @Override
    public String toString() {
        return "ClusterDocument{" +
                "name='" + name + '\'' +
                ", visibility=" + visibility +
                '}';
    }

    @Override
    public ClusterDocument getCluster() {
        return this;
    }

    /**
     * An enum representation of all fields in the class for reference in Mongo operations.
     */
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
