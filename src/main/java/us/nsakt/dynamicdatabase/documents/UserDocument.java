package us.nsakt.dynamicdatabase.documents;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Property;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Entity("users")
public class UserDocument extends Document {

    private UUID uuid;

    private List<String> usernames;

    @Property("last_username") private
    String lastUsername;

    private String email;

    @Property("last_sign_in_ip") private
    String lastSignInIp;

    @Property("mc_sign_ins") private
    int mcSignIns;

    @Property("last_sign_in") private
    Date lastSignIn;

    @Property("first_sign_in") private
    Date firstSignIn;

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

    public String getLastSignInIp() {
        return lastSignInIp;
    }

    public void setLastSignInIp(String lastSignInIp) {
        this.lastSignInIp = lastSignInIp;
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

    public enum MongoFields {
        id("_id"),
        UUID("uuid"),
        USERNAMES("usernames"),
        LAST_USERNAME("last_username"),
        EMAIL("email"),
        LAST_SIGN_IN_IP("last_sign_in_ip"),
        MC_SIGN_INS("mc_sign_ins"),
        LAST_SIGN_IN("last_sign_in"),
        FIRST_SIGN_IN("first_sign_in");

        public String fieldName;

        MongoFields(String fieldName) {
            this.fieldName = fieldName;
        }
    }
}