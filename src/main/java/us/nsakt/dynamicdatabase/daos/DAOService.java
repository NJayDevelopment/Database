package us.nsakt.dynamicdatabase.daos;

import us.nsakt.dynamicdatabase.DynamicDatabasePlugin;
import us.nsakt.dynamicdatabase.documents.ClusterDocument;
import us.nsakt.dynamicdatabase.documents.GroupDocument;
import us.nsakt.dynamicdatabase.documents.PunishmentDocument;
import us.nsakt.dynamicdatabase.documents.ServerDocument;
import us.nsakt.dynamicdatabase.documents.SessionDocument;
import us.nsakt.dynamicdatabase.documents.UserDocument;

/**
 * A simple utility class to easily access DAO instance methods.
 *
 * @author NathanTheBook
 */
public class DAOService {
    private static Clusters clusters;
    private static Groups groups;
    private static Punishments punishments;
    private static Servers servers;
    private static Sessions sessions;
    private static Users users;

    public static Clusters getClusters() {
        return clusters;
    }

    public static Groups getGroups() {
        return groups;
    }

    public static Punishments getPunishments() {
        return punishments;
    }

    public static Servers getServers() {
        return servers;
    }

    public static Sessions getSessions() {
        return sessions;
    }

    public static Users getUsers() {
        return users;
    }

    static {
        clusters = new Clusters(DynamicDatabasePlugin.getInstance().getDatastores().get(ClusterDocument.class));
        groups = new Groups(DynamicDatabasePlugin.getInstance().getDatastores().get(GroupDocument.class));
        punishments = new Punishments(DynamicDatabasePlugin.getInstance().getDatastores().get(PunishmentDocument.class));
        servers = new Servers(DynamicDatabasePlugin.getInstance().getDatastores().get(ServerDocument.class));
        sessions = new Sessions(DynamicDatabasePlugin.getInstance().getDatastores().get(SessionDocument.class));
        users = new Users(DynamicDatabasePlugin.getInstance().getDatastores().get(UserDocument.class));
    }
}
