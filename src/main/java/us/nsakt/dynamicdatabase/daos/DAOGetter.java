package us.nsakt.dynamicdatabase.daos;

import us.nsakt.dynamicdatabase.DynamicDatabasePlugin;
import us.nsakt.dynamicdatabase.documents.ClusterDocument;
import us.nsakt.dynamicdatabase.documents.GroupDocument;
import us.nsakt.dynamicdatabase.documents.PunishmentDocument;
import us.nsakt.dynamicdatabase.documents.ServerDocument;
import us.nsakt.dynamicdatabase.documents.SessionDocument;
import us.nsakt.dynamicdatabase.documents.UserDocument;

public class DAOGetter {

    private Clusters clusters = new Clusters(ClusterDocument.class, DynamicDatabasePlugin.getInstance().getDatastores().get(ClusterDocument.class));

    private Groups groups = new Groups(GroupDocument.class, DynamicDatabasePlugin.getInstance().getDatastores().get(GroupDocument.class));

    private Punishments punishments = new Punishments(PunishmentDocument.class, DynamicDatabasePlugin.getInstance().getDatastores().get(PunishmentDocument.class));

    private Servers servers = new Servers(ServerDocument.class, DynamicDatabasePlugin.getInstance().getDatastores().get(ServerDocument.class));

    private Sessions sessions = new Sessions(SessionDocument.class, DynamicDatabasePlugin.getInstance().getDatastores().get(SessionDocument.class));

    private Users users = new Users(UserDocument.class, DynamicDatabasePlugin.getInstance().getDatastores().get(UserDocument.class));

    public DAOGetter() {
        // Have to have an instance so these don't hav to be static :)
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
