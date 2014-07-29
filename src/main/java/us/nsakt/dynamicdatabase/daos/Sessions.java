package us.nsakt.dynamicdatabase.daos;

import com.google.common.collect.Lists;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.dao.BasicDAO;
import us.nsakt.dynamicdatabase.MongoExecutionService;
import us.nsakt.dynamicdatabase.documents.ClusterDocument;
import us.nsakt.dynamicdatabase.documents.SessionDocument;
import us.nsakt.dynamicdatabase.tasks.core.QueryActionTask;
import us.nsakt.dynamicdatabase.tasks.core.base.DBCallback;

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
     * Find all sessions that start at a certain time, with an optional cluster specification, and run a task when the query is completed.
     * NOTE: Callers need to check if the session has already ended if they plan on modifying it.
     * <p/>
     * ----------| CALLBACK INFORMATION |----------
     * The only object that is passed to the callback is a list of SessionDocuments.
     *
     * @param start    Date that the session started.
     * @param cluster  Optional cluster that the session was in. Null value can be provided, null checks are ran (null = all).
     * @param callback Action to run when the query is completed
     */
    public void finSessionsStartingAt(final Date start, final @Nullable ClusterDocument cluster, final DBCallback callback) {
        QueryActionTask task = new QueryActionTask(getDatastore(), getDatastore().createQuery(getEntityClazz())) {
            @Override
            public void run() {
                List<SessionDocument> result = Lists.newArrayList();
                if (cluster == null)
                    result = getDatastore().find(SessionDocument.class).field(SessionDocument.MongoFields.START.fieldName).equal(start).asList();
                else {
                    List<SessionDocument> docs = getDatastore().find(SessionDocument.class).field(SessionDocument.MongoFields.START.fieldName).equal(start).asList();
                    for (SessionDocument document : docs) {
                        if (document.getServer().getCluster().equals(cluster)) result.add(document);
                    }
                }
                callback.call(result);
            }
        };
        MongoExecutionService.getExecutorService().submit(task);
    }

    /**
     * Find all sessions that end at a certain time, with an optional cluster specification, and run a task when the query is completed.
     * <p/>
     * ----------| CALLBACK INFORMATION |----------
     * The only object that is passed to the callback is a list of SessionDocuments.
     *
     * @param end      Date that the session ended.
     * @param cluster  Optional cluster that the session was in. Null value can be provided, null checks are ran (null = all).
     * @param callback Action to run when the query is completed
     */
    public void finSessionsEndingAt(final Date end, final @Nullable ClusterDocument cluster, final DBCallback callback) {
        QueryActionTask task = new QueryActionTask(getDatastore(), getDatastore().createQuery(getEntityClazz())) {
            @Override
            public void run() {
                List<SessionDocument> result = Lists.newArrayList();
                if (cluster == null)
                    result = getDatastore().find(SessionDocument.class).field(SessionDocument.MongoFields.END.fieldName).equal(end).asList();
                else {
                    List<SessionDocument> docs = getDatastore().find(SessionDocument.class).field(SessionDocument.MongoFields.END.fieldName).equal(end).asList();
                    for (SessionDocument document : docs) {
                        if (document.getServer().getCluster().equals(cluster)) result.add(document);
                    }
                }
                callback.call(result);
            }
        };
        MongoExecutionService.getExecutorService().submit(task);
    }

}
