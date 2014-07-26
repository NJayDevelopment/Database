package us.nsakt.dynamicdatabase.tasks.core;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;
import us.nsakt.dynamicdatabase.documents.Document;
import us.nsakt.dynamicdatabase.tasks.core.base.DBCallable;

/**
 * Class to represent a runnable that is meant to get a document.
 */
public class QueryTask extends DBCallable {

    private Query query;
    private Document result;

    /**
     * Constructor
     *
     * @param store The document's datastore
     * @param query The main query to interact with
     */
    public QueryTask(Datastore store, Query query) {
        super(store);
        this.query = query;
    }

    public Document getResult() {
        return result;
    }

    public void setResult(Document result) {
        this.result = result;
    }

    /**
     * Get the main document to be interacted with
     *
     * @return the main document
     */
    public Query getQuery() {
        return query;
    }

    /**
     * Called when the task is ran, needs to be overridden.
     */
    @Override
    public Query call() {
        return this.getQuery();
    }
}
