package net.njay.dynamicdatabase.tasks.core;

import org.mongodb.morphia.Datastore;

/**
 * Base class to represent a runnable that is fitted with some extra database data.
 *
 * @author Austin Mayes
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
