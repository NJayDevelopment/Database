package us.nsakt.dynamicdatabase.tasks.runners;

import org.mongodb.morphia.Datastore;
import us.nsakt.dynamicdatabase.documents.ClusterDocument;

public class ClusterTask extends Task {

    private ClusterDocument cluster;

    public ClusterTask(Datastore store, ClusterDocument cluster) {
        super(store);
        this.cluster = cluster;
    }

    public ClusterDocument getCluster() {
        return cluster;
    }

    @Override
    public void run() {

    }
}
