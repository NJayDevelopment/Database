package us.nsakt.dynamicdatabase.datastores;

import com.google.common.collect.Lists;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerLoginEvent;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.UpdateOperations;
import us.nsakt.dynamicdatabase.DynamicDatabasePlugin;
import us.nsakt.dynamicdatabase.documents.UserDocument;

import javax.annotation.Nullable;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class Users {

    private static Datastore datastore = DynamicDatabasePlugin.getInstance().getDatastores().get(UserDocument.class);

    /**
     * Get the datastore that links to the users database.
     *
     * @return the datastore that links to the users database.
     */
    public static Datastore getDatastore() {
        return datastore;
    }

    /**
     * Get all users who have logged in with that username
     *
     * @param username Username to look for
     * @param limit    Limit of how many should be returned
     * @return all users who have logged in with that username
     */
    public static List<UserDocument> getAllMatchingUsers(String username, @Nullable Integer limit) {
        return datastore.find(UserDocument.class).field(UserDocument.MongoFields.USERNAMES.fieldName).contains(username).limit((limit != null) ? limit : 10000000).asList();
    }

    /**
     * See if a user is in the database
     *
     * @param uuid UUID to look for
     * @return If the user exists
     */
    public static boolean exists(UUID uuid) {
        return datastore.find(UserDocument.class).field(UserDocument.MongoFields.UUID.fieldName).equal(uuid).get() != null;
    }

    // ----------- Tasks -----------

    /**
     * Create a new user in the database
     *
     * @param player     Player to pull information from
     * @param fistSignIn Optional first sign in date
     */
    public static void createUser(Player player, @Nullable Date fistSignIn) {
        UserDocument user = new UserDocument();
        user.setFirstSignIn(fistSignIn);
        user.setUuid(player.getUniqueId());
        user.setUsernames(Lists.newArrayList(player.getName()));
        user.setLastUsername(player.getName());
        user.setMcSignIns(1);
        user.setLastSignIn(fistSignIn);
        datastore.save(user);
    }

    /**
     * Update a user's stats on a PlayerLoginEvent
     *
     * @param event the PlayerLoginEvent
     */
    public void updateUserFromEvent(PlayerLoginEvent event) {
        UUID query = event.getPlayer().getUniqueId();
        UserDocument result = datastore.find(UserDocument.class).field("UUID").equal(query).get();
        if (result == null) return;
        UpdateOperations<UserDocument> updates = datastore.createUpdateOperations(UserDocument.class);
        updates.inc("McSignIns");
        updates.set("lastUsername", event.getPlayer().getName());
        updates.add("usernames", event.getPlayer().getName());
        updates.set("lastSignInIp", event.getAddress().getAddress());
        updates.set("lastSignInAt", new Date());
        datastore.update(result, updates);
    }
}
