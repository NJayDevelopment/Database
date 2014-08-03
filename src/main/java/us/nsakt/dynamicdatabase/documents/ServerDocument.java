package us.nsakt.dynamicdatabase.documents;

import org.bson.types.ObjectId;
import org.bukkit.entity.Player;
import org.joda.time.Duration;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Property;
import us.nsakt.dynamicdatabase.daos.DAOGetter;
import us.nsakt.dynamicdatabase.util.Visibility;

import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Class to represent a "server" document in the database.
 * DOCUMENT DESCRIPTION: This document is meant to represent a Bukkit server and stores general information about the server's statistics.
 *
 * @author NathanTheBook
 */
@Entity("dndb_servers")
public class ServerDocument extends Document {
    private String name;
    private String address;
    private Visibility visibility;
    @Property("internal_name")
    private String internalName;
    @Property("internal_address")
    private String internalAddress;
    private ObjectId cluster;
    private int port;
    @Property("internal_port")
    private int internalPort;
    @Property("max_players")
    private int maxPlayers;
    private Date start;
    @Property("online_players")
    private List<UUID> onlinePlayers;
    @Property("online_staff")
    private List<UUID> onlineStaff;
    private boolean online;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Visibility getVisibility() {
        return visibility;
    }

    public void setVisibility(Visibility visibility) {
        this.visibility = visibility;
    }

    public String getInternalName() {
        return internalName;
    }

    public void setInternalName(String internalName) {
        this.internalName = internalName;
    }

    public String getInternalAddress() {
        return internalAddress;
    }

    public void setInternalAddress(String internalAddress) {
        this.internalAddress = internalAddress;
    }

    public ClusterDocument getCluster() {
        return new DAOGetter().getClusters().findOne(ClusterDocument.MongoFields.id.fieldName, cluster);
    }

    public void setCluster(ClusterDocument cluster) {
        this.cluster = cluster.getObjectId();
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getInternalPort() {
        return internalPort;
    }

    public void setInternalPort(int internalPort) {
        this.internalPort = internalPort;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public List<UUID> getOnlinePlayers() {
        return onlinePlayers;
    }

    public void setOnlinePlayers(List<UUID> onlinePlayers) {
        this.onlinePlayers = onlinePlayers;
    }

    public List<UUID> getOnlineStaff() {
        return onlineStaff;
    }

    public void setOnlineStaff(List<UUID> onlineStaff) {
        this.onlineStaff = onlineStaff;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    @Override
    public String toString() {
        return "ServerDocument{" +
                "name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", visibility=" + visibility +
                ", internalName='" + internalName + '\'' +
                ", internalAddress='" + internalAddress + '\'' +
                ", cluster=" + cluster +
                ", port=" + port +
                ", internalPort=" + internalPort +
                ", maxPlayers=" + maxPlayers +
                ", start=" + start +
                ", onlinePlayers=" + onlinePlayers +
                ", onlineStaff=" + onlineStaff +
                ", online=" + online +
                '}';
    }

    /**
     * Check if a player can see the current server, based on various permission checks.
     *
     * @param player          Player to perform the checks on.
     * @param providingPlugin Plugin that is the parent of the permission, for permission checks.
     * @return If the player can see the server.
     */
    public boolean canSee(Player player, String providingPlugin) {
        return (
                player.hasPermission(providingPlugin.toLowerCase() + ".servers.see.all") ||
                        player.hasPermission(providingPlugin.toLowerCase() + ".servers.types.see.all") ||
                        player.hasPermission(providingPlugin.toLowerCase() + ".servers.types.see." + this.getVisibility().dbName) ||
                        player.hasPermission(providingPlugin.toLowerCase() + ".servers.see." + this.getName().toLowerCase()) ||
                        player.hasPermission(providingPlugin.toLowerCase() + ".servers.clusters.see." + this.getCluster().getName().toLowerCase()) ||
                        player.hasPermission(providingPlugin.toLowerCase() + ".servers.clusters.see.any"
                        )
        );
    }

    /**
     * Check if a player can join the current server, based on various permission checks.
     *
     * @param player          Player to perform the checks on.
     * @param providingPlugin Plugin that is the parent of the permission, for permission checks.
     * @return If the player can join the server.
     */
    public boolean canJoin(Player player, String providingPlugin) {
        return (
                player.hasPermission(providingPlugin.toLowerCase() + ".servers.join.all") ||
                        player.hasPermission(providingPlugin.toLowerCase() + ".servers.types.join.all") ||
                        player.hasPermission(providingPlugin.toLowerCase() + ".servers.types.join." + this.getVisibility().dbName) ||
                        player.hasPermission(providingPlugin.toLowerCase() + ".servers.join." + this.getName().toLowerCase()) ||
                        player.hasPermission(providingPlugin.toLowerCase() + ".servers.clusters.join." + this.getCluster().getName().toLowerCase()) ||
                        player.hasPermission(providingPlugin.toLowerCase() + ".servers.clusters.join.any"
                        )
        );
    }

    /**
     * An enum representation of all fields in the class for reference in Mongo operations.
     */
    public enum MongoFields {
        id("_id"),
        NAME("name"),
        ADDRESS("address"),
        VISIBILITY("visibility"),
        INTERNAL_NAME("internal_name"),
        INTERNAL_ADDRESS("internal_address"),
        CLUSTER("cluster"),
        PORT("port"),
        INTERNAL_PORT("internal_port"),
        START("start"),
        ONLINE_PLAYERS("online_players"),
        ONLINE_STAFF("online_staff"),
        ONLINE("online");

        public String fieldName;

        MongoFields(String fieldName) {
            this.fieldName = fieldName;
        }
    }
}
