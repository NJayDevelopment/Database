package us.nsakt.dynamicdatabase.tasks;

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
    private Groups getDao() {
        return new DAOGetter().getGroups();
    }

    /**
     * Assign permissions to a player
     *
     * @param player Player to assign permissions to
     */
    public void assignPermissions(final Player player) {
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
    public void addPlayerToGroupAndRecalculate(final Player player, final GroupDocument groupDocument) {
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
    public void removePlayerFromGroupAndRecalculate(final Player player, final GroupDocument groupDocument) {
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
}
