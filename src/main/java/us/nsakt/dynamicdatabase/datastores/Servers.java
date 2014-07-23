package us.nsakt.dynamicdatabase.datastores;

import com.sk89q.minecraft.util.commands.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.mongodb.morphia.Datastore;
import us.nsakt.dynamicdatabase.DynamicDatabasePlugin;
import us.nsakt.dynamicdatabase.documents.ServerDocument;
import us.nsakt.dynamicdatabase.util.Visibility;

import java.util.UUID;

public class Servers {

    private static Datastore datastore = DynamicDatabasePlugin.getInstance().getDatastores().get(ServerDocument.class);

    public static Datastore getDatastore() {
        return datastore;
    }

    /**
     * Adds a player to the online players list.
     *
     * @param serverDocument Server to add the player to
     * @param player         Player to be added
     */
    public void addPlayerToOnline(ServerDocument serverDocument, UUID player) {
        serverDocument.getOnlinePlayers().add(player);
    }

    /**
     * Remove a player from the online players list
     *
     * @param serverDocument Server the player is leaving
     * @param player         Player to be removed
     */
    public void removePlayerFromOnline(ServerDocument serverDocument, UUID player) {
        serverDocument.getOnlinePlayers().remove(player);
    }

    /**
     * Check if a server is full
     *
     * @param serverDocument Server to check
     * @return If the server's max player limit is equal to or below the number of players online
     */
    public boolean isFull(ServerDocument serverDocument) {
        return serverDocument.getMaxPlayers() <= serverDocument.getOnlinePlayers().size();
    }

    /**
     * Check if a server is online
     *
     * @param serverDocument Server to check
     * @return If the server is marked as online
     */
    public boolean isOnline(ServerDocument serverDocument) {
        return serverDocument.isOnline();
    }

    /**
     * Check if a server is public
     *
     * @param serverDocument Server to check
     * @return If the server is public
     */
    public boolean isPublic(ServerDocument serverDocument) {
        return serverDocument.getCluster().getVisibility().equals(Visibility.PUBLIC) && serverDocument.getVisibilityEnum().equals(Visibility.PUBLIC);
    }

    /**
     * Check if a player has permission to see a server
     *
     * @param player          Player to check
     * @param serverDocument  Server to check against
     * @param providingPlugin Plugin that provides the servers (for permissions)
     * @return If the player can see the server
     */
    public boolean canSee(Player player, ServerDocument serverDocument, Plugin providingPlugin) {
        return (
                player.hasPermission(providingPlugin.getName().toLowerCase() + ".servers.see.all") ||
                        player.hasPermission(providingPlugin.getName().toLowerCase() + ".servers.types.see.all") ||
                        player.hasPermission(providingPlugin.getName().toLowerCase() + ".servers.types.see." + serverDocument.getVisibility().toLowerCase()) ||
                        player.hasPermission(providingPlugin.getName().toLowerCase() + ".servers.see." + serverDocument.getName().toLowerCase()) ||
                        player.hasPermission(providingPlugin.getName().toLowerCase() + ".servers.clusters.see." + serverDocument.getCluster().getName().toLowerCase()) ||
                        player.hasPermission(providingPlugin.getName().toLowerCase() + ".servers.clusters.see.all")
        );
    }

    /**
     * Check if a player has permission to join a server
     *
     * @param player          Player to check
     * @param serverDocument  Server to check against
     * @param providingPlugin Plugin that provides the servers (for permissions)
     * @return If the player can join the server
     */
    public boolean canJoin(Player player, ServerDocument serverDocument, Plugin providingPlugin) {
        return (
                player.hasPermission(providingPlugin.getName().toLowerCase() + ".servers.join.all") ||
                        player.hasPermission(providingPlugin.getName().toLowerCase() + ".servers.types.join.all") ||
                        player.hasPermission(providingPlugin.getName().toLowerCase() + ".servers.types.join." + serverDocument.getVisibility().toLowerCase()) ||
                        player.hasPermission(providingPlugin.getName().toLowerCase() + ".servers.join." + serverDocument.getName().toLowerCase()) ||
                        player.hasPermission(providingPlugin.getName().toLowerCase() + ".servers.clusters.join." + serverDocument.getCluster().getName().toLowerCase()) ||
                        player.hasPermission(providingPlugin.getName().toLowerCase() + ".servers.clusters.join.all")
        );
    }

    /**
     * Returns a ChatColor corresponding to a visibility
     *
     * @param visibility Visibility to get the color for
     * @return The appropriate color corresponding the the supplied visibility
     */
    public ChatColor getVisibilityColor(Visibility visibility) {
        switch (visibility) {
            case PUBLIC:
                return ChatColor.GREEN;
            case STAFF_ONLY:
                return ChatColor.GOLD;
            case PRIVATE:
                return ChatColor.RED;
        }
        return ChatColor.WHITE;
    }

    /**
     * Returns a colored message to display a server's visibility
     *
     * @param visibility Visibility to get the message for
     * @return A message with the appropriate color and display name for the visibility
     */
    public String visibilityText(Visibility visibility) {
        return getVisibilityColor(visibility) + visibility.displayName;
    }
}
