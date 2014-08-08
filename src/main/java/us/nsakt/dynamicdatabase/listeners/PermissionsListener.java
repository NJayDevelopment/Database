package us.nsakt.dynamicdatabase.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.permissions.ServerOperator;
import us.nsakt.dynamicdatabase.daos.DAOService;
import us.nsakt.dynamicdatabase.tasks.GroupTasks;
import us.nsakt.dynamicdatabase.util.ReflectionExecutor;
import us.nsakt.dynamicdatabase.util.StarPermissibleBase;

public class PermissionsListener implements Listener {

    @EventHandler
    public static void addToMap(final AsyncPlayerPreLoginEvent event) {
        boolean exists = DAOService.getUsers().exists(event.getUniqueId());
        if (!exists) return;
        GroupTasks.addPlayerToAllDefaults(event.getUniqueId());
        GroupTasks.LoginTasks.addPermsToMap(event.getUniqueId());
    }

    @EventHandler
    public static void assignPermissions(final PlayerJoinEvent event) {
        ReflectionExecutor.ReflectionObject obj = new ReflectionExecutor.ReflectionObject(event.getPlayer());
        obj.set("perm", new StarPermissibleBase(obj.get("perm"), (ServerOperator) obj.fetch()));

        GroupTasks.LoginTasks.assignPermissions(event.getPlayer());
        GroupTasks.addGroupFlairs(event.getPlayer());
    }
}
