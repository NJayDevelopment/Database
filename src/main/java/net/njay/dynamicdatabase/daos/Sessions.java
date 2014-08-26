package net.njay.dynamicdatabase.daos;

import com.google.common.collect.Lists;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.dao.BasicDAO;
import net.njay.dynamicdatabase.documents.ClusterDocument;
import net.njay.dynamicdatabase.documents.SessionDocument;

import javax.annotation.Nullable;
import java.util.Date;
import java.util.List;

/**
 * A dynamic way to interact with all sessions.
 * This is the proper place to put find methods.
 *
 * @author NathanTheBook
 */
public class Sessions extends BasicDAO<SessionDocument, ObjectId> {
    /**
     * Default constructor for an instance of the DAO.
     * DAO needs to be initiated before any finder methods can be ran.
     *
     * @param datastore The datastore that the documents are stored in.
     */
    public Sessions(Datastore datastore) {
        super(datastore);
    }

    /**
     * Find all sessions that start at a certain time, with an optional cluster specification.
     * NOTE: Callers need to check if the session has already ended if they plan on modifying it.
     *
     * @param start   Date that the session started.
     * @param cluster Optional cluster that the session was in. Null value can be provided, null checks are ran (null = all).
     */
    public List<SessionDocument> finSessionsStartingAt(final Date start, final @Nullable ClusterDocument cluster) {
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
     * Find all sessions that end at a certain time, with an optional cluster specification.
     *
     * @param end     Date that the session ended.
     * @param cluster Optional cluster that the session was in. Null value can be provided, null checks are ran (null = all).
     */
    public List<SessionDocument> finSessionsEndingAt(final Date end, final @Nullable ClusterDocument cluster) {
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
