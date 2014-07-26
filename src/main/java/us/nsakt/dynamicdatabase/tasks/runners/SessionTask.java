package us.nsakt.dynamicdatabase.tasks.runners;

import org.mongodb.morphia.Datastore;
import us.nsakt.dynamicdatabase.documents.SessionDocument;

/**
 * Class to represent a runnable that is meant to interact with a document.
 */
public class SessionTask extends Task {

    private SessionDocument session;

    /**
     * Constructor
     *
     * @param store   The document's datastore
     * @param session The main document to interact with
     */
    public SessionTask(Datastore store, SessionDocument session) {
        super(store);
        this.session = session;
    }

    /**
     * Get the main document to be interacted with
     *
     * @return the main document
     */
    public SessionDocument getSession() {
        return session;
    }

    /**
     * Called when the task is ran, needs to be overridden.
     */
    @Override
    public void run() {

    }
}
