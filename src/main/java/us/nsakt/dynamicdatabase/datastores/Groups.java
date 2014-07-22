package us.nsakt.dynamicdatabase.datastores;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.mongodb.morphia.Datastore;
import us.nsakt.dynamicdatabase.DynamicDatabasePlugin;
import us.nsakt.dynamicdatabase.documents.GroupDocument;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Groups {

    private static Datastore datastore = DynamicDatabasePlugin.getInstance().getDatastores().get(GroupDocument.class);

    /**
     * Check if a group's members contains a UUID
     *
     * @param groupDocument Group to look in
     * @param uuid  UUID to search for
     * @return If the UUID was in the group
     */
    public static boolean isInGroup(GroupDocument groupDocument, UUID uuid) {
        return groupDocument.getMembers().contains(uuid);
    }

    /**
     * Get all groups a UUId is in
     *
     * @param uuid UUID to search for
     * @return A list of groups the UUID was found in
     */
    public static List<GroupDocument> getAllGroups(UUID uuid) {
        List<GroupDocument> groupDocuments = Lists.newArrayList();
        groupDocuments = datastore.find(GroupDocument.class).field("members").contains(uuid.toString()).order("priority").asList();
        return groupDocuments;
    }

    /**
     * Get a nicely formatted HashMap of a group's permissions
     *
     * @param groupDocument Group to get the permissions from
     * @return a nicely formatted HashMap of a group's permissions
     */
    public static HashMap<Permission, Boolean> getGroupPermissions(GroupDocument groupDocument) {
        HashMap<Permission, Boolean> formattedPermissions = Maps.newHashMap();
        List<String> stringPerms = groupDocument.getMc_permissions();
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
        for (GroupDocument groupDocument : getAllGroups(uuid)) {
            result.putAll(getGroupPermissions(groupDocument));
        }
        return result;
    }

    /**
     * Get a UUID's highest priority group
     *
     * @param uuid UUID to search for
     * @return a UUID's highest priority group
     */
    public static GroupDocument getHighestPriorityGroup(UUID uuid) {
        return datastore.find(GroupDocument.class).field("members").contains(uuid.toString()).order("priority").limit(1).get();
    }

    /**
     * Get all groups lower in priority than the group
     *
     * @param groupDocument Group to check priority against
     * @return all groups lower in priority than the group
     */
    public static List<GroupDocument> getLowerGroups(GroupDocument groupDocument) {
        return datastore.find(GroupDocument.class).field("priority").lessThanOrEq(groupDocument.getPriority()).asList();
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
     * @param groupDocument  Group to add the player to
     */
    public static void addPlayerToGroupAndRecalculate(Player player, GroupDocument groupDocument) {
        groupDocument.getMembers().add(player.getUniqueId());
        datastore.save(groupDocument);
        assignPermissions(player);
    }

    /**
     * Remove a player from a group, then recalculate the player's permissions
     *
     * @param player Player to be removed
     * @param groupDocument  Group to remove the player from
     */
    public static void removePlayerFromGroupAndRecalculate(Player player, GroupDocument groupDocument) {
        groupDocument.getMembers().remove(player.getUniqueId());
        datastore.save(groupDocument);
        assignPermissions(player);
    }

    /**
     * Creates the default group
     */
    public static void setupDefaultGroup() {
        GroupDocument groupDocument = new GroupDocument();
        groupDocument.setCluster(Clusters.createDefaultAllCkuster());
        groupDocument.setName("default");
        groupDocument.setPriority(0);
        groupDocument.setMc_permissions(Lists.newArrayList(
                "#Hi, this is the default group created by DynamicDatabase",
                "#All players will be in this group",
                "#If this group is deleted, it will be re-created",
                "#DO NOT change this group's priority!",
                "#All groups above this group will inherit its permissions",
                "#You can negate said permissions by adding a '-' before the permission in the higher groups"
        ));
        datastore.save(groupDocument);
    }

    public static Datastore getDatastore() {
        return datastore;
    }
}