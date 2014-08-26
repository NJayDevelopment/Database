package net.njay.dynamicdatabase.tasks;

import com.google.common.collect.Maps;
import com.sk89q.minecraft.util.commands.ChatColor;
import org.bson.types.ObjectId;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import net.njay.dynamicdatabase.ConfigEnforcer;
import net.njay.dynamicdatabase.Debug;
import net.njay.dynamicdatabase.DynamicDatabasePlugin;
import net.njay.dynamicdatabase.MongoExecutionService;
import net.njay.dynamicdatabase.daos.DAOService;
import net.njay.dynamicdatabase.daos.Groups;
import net.njay.dynamicdatabase.documents.GroupDocument;
import net.njay.dynamicdatabase.documents.UserDocument;
import net.njay.dynamicdatabase.tasks.core.SaveTask;
import net.njay.dynamicdatabase.util.NsaktException;

import java.util.*;

/**
 * Basic Utility class to perform action related to group documents.
 *
 * @author NathanTheBook
 */
public class GroupTasks {

    private static Groups getDao() {
        return DAOService.getGroups();
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
            final UserDocument userDocument = DAOService.getUsers().getUserFromUuid(uuid);
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
            final UserDocument userDocument = DAOService.getUsers().getUserFromPlayer(player);
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
        final UserDocument userDocument = DAOService.getUsers().getUserFromPlayer(player);
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final HashMap<Permission, Boolean> permissions = getDao().getAllPermissions(userDocument);
                if (permissions == null || permissions.isEmpty()) return;
                Bukkit.getScheduler().runTask(DynamicDatabasePlugin.getInstance(), new Runnable() {
                    public void run() {
                        for (Map.Entry<Permission, Boolean> entry : permissions.entrySet()) {
                            PermissionAttachment attachment = player.addAttachment(DynamicDatabasePlugin.getInstance());
                            attachment.setPermission(entry.getKey(), entry.getValue());
                        }
                        player.recalculatePermissions();
                    }
                });
            }
        };
        MongoExecutionService.getExecutorService().execute(runnable);
    }

    public static void addGroupFlairs(final Player player) {
        try {
            ConfigEnforcer.Documents.Groups.ensureEnabled();
        } catch (NsaktException e) {
        }
        final UserDocument userDocument = DAOService.getUsers().getUserFromPlayer(player);
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
                    if (group.getFlairColor() == null || group.getFlairColor().isEmpty())
                        flairBuilder.append(ChatColor.WHITE);
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
                groupDocument.getMembers().add(DAOService.getUsers().getUserFromPlayer(player).getObjectId());
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
                    if (groupDocument.getMembers().contains(DAOService.getUsers().getUserFromUuid(uuid).getObjectId()))
                        return;
                    groupDocument.getMembers().add(DAOService.getUsers().getUserFromUuid(uuid).getObjectId());
                    getDao().save(groupDocument);
                }
            }
        };
        MongoExecutionService.getExecutorService().execute(task);
    }

    /**
     * Opposite action of {@link GroupTasks#addPlayerToGroupAndRecalculate}
     */
    public static void removePlayerFromGroupAndRecalculate(final Player player, final GroupDocument groupDocument) {
        try {
            ConfigEnforcer.Documents.Groups.ensureEnabled();
        } catch (NsaktException e) {
        }
        SaveTask task = new SaveTask(getDao().getDatastore(), groupDocument) {
            @Override
            public void run() {
                groupDocument.getMembers().remove(DAOService.getUsers().getUserFromPlayer(player).getObjectId());
                if (groupDocument.getMembers() == null) groupDocument.setMembers(new ArrayList<ObjectId>());
                getDao().save(groupDocument);
                Bukkit.getScheduler().runTask(DynamicDatabasePlugin.getInstance(), new Runnable() {
                    public void run() {
                        synchronized (DynamicDatabasePlugin.getInstance()) {
                            for (Map.Entry<Permission, Boolean> entry : groupDocument.getGroupPermissions().entrySet()) {
                                PermissionAttachment attachment = player.addAttachment(DynamicDatabasePlugin.getInstance());
                                attachment.setPermission(entry.getKey(), false);
                            }
                            player.recalculatePermissions();
                        }
                    }
                });
            }
        };
        MongoExecutionService.getExecutorService().execute(task);
    }
}
