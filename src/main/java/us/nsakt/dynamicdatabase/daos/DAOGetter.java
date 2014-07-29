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
public class DAOGetter {
    private Clusters clusters = new Clusters(DynamicDatabasePlugin.getInstance().getDatastores().get(ClusterDocument.class));
    private Groups groups = new Groups(DynamicDatabasePlugin.getInstance().getDatastores().get(GroupDocument.class));
    private Punishments punishments = new Punishments(DynamicDatabasePlugin.getInstance().getDatastores().get(PunishmentDocument.class));
    private Servers servers = new Servers(DynamicDatabasePlugin.getInstance().getDatastores().get(ServerDocument.class));
    private Sessions sessions = new Sessions(DynamicDatabasePlugin.getInstance().getDatastores().get(SessionDocument.class));
    private Users users = new Users(DynamicDatabasePlugin.getInstance().getDatastores().get(UserDocument.class));

    /**
     * Class needs to be initialised before and DAO getter methods can be accessed.
     */
    public DAOGetter() {
    }

    public Clusters getClusters() {
        return this.clusters;
    }

    public Groups getGroups() {
        return this.groups;
    }

    public Punishments getPunishments() {
        return this.punishments;
    }

    public Servers getServers() {
        return this.servers;
    }

    public Sessions getSessions() {
        return this.sessions;
    }

    public Users getUsers() {
        return this.users;
    }
}
