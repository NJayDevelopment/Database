package us.nsakt.dynamicdatabase.tasks;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import us.nsakt.dynamicdatabase.DynamicDatabasePlugin;
import us.nsakt.dynamicdatabase.daos.Groups;
import us.nsakt.dynamicdatabase.documents.GroupDocument;
import us.nsakt.dynamicdatabase.tasks.runners.GroupTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Different tasks for working with groups.
 */
public class GroupTasks {

    Groups groups = new Groups(GroupDocument.class, DynamicDatabasePlugin.getInstance().getDatastores().get(GroupDocument.class));

    /**
     * Get the document's relative data access object.
     *
     * @return the document's relative data access object.
     */
    private Groups getDao() {
        return groups;
    }

    /**
     * Assign permissions to a player
     *
     * @param player Player to assign permissions to
     */
    public void assignPermissions(final Player player) {
        final UUID playerUUID = player.getUniqueId();
        GroupTask task = new GroupTask(getDao().getDatastore(), null) {
            @Override
            public void run() {
                HashMap<Permission, Boolean> permissions = getDao().getAllPermissions(playerUUID);
                for (Map.Entry<Permission, Boolean> entry : permissions.entrySet()) {
                    PermissionAttachment attachment = player.addAttachment(DynamicDatabasePlugin.getInstance());
                    attachment.setPermission(entry.getKey(), entry.getValue());
                }
                player.recalculatePermissions();
            }
        };
        Bukkit.getScheduler().runTaskAsynchronously(DynamicDatabasePlugin.getInstance(), task);
    }

    /**
     * Add a player to a group, then recalculate the player's permissions
     *
     * @param player        Player to be added
     * @param groupDocument Group to add the player to
     */
    public void addPlayerToGroupAndRecalculate(final Player player, final GroupDocument groupDocument) {
        GroupTask task = new GroupTask(getDao().getDatastore(), null) {
            @Override
            public void run() {
                groupDocument.getMembers().add(player.getUniqueId());
                getDao().save(groupDocument);
                assignPermissions(player);
            }
        };
        Bukkit.getScheduler().runTaskAsynchronously(DynamicDatabasePlugin.getInstance(), task);
    }

    /**
     * Remove a player from a group, then recalculate the player's permissions
     *
     * @param player        Player to be removed
     * @param groupDocument Group to remove the player from
     */
    public void removePlayerFromGroupAndRecalculate(final Player player, final GroupDocument groupDocument) {
        GroupTask task = new GroupTask(getDao().getDatastore(), null) {
            @Override
            public void run() {
                groupDocument.getMembers().remove(player.getUniqueId());
                getDao().save(groupDocument);
                assignPermissions(player);
            }
        };
        Bukkit.getScheduler().runTaskAsynchronously(DynamicDatabasePlugin.getInstance(), task);
    }
}
