package us.nsakt.dynamicdatabase.tasks;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerLoginEvent;
import org.joda.time.Duration;
import us.nsakt.dynamicdatabase.Debug;
import us.nsakt.dynamicdatabase.DynamicDatabasePlugin;
import us.nsakt.dynamicdatabase.daos.Sessions;
import us.nsakt.dynamicdatabase.daos.Users;
import us.nsakt.dynamicdatabase.documents.SessionDocument;
import us.nsakt.dynamicdatabase.documents.UserDocument;

import java.util.Date;

/**
 * Different tasks for working with sessions.
 */
public class SessionTasks {

    Sessions sessions = new Sessions(SessionDocument.class, DynamicDatabasePlugin.getInstance().getDatastores().get(SessionDocument.class));

    /**
     * Get the document's relative data access object.
     *
     * @return the document's relative data access object.
     */
    private Sessions getDao() {
        return sessions;
    }

    /**
     * Start a session from a PlayerLoginEvent
     *
     * @param event Event to start the session from
     */
    public void startSession(PlayerLoginEvent event) {
        SessionDocument document = new SessionDocument();
        UserDocument userDocument = new Users(UserDocument.class, DynamicDatabasePlugin.getInstance().getDatastores().get(UserDocument.class)).getUserFromPlayer(event.getPlayer());
        document.setServer(DynamicDatabasePlugin.getInstance().getCurrentServerDocument());
        document.setUser(userDocument);
        document.setStart(new Date());
        document.setIp(event.getPlayer().getAddress().getAddress().toString());
        getDao().save(document);
    }

    /**
     * End an already started session
     *
     * @param player          Player who owns the session
     * @param endedCorrectly  If the session was ended correctly (Actually was intentional)
     * @param endedWithPunish if the session was ended by a punishment
     */
    public void endSession(Player player, boolean endedCorrectly, boolean endedWithPunish) {
        UserDocument userDocument = new Users(UserDocument.class, DynamicDatabasePlugin.getInstance().getDatastores().get(UserDocument.class)).getUserFromPlayer(player);
        SessionDocument sessionDocument = userDocument.getLastSession();
        if (sessionDocument.getEnd() != null) Debug.EXCEPTION.debug("Tried to end an already ended session!");
        sessionDocument.setEnd(new Date());
        sessionDocument.setEndedCorrectly(endedCorrectly);
        sessionDocument.setEndedWithPunishment(endedWithPunish);
        sessionDocument.setLength(new Duration(sessionDocument.getStart().getTime() - sessionDocument.getEnd().getTime()));
        getDao().save(sessionDocument);
    }
}
