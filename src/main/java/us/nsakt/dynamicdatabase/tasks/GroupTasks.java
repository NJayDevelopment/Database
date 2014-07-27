package us.nsakt.dynamicdatabase.tasks;

import com.google.common.collect.Lists;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import us.nsakt.dynamicdatabase.DynamicDatabasePlugin;
import us.nsakt.dynamicdatabase.QueryExecutor;
import us.nsakt.dynamicdatabase.daos.DAOGetter;
import us.nsakt.dynamicdatabase.daos.Groups;
import us.nsakt.dynamicdatabase.daos.Users;
import us.nsakt.dynamicdatabase.documents.GroupDocument;
import us.nsakt.dynamicdatabase.documents.UserDocument;
import us.nsakt.dynamicdatabase.tasks.core.QueryActionTask;
import us.nsakt.dynamicdatabase.tasks.core.SaveTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Different tasks for working with groups.
 */
public class GroupTasks {


    /**
     * Get the document's relative data access object.
     *
     * @return the document's relative data access object.
     */
    private static Groups getDao() {
        return new DAOGetter().getGroups();
    }

    /**
     * Assign permissions to a player
     *
     * @param player Player to assign permissions to
     */
    public static void assignPermissions(final Player player) {
        final UUID playerUUID = player.getUniqueId();
        QueryActionTask task = new QueryActionTask(getDao().getDatastore(), null) {
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
        QueryExecutor.getExecutorService().submit(task);
    }

    /**
     * Add a player to a groups, then recalculate the player's permissions
     *
     * @param player        Player to be added
     * @param groupDocument Groups to add the player to
     */
    public static void addPlayerToGroupAndRecalculate(final Player player, final GroupDocument groupDocument) {
        SaveTask task = new SaveTask(getDao().getDatastore(), groupDocument) {
            @Override
            public void run() {
                Users users = new Users(UserDocument.class, DynamicDatabasePlugin.getInstance().getDatastores().get(UserDocument.class));
                GroupDocument document = (GroupDocument) getDocument();
                document.getMembers().add(users.getUserFromPlayer(player));
                getDao().save(document);
                assignPermissions(player);
            }
        };
        QueryExecutor.getExecutorService().submit(task);
    }

    /**
     * Remove a player from a groups, then recalculate the player's permissions
     *
     * @param player        Player to be removed
     * @param groupDocument Groups to remove the player from
     */
    public static void removePlayerFromGroupAndRecalculate(final Player player, final GroupDocument groupDocument) {
        SaveTask task = new SaveTask(getDao().getDatastore(), groupDocument) {
            @Override
            public void run() {
                Users users = new Users(UserDocument.class, DynamicDatabasePlugin.getInstance().getDatastores().get(UserDocument.class));
                GroupDocument document = (GroupDocument) getDocument();
                document.getMembers().remove(users.getUserFromPlayer(player));
                getDao().save(document);
                assignPermissions(player);
            }
        };
        QueryExecutor.getExecutorService().submit(task);
    }

    /**
     * Creates the default group
     */
    public static void setupDefaultGroup() {
        SaveTask task = new SaveTask(getDao().getDatastore(), new GroupDocument()) {
            @Override
            public void run() {
                try {
                    GroupDocument document = (GroupDocument) getDocument();
                    document.setCluster(ClusterTasks.createDefaultAllCluster().get());
                    document.setName("default");
                    document.setPriority(0);
                    document.setMcPermissions(Lists.newArrayList(
                            "#Hi, this is the default group created by DynamicDatabase",
                            "#All players will be in this group",
                            "#If this group is deleted, it will be re-created",
                            "#DO NOT change this group's priority!",
                            "#All groups above this group will inherit its permissions",
                            "#You can negate said permissions by adding a '-' before the permission in the higher groups"
                    ));
                    getDao().save(document);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        };
        QueryExecutor.getExecutorService().submit(task);
    }
}
