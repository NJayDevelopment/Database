package us.nsakt.dynamicdatabase.documents;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Property;
import us.nsakt.dynamicdatabase.daos.DAOService;

import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Class to represent a "user" document in the database.
 * DOCUMENT DESCRIPTION: This document is meant to serve as an easy way to represent a Bukkit player and store their information.
 * NOTE: This class is UUID ready!
 *
 * @author NathanTheBook
 */
@Entity("dndb_users")
public class UserDocument extends Document {
    private UUID uuid;
    private List<String> usernames;
    @Property("last_username")
    private String lastUsername;
    private String email;
    @Property("mc_sign_ins")
    private int mcSignIns;
    @Property("mc_last_sign_in")
    private Date lastSignIn;
    @Property("mc_first_sign_in")
    private Date firstSignIn;
    @Property("mc_last_session")
    private ObjectId lastSession;

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public List<String> getUsernames() {
        return usernames;
    }

    public void setUsernames(List<String> usernames) {
        this.usernames = usernames;
    }

    public String getLastUsername() {
        return lastUsername;
    }

    public void setLastUsername(String lastUsername) {
        this.lastUsername = lastUsername;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getMcSignIns() {
        return mcSignIns;
    }

    public void setMcSignIns(int mcSignIns) {
        this.mcSignIns = mcSignIns;
    }

    public Date getLastSignIn() {
        return lastSignIn;
    }

    public void setLastSignIn(Date lastSignIn) {
        this.lastSignIn = lastSignIn;
    }

    public Date getFirstSignIn() {
        return firstSignIn;
    }

    public void setFirstSignIn(Date firstSignIn) {
        this.firstSignIn = firstSignIn;
    }

    public SessionDocument getLastSession() {
        return DAOService.getSessions().findOne(SessionDocument.MongoFields.id.fieldName, lastSession);
    }

    public void setLastSession(SessionDocument session) {
        this.lastSession = session.getObjectId();
    }

    @Override
    public String toString() {
        return "UserDocument{" +
                "lastSession=" + lastSession +
                ", uuid=" + uuid +
                ", usernames=" + usernames +
                ", lastUsername='" + lastUsername + '\'' +
                ", email='" + email + '\'' +
                ", mcSignIns=" + mcSignIns +
                ", lastSignIn=" + lastSignIn +
                ", firstSignIn=" + firstSignIn +
                '}';
    }

    /**
     * An enum representation of all fields in the class for reference in Mongo operations.
     */
    public enum MongoFields {
        id("_id"),
        UUID("uuid"),
        USERNAMES("usernames"),
        LAST_USERNAME("last_username"),
        EMAIL("email"),
        MC_SIGN_INS("mc_sign_ins"),
        LAST_SIGN_IN("mc_last_sign_in"),
        FIRST_SIGN_IN("mc_first_sign_in"),
        LAST_SESSION("mc_last_session");

        public String fieldName;

        MongoFields(String fieldName) {
            this.fieldName = fieldName;
        }
    }
}