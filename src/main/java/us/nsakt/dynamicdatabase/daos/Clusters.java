package us.nsakt.dynamicdatabase.daos;

import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.dao.BasicDAO;
import us.nsakt.dynamicdatabase.documents.ClusterDocument;

/**
 * A dynamic way to interact with all clusters.
 * This is the proper place to put find methods.
 *
 * @author NathanTheBook
 */
public class Clusters extends BasicDAO<ClusterDocument, ObjectId> {
    /**
     * Default constructor for an instance of the DAO.
     * DAO needs to be initiated before any finder methods can be ran.
     *
     * @param datastore The datastore that the documents are stored in.
     */
    public Clusters(Datastore datastore) {
        super(datastore);
    }
}
