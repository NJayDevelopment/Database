package us.nsakt.dynamicdatabase.tasks;

import org.bukkit.event.player.PlayerLoginEvent;
import org.mongodb.morphia.query.UpdateOperations;
import us.nsakt.dynamicdatabase.ConfigEnforcer;
import us.nsakt.dynamicdatabase.MongoExecutionService;
import us.nsakt.dynamicdatabase.daos.DAOService;
import us.nsakt.dynamicdatabase.daos.Users;
import us.nsakt.dynamicdatabase.documents.UserDocument;
import us.nsakt.dynamicdatabase.tasks.core.QueryActionTask;
import us.nsakt.dynamicdatabase.tasks.core.SaveTask;
import us.nsakt.dynamicdatabase.util.NsaktException;

import java.util.Date;
import java.util.UUID;

/**
 * Basic Utility class to perform action related to user documents.
 *
 * @author NathanTheBook
 */
public class UserTasks {
    private static Users getDao() {
        return DAOService.getUsers();
    }

    /**
     * Add a user to the database
     *
     * @param uuid UUID to get the user information from
     */
    public static void createUser(final UUID uuid) {
        try {
            ConfigEnforcer.Documents.Users.ensureEnabled();
        } catch (NsaktException e) {
        }
        SaveTask task = new SaveTask(getDao().getDatastore(), new UserDocument()) {
            @Override
            public void run() {
                UserDocument user = (UserDocument) getDocument();
                user.setFirstSignIn(new Date());
                user.setUuid(uuid);
                user.setMcSignIns(0);
                user.setLastSignIn(new Date());
                getDao().save(user);
            }
        };
        MongoExecutionService.getExecutorService().execute(task);
    }

    /**
     * Update a user's information when they sign in.
     *
     * @param event Event to update player information from
     */
    public static void updateUserFromEvent(final PlayerLoginEvent event) {
        try {
            ConfigEnforcer.Documents.Users.ensureEnabled();
        } catch (NsaktException e) {
        }
        // Add listeners here for future reference.
        QueryActionTask<UserDocument> task = new QueryActionTask<UserDocument>(getDao().getDatastore(), getDao().createQuery().field("uuid").equal(event.getPlayer().getUniqueId())) {
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
        MongoExecutionService.getExecutorService().execute(task);
    }
}
