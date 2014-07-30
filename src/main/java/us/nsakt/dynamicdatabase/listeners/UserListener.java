package us.nsakt.dynamicdatabase.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import us.nsakt.dynamicdatabase.daos.DAOGetter;
import us.nsakt.dynamicdatabase.tasks.UserTasks;

public class UserListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST) // Has to run first
    public static void onLogin(PlayerLoginEvent event) {
        boolean exists = new DAOGetter().getUsers().exists(event.getPlayer().getUniqueId());
        if (exists) {
            UserTasks.updateUserFromEvent(event);
        } else {
            UserTasks.createUser(event.getPlayer());
        }
    }
}
