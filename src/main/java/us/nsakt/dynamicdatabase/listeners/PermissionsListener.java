package us.nsakt.dynamicdatabase.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import us.nsakt.dynamicdatabase.daos.DAOGetter;
import us.nsakt.dynamicdatabase.tasks.GroupTasks;

public class PermissionsListener implements Listener {

    @EventHandler
    public static void addToMap(final AsyncPlayerPreLoginEvent event) {
        boolean exists = new DAOGetter().getUsers().exists(event.getUniqueId());
        if (!exists) return;
        GroupTasks.addPlayerToAllDefaults(event.getUniqueId());
        GroupTasks.LoginTasks.addPermsToMap(event.getUniqueId());
    }

    @EventHandler
    public static void assignPermissions(final PlayerJoinEvent event) {
        GroupTasks.LoginTasks.assignPermissions(event.getPlayer());
        GroupTasks.addGroupFlairs(event.getPlayer());
    }
}
