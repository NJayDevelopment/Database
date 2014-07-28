package us.nsakt.dynamicdatabase.tasks;

import us.nsakt.dynamicdatabase.ConfigEnforcer;
import us.nsakt.dynamicdatabase.QueryExecutor;
import us.nsakt.dynamicdatabase.daos.Clusters;
import us.nsakt.dynamicdatabase.daos.DAOGetter;
import us.nsakt.dynamicdatabase.documents.ClusterDocument;
import us.nsakt.dynamicdatabase.tasks.core.ResultedSaveTask;
import us.nsakt.dynamicdatabase.util.NsaktException;
import us.nsakt.dynamicdatabase.util.Visibility;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Different tasks for working with clusters.
 */
public class ClusterTasks {

    /**
     * Get the document's relative data access object.
     *
     * @return the document's relative data access object.
     */
    private static Clusters getDao() {
        return new DAOGetter().getClusters();
    }

    public static Future<ClusterDocument> createDefaultAllCluster() throws InterruptedException, ExecutionException {
        try {
            ConfigEnforcer.Documents.Clusters.ensureEnabled();
        } catch (NsaktException e) {
            // silence
        }
        ResultedSaveTask task = new ResultedSaveTask(getDao().getDatastore(), new ClusterDocument()) {
            @Override
            public ClusterDocument call() {
                ClusterDocument cluster = (ClusterDocument) getDocument();
                cluster.setName("all");
                cluster.setVisibility(Visibility.PUBLIC);
                getDao().save(cluster);
                return cluster;
            }
        };
        return QueryExecutor.getExecutorService().submit(task);
    }
}
