package net.njay.dynamicdatabase.tasks.core;

import org.mongodb.morphia.Datastore;
import net.njay.dynamicdatabase.documents.Document;
import net.njay.dynamicdatabase.tasks.core.base.DBRunnable;

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
