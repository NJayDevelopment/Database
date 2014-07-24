package us.nsakt.dynamicdatabase.tasks.runners;

import org.mongodb.morphia.Datastore;
import us.nsakt.dynamicdatabase.documents.ServerDocument;

/**
 * Class to represent a runnable that is meant to interact with a document.
 */
public class ServerTask extends Task {

    private ServerDocument server;

    /**
     * Constructor
     *
     * @param store  The document's datastore
     * @param server The main document to interact with
     */
    public ServerTask(Datastore store, ServerDocument server) {
        super(store);
        this.server = server;
    }

    /**
     * Get the main document to be interacted with
     *
     * @return the main document
     */
    public ServerDocument getServer() {
        return server;
    }

    /**
     * Called when the task is ran, needs to be overridden.
     */
    @Override
    public void run() {

    }
}
