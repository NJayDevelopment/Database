package us.nsakt.dynamicdatabase.daos;

import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.dao.BasicDAO;
import us.nsakt.dynamicdatabase.documents.ClusterDocument;

/**
 * Data access object to represent the clusters datastore.
 */
public class Clusters extends BasicDAO<ClusterDocument, ObjectId> {

    /**
     * Constructor
     *
     * @param document  Document class to represent
     * @param datastore Datastore that contains the objects
     */
    public Clusters(Class<ClusterDocument> document, Datastore datastore) {
        super(document, datastore);
    }

}
