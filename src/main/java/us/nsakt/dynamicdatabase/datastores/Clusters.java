package us.nsakt.dynamicdatabase.datastores;

import org.mongodb.morphia.Datastore;
import us.nsakt.dynamicdatabase.DynamicDatabasePlugin;
import us.nsakt.dynamicdatabase.documents.ClusterDocument;
import us.nsakt.dynamicdatabase.documents.Document;
import us.nsakt.dynamicdatabase.util.Visibility;

public class Clusters {

    private static Datastore datastore = DynamicDatabasePlugin.getInstance().getDatastores().get(ClusterDocument.class);

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
        return isSameCluster(document, DynamicDatabasePlugin.getInstance().getCurrentServerDocument().getCluster());
    }

    /**
     * Create the default cluster
     *
     * @return the default cluster
     */
    public static ClusterDocument createDefaultAllCkuster() {
        ClusterDocument clusterDocument = new ClusterDocument();
        clusterDocument.setName("all");
        clusterDocument.setVisibility(Visibility.PUBLIC);
        datastore.save(clusterDocument);
        return clusterDocument;
    }

    public static Datastore getDatastore() {
        return datastore;
    }
}
