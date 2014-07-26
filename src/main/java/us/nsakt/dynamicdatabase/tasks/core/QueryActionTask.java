package us.nsakt.dynamicdatabase.tasks.core;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;
import us.nsakt.dynamicdatabase.tasks.core.base.DBRunnable;

/**
 * Class to represent a runnable that is meant to interact with a document.
 */
public class QueryActionTask extends DBRunnable {

    Query query;

    /**
     * Constructor
     *
     * @param store The document's datastore
     */
    public QueryActionTask(Datastore store, Query query) {
        super(store);
        this.query = query;
    }

    public Query getQuery() {
        return query;
    }

    /**
     * Called when the task is ran.
     */
    @Override
    public void run() {

    }
}
