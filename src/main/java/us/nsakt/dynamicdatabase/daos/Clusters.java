package us.nsakt.dynamicdatabase.daos;

import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.dao.BasicDAO;
import us.nsakt.dynamicdatabase.documents.ClusterDocument;

public class Clusters extends BasicDAO<ClusterDocument, ObjectId> {

    public Clusters(Class<ClusterDocument> document, Datastore datastore) {
        super(document, datastore);
    }

}
