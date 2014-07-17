package us.nsakt.dynamicdatabase.documents;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

/**
 * Base class for all Mongo Documents
 * </p>
 * ObjectId's are used by all Mongo documents, see {@link org.mongodb.morphia.annotations.Id}
 */
@Entity
public abstract class Document {
    /**
     * Mongo ObjectId
     */
    protected
    @Id
    ObjectId _id;


    /**
     * Default constructor
     */
    public Document() {
    }

    /**
     * Gets the ObjectId used by Mongo
     *
     * @return
     */
    public ObjectId getObjectId() {
        return _id;
    }

    /**
     * Sets the ObjectId used by Mongo
     *
     * @param newObjectId the new ObjectId to set
     */
    protected void setObjectId(ObjectId newObjectId) {
        this._id = newObjectId;
    }

    /**
     * Returns the object's cluster.
     * NOTE: Classes that use clusters need to override and return appropriate values.
     * NOTE: Callers need to handle null responses as an "all" result.
     *
     * @return the object's cluster
     */
    public Cluster getCluster() {
        return null;
    }
}
