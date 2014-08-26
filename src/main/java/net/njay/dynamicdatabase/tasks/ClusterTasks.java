package net.njay.dynamicdatabase.tasks;

import net.njay.dynamicdatabase.ConfigEnforcer;
import net.njay.dynamicdatabase.MongoExecutionService;
import net.njay.dynamicdatabase.daos.Clusters;
import net.njay.dynamicdatabase.daos.DAOService;
import net.njay.dynamicdatabase.documents.ClusterDocument;
import net.njay.dynamicdatabase.tasks.core.SaveTask;
import net.njay.dynamicdatabase.util.NsaktException;
import net.njay.dynamicdatabase.util.Visibility;

/**
 * Basic Utility class to perform action related to cluster documents.
 *
 * @author NathanTheBook
 */
public class ClusterTasks {
    private static Clusters getDao() {
        return DAOService.getClusters();
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
