package us.nsakt.dynamicdatabase.tasks;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.sk89q.minecraft.util.commands.ChatColor;
import org.bson.types.ObjectId;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import us.nsakt.dynamicdatabase.ConfigEnforcer;
import us.nsakt.dynamicdatabase.Debug;
import us.nsakt.dynamicdatabase.DynamicDatabasePlugin;
import us.nsakt.dynamicdatabase.MongoExecutionService;
import us.nsakt.dynamicdatabase.daos.DAOGetter;
import us.nsakt.dynamicdatabase.daos.Groups;
import us.nsakt.dynamicdatabase.documents.ClusterDocument;
import us.nsakt.dynamicdatabase.documents.GroupDocument;
import us.nsakt.dynamicdatabase.documents.UserDocument;
import us.nsakt.dynamicdatabase.tasks.core.SaveTask;
import us.nsakt.dynamicdatabase.util.NsaktException;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
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

    public static class LoginTasks {
        static final HashMap<UserDocument, HashMap<Permission, Boolean>> permsMap = Maps.newHashMap();

        /**
         * Get all of a player's groups (based on clusters from config) and add them to the permsMap.
         *
         * @param uuid UUID to get the permissions from.
         */
        public static void addPermsToMap(final UUID uuid) {
            try {
                ConfigEnforcer.Documents.Groups.ensureEnabled();
            } catch (NsaktException e) {
            }
            final UserDocument userDocument = new DAOGetter().getUsers().getUserFromUuid(uuid);
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    HashMap<Permission, Boolean> permissions = getDao().getAllPermissions(userDocument);
                    if (permissions == null || permissions.isEmpty()) return;
                    permsMap.put(userDocument, permissions);
                }
            };
            MongoExecutionService.getExecutorService().execute(runnable);
        }

        /**
         * Get player's perms from permsMap and apply them to the player.
         *
         * @param player Player to apply the permissions to.
         */
        public static void assignPermissions(final Player player) {
            try {
                ConfigEnforcer.Documents.Groups.ensureEnabled();
            } catch (NsaktException e) {
            }
            final UserDocument userDocument = new DAOGetter().getUsers().getUserFromPlayer(player);
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    HashMap<Permission, Boolean> permissions = permsMap.get(userDocument);
                    if (permissions == null || permissions.isEmpty()) return;
                    for (Map.Entry<Permission, Boolean> entry : permissions.entrySet()) {
                        PermissionAttachment attachment = player.addAttachment(DynamicDatabasePlugin.getInstance());
                        attachment.setPermission(entry.getKey(), entry.getValue());
                        Debug.log(Debug.LogLevel.INFO, attachment.toString());
                    }
                    permsMap.remove(userDocument);
                    player.recalculatePermissions();
                }
            };
            MongoExecutionService.getExecutorService().execute(runnable);
        }
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
        final UserDocument userDocument = new DAOGetter().getUsers().getUserFromPlayer(player);
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                HashMap<Permission, Boolean> permissions = getDao().getAllPermissions(userDocument);
                if (permissions == null || permissions.isEmpty()) return;
                for (Map.Entry<Permission, Boolean> entry : permissions.entrySet()) {
                    PermissionAttachment attachment = player.addAttachment(DynamicDatabasePlugin.getInstance());
                    attachment.setPermission(entry.getKey(), entry.getValue());
                    Debug.log(Debug.LogLevel.INFO, attachment.toString());
                }
                player.recalculatePermissions();
            }
        };
        MongoExecutionService.getExecutorService().execute(runnable);
    }

    public static void addGroupFlairs(final Player player) {
        try {
            ConfigEnforcer.Documents.Groups.ensureEnabled();
        } catch (NsaktException e) {
        }
        final UserDocument userDocument = new DAOGetter().getUsers().getUserFromPlayer(player);
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                StringBuilder flairBuilder = new StringBuilder();
                List<GroupDocument> groups = getDao().getAllGroups(userDocument);
                if (groups == null || groups.isEmpty()) {
                    player.setDisplayName(ChatColor.AQUA + player.getName() + ChatColor.RESET);
                    return;
                }
                for (GroupDocument group : groups) {
                    if (group.getFlair() == null || group.getFlair().isEmpty()) return;
                    if (group.getFlairColor() == null || group.getFlairColor().isEmpty()) flairBuilder.append(ChatColor.WHITE);
                    else flairBuilder.append(ChatColor.valueOf(group.getFlairColor()));
                    flairBuilder.append(group.getFlair());
                }
                player.setDisplayName(flairBuilder.toString() + ChatColor.RESET + ChatColor.AQUA + player.getName() + ChatColor.RESET);
            }
        };
        MongoExecutionService.getExecutorService().execute(runnable);
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
        SaveTask task = new SaveTask(getDao().getDatastore(), groupDocument) {
            @Override
            public void run() {
                groupDocument.getMembers().add(new DAOGetter().getUsers().getUserFromPlayer(player).getObjectId());
                getDao().save(groupDocument);
                assignPermissions(player);
            }
        };
        MongoExecutionService.getExecutorService().execute(task);
    }

    public static void addPlayerToAllDefaults(final UUID uuid) {
        try {
            ConfigEnforcer.Documents.Groups.ensureEnabled();
        } catch (NsaktException e) {
        }
        SaveTask task = new SaveTask(getDao().getDatastore(), null) {
            @Override
            public void run() {
                for (GroupDocument groupDocument : getDao().getAllDefaultGroups(DynamicDatabasePlugin.getInstance().getCurrentServerDocument().getCluster())) {
                    if (groupDocument.getMembers().contains(new DAOGetter().getUsers().getUserFromUuid(uuid).getObjectId())) return;
                    groupDocument.getMembers().add(new DAOGetter().getUsers().getUserFromUuid(uuid).getObjectId());
                    getDao().save(groupDocument);
                }
            }
        };
        MongoExecutionService.getExecutorService().execute(task);
    }

    /**
     * Opposite action of {@link us.nsakt.dynamicdatabase.tasks.GroupTasks#addPlayerToGroupAndRecalculate}
     */
    public static void removePlayerFromGroupAndRecalculate(final Player player, final GroupDocument groupDocument) {
        try {
            ConfigEnforcer.Documents.Groups.ensureEnabled();
        } catch (NsaktException e) {
        }
        SaveTask task = new SaveTask(getDao().getDatastore(), groupDocument) {
            @Override
            public void run() {
                groupDocument.getMembers().remove(new DAOGetter().getUsers().getUserFromPlayer(player).getObjectId());
                getDao().save(groupDocument);
                assignPermissions(player);
            }
        };
        MongoExecutionService.getExecutorService().execute(task);
    }
}
