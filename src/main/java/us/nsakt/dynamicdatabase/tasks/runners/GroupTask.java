package us.nsakt.dynamicdatabase.tasks.runners;

import org.mongodb.morphia.Datastore;
import us.nsakt.dynamicdatabase.documents.GroupDocument;

/**
 * Class to represent a runnable that is meant to interact with a document.
 */
public class GroupTask extends Task {

    private GroupDocument group;

    /**
     * Constructor
     *
     * @param store         The document's datastore
     * @param groupDocument The main document to interact with
     */
    public GroupTask(Datastore store, GroupDocument groupDocument) {
        super(store);
        this.group = groupDocument;
    }

    /**
     * Get the main document to be interacted with
     *
     * @return the main document
     */
    public GroupDocument getGroup() {
        return group;
    }

    /**
     * Called when the task is ran, needs to be overridden.
     */
    @Override
    public void run() {

    }
}
