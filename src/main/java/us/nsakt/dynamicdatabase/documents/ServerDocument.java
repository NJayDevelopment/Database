package us.nsakt.dynamicdatabase.documents;

import org.bukkit.entity.Player;
import org.joda.time.Duration;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Property;
import org.mongodb.morphia.annotations.Reference;
import us.nsakt.dynamicdatabase.util.Visibility;

import java.util.List;
import java.util.UUID;

/**
 * Class to represent a "server" document in the database.
 * DOCUMENT DESCRIPTION: This document is meant to represent a Bukkit server and stores general information about the server's statistics.
 *
 * @author NathanTheBook
 */
@Entity("servers")
public class ServerDocument extends Document {
    private String name;
    private String address;
    private Visibility visibility;
    @Property("internal_name")
    private String internalName;
    @Property("internal_address")
    private String internalAddress;
    @Reference
    private ClusterDocument cluster;
    private int port;
    @Property("internal_port")
    private int internalPort;
    @Property("max_players")
    private int maxPlayers;
    private Duration uptime;
    @Property("online_players")
    private List<UUID> onlinePlayers;
    private boolean online;

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

    public int getInternalPort() {
        return internalPort;
    }

    public void setInternalPort(int internalPort) {
        this.internalPort = internalPort;
    }

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

    @Override
    public ClusterDocument getCluster() {
        return cluster;
    }

    public void setCluster(ClusterDocument cluster) {
        this.cluster = cluster;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public Duration getUptime() {
        return uptime;
    }

    public void setUptime(Duration uptime) {
        this.uptime = uptime;
    }

    public List<UUID> getOnlinePlayers() {
        return onlinePlayers;
    }

    public void setOnlinePlayers(List<UUID> onlinePlayers) {
        this.onlinePlayers = onlinePlayers;
    }

    @Override
    public String toString() {
        return "Server{" +
                "name='" + getName() + '\'' +
                ", address='" + getAddress() + '\'' +
                ", visibility='" + getVisibility() + '\'' +
                ", cluster=" + getCluster() +
                ", port=" + getPort() +
                ", maxPlayers=" + getMaxPlayers() +
                ", uptime=" + getUptime() +
                ", onlinePlayers=" + getOnlinePlayers() +
                '}';
    }

    public boolean isFull() {
        return this.getMaxPlayers() <= this.getOnlinePlayers().size();
    }

    public boolean isOnline() {
        return this.online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public boolean isPublic() {
        return this.getCluster().getVisibility().equals(Visibility.PUBLIC) && this.getVisibility().equals(Visibility.PUBLIC);
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
        UPTIME("uptime"),
        ONLINE_PLAYERS("online_players"),
        ONLINE("online");

        public String fieldName;

        MongoFields(String fieldName) {
            this.fieldName = fieldName;
        }
    }
}
