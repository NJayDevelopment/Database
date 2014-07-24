package us.nsakt.dynamicdatabase.tasks.runners;

import org.mongodb.morphia.Datastore;

public abstract class Task implements Runnable {

    protected Datastore datastore;

    public Task(Datastore datastore) {
        this.datastore = datastore;
    }

    public Datastore getDatastore() {
        return datastore;
    }
}
