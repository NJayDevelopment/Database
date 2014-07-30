package us.nsakt.dynamicdatabase.tasks;

import com.google.common.collect.Lists;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import us.nsakt.dynamicdatabase.ConfigEnforcer;
import us.nsakt.dynamicdatabase.DynamicDatabasePlugin;
import us.nsakt.dynamicdatabase.MongoExecutionService;
import us.nsakt.dynamicdatabase.daos.DAOGetter;
import us.nsakt.dynamicdatabase.daos.Groups;
import us.nsakt.dynamicdatabase.daos.Users;
import us.nsakt.dynamicdatabase.documents.ClusterDocument;
import us.nsakt.dynamicdatabase.documents.GroupDocument;
import us.nsakt.dynamicdatabase.documents.UserDocument;
import us.nsakt.dynamicdatabase.tasks.core.SaveTask;
import us.nsakt.dynamicdatabase.util.NsaktException;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Basic Utility class to perform action related to group documents.
 *
 * @author NathanTheBook
 */
public class GroupTasks {
    private static Groups getDao() {
        return new DAOGetter().getGroups();
    }

    /**
     * Get all of a player's groups (based on clusters from config) and apply them to the player.
     *
     * @param player Player to apply the permissions to.
     */
    public static void assignPermissions(final Player player) {
        try {
            ConfigEnforcer.Documents.Groups.ensureEnabled();
        } catch (NsaktException e) {
        }
        final UUID playerUUID = player.getUniqueId();

        HashMap<Permission, Boolean> permissions = getDao().getAllPermissions(playerUUID);
        for (Map.Entry<Permission, Boolean> entry : permissions.entrySet()) {
            PermissionAttachment attachment = player.addAttachment(DynamicDatabasePlugin.getInstance());
            attachment.setPermission(entry.getKey(), entry.getValue());
        }
        player.recalculatePermissions();
    }

    /**
     * Add a player to a group, then recalculate the player's permissions.
     *
     * @param player        Player to be added to the group.
     * @param groupDocument Group to add the player to.
     */
    public static void addPlayerToGroupAndRecalculate(final Player player, final GroupDocument groupDocument) {
        try {
            ConfigEnforcer.Documents.Groups.ensureEnabled();
        } catch (NsaktException e) {
        }
        Users users = new Users(DynamicDatabasePlugin.getInstance().getDatastores().get(UserDocument.class));
        groupDocument.getMembers().add(player.getUniqueId());
        getDao().save(groupDocument);
        assignPermissions(player);
    }

    /**
     * Opposite action of {@link us.nsakt.dynamicdatabase.tasks.GroupTasks#addPlayerToGroupAndRecalculate}
     */
    public static void removePlayerFromGroupAndRecalculate(final Player player, final GroupDocument groupDocument) {
        try {
            ConfigEnforcer.Documents.Groups.ensureEnabled();
        } catch (NsaktException e) {
        }

        Users users = new Users(DynamicDatabasePlugin.getInstance().getDatastores().get(UserDocument.class));

        groupDocument.getMembers().remove(player.getUniqueId());
        getDao().save(groupDocument);
        assignPermissions(player);
    }

    /**
     * Add the default group to the database.
     * NOTE: Callers need to check if the default group is not already there.
     * NOTE: The default cluster needs to be created first.
     */
    public static void setupDefaultGroup() {
        try {
            ConfigEnforcer.Documents.Groups.ensureEnabled();
        } catch (NsaktException e) {
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
        MongoExecutionService.getExecutorService().submit(task);
    }
}
