package us.nsakt.dynamicdatabase.documents;

import com.sk89q.minecraft.util.commands.ChatColor;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.mongodb.morphia.annotations.Entity;
import us.nsakt.dynamicdatabase.Config;
import us.nsakt.dynamicdatabase.daos.DAOService;
import us.nsakt.dynamicdatabase.util.StringUtils;

import java.util.Date;
import java.util.UUID;

/**
 * Class to represent a "punishment" document in the database.
 * DOCUMENT DESCRIPTION: This document is meant to serve as a way to remove a player from the server (temporarily or permanently) or warn them for breaking the rules.
 *
 * @author NathanTheBook
 */
@Entity("dndb_punishments")
public class PunishmentDocument extends Document {
    private UUID punisher;
    private UUID punished;
    private String reason;
    private ObjectId server;
    private PunishmentType type;
    private ObjectId cluster;
    private boolean active;
    private boolean appealable;
    private boolean automatic;
    private Date when;
    private Date expires;

    public UUID getPunisher() {
        return punisher;
    }

    public void setPunisher(UUID punisher) {
        this.punisher = punisher;
    }

    public UUID getPunished() {
        return punished;
    }

    public void setPunished(UUID punished) {
        this.punished = punished;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public ServerDocument getServer() {
        return DAOService.getServers().findOne(ServerDocument.MongoFields.id.fieldName, cluster);
    }

    public void setServer(ServerDocument server) {
        this.cluster = server.getObjectId();
    }

    public PunishmentType getType() {
        return type;
    }

    public void setType(PunishmentType type) {
        this.type = type;
    }

    public ClusterDocument getCluster() {
        return DAOService.getClusters().findOne(ClusterDocument.MongoFields.id.fieldName, cluster);
    }

    public void setCluster(ClusterDocument cluster) {
        this.cluster = cluster.getObjectId();
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

    public Date getExpires() {
        return expires;
    }

    public void setExpires(Date expires) {
        this.expires = expires;
    }

    @Override
    public String toString() {
        return "PunishmentDocument{" +
                "punisher=" + punisher +
                ", punished=" + punished +
                ", reason='" + reason + '\'' +
                ", server=" + server +
                ", type=" + type +
                ", cluster=" + cluster +
                ", active=" + active +
                ", appealable=" + appealable +
                ", automatic=" + automatic +
                ", when=" + when +
                ", expires=" + expires +
                '}';
    }

    /**
     * Generate a kick message for the certain punishment
     *
     * @return A nicely formatted kick message.
     */
    public String generateKickMessage() {
        StringBuilder builder = new StringBuilder();
        builder.append(ChatColor.RED).append("You have been ").append(ChatColor.GOLD).append(this.getType().past).append("!");
        if (this.getType().equals(PunishmentType.BAN)) {
            builder.append("\n");
            builder.append(ChatColor.DARK_GREEN).append("Expires: ").append(ChatColor.BLUE);
            builder.append(this.getExpires() == null? "Never" : new DateTime(this.getExpires()).toString("MM/dd/yyyy"));
        }
        builder.append("\n\n").append(ChatColor.BLUE).append(this.getReason());
        if (Config.Documents.Punishments.Appeals.enabled)
            builder.append("\n\n").append(ChatColor.AQUA).append("Appeal at: ").append(ChatColor.GOLD).append(Config.Documents.Punishments.Appeals.url);
        return builder.toString();
    }

    public String generateWarnMessage() {
        StringBuilder builder = new StringBuilder();
        builder.append(StringUtils.padMessage(" WARNING ", "-", ChatColor.DARK_RED, ChatColor.GOLD));
        builder.append("\n");
        builder.append(ChatColor.RESET.toString() + ChatColor.YELLOW + ChatColor.BOLD + this.getReason());
        builder.append("\n");
        builder.append(StringUtils.padMessage(" WARNING ", "-", ChatColor.DARK_RED, ChatColor.GOLD));
        return builder.toString();
    }

    public enum PunishmentType {
        WARN("warned"),
        KICK("kicked"),
        BAN("banned"),
        UNKNOWN(null);
        public String past;

        PunishmentType(String past) {
            this.past = past;
        }
    }

    /**
     * An enum representation of all fields in the class for reference in Mongo operations.
     */
    public enum MongoFields {
        id("_id"),
        PUNISHER("punisher"),
        PUNISHED("punished"),
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
