package us.nsakt.dynamicdatabase.tasks.core;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;
import us.nsakt.dynamicdatabase.tasks.core.base.DBRunnable;

/**
 * Base class to represent a runnable that is fitted with some extra database query data.
 */
public class QueryActionTask extends DBRunnable {
    Query query;

    public QueryActionTask(Datastore store, Query query) {
        super(store);
        this.query = query;
    }

    public Query getQuery() {
        return query;
    }

    @Override
    public void run() {
    }
}
