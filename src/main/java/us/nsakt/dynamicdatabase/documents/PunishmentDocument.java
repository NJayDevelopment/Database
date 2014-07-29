package us.nsakt.dynamicdatabase.documents;

import com.sk89q.minecraft.util.commands.ChatColor;
import org.joda.time.Duration;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Reference;

import java.util.Date;

/**
 * Class to represent a punishment to a user.
 */
@Entity("punishments")
public class PunishmentDocument extends Document {

    @Reference
    private UserDocument punisher;

    @Reference
    private UserDocument punished;

    private String reason;

    @Reference
    private ServerDocument server;

    private PunishmentType type;

    @Reference
    private ClusterDocument cluster;

    private boolean active;
    private boolean appealable;
    private boolean automatic;
    private Date when;
    private Duration expires;

    public UserDocument getPunisher() {
        return punisher;
    }

    public void setPunisher(UserDocument punisher) {
        this.punisher = punisher;
    }

    public UserDocument getPunished() {
        return punished;
    }

    public void setPunished(UserDocument punished) {
        this.punished = punished;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public ServerDocument getServer() {
        return server;
    }

    public void setServer(ServerDocument server) {
        this.server = server;
    }

    public PunishmentType getType() {
        return type;
    }

    public void setType(PunishmentType type) {
        this.type = type;
    }

    public ClusterDocument getCluster() {
        return cluster;
    }

    public void setCluster(ClusterDocument cluster) {
        this.cluster = cluster;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isAppealable() {
        return appealable;
    }

    public void setAppealable(boolean appealable) {
        this.appealable = appealable;
    }

    public boolean isAutomatic() {
        return automatic;
    }

    public void setAutomatic(boolean automatic) {
        this.automatic = automatic;
    }

    public Date getWhen() {
        return when;
    }

    public void setWhen(Date when) {
        this.when = when;
    }

    public Duration getExpires() {
        return expires;
    }

    public void setExpires(Duration expires) {
        this.expires = expires;
    }

    @Override
    public String toString() {
        return "PunishmentDocument{" +
                "punisher=" + getPunisher() +
                ", punished=" + getPunished() +
                ", reason='" + getReason() + '\'' +
                ", server=" + getServer() +
                ", type='" + getType() + '\'' +
                ", cluster=" + getCluster() +
                ", active=" + isActive() +
                ", appealable=" + isAppealable() +
                ", automatic=" + isAutomatic() +
                ", when=" + getWhen() +
                ", expires=" + getExpires() +
                '}';
    }

    public String generateKickmessage() {
        StringBuilder builder = new StringBuilder();

        builder.append(ChatColor.RED).append("You have been ").append(ChatColor.GOLD).append(this.getType().past).append("!");

        return builder.toString();
    }

    public enum PunishmentType {
        WARN("warned"),
        KICK("kicked"),
        BAN("banned"),
        UNKNOWN(null);

        String past;

        PunishmentType(String past) {
            this.past = past;
        }
    }

    public enum MongoFields {
        id("_id"),
        PUNISHER("punished"),
        PUNISHED("punisher"),
        REASON("reason"),
        SERVER("server"),
        TYPE("type"),
        CLUSTER("cluster"),
        ACTIVE("active"),
        APPEALABLE("appealable"),
        AUTOMATIC("automatic"),
        WHEN("when"),
        EXPIRES("expires");

        public String fieldName;

        MongoFields(String fieldName) {
            this.fieldName = fieldName;
        }
    }
}
