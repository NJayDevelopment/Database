package us.nsakt.dynamicdatabase.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import us.nsakt.dynamicdatabase.DynamicDatabasePlugin;
import us.nsakt.dynamicdatabase.daos.DAOService;
import us.nsakt.dynamicdatabase.tasks.GroupTasks;
import us.nsakt.dynamicdatabase.tasks.ServerTasks;
import us.nsakt.dynamicdatabase.tasks.UserTasks;

public class UserListener implements Listener {

    @EventHandler
    public static void handlePreLogin(final AsyncPlayerPreLoginEvent event) {
        boolean exists = DAOService.getUsers().exists(event.getUniqueId());
        if (!exists) {
            UserTasks.createUser(event.getUniqueId());
            GroupTasks.addPlayerToAllDefaults(event.getUniqueId());
            GroupTasks.LoginTasks.addPermsToMap(event.getUniqueId());
       }
    }

    @EventHandler(priority = EventPriority.HIGHEST) // Has to run first
    public static void onLogin(PlayerLoginEvent event) {
        UserTasks.updateUserFromEvent(event);
    }

    @EventHandler
    public static void addToOnline(final PlayerLoginEvent event) {
        ServerTasks.addPlayerToOnline(DynamicDatabasePlugin.getInstance().getCurrentServerDocument(), event.getPlayer());
    }

    @EventHandler
    public static void removeFromOnline(final PlayerQuitEvent event) {
        ServerTasks.removePlayerFromOnline(DynamicDatabasePlugin.getInstance().getCurrentServerDocument(), event.getPlayer());
    }
}
