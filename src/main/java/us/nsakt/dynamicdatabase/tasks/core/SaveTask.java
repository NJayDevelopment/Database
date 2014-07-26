package us.nsakt.dynamicdatabase.tasks.core;

import org.mongodb.morphia.Datastore;
import us.nsakt.dynamicdatabase.documents.Document;
import us.nsakt.dynamicdatabase.tasks.core.base.DBRunnable;

/**
 * Class to represent a runnable that is meant to interact with a document.
 */
public class SaveTask extends DBRunnable {

    Document document;

    /**
     * Constructor
     *
     * @param store The document's datastore
     */
    public SaveTask(Datastore store, Document document) {
        super(store);
        this.document = document;
    }

    public Document getDocument() {
        return document;
    }

    /**
     * Called when the task is ran.
     */
    @Override
    public void run() {
        this.getDatastore().save(getDocument());
    }
}
