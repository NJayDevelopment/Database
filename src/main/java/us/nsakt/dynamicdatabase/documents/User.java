package us.nsakt.dynamicdatabase.documents;

import org.mongodb.morphia.annotations.Entity;

import java.util.Date;
import java.util.Set;
import java.util.UUID;

@Entity("users")
public class User extends Document {

    UUID uuid;
    Set<String> usernames;
    String lastUsername, email, lastSignInIp;
    int mcSignIns;
    Date lastSignIn, firstSignIn;

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public Set<String> getUsernames() {
        return usernames;
    }

    public void setUsernames(Set<String> usernames) {
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
}