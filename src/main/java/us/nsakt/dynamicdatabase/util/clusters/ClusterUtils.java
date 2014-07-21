package us.nsakt.dynamicdatabase.util.clusters;

import us.nsakt.dynamicdatabase.DynamicDatabasePlugin;
import us.nsakt.dynamicdatabase.documents.Document;

public class ClusterUtils {

    /**
     * Check if the documents are in the same cluster
     *
     * @param doc1 Document 1 to check
     * @param doc2 Document to check against
     * @return If the documents are in the same cluster
     */
    public boolean isSameCluster(Document doc1, Document doc2) {
        return doc1.getCluster().equals(doc2.getCluster());
    }

    /**
     * Check if the document is in the same cluster as the current server
     *
     * @param document Document to check
     * @return If the document is in the same cluster as the current server
     */
    public boolean isSameAsServer(Document document) {
        return isSameCluster(document, DynamicDatabasePlugin.getInstance().getCurrentServer().getCluster());
    }
}
