package us.nsakt.dynamicdatabase.tasks.core.base;

import org.mongodb.morphia.Datastore;

/**
 * Superclass to represent a runnable that is meant to interact with a document.
 */
public abstract class DBRunnable implements Runnable {

    protected Datastore datastore;

    public DBRunnable(Datastore datastore) {
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
