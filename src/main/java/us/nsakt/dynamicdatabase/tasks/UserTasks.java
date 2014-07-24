package us.nsakt.dynamicdatabase.tasks;

import com.google.common.collect.Lists;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerLoginEvent;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import us.nsakt.dynamicdatabase.DynamicDatabasePlugin;
import us.nsakt.dynamicdatabase.daos.Users;
import us.nsakt.dynamicdatabase.documents.UserDocument;

import javax.annotation.Nullable;
import java.util.Date;
import java.util.UUID;

public class UserTasks {

    Users users = new Users(UserDocument.class, DynamicDatabasePlugin.getInstance().getDatastores().get(UserDocument.class));

    private Users getDao() {
        return users;
    }

    /**
     * Create a new user in the database
     *
     * @param player     Player to pull information from
     * @param fistSignIn Optional first sign in date
     */
    public void createUser(Player player, @Nullable Date fistSignIn) {
        UserDocument user = new UserDocument();
        user.setFirstSignIn(fistSignIn);
        user.setUuid(player.getUniqueId());
        user.setUsernames(Lists.newArrayList(player.getName()));
        user.setLastUsername(player.getName());
        user.setMcSignIns(1);
        user.setLastSignIn(fistSignIn);
        getDao().save(user);
    }

    /**
     * Update a user's stats on a PlayerLoginEvent
     *
     * @param event the PlayerLoginEvent
     */
    public void updateUserFromEvent(PlayerLoginEvent event) {
        UUID query = event.getPlayer().getUniqueId();
        Query<UserDocument> result = getDao().createQuery().field("UUID").equal(query);
        if (result.get() == null) return;
        UpdateOperations<UserDocument> updates = getDao().createUpdateOperations();
        updates.inc(UserDocument.MongoFields.MC_SIGN_INS.fieldName);
        updates.set(UserDocument.MongoFields.LAST_USERNAME.fieldName, event.getPlayer().getName());
        updates.add(UserDocument.MongoFields.USERNAMES.fieldName, event.getPlayer().getName());
        updates.set(UserDocument.MongoFields.LAST_SIGN_IN_IP.fieldName, event.getAddress().getAddress());
        updates.set(UserDocument.MongoFields.LAST_SIGN_IN.fieldName, new Date());
        getDao().update(result, updates);
    }
}
