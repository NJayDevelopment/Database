package net.njay.dynamicdatabase.tasks.core;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;
import net.njay.dynamicdatabase.tasks.core.base.DBRunnable;

/**
 * Base class to represent a runnable that is fitted with some extra database query data.
 */
public class QueryActionTask<T> extends DBRunnable {
    Query<T> query;

    public QueryActionTask(Datastore store, Query<T> query) {
        super(store);
        this.query = query;
    }

    public Query<T> getQuery() {
        return query;
    }

    @Override
    public void run() {
    }
}
