package us.nsakt.dynamicdatabase.tasks.core;

import org.mongodb.morphia.Datastore;
import us.nsakt.dynamicdatabase.documents.Document;
import us.nsakt.dynamicdatabase.tasks.core.base.DBRunnable;

/**
 * Base class to represent a runnable that is fitted with some extra document data.
 */
public class SaveTask extends DBRunnable {
    Document document;

    public SaveTask(Datastore store, Document document) {
        super(store);
        this.document = document;
    }

    public Document getDocument() {
        return document;
    }

    @Override
    public void run() {
        this.getDatastore().save(getDocument());
    }
}
