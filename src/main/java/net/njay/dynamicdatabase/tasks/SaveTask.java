package net.njay.dynamicdatabase.tasks;

import net.njay.dynamicdatabase.Document;
import net.njay.dynamicdatabase.tasks.core.DBRunnable;
import org.mongodb.morphia.Datastore;

/**
 * Base class to represent a runnable that is fitted with some extra document data.
 *
 * @author Austin Mayes
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
