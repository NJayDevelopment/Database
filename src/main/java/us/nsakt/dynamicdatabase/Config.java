package us.nsakt.dynamicdatabase;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.bson.types.ObjectId;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.joda.time.Duration;
import org.mongodb.morphia.Datastore;
import us.nsakt.dynamicdatabase.daos.DAOGetter;
import us.nsakt.dynamicdatabase.documents.ClusterDocument;
import us.nsakt.dynamicdatabase.documents.Document;
import us.nsakt.dynamicdatabase.tasks.core.QueryActionTask;
import us.nsakt.dynamicdatabase.util.LanguageFile;
import us.nsakt.dynamicdatabase.util.config.ConfigAnnotation;
import us.nsakt.dynamicdatabase.util.config.ConfigStructure;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Class to help manage the Bukkit  configuration.
 *
 * @author skipperguy12
 * @author NathanTheBook
 */
public class Config {

    public static HashMap<String, ObjectId> namesWithIds = Maps.newHashMap();
    private static HashMap<LanguageFile.LanguageEnum, LanguageFile> languageHashMap = Maps.newHashMap();

    /**
     * Return a HashMap of languages that are loaded.
     */
    public static HashMap<LanguageFile.LanguageEnum, LanguageFile> getLanguageMap() {
        return languageHashMap;
    }

    /**
     * Get the Bukkit configuration instance.
     */
    public static Configuration getBukkitConfig() {
        DynamicDatabasePlugin plugin = DynamicDatabasePlugin.getInstance();
        FileConfiguration res = plugin.getConfig();

        if (res != null) return res;
        else return new YamlConfiguration();
    }

    /**
     * Save the Bukkit config to disk.
     */
    public static void saveBukkitConfig() {
        DynamicDatabasePlugin.getInstance().saveConfig();
    }

    /**
     * Generic way to get an Object from the config.
     *
     * @param path Path to the object
     * @param <T>  Type of the object to be retrieved.
     * @return The configuration object that resisded at the supplied path
     */
    @SuppressWarnings("unchecked")
    public static <T> T get(String path) {
        return (T) getBukkitConfig().get(path);
    }

    /**
     * Loop through the config, searching for all instances of a key matching the search term and compile a list of objects that were retrieved.
     *
     * @param search key to search for
     * @param <T>    Type of the object to be retrieved.
     * @return a list of objects that were retrieved
     */
    @SuppressWarnings("unchecked")
    public static <T> List<T> getAllMatchingSections(String search) {
        List<T> results = Lists.newArrayList();
        for (String key : getBukkitConfig().getKeys(true)) {
            if (!key.endsWith(search)) continue;
            results.add((T) getBukkitConfig().get(key));
        }
        return results;
    }

    /**
     * Same operation as {@link us.nsakt.dynamicdatabase.Config#get(String)}, just with a default value.
     */
    @SuppressWarnings("unchecked")
    public static <T> T get(String path, Object def) {
        return (T) getBukkitConfig().get(path, def);
    }

    /**
     * Set a key's value in the config.
     *
     * @param path Path of the key
     * @param val  Value to be set
     */
    public static void set(String path, Object val) {
        getBukkitConfig().set(path, val);
    }

    // General Tasks to make working with the config easier later.
    public static class Tasks {
        public static void stringToCluster(final String clusterName) {
            Datastore datastore = new DAOGetter().getClusters().getDatastore();
            QueryActionTask task = new QueryActionTask(datastore, datastore.createQuery(ClusterDocument.class)) {
                @Override
                public void run() {
                    getQuery().field(ClusterDocument.MongoFields.NAME.fieldName).equal(clusterName);
                    Document document = (Document) getQuery().get();
                    us.nsakt.dynamicdatabase.Debug.log(us.nsakt.dynamicdatabase.Debug.LogLevel.INFO, document.getObjectId().toString());
                    namesWithIds.keySet().removeAll(Lists.newArrayList(clusterName));
                    namesWithIds.put(clusterName, document.getObjectId());
                }
            };
            MongoExecutionService.getExecutorService().submit(task);
        }

        public static void convertAllNamesToClusters() {
            for (Object t : getAllMatchingSections("clusters")) {
                if (!(t instanceof List)) continue;
                for (String s : (List<String>) t) {
                    stringToCluster(s);
                }
            }
        }

    }

    @ConfigAnnotation(type = ConfigStructure.SECTION, desc = "Information for Mongo")
    public static class Mongo {
        @ConfigAnnotation(type = ConfigStructure.VARIABLE, desc = "Describes if all Morphia methods are executed asyncly using a fork of Morphia such as @skipperguy12's AsyncMorphia", def = "false")
        public static final boolean usingAsyncMorphia = get("mongo.using-async-morphia", false);

