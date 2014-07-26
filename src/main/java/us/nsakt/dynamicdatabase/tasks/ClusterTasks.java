package us.nsakt.dynamicdatabase.tasks;

import org.bukkit.Bukkit;
import us.nsakt.dynamicdatabase.DynamicDatabasePlugin;
import us.nsakt.dynamicdatabase.daos.Clusters;
import us.nsakt.dynamicdatabase.daos.DAOGetter;
import us.nsakt.dynamicdatabase.documents.ClusterDocument;
import us.nsakt.dynamicdatabase.tasks.runners.ClusterTask;
import us.nsakt.dynamicdatabase.util.Visibility;

/**
 * Different tasks for working with clusters.
 */
public class ClusterTasks {

    /**
     * Get the document's relative data access object.
     *
     * @return the document's relative data access object.
     */
    private Clusters getDao() {
        return new DAOGetter().getClusters();
    }

    /**
     * Create the default clusters
     *
     * @return the default clusters
     */
    public ClusterDocument createDefaultAllCluster() {
        ClusterDocument clusterDocument = new ClusterDocument();
        ClusterTask task = new ClusterTask(getDao().getDatastore(), clusterDocument) {
            @Override
            public void run() {
                getCluster().setName("all");
                getCluster().setVisibility(Visibility.PUBLIC);
                getDao().save(getCluster());
            }
        };
        Bukkit.getScheduler().runTaskAsynchronously(DynamicDatabasePlugin.getInstance(), task);
        return clusterDocument;
    }
}
