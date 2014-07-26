package us.nsakt.dynamicdatabase.tasks.core;

import org.mongodb.morphia.Datastore;
import us.nsakt.dynamicdatabase.documents.Document;
import us.nsakt.dynamicdatabase.tasks.core.base.DBCallable;

/**
 * Class to represent a runnable that is meant to get a document.
 */
public class ResultedSaveTask extends DBCallable {

    private Document document;

    /**
     * Constructor
     *
     * @param store    The document's datastore
     * @param document The main document to interact with
     */
    public ResultedSaveTask(Datastore store, Document document) {
        super(store);
        this.document = document;
    }

    /**
     * Get the main document to be interacted with
     *
     * @return the main document
     */
    public Document getDocument() {
        return document;
    }

    /**
     * Called when the task is ran, needs to be overridden.
     */
    @Override
    public Document call() {
        return this.getDocument();
    }
}
