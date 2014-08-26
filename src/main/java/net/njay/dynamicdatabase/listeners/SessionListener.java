package net.njay.dynamicdatabase.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import net.njay.dynamicdatabase.tasks.SessionTasks;

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
