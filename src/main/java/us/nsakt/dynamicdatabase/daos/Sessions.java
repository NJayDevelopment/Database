package us.nsakt.dynamicdatabase.daos;

import com.google.common.collect.Lists;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.dao.BasicDAO;
import us.nsakt.dynamicdatabase.documents.ClusterDocument;
import us.nsakt.dynamicdatabase.documents.SessionDocument;

import javax.annotation.Nullable;
import java.util.Date;
import java.util.List;

public class Sessions extends BasicDAO<SessionDocument, ObjectId> {

    /**
     * Constructor
     *
     * @param document  Document class to represent
     * @param datastore Datastore that contains the objects
     */
    public Sessions(Class<SessionDocument> document, Datastore datastore) {
        super(document, datastore);
    }

    /**
     * Find all sessions that start at the given time in the optional cluster.
     *
     * @param start   When the session started
     * @param cluster The optional cluster the session was in, null for all
     * @return all sessions that start at the given time in the optional cluster.
     */
    public List<SessionDocument> finSessionsStartingAt(Date start, @Nullable ClusterDocument cluster) {
        List<SessionDocument> result = Lists.newArrayList();

        if (cluster == null)
            result = getDatastore().find(SessionDocument.class).field(SessionDocument.MongoFields.START.fieldName).equal(start).asList();
        else {
            List<SessionDocument> docs = getDatastore().find(SessionDocument.class).field(SessionDocument.MongoFields.START.fieldName).equal(start).asList();
            for (SessionDocument document : docs) {
                if (document.getServer().getCluster().equals(cluster)) result.add(document);
            }
        }

        return result;
    }

    /**
     * Find all sessions that end at the given time in the optional cluster.
     *
     * @param end     When the session ended
     * @param cluster The optional cluster the session was in, null for all
     * @return all sessions that end at the given time in the optional cluster.
     */
    public List<SessionDocument> finSessionsEndingAt(Date end, @Nullable ClusterDocument cluster) {
        List<SessionDocument> result = Lists.newArrayList();

        if (cluster == null)
            result = getDatastore().find(SessionDocument.class).field(SessionDocument.MongoFields.END.fieldName).equal(end).asList();
        else {
            List<SessionDocument> docs = getDatastore().find(SessionDocument.class).field(SessionDocument.MongoFields.END.fieldName).equal(end).asList();
            for (SessionDocument document : docs) {
                if (document.getServer().getCluster().equals(cluster)) result.add(document);
            }
        }

        return result;
    }


}
