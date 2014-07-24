package us.nsakt.dynamicdatabase.documents;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.joda.time.Duration;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Property;
import org.mongodb.morphia.annotations.Reference;
import org.mongodb.morphia.annotations.Transient;
import us.nsakt.dynamicdatabase.util.Visibility;

import java.util.List;
import java.util.UUID;

@Entity("servers")
public class ServerDocument extends Document {

    private String name;
    private String address;
    private String visibility;

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

    @Transient private Visibility visibilityEnum;

    /**
     * Get the server's private name for internal operations
     *
     * @return
     */
    public String getInternalName() {
        return internalName;
    }

    /**
     * Set the server's private name for internal operations
     *
     * @param internalName
     */
    public void setInternalName(String internalName) {
        this.internalName = internalName;
    }

    /**
     * Get the server's private address for internal operations
     *
     * @return
     */
    public String getInternalAddress() {
        return internalAddress;
    }

    /**
     * Set the server's private address for internal operations
     *
     * @param internalAddress
     */
    public void setInternalAddress(String internalAddress) {
        this.internalAddress = internalAddress;
    }

    /**
     * Get the server's private port for internal operations
     *
     * @return
     */
    public int getInternalPort() {
        return internalPort;
    }

    /**
     * Set the server's private port for internal operations
     *
     * @param internalPort
     */
    public void setInternalPort(int internalPort) {
        this.internalPort = internalPort;
    }

    /**
     * Get the server's display name
     *
     * @return
     */
    public String getName() {
        return name;
    }

    //Public things

    /**
     * Set the server's display name
     *
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get the server's public address
     *
     * @return
     */
    public String getAddress() {
        return address;
    }

    /**
     * Set the server's public address
     *
     * @param address
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * Get the server's visibility
     *
     * @return
     */
    public String getVisibility() {
        return visibility;
    }

    /**
     * Set the server's visibility
     *
     * @param visibility
     */
    public void setVisibility(String visibility) {
        this.visibility = visibility;
        this.setVisibilityEnum(Visibility.valueOf(visibility));
    }

    /**
     * Get the server's cluster
     *
     * @return
     */
    @Override
    public ClusterDocument getCluster() {
        return cluster;
    }

    /**
     * Set the server's cluster
     *
     * @param cluster
     */
    public void setCluster(ClusterDocument cluster) {
        this.cluster = cluster;
    }

    /**
     * Get the server's port
     *
     * @return
     */
    public int getPort() {
        return port;
    }

    /**
     * Set the server's port
     *
     * @param port
     */
    public void setPort(int port) {
        this.port = port;
    }

    /**
     * Get the server's max player count
     *
     * @return
     */
    public int getMaxPlayers() {
        return maxPlayers;
    }

    /**
     * Set the server's max player count
     *
     * @param maxPlayers
     */
    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    /**
     * Get the server's up time since last boot
     *
     * @return
     */
    public Duration getUptime() {
        return uptime;
    }

    /**
     * Set the server's up time since last boot
     *
     * @param uptime
     */
    public void setUptime(Duration uptime) {
        this.uptime = uptime;
    }

    /**
     * Get a list of the server's online players
     *
     * @return
     */
    public List<UUID> getOnlinePlayers() {
        return onlinePlayers;
    }

    /**
     * Set a list of the server's online players
     *
     * @param onlinePlayers
     */
    public void setOnlinePlayers(List<UUID> onlinePlayers) {
        this.onlinePlayers = onlinePlayers;
    }

    public Visibility getVisibilityEnum() {
        return visibilityEnum;
    }

    public void setVisibilityEnum(Visibility visibilityEnum) {
        this.visibilityEnum = visibilityEnum;
        this.setVisibility(visibilityEnum.dbName);
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

    /**
     * Check if a server is full
     *
     * @return If the server's max player limit is equal to or below the number of players online
     */
    public boolean isFull() {
        return this.getMaxPlayers() <= this.getOnlinePlayers().size();
    }

    /**
     * Check if a server is online
     *
     * @return If the server is marked as online
     */
    public boolean isOnline() {
        return this.online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    /**
     * Check if a server is public
     *
     * @return If the server is public
     */
    public boolean isPublic() {
        return this.getCluster().getVisibility().equals(Visibility.PUBLIC) && this.getVisibilityEnum().equals(Visibility.PUBLIC);
    }

    /**
     * Check if a player has permission to see a server
     *
     * @param player          Player to check
     * @param providingPlugin Plugin that provides the servers (for permissions)
     * @return If the player can see the server
     */
    public boolean canSee(Player player, Plugin providingPlugin) {
        return (
                player.hasPermission(providingPlugin.getName().toLowerCase() + ".servers.see.all") ||
                        player.hasPermission(providingPlugin.getName().toLowerCase() + ".servers.types.see.all") ||
                        player.hasPermission(providingPlugin.getName().toLowerCase() + ".servers.types.see." + this.getVisibility().toLowerCase()) ||
                        player.hasPermission(providingPlugin.getName().toLowerCase() + ".servers.see." + this.getName().toLowerCase()) ||
                        player.hasPermission(providingPlugin.getName().toLowerCase() + ".servers.clusters.see." + this.getCluster().getName().toLowerCase()) ||
                        player.hasPermission(providingPlugin.getName().toLowerCase() + ".servers.clusters.see.all")
        );
    }

    /**
     * Check if a player has permission to join a server
     *
     * @param player          Player to check
     * @param providingPlugin Plugin that provides the servers (for permissions)
     * @return If the player can join the server
     */
    public boolean canJoin(Player player, Plugin providingPlugin) {
        return (
                player.hasPermission(providingPlugin.getName().toLowerCase() + ".servers.join.all") ||
                        player.hasPermission(providingPlugin.getName().toLowerCase() + ".servers.types.join.all") ||
                        player.hasPermission(providingPlugin.getName().toLowerCase() + ".servers.types.join." + this.getVisibility().toLowerCase()) ||
                        player.hasPermission(providingPlugin.getName().toLowerCase() + ".servers.join." + this.getName().toLowerCase()) ||
                        player.hasPermission(providingPlugin.getName().toLowerCase() + ".servers.clusters.join." + this.getCluster().getName().toLowerCase()) ||
                        player.hasPermission(providingPlugin.getName().toLowerCase() + ".servers.clusters.join.all")
        );
    }

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
