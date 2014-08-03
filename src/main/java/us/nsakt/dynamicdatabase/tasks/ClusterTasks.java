package us.nsakt.dynamicdatabase.tasks;

import us.nsakt.dynamicdatabase.ConfigEnforcer;
import us.nsakt.dynamicdatabase.MongoExecutionService;
import us.nsakt.dynamicdatabase.daos.Clusters;
import us.nsakt.dynamicdatabase.daos.DAOGetter;
import us.nsakt.dynamicdatabase.documents.ClusterDocument;
import us.nsakt.dynamicdatabase.tasks.core.SaveTask;
import us.nsakt.dynamicdatabase.util.NsaktException;
import us.nsakt.dynamicdatabase.util.Visibility;

/**
 * Basic Utility class to perform action related to cluster documents.
 *
 * @author NathanTheBook
 */
public class ClusterTasks {
    private static Clusters getDao() {
        return new DAOGetter().getClusters();
    }

    /**
     * Add the default cluster to the database.
     * NOTE: Callers need to check if there already is a default cluster.
     */
    public static void createDefaultAllCluster() {
        try {
            ConfigEnforcer.Documents.Clusters.ensureEnabled();
        } catch (NsaktException e) {
        }
        SaveTask task = new SaveTask(getDao().getDatastore(), new ClusterDocument()) {
            @Override
            public void run() {
                ClusterDocument cluster = (ClusterDocument) getDocument();
                cluster.setName("all");
                cluster.setVisibility(Visibility.PUBLIC);
                getDao().save(cluster);
            }
        };
        MongoExecutionService.getExecutorService().execute(task);
    }
}
