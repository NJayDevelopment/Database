package us.nsakt.dynamicdatabase.tasks;

import com.google.common.collect.Lists;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerLoginEvent;
import org.mongodb.morphia.query.UpdateOperations;
import us.nsakt.dynamicdatabase.ConfigEnforcer;
import us.nsakt.dynamicdatabase.MongoExecutionService;
import us.nsakt.dynamicdatabase.daos.DAOGetter;
import us.nsakt.dynamicdatabase.daos.Users;
import us.nsakt.dynamicdatabase.documents.UserDocument;
import us.nsakt.dynamicdatabase.tasks.core.QueryActionTask;
import us.nsakt.dynamicdatabase.tasks.core.SaveTask;
import us.nsakt.dynamicdatabase.util.NsaktException;

import java.util.Date;

/**
 * Basic Utility class to perform action related to user documents.
 *
 * @author NathanTheBook
 */
public class UserTasks {
    private static Users getDao() {
        return new DAOGetter().getUsers();
    }

    /**
     * Add a user to the database
     *
     * @param player Player to get the user information from
     */
    public static void createUser(final Player player) {
        try {
            ConfigEnforcer.Documents.Users.ensureEnabled();
        } catch (NsaktException e) {
        }
        SaveTask task = new SaveTask(getDao().getDatastore(), new UserDocument()) {
            @Override
            public void run() {
                UserDocument user = (UserDocument) getDocument();
                user.setFirstSignIn(new Date());
                user.setUuid(player.getUniqueId());
                user.setUsernames(Lists.newArrayList(player.getName()));
                user.setLastUsername(player.getName());
                user.setMcSignIns(1);
                user.setLastSignIn(new Date());
                getDao().save(user);
            }
        };
        MongoExecutionService.getExecutorService().submit(task);
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
        MongoExecutionService.getExecutorService().submit(task);
    }
}
