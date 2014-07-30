package us.nsakt.dynamicdatabase.tasks;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerLoginEvent;
import org.joda.time.Duration;
import us.nsakt.dynamicdatabase.ConfigEnforcer;
import us.nsakt.dynamicdatabase.Debug;
import us.nsakt.dynamicdatabase.DynamicDatabasePlugin;
import us.nsakt.dynamicdatabase.daos.DAOGetter;
import us.nsakt.dynamicdatabase.daos.Sessions;
import us.nsakt.dynamicdatabase.documents.SessionDocument;
import us.nsakt.dynamicdatabase.documents.UserDocument;
import us.nsakt.dynamicdatabase.tasks.core.base.DBCallback;
import us.nsakt.dynamicdatabase.util.NsaktException;

import java.util.Date;

/**
 * Basic Utility class to perform action related to session documents.
 *
 * @author NathanTheBook
 */
public class SessionTasks {
    private static Sessions getDao() {
        return new DAOGetter().getSessions();
    }

    /**
     * Start a new player session from a PlayerLoginEvent.
     *
     * @param event Event that is starting the session
     */
    public static void startSession(final PlayerLoginEvent event) {
        try {
            ConfigEnforcer.Documents.Sessions.ensureEnabled();
        } catch (NsaktException e) {
        }
        final SessionDocument document = new SessionDocument();
        document.setServer(DynamicDatabasePlugin.getInstance().getCurrentServerDocument());
        document.setUser(new DAOGetter().getUsers().getUserFromPlayer(event.getPlayer()));
        document.setStart(new Date());
        document.setIp(event.getPlayer().getAddress().getAddress().toString());
        getDao().save(document);
    }

    /**
     * End a player's session
     *
     * @param player          Player that is quitting
     * @param endedCorrectly  If the session was ended correctly (The player disconnected correctly)
     * @param endedWithPunish If the player was kicked from the server
     */
    public static void endSession(final Player player, final boolean endedCorrectly, final boolean endedWithPunish) {
        try {
            ConfigEnforcer.Documents.Sessions.ensureEnabled();
        } catch (NsaktException e) {
        }
        final UserDocument userDocument = new DAOGetter().getUsers().getUserFromPlayer(player);
        final SessionDocument sessionDocument = userDocument.getLastSession();
        if (sessionDocument.getEnd() != null)
            Debug.log(Debug.LogLevel.WARNING, "Tried to end an already ended session!");
        sessionDocument.setEnd(new Date());
        sessionDocument.setEndedCorrectly(endedCorrectly);
        sessionDocument.setEndedWithPunishment(endedWithPunish);
        sessionDocument.setLength(new Duration(sessionDocument.getStart().getTime() - sessionDocument.getEnd().getTime()));
        getDao().save(sessionDocument);
    }
}
