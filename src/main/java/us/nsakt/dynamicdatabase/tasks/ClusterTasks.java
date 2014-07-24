package us.nsakt.dynamicdatabase.tasks;

import org.bukkit.Bukkit;
import us.nsakt.dynamicdatabase.DynamicDatabasePlugin;
import us.nsakt.dynamicdatabase.daos.Clusters;
import us.nsakt.dynamicdatabase.documents.ClusterDocument;
import us.nsakt.dynamicdatabase.tasks.runners.ClusterTask;
import us.nsakt.dynamicdatabase.util.Visibility;

public class ClusterTasks {

    Clusters clusters = new Clusters(ClusterDocument.class, DynamicDatabasePlugin.getInstance().getDatastores().get(ClusterDocument.class));

    private Clusters getDao() {
        return clusters;
    }

    /**
     * Create the default cluster
     *
     * @return the default cluster
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
