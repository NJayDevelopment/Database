package us.nsakt.dynamicdatabase.tasks;

import com.google.common.collect.Lists;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import us.nsakt.dynamicdatabase.ConfigEnforcer;
import us.nsakt.dynamicdatabase.DynamicDatabasePlugin;
import us.nsakt.dynamicdatabase.QueryExecutor;
import us.nsakt.dynamicdatabase.daos.DAOGetter;
import us.nsakt.dynamicdatabase.daos.Groups;
import us.nsakt.dynamicdatabase.daos.Users;
import us.nsakt.dynamicdatabase.documents.ClusterDocument;
import us.nsakt.dynamicdatabase.documents.GroupDocument;
import us.nsakt.dynamicdatabase.documents.UserDocument;
import us.nsakt.dynamicdatabase.tasks.core.SaveTask;
import us.nsakt.dynamicdatabase.tasks.core.base.DBCallback;
import us.nsakt.dynamicdatabase.util.NsaktException;

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
        try {
            ConfigEnforcer.Documents.Groups.ensureEnabled();
        } catch (NsaktException e) {
            // silence
        }

        final UUID playerUUID = player.getUniqueId();
        DBCallback callback = new DBCallback() {
            @Override
            public void call() {
            }

            @Override
            public void call(Object... objects) {
                HashMap<Permission, Boolean> permissions = (HashMap<Permission, Boolean>) objects[0];
                for (Map.Entry<Permission, Boolean> entry : permissions.entrySet()) {
                    PermissionAttachment attachment = player.addAttachment(DynamicDatabasePlugin.getInstance());
                    attachment.setPermission(entry.getKey(), entry.getValue());
                }
                player.recalculatePermissions();
            }
        };
        getDao().getAllPermissions(playerUUID, callback);
    }

    /**
     * Add a player to a groups, then recalculate the player's permissions
     *
     * @param player        Player to be added
     * @param groupDocument Groups to add the player to
     */
    public static void addPlayerToGroupAndRecalculate(final Player player, final GroupDocument groupDocument) {
        try {
            ConfigEnforcer.Documents.Groups.ensureEnabled();
        } catch (NsaktException e) {
            // silence
        }

        DBCallback callback = new DBCallback() {
            @Override
            public void call() {
            }

            @Override
            public void call(Object... objects) {
                groupDocument.getMembers().add((UserDocument) objects[0]);
                getDao().save(groupDocument);
                assignPermissions(player);
            }
        };
        Users users = new Users(UserDocument.class, DynamicDatabasePlugin.getInstance().getDatastores().get(UserDocument.class));
        users.getUserFromPlayer(player, callback);
    }

    /**
     * Remove a player from a groups, then recalculate the player's permissions
     *
     * @param player        Player to be removed
     * @param groupDocument Groups to remove the player from
     */
    public static void removePlayerFromGroupAndRecalculate(final Player player, final GroupDocument groupDocument) {
        try {
            ConfigEnforcer.Documents.Groups.ensureEnabled();
        } catch (NsaktException e) {
            // silence
        }

        DBCallback callback = new DBCallback() {
            @Override
            public void call() {
            }

            @Override
            public void call(Object... objects) {
                groupDocument.getMembers().remove((UserDocument) objects[0]);
                getDao().save(groupDocument);
                assignPermissions(player);
            }
        };
        Users users = new Users(UserDocument.class, DynamicDatabasePlugin.getInstance().getDatastores().get(UserDocument.class));
        users.getUserFromPlayer(player, callback);
    }

    /**
     * Creates the default group
     */
    public static void setupDefaultGroup() {
        try {
            ConfigEnforcer.Documents.Groups.ensureEnabled();
        } catch (NsaktException e) {
            // silence
        }
        SaveTask task = new SaveTask(getDao().getDatastore(), new GroupDocument()) {
            @Override
            public void run() {
                try {
                    GroupDocument document = (GroupDocument) getDocument();
                    document.setCluster(new DAOGetter().getClusters().getDatastore().find(ClusterDocument.class).field(ClusterDocument.MongoFields.NAME.fieldName).equal("all").get());
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
