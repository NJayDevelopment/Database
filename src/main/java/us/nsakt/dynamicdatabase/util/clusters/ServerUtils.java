package us.nsakt.dynamicdatabase.util.clusters;

import com.sk89q.minecraft.util.commands.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import us.nsakt.dynamicdatabase.documents.Server;
import us.nsakt.dynamicdatabase.util.Visibility;

import java.util.UUID;

public class ServerUtils {

    /**
     * Adds a player to the online players list.
     *
     * @param server Server to add the player to
     * @param player Player to be added
     */
    public void addPlayerToOnline(Server server, UUID player) {
        server.getOnlinePlayers().add(player);
    }

    /**
     * Remove a player from the online players list
     *
     * @param server Server the player is leaving
     * @param player Player to be removed
     */
    public void removePlayerFromOnline(Server server, UUID player) {
        server.getOnlinePlayers().remove(player);
    }

    /**
     * Check if a server is full
     *
     * @param server Server to check
     * @return If the server's max player limit is equal to or below the number of players online
     */
    public boolean isFull(Server server) {
        return server.getMaxPlayers() <= server.getOnlinePlayers().size();
    }

    /**
     * Check if a server is online
     *
     * @param server Server to check
     * @return If the server is marked as online
     */
    public boolean isOnline(Server server) {
        return server.isOnline();
    }

    /**
     * Check if a server is public
     *
     * @param server Server to check
     * @return If the server is public
     */
    public boolean isPublic(Server server) {
        return server.getCluster().getVisibility().equals(Visibility.PUBLIC) && server.getVisibilityEnum().equals(Visibility.PUBLIC);
    }

    /**
     * Check if a player has permission to see a server
     *
     * @param player          Player to check
     * @param server          Server to check against
     * @param providingPlugin Plugin that provides the servers (for permissions)
     * @return If the player can see the server
     */
    public boolean canSee(Player player, Server server, Plugin providingPlugin) {
        return (
                player.hasPermission(providingPlugin.getName().toLowerCase() + ".servers.see.all") ||
                        player.hasPermission(providingPlugin.getName().toLowerCase() + ".servers.types.see.all") ||
                        player.hasPermission(providingPlugin.getName().toLowerCase() + ".servers.types.see." + server.getVisibility().toLowerCase()) ||
                        player.hasPermission(providingPlugin.getName().toLowerCase() + ".servers.see." + server.getName().toLowerCase()) ||
                        player.hasPermission(providingPlugin.getName().toLowerCase() + ".servers.clusters.see." + server.getCluster().getName().toLowerCase()) ||
                        player.hasPermission(providingPlugin.getName().toLowerCase() + ".servers.clusters.see.all")
        );
    }

    /**
     * Check if a player has permission to join a server
     *
     * @param player          Player to check
     * @param server          Server to check against
     * @param providingPlugin Plugin that provides the servers (for permissions)
     * @return If the player can join the server
     */
    public boolean canJoin(Player player, Server server, Plugin providingPlugin) {
        return (
                player.hasPermission(providingPlugin.getName().toLowerCase() + ".servers.join.all") ||
                        player.hasPermission(providingPlugin.getName().toLowerCase() + ".servers.types.join.all") ||
                        player.hasPermission(providingPlugin.getName().toLowerCase() + ".servers.types.join." + server.getVisibility().toLowerCase()) ||
                        player.hasPermission(providingPlugin.getName().toLowerCase() + ".servers.join." + server.getName().toLowerCase()) ||
                        player.hasPermission(providingPlugin.getName().toLowerCase() + ".servers.clusters.join." + server.getCluster().getName().toLowerCase()) ||
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
