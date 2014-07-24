package us.nsakt.dynamicdatabase.tasks.runners;

import org.mongodb.morphia.Datastore;
import us.nsakt.dynamicdatabase.documents.ClusterDocument;

/**
 * Class to represent a runnable that is meant to interact with a document.
 */
public class ClusterTask extends Task {

    private ClusterDocument cluster;

    /**
     * Constructor
     *
     * @param store   The document's datastore
     * @param cluster The main document to interact with
     */
    public ClusterTask(Datastore store, ClusterDocument cluster) {
        super(store);
        this.cluster = cluster;
    }

    /**
     * Get the main document to be interacted with
     *
     * @return the main document
     */
    public ClusterDocument getCluster() {
        return cluster;
    }

    /**
     * Called when the task is ran, needs to be overridden.
     */
    @Override
    public void run() {

    }
}
