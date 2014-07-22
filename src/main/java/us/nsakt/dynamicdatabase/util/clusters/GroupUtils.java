package us.nsakt.dynamicdatabase.util.clusters;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.mongodb.morphia.Datastore;
import us.nsakt.dynamicdatabase.DynamicDatabasePlugin;
import us.nsakt.dynamicdatabase.documents.Group;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class GroupUtils {

    private static Datastore groupsDataStore = DynamicDatabasePlugin.getInstance().getDatastores().get(Group.class);

    /**
     * Check if a group's members contains a UUID
     *
     * @param group Group to look in
     * @param uuid  UUID to search for
     * @return If the UUID was in the group
     */
    public static boolean isInGroup(Group group, UUID uuid) {
        return group.getMembers().contains(uuid);
    }

    /**
     * Get all groups a UUId is in
     *
     * @param uuid UUID to search for
     * @return A list of groups the UUID was found in
     */
    public static List<Group> getAllGroups(UUID uuid) {
        List<Group> groups = Lists.newArrayList();
        groups = groupsDataStore.find(Group.class).field("members").contains(uuid.toString()).order("priority").asList();
        return groups;
    }

    /**
     * Get a nicely formatted HashMap of a group's permissions
     *
     * @param group Group to get the permissions from
     * @return a nicely formatted HashMap of a group's permissions
     */
    public static HashMap<Permission, Boolean> getGroupPermissions(Group group) {
        HashMap<Permission, Boolean> formattedPermissions = Maps.newHashMap();
        List<String> stringPerms = group.getMc_permissions();
        for (String permission : stringPerms) {
            if (permission.startsWith("#")) continue; // We can have comments in the permissions field. Yay!
            boolean add = !permission.startsWith("-");
            formattedPermissions.put(new Permission(add ? permission : permission.substring(1)), add);
        }
        return formattedPermissions;
    }

    /**
     * Get all permissions a UUID has
     *
     * @param uuid UUID to search for
     * @return all permissions the UUID has
     */
    public static HashMap<Permission, Boolean> getAllPermissions(UUID uuid) {
        HashMap<Permission, Boolean> result = Maps.newHashMap();
        for (Group group : getAllGroups(uuid)) {
            result.putAll(getGroupPermissions(group));
        }
        return result;
    }

    /**
     * Get a UUID's highest priority group
     *
     * @param uuid UUID to search for
     * @return a UUID's highest priority group
     */
    public static Group getHighestPriorityGroup(UUID uuid) {
        return groupsDataStore.find(Group.class).field("members").contains(uuid.toString()).order("priority").limit(1).get();
    }

    /**
     * Get all groups lower in priority than the group
     *
     * @param group Group to check priority against
     * @return all groups lower in priority than the group
     */
    public static List<Group> getLowerGroups(Group group) {
        return groupsDataStore.find(Group.class).field("priority").lessThanOrEq(group.getPriority()).asList();
    }

    // ----------- Tasks -----------

    /**
     * Assign permissions to a player
     *
     * @param player Player to assign permissions to
     */
    public static void assignPermissions(Player player) {
        UUID playerUUID = player.getUniqueId();
        HashMap<Permission, Boolean> permissions = getAllPermissions(playerUUID);
        for (Map.Entry<Permission, Boolean> entry : permissions.entrySet()) {
            PermissionAttachment attachment = player.addAttachment(DynamicDatabasePlugin.getInstance());
            attachment.setPermission(entry.getKey(), entry.getValue());
        }
        player.recalculatePermissions();
    }

    /**
     * Add a player to a group, then recalculate the player's permissions
     *
     * @param player Player to be added
     * @param group  Group to add the player to
     */
    public static void addPlayerToGroupAndRecalculate(Player player, Group group) {
        group.getMembers().add(player.getUniqueId());
        groupsDataStore.save(group);
        assignPermissions(player);
    }

    /**
     * Remove a player from a group, then recalculate the player's permissions
     *
     * @param player Player to be removed
     * @param group  Group to remove the player from
     */
    public static void removePlayerFromGroupAndRecalculate(Player player, Group group) {
        group.getMembers().remove(player.getUniqueId());
        groupsDataStore.save(group);
        assignPermissions(player);
    }

    /**
     * Creates the default group
     */
    public static void setupDefaultGroup() {
        Group group = new Group();
        group.setCluster(ClusterUtils.createDefaultAllCkuster());
        group.setName("default");
        group.setPriority(0);
        group.setMc_permissions(Lists.newArrayList(
                "#Hi, this is the default group created by DynamicDatabase",
                "#All players will be in this group",
                "#If this group is deleted, it will be re-created",
                "#DO NOT change this group's priority!",
                "#All groups above this group will inherit its permissions",
                "#You can negate said permissions by adding a '-' before the permission in the higher groups"
        ));
        groupsDataStore.save(group);
    }
}