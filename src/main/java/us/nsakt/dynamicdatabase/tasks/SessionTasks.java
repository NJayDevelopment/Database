package us.nsakt.dynamicdatabase.tasks;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerLoginEvent;
import org.joda.time.Duration;
import us.nsakt.dynamicdatabase.ConfigEnforcer;
import us.nsakt.dynamicdatabase.Debug;
import us.nsakt.dynamicdatabase.DynamicDatabasePlugin;
import us.nsakt.dynamicdatabase.QueryExecutor;
import us.nsakt.dynamicdatabase.daos.DAOGetter;
import us.nsakt.dynamicdatabase.daos.Sessions;
import us.nsakt.dynamicdatabase.daos.Users;
import us.nsakt.dynamicdatabase.documents.SessionDocument;
import us.nsakt.dynamicdatabase.documents.UserDocument;
import us.nsakt.dynamicdatabase.tasks.core.SaveTask;
import us.nsakt.dynamicdatabase.util.LogLevel;
import us.nsakt.dynamicdatabase.util.NsaktException;

import java.util.Date;

/**
 * Different tasks for working with sessions.
 */
public class SessionTasks {


    /**
     * Get the document's relative data access object.
     *
     * @return the document's relative data access object.
     */
    private static Sessions getDao() {
        return new DAOGetter().getSessions();
    }

    /**
     * Start a session from a PlayerLoginEvent
     *
     * @param event Event to start the session from
     */
    public static void startSession(final PlayerLoginEvent event) {
        try {
            ConfigEnforcer.Documents.Sessions.ensureEnabled();
        } catch (NsaktException e) {
            // silence
        }
        final SessionDocument document = new SessionDocument();
        final UserDocument userDocument = new Users(UserDocument.class, DynamicDatabasePlugin.getInstance().getDatastores().get(UserDocument.class)).getUserFromPlayer(event.getPlayer());
        SaveTask task = new SaveTask(getDao().getDatastore(), document) {
            @Override
            public void run() {
                document.setServer(DynamicDatabasePlugin.getInstance().getCurrentServerDocument());
                document.setUser(userDocument);
                document.setStart(new Date());
                document.setIp(event.getPlayer().getAddress().getAddress().toString());
                getDao().save(document);
            }
        };
        QueryExecutor.getExecutorService().submit(task);
    }

    /**
     * End an already started session
     *
     * @param player          Player who owns the session
     * @param endedCorrectly  If the session was ended correctly (Actually was intentional)
     * @param endedWithPunish if the session was ended by a punishment
     */
    public static void endSession(final Player player, final boolean endedCorrectly, final boolean endedWithPunish) {
        try {
            ConfigEnforcer.Documents.Sessions.ensureEnabled();
        } catch (NsaktException e) {
            // silence
        }
        final UserDocument userDocument = new Users(UserDocument.class, DynamicDatabasePlugin.getInstance().getDatastores().get(UserDocument.class)).getUserFromPlayer(player);
        final SessionDocument sessionDocument = userDocument.getLastSession();
        SaveTask task = new SaveTask(getDao().getDatastore(), sessionDocument) {
            @Override
            public void run() {
                if (sessionDocument.getEnd() != null)
                    Debug.log(LogLevel.WARNING, "Tried to end an already ended session!");
                sessionDocument.setEnd(new Date());
                sessionDocument.setEndedCorrectly(endedCorrectly);
                sessionDocument.setEndedWithPunishment(endedWithPunish);
                sessionDocument.setLength(new Duration(sessionDocument.getStart().getTime() - sessionDocument.getEnd().getTime()));
                getDao().save(sessionDocument);
            }
        };
        QueryExecutor.getExecutorService().submit(task);
    }
}
