package us.nsakt.dynamicdatabase.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.permissions.ServerOperator;

import us.nsakt.dynamicdatabase.tasks.SessionTasks;
import us.nsakt.dynamicdatabase.util.ReflectionExecutor;
import us.nsakt.dynamicdatabase.util.StarPermissibleBase;

public class SessionListener implements Listener {

    @EventHandler
    public static void beginSession(final PlayerJoinEvent event) {
        SessionTasks.startSession(event);

    }

    @EventHandler
    public static void endSessionPropperly(final PlayerQuitEvent event) {
        SessionTasks.endSession(event.getPlayer(), true, false);
    }
}
