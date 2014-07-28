package us.nsakt.dynamicdatabase;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import us.nsakt.dynamicdatabase.daos.DAOGetter;
import us.nsakt.dynamicdatabase.documents.ClusterDocument;
import us.nsakt.dynamicdatabase.documents.Document;
import us.nsakt.dynamicdatabase.tasks.core.QueryActionTask;
import us.nsakt.dynamicdatabase.util.LogLevel;
import us.nsakt.dynamicdatabase.util.NsaktException;

import java.util.HashMap;
import java.util.List;

public class ConfigEnforcer {

    public static class ConfigTasks {

        public static HashMap<String, ObjectId> namesWithIds = Maps.newHashMap();

        /**
         * Adds a cluster's objectId to the hashmap of clusters with objectIds
         *
         * @param clusterName Cluster to get and add to list
         */
        public static void stringToCluster(final String clusterName) {
            Datastore datastore = new DAOGetter().getClusters().getDatastore();
            QueryActionTask task = new QueryActionTask(datastore, datastore.createQuery(ClusterDocument.class)) {
                @Override
                public void run() {
                    getQuery().field(ClusterDocument.MongoFields.NAME.fieldName).equal(clusterName);
                    Document document = (Document) getQuery().get();
                    Debug.log(LogLevel.INFO, document.getObjectId().toString());
                    namesWithIds.keySet().removeAll(Lists.newArrayList(clusterName));
                    namesWithIds.put(clusterName, document.getObjectId());
                }
            };
            QueryExecutor.getExecutorService().submit(task);
        }

        /**
         * Loops through the config, and converts all instances of a string representation of a cluster to an actual referencable cluster.
         */
        public static void convertAllNamesToClusters() {
            for (Object t : Config.getAllMatchingSections("clusters")) {
                if (!(t instanceof List)) continue;
                for (String s : (List<String>) t) {
                    stringToCluster(s);
                }
            }
        }
    }

    public static class Documents {
        public static class Clusters {
            public static void ensureEnabled() throws NsaktException {
                if (!Config.Documents.Clusters.enabled) throw new NsaktException();
            }
        }

        public static class Groups {
            public static void ensureEnabled() throws NsaktException {
                if (!Config.Documents.Groups.enabled) throw new NsaktException();
            }
        }

        public static class Punishments {
            public static void ensureEnabled() throws NsaktException {
                if (!Config.Documents.Punishments.enabled) throw new NsaktException();
            }
        }

        public static class Sessions {
            public static void ensureEnabled() throws NsaktException {
                if (!Config.Documents.Sessions.record) throw new NsaktException();
            }
        }

        public static class Users {
            public static void ensureEnabled() throws NsaktException {
                if (!Config.Documents.Users.enabled) throw new NsaktException();
            }
        }
    }

    public static class CrossServer {
        public static void ensureEnabled() throws NsaktException {
            if (!Config.CrossServer.enabled) throw new NsaktException();
        }

        public static class AdminChat {
            public static void ensureEnabled() throws NsaktException {
                if (!Config.CrossServer.AdminChat.enabled) throw new NsaktException();
            }

            public static void canSend() throws NsaktException {
                if (!Config.CrossServer.AdminChat.send) throw new NsaktException();
            }

            public static void canReceive() throws NsaktException {
                if (!Config.CrossServer.AdminChat.receive) throw new NsaktException();
            }

            public static void allowedCluster(ObjectId cluster) throws NsaktException {
                if (!Config.CrossServer.AdminChat.send) throw new NsaktException();
            }
        }

        public static class Punishments {
            public static void ensureEnabled() throws NsaktException {
                if (!Config.CrossServer.Punishments.enabled) throw new NsaktException();
            }
        }
    }
}
