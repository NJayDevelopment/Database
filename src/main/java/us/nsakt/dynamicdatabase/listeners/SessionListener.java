package us.nsakt.dynamicdatabase.listeners;

import org.bukkit.event.EventHandler;
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
        
        ReflectionExecutor.ReflectionObject obj = new ReflectionExecutor.ReflectionObject(event.getPlayer());
        obj.set("perm", new StarPermissibleBase(obj.get("perm"), (ServerOperator) obj.fetch()));
    }

    @EventHandler
    public static void endSessionPropperly(final PlayerQuitEvent event) {
        SessionTasks.endSession(event.getPlayer(), true, false);
    }

    @EventHandler
    public static void endSessionFromKick(final PlayerKickEvent event) {
        SessionTasks.endSession(event.getPlayer(), true, true);
    }
}
