package us.nsakt.dynamicdatabase.tasks.runners;

import org.mongodb.morphia.Datastore;

/**
 * Superclass to represent a runnable that is meant to interact with a document.
 */
public abstract class Task implements Runnable {

    protected Datastore datastore;

    public Task(Datastore datastore) {
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
