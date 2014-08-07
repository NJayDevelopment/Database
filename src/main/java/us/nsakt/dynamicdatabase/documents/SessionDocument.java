package us.nsakt.dynamicdatabase.documents;

import org.bson.types.ObjectId;
import org.joda.time.Duration;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Property;
import us.nsakt.dynamicdatabase.daos.DAOService;

import java.util.Date;
import java.util.UUID;

/**
 * Class to represent a "session" document in the database.
 * DOCUMENT DESCRIPTION: This document is meant to serve as an easy way to represent a user's online sessions on the server and store them for future reference.
 *
 * @author NathanTheBook
 */
@Entity("dndb_sessions")
public class SessionDocument extends Document {
    private ObjectId server;
    private Date start;
    private Date end;
    private Duration length;
    private UUID user;
    @Property("ended_correctly")
    private boolean endedCorrectly;
    @Property("ended_with_punish")
    private boolean endedWithPunishment;
    private String ip;

    public ServerDocument getServer() {
        return DAOService.getServers().findOne(ServerDocument.MongoFields.id.fieldName, server);
    }

    public void setServer(ServerDocument server) {
        this.server = server.getObjectId();
    }

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    public Duration getLength() {
        return length;
    }

    public void setLength(Duration length) {
        this.length = length;
    }

    public UUID getUser() {
        return user;
    }

    public void setUser(UUID user) {
        this.user = user;
    }

    public boolean isEndedCorrectly() {
        return endedCorrectly;
    }

    public void setEndedCorrectly(boolean endedCorrectly) {
        this.endedCorrectly = endedCorrectly;
    }

    public boolean isEndedWithPunishment() {
        return endedWithPunishment;
    }

    public void setEndedWithPunishment(boolean endedWithPunishment) {
        this.endedWithPunishment = endedWithPunishment;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    @Override
    public String toString() {
        return "SessionDocument{" +
                "server=" + server +
                ", start=" + start +
                ", end=" + end +
                ", length=" + length +
                ", user=" + user +
                ", endedCorrectly=" + endedCorrectly +
                ", endedWithPunishment=" + endedWithPunishment +
                ", ip='" + ip + '\'' +
                '}';
    }

    /**
     * An enum representation of all fields in the class for reference in Mongo operations.
     */
    public enum MongoFields {
        id("_id"),
        SERVER("server"),
        START("start"),
        END("end"),
        LENGTH("length"),
        USER("user"),
        ENEDED_CORRECTLY("ended_correctly"),
        ENDED_WITH_PUNISH("ended_with_punish"),
        IP("ip"),
        EXPIRES("expires");

        public String fieldName;

        MongoFields(String fieldName) {
            this.fieldName = fieldName;
        }
    }
}