        @ConfigAnnotation(type = ConfigStructure.VARIABLE, desc = "The current Server's ObjectId, must be in database.")
        public static final String serverId = get("mongo.server-id", "nil");

        @ConfigAnnotation(type = ConfigStructure.VARIABLE, desc = "The current Server's cluster.")
        public static final String serverCluster = get("mongo.server-cluster", "all");

        @ConfigAnnotation(type = ConfigStructure.VARIABLE, desc = "The hostname to attempt to connect to", def = "localhost")
        public static final List<String> hostnames = get("mongo.hostnames", Arrays.asList("localhost"));

        @ConfigAnnotation(type = ConfigStructure.VARIABLE, desc = "The port to connect to", def = "27017")
        public static final int port = get("mongo.port", 27017);

        @ConfigAnnotation(type = ConfigStructure.VARIABLE, desc = "The main Mongo database to connect to", def = "nsakt_database")
        public static final String database = get("mongo.database", "nsakt_database");

        @ConfigAnnotation(type = ConfigStructure.SECTION, desc = "Information for Mongo authentication")
        public static class Authentication {
            @ConfigAnnotation(type = ConfigStructure.VARIABLE, desc = "Boolean if Mongo requires authentication to the DB", def = "false")
            public static final boolean useAthentication = get("mongo.authentication.use-authentication", false);

            @ConfigAnnotation(type = ConfigStructure.VARIABLE, desc = "The username to the database", def = "username")
            public static final String username = get("mongo.authentication.username", "username");

            @ConfigAnnotation(type = ConfigStructure.VARIABLE, desc = "The password to the database", def = "password")
            public static final String password = get("mongo.authentication.password", "password");
        }
    }

    @ConfigAnnotation(type = ConfigStructure.SECTION, desc = "Configurable settings on Languages handled by DynamicDatabase")
    public static class Languages {
        @ConfigAnnotation(type = ConfigStructure.VARIABLE, desc = "List of supported languages")
        public static final List<String> supportedLanguages = get("languages.supported-languages", Arrays.asList("en_CA"));
    }

    @ConfigAnnotation(type = ConfigStructure.SECTION, desc = "Configurable debugging options")
    public static class Debug {
        @ConfigAnnotation(type = ConfigStructure.VARIABLE, desc = "The mode of filtering", def = "BLACKLIST")
        public static final String filterMode = get("debug.filterMode", "BLACKLIST");

        @ConfigAnnotation(type = ConfigStructure.VARIABLE, desc = "Determines which channels are allowed to debug", def = "[\"Generic\", \"Morphia\", \"Exception\"]")
        public static final List<String> allowedChannels = get("debug.channels", Arrays.asList());
    }

    @ConfigAnnotation(type = ConfigStructure.SECTION, desc = "Configurable document options")
    public static class Documents {
        @ConfigAnnotation(type = ConfigStructure.SECTION, desc = "Configurable cluster options")
        public static class Clusters {
            @ConfigAnnotation(type = ConfigStructure.VARIABLE, desc = "Boolean if the server should respect a document's cluster. If false, all documents will be called, regardless of cluster", def = "true")
            public static final boolean enabled = get("documents.clusters.enabled", true);
        }

        @ConfigAnnotation(type = ConfigStructure.SECTION, desc = "Configurable group options")
        public static class Groups {
            @ConfigAnnotation(type = ConfigStructure.VARIABLE, desc = "Boolean if groups are enabled", def = "true")
            public static final boolean enabled = get("documents.groups.enabled", true);

            @ConfigAnnotation(type = ConfigStructure.VARIABLE, desc = "Boolean if users of a group with a flair should get the flair", def = "true")
            public static final boolean giveFlair = get("documents.groups.give-flair", true);

            @ConfigAnnotation(type = ConfigStructure.VARIABLE, desc = "Boolean if permissions should be grandfathered in from lower groups", def = "true")
            public static final boolean pullLowerPerms = get("documents.groups.pull-perms-from-lower", true);

            @ConfigAnnotation(type = ConfigStructure.VARIABLE, desc = "List of clusters groups should be queried from", def = "all")
            public static final List<String> clusters = get("documents.groups.clusters", Arrays.asList("all"));

            @ConfigAnnotation(type = ConfigStructure.VARIABLE, desc = "List of groups that should be ignored when giving permissions/flairs", def = "{}")
            public static final List<String> ignoredGroups = get("documents.groups.ignored-groups", Arrays.asList());
        }

        @ConfigAnnotation(type = ConfigStructure.SECTION, desc = "Configurable punishment options")
        public static class Punishments {
            @ConfigAnnotation(type = ConfigStructure.VARIABLE, desc = "Boolean if punishments are enabled", def = "true")
            public static final boolean enabled = get("documents.punishments.enabled", true);

            @ConfigAnnotation(type = ConfigStructure.SECTION, desc = "Configurable punishment type options")
            public static class Types {
                @ConfigAnnotation(type = ConfigStructure.SECTION, desc = "Configurable punishment type options (Warns)")
                public static class Warns {
                    @ConfigAnnotation(type = ConfigStructure.VARIABLE, desc = "Boolean if warns are enabled", def = "true")
                    public static final boolean enabled = get("documents.punishments.types.warns.enabled", true);

                    @ConfigAnnotation(type = ConfigStructure.VARIABLE, desc = "Boolean if warns should be broadcast (locally)", def = "true")
                    public static final boolean globalBroadcast = get("documents.punishments.types.warns.global-broadcast", true);
                }

                @ConfigAnnotation(type = ConfigStructure.SECTION, desc = "Configurable punishment type options (Kicks)")
                public static class Kicks {
                    @ConfigAnnotation(type = ConfigStructure.VARIABLE, desc = "Boolean if kicks are enabled", def = "true")
                    public static final boolean enabled = get("documents.punishments.types.kicks.enabled", true);

                    @ConfigAnnotation(type = ConfigStructure.VARIABLE, desc = "Boolean if kicks should be broadcast (locally)", def = "true")
                    public static final boolean globalBroadcast = get("documents.punishments.types.kicks.global-broadcast", true);
                }

                @ConfigAnnotation(type = ConfigStructure.SECTION, desc = "Configurable punishment type options (Bans)")
                public static class Bans {
                    @ConfigAnnotation(type = ConfigStructure.VARIABLE, desc = "Boolean if bans are enabled", def = "true")
                    public static final boolean enabled = get("documents.punishments.types.bans.enabled", true);

                    @ConfigAnnotation(type = ConfigStructure.VARIABLE, desc = "Boolean if bans should be broadcast (locally)", def = "true")
                    public static final boolean globalBroadcast = get("documents.punishments.types.bans.global-broadcast", true);

                    @ConfigAnnotation(type = ConfigStructure.VARIABLE, desc = "The default ban time", def = "10d")
                    public static final Duration defBanTime = Duration.parse((String) get("documents.punishments.types.bans.default-ban-time", "10d"));

                    @ConfigAnnotation(type = ConfigStructure.VARIABLE, desc = "Boolean if vans should be checked on login. If false, BANNED USERS CAN JOIN", def = "true")
                    public static final boolean checkOnLogin = get("documents.punishments.types.bans.check-on-login", true);
                }
            }

            @ConfigAnnotation(type = ConfigStructure.SECTION, desc = "Configurable appeal options")
            public static class Appeals {
                @ConfigAnnotation(type = ConfigStructure.VARIABLE, desc = "Boolean if appeals are enabled (For ban messages and appeal alerts)", def = "true")
                public static final boolean enabled = get("documents.punishments.appeals.enabled", true);

                @ConfigAnnotation(type = ConfigStructure.VARIABLE, desc = "The URL users should be linked to if they need to access appeals", def = "raino.me")
                public static final String url = get("documents.punishments.appeals.url", "raino.me");
            }
        }

        @ConfigAnnotation(type = ConfigStructure.SECTION, desc = "Configurable session options")
        public static class Sessions {
            @ConfigAnnotation(type = ConfigStructure.VARIABLE, desc = "Boolean if sessions should be recorded", def = "true")
            public static final boolean record = get("documents.sessions.record", true);
        }

        @ConfigAnnotation(type = ConfigStructure.SECTION, desc = "Configurable user options")
        public static class Users {
            @ConfigAnnotation(type = ConfigStructure.VARIABLE, desc = "Boolean if users are enabled", def = "true")
            public static final boolean enabled = get("documents.users.enabled", true);

            @ConfigAnnotation(type = ConfigStructure.VARIABLE, desc = "Boolean if new users should be recorded", def = "true")
            public static final boolean recordnNew = get("documents.users.record-new", true);

            @ConfigAnnotation(type = ConfigStructure.VARIABLE, desc = "Boolean if existing users should be updated on login", def = "true")
            public static final boolean updateExisiting = get("documents.users.update-existing", true);

            @ConfigAnnotation(type = ConfigStructure.VARIABLE, desc = "Boolean if users' IPs should be recorded", def = "true")
            public static final boolean logIp = get("documents.users.log-ip", true);
        }
    }

    @ConfigAnnotation(type = ConfigStructure.SECTION, desc = "Configurable cross server options")
    public static class CrossServer {
        @ConfigAnnotation(type = ConfigStructure.VARIABLE, desc = "The ip address of the cross-server server", def = "localhost")
        public static final String ip = get("cross-server.ip", "localhost");

        @ConfigAnnotation(type = ConfigStructure.VARIABLE, desc = "The port of the cross-server server", def = "87652")
        public static final int port = get("cross-server.port", 1330);

        @ConfigAnnotation(type = ConfigStructure.VARIABLE, desc = "Boolean if cross server actions are enabled", def = "true")
        public static final boolean enabled = get("cross-server.enabled", true);

        @ConfigAnnotation(type = ConfigStructure.SECTION, desc = "Configurable admin chat options")
        public static class AdminChat {
            @ConfigAnnotation(type = ConfigStructure.VARIABLE, desc = "Boolean if admin chat is enabled", def = "true")
            public static final boolean enabled = get("cross-server.admin-chat.enabled", true);

            @ConfigAnnotation(type = ConfigStructure.VARIABLE, desc = "Boolean if admin chat should be sent", def = "true")
            public static final boolean send = get("cross-server.admin-chat.send", true);

            @ConfigAnnotation(type = ConfigStructure.VARIABLE, desc = "Boolean if admin chat should be received", def = "true")
            public static final boolean receive = get("cross-server.admin-chat.receive", true);

            @ConfigAnnotation(type = ConfigStructure.VARIABLE, desc = "List of clusters that should be pulled from when displaying admin chat", def = "all")
            public static final List<String> clusters = get("cross-server.admin-chat.clusters", Arrays.asList("all"));
        }

        @ConfigAnnotation(type = ConfigStructure.SECTION, desc = "Configurable punishment options")
        public static class Punishments {
            @ConfigAnnotation(type = ConfigStructure.VARIABLE, desc = "Boolean if punishments are enabled", def = "true")
            public static final boolean enabled = get("cross-server.punishments.enabled", true);

            @ConfigAnnotation(type = ConfigStructure.VARIABLE, desc = "List of clusters that should be pulled from when displaying punishments", def = "all")
            public static final List<String> clusters = get("cross-server.punishments.clusters", Arrays.asList("all"));

            @ConfigAnnotation(type = ConfigStructure.SECTION, desc = "Configurable broadcast options")
            public static class Broadcast {
                @ConfigAnnotation(type = ConfigStructure.VARIABLE, desc = "Boolean if warns should be broadcast", def = "true")
                public static final boolean warns = get("cross-server.punishments.broadcast.warns", true);

                @ConfigAnnotation(type = ConfigStructure.VARIABLE, desc = "Boolean if kicks should be broadcast", def = "true")
                public static final boolean kicks = get("cross-server.punishments.broadcast.kicks", true);

                @ConfigAnnotation(type = ConfigStructure.VARIABLE, desc = "Boolean if bans should be broadcast", def = "true")
                public static final boolean bans = get("cross-server.punishments.broadcast.bans", true);
            }

            @ConfigAnnotation(type = ConfigStructure.SECTION, desc = "Configurable send options")
            public static class Send {
                @ConfigAnnotation(type = ConfigStructure.VARIABLE, desc = "Boolean if warns should be sent", def = "true")
                public static final boolean warns = get("cross-server.punishments.send.warns", true);

                @ConfigAnnotation(type = ConfigStructure.VARIABLE, desc = "Boolean if kicks should be sent", def = "true")
                public static final boolean kicks = get("cross-server.punishments.send.kicks", true);

                @ConfigAnnotation(type = ConfigStructure.VARIABLE, desc = "Boolean if bans should be sent", def = "true")
                public static final boolean bans = get("cross-server.punishments.send.bans", true);
            }

            @ConfigAnnotation(type = ConfigStructure.SECTION, desc = "Configurable receive options")
            public static class Receive {
                @ConfigAnnotation(type = ConfigStructure.VARIABLE, desc = "Boolean if warns should be received", def = "true")
                public static final boolean warns = get("cross-server.punishments.receive.warns", true);

                @ConfigAnnotation(type = ConfigStructure.VARIABLE, desc = "Boolean if kicks should be received", def = "true")
                public static final boolean kicks = get("cross-server.punishments.receive.kicks", true);

                @ConfigAnnotation(type = ConfigStructure.VARIABLE, desc = "Boolean if bans should be received", def = "true")
                public static final boolean bans = get("cross-server.punishments.receive.bans", true);
            }
        }
    }
}
