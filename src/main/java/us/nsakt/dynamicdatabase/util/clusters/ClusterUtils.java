package us.nsakt.dynamicdatabase.util.clusters;

import org.mongodb.morphia.Datastore;
import us.nsakt.dynamicdatabase.DynamicDatabasePlugin;
import us.nsakt.dynamicdatabase.documents.Cluster;
import us.nsakt.dynamicdatabase.documents.Document;
import us.nsakt.dynamicdatabase.util.Visibility;

public class ClusterUtils {

    private static Datastore clustersDataStore = DynamicDatabasePlugin.getInstance().getDatastores().get(Cluster.class);

    /**
     * Check if the documents are in the same cluster
     *
     * @param doc1 Document 1 to check
     * @param doc2 Document to check against
     * @return If the documents are in the same cluster
     */
    public static boolean isSameCluster(Document doc1, Document doc2) {
        return doc1.getCluster().equals(doc2.getCluster());
    }

    /**
     * Check if the document is in the same cluster as the current server
     *
     * @param document Document to check
     * @return If the document is in the same cluster as the current server
     */
    public static boolean isSameAsServer(Document document) {
        return isSameCluster(document, DynamicDatabasePlugin.getInstance().getCurrentServer().getCluster());
    }

    /**
     * Create the default cluster
     *
     * @return the default cluster
     */
    public static Cluster createDefaultAllCkuster() {
        Cluster cluster = new Cluster();
        cluster.setName("all");
        cluster.setVisibility(Visibility.PUBLIC);
        clustersDataStore.save(cluster);
        return cluster;
    }
}
