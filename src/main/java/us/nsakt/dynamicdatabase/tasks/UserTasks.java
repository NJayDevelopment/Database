package us.nsakt.dynamicdatabase.tasks;

import com.google.common.collect.Lists;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerLoginEvent;
import org.mongodb.morphia.query.UpdateOperations;
import us.nsakt.dynamicdatabase.ConfigEnforcer;
import us.nsakt.dynamicdatabase.QueryExecutor;
import us.nsakt.dynamicdatabase.daos.DAOGetter;
import us.nsakt.dynamicdatabase.daos.Users;
import us.nsakt.dynamicdatabase.documents.UserDocument;
import us.nsakt.dynamicdatabase.tasks.core.QueryActionTask;
import us.nsakt.dynamicdatabase.tasks.core.SaveTask;
import us.nsakt.dynamicdatabase.util.NsaktException;

import javax.annotation.Nullable;
import java.util.Date;

/**
 * Different tasks for working with users.
 */
public class UserTasks {


    /**
     * Get the document's relative data access object.
     *
     * @return the document's relative data access object.
     */
    private static Users getDao() {
        return new DAOGetter().getUsers();
    }

    /**
     * Create a new user in the database
     *
     * @param player     Player to pull information from
     * @param fistSignIn Optional first sign in date
     */
    public static void createUser(final Player player, final @Nullable Date fistSignIn) {
        try {
            ConfigEnforcer.Documents.Users.ensureEnabled();
        } catch (NsaktException e) {
            // silence
        }
        SaveTask task = new SaveTask(getDao().getDatastore(), new UserDocument()) {
            @Override
            public void run() {
                UserDocument user = (UserDocument) getDocument();
                user.setFirstSignIn(fistSignIn);
                user.setUuid(player.getUniqueId());
                user.setUsernames(Lists.newArrayList(player.getName()));
                user.setLastUsername(player.getName());
                user.setMcSignIns(1);
                user.setLastSignIn(fistSignIn);
                getDao().save(user);
            }
        };
        QueryExecutor.getExecutorService().submit(task);
    }

    /**
     * Update a user's stats on a PlayerLoginEvent
     *
     * @param event the PlayerLoginEvent
     */
    public static void updateUserFromEvent(final PlayerLoginEvent event) {
        try {
            ConfigEnforcer.Documents.Users.ensureEnabled();
        } catch (NsaktException e) {
            // silence
        }
        QueryActionTask task = new QueryActionTask(getDao().getDatastore(), getDao().createQuery().field("UUID").equal(event.getPlayer().getUniqueId())) {
            @Override
            public void run() {
                UpdateOperations<UserDocument> updates = getDao().createUpdateOperations();
                updates.inc(UserDocument.MongoFields.MC_SIGN_INS.fieldName);
                updates.set(UserDocument.MongoFields.LAST_USERNAME.fieldName, event.getPlayer().getName());
                updates.add(UserDocument.MongoFields.USERNAMES.fieldName, event.getPlayer().getName());
                updates.set(UserDocument.MongoFields.LAST_SIGN_IN.fieldName, new Date());
                getDao().update(getQuery(), updates);
            }
        };
        QueryExecutor.getExecutorService().submit(task);
    }
}
