package net.njay.dynamicdatabase.documents;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

/**
 * Base document to provide a documents base attributes.
 *
 * @author skipperguy12
 */
@Entity
public abstract class Document {
    @Id
    protected ObjectId _id;

    public Document() {
    }

    public ObjectId getObjectId() {
        return _id;
    }

    protected void setObjectId(ObjectId newObjectId) {
        this._id = newObjectId;
    }

    public ClusterDocument getCluster() {
        return null;
    }

    /**
     * An enum representation of all fields in the class for reference in Mongo operations.
     */
    public enum MongoFields {
        id("_id");

        public String fieldName;

        MongoFields(String fieldName) {
            this.fieldName = fieldName;
        }
    }
}
