package us.nsakt.dynamicdatabase.tasks.core.base;

import org.mongodb.morphia.Datastore;

import java.util.concurrent.Callable;

/**
 * Superclass to represent a runnable that is meant to interact with a document.
 */
public abstract class DBCallable implements Callable {

    protected Datastore datastore;

    public DBCallable(Datastore datastore) {
        this.datastore = datastore;
    }

    /**
     * Get the datastore
     *
     * @return the datastore
     */
    public Datastore getDatastore() {
        return datastore;
    }
}
