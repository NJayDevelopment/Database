package net.njay.dynamicdatabase.tasks.core.base;

import org.mongodb.morphia.Datastore;

/**
 * Base class to represent a runnable that is fitted with some extra database data.
 */
public abstract class DBRunnable implements Runnable {
    protected Datastore datastore;

    public DBRunnable(Datastore datastore) {
        this.datastore = datastore;
    }

    public Datastore getDatastore() {
        return datastore;
    }
}
