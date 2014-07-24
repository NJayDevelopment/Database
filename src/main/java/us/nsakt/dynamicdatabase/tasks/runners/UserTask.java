package us.nsakt.dynamicdatabase.tasks.runners;

import org.mongodb.morphia.Datastore;
import us.nsakt.dynamicdatabase.documents.UserDocument;

/**
 * Class to represent a runnable that is meant to interact with a document.
 */
public class UserTask extends Task {

    private UserDocument user;

    /**
     * Constructor
     *
     * @param store The document's datastore
     * @param user  The main document to interact with
     */
    public UserTask(Datastore store, UserDocument user) {
        super(store);
        this.user = user;
    }

    /**
     * Get the main document to be interacted with
     *
     * @return the main document
     */
    public UserDocument getUser() {
        return user;
    }

    /**
     * Called when the task is ran, needs to be overridden.
     */
    @Override
    public void run() {

    }
}
