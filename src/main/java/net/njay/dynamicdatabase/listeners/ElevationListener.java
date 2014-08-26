package net.njay.dynamicdatabase.listeners;

import it.mapdev.elevation.Elevation;
import it.mapdev.elevation.map.ElevationWorld;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import net.njay.dynamicdatabase.daos.DAOService;
import net.njay.dynamicdatabase.documents.GroupDocument;
import net.njay.dynamicdatabase.tasks.GroupTasks;

/**
 * Created by Austin on 8/16/14.
 */
public class ElevationListener implements Listener {

    static GroupDocument buildersGroup;
    static GroupDocument ownerGroup;

    static {
        buildersGroup = DAOService.getGroups().findOne(GroupDocument.MongoFields.NAME.fieldName, "elevationBuilders");
        ownerGroup = DAOService.getGroups().findOne(GroupDocument.MongoFields.NAME.fieldName, "elevationOwner");
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        updatePermissions(event.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        GroupTasks.removePlayerFromGroupAndRecalculate(event.getPlayer(), buildersGroup);
        GroupTasks.removePlayerFromGroupAndRecalculate(event.getPlayer(), ownerGroup);
    }

    @EventHandler
    public void onPlayerChangeWorld(PlayerChangedWorldEvent event) {
        updatePermissions(event.getPlayer());
    }

    private void updatePermissions(final Player player) {
        if (buildersGroup == null || ownerGroup == null) return;
        GroupTasks.removePlayerFromGroupAndRecalculate(player, buildersGroup);
        GroupTasks.removePlayerFromGroupAndRecalculate(player, ownerGroup);
        ElevationWorld world = Elevation.getInstance().getWorldContext().getElevationWorld(player.getWorld().getName());
        if (world == null)
            return;
        if (world.canBuild(player))
            GroupTasks.addPlayerToGroupAndRecalculate(player, buildersGroup);
        if (world.isOwner(player))
            GroupTasks.addPlayerToGroupAndRecalculate(player, ownerGroup);
    }
}
