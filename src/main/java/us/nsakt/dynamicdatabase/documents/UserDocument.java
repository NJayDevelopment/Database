package us.nsakt.dynamicdatabase.documents;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Property;
import org.mongodb.morphia.annotations.Reference;

import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Class to represent a user.
 */
@Entity("users")
public class UserDocument extends Document {

    private UUID uuid;

    private List<String> usernames;

    @Property("last_username")
    private String lastUsername;

    private String email;

    @Property("mc_sign_ins")
    private int mcSignIns;

    @Property("last_sign_in")
    private Date lastSignIn;

    @Property("first_sign_in")
    private Date firstSignIn;

    @Reference
    @Property("last_session")
    private SessionDocument lastSession;

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
        return lastSession;
    }

    public void setLastSession(SessionDocument lastSession) {
        this.lastSession = lastSession;
    }

    public enum MongoFields {
        id("_id"),
        UUID("uuid"),
        USERNAMES("usernames"),
        LAST_USERNAME("last_username"),
        EMAIL("email"),
        MC_SIGN_INS("mc_sign_ins"),
        LAST_SIGN_IN("last_sign_in"),
        FIRST_SIGN_IN("first_sign_in"),
        LAST_SESSION("last_session");

        public String fieldName;

        MongoFields(String fieldName) {
            this.fieldName = fieldName;
        }
    }
}