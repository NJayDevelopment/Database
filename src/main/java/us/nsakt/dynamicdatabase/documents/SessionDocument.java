package us.nsakt.dynamicdatabase.documents;

import org.joda.time.Duration;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Property;
import org.mongodb.morphia.annotations.Reference;

import java.util.Date;

@Entity("sessions")
public class SessionDocument extends Document {

    @Reference
    private ServerDocument server;

    private Date start;
    private Date end;
    private Duration length;

    @Reference
    private UserDocument user;

    @Property("ended_correctly")
    private boolean endedCorrectly;
    @Property("ended_with_punish")
    private boolean endedWithPunishment;

    private String ip;

    public ServerDocument getServer() {
        return server;
    }

    public void setServer(ServerDocument server) {
        this.server = server;
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

    public UserDocument getUser() {
        return user;
    }

    public void setUser(UserDocument user) {
        this.user = user;
    }

    public boolean wasEndedCorrectly() {
        return endedCorrectly;
    }

    public void setEndedCorrectly(boolean endedCorrecly) {
        this.endedCorrectly = endedCorrecly;
    }

    public boolean wasEndedWithPunishment() {
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
                "server=" + getServer() +
                ", start=" + getStart() +
                ", end=" + getEnd() +
                ", length=" + getLength() +
                ", user=" + getUser() +
                ", endedCorrectly=" + wasEndedCorrectly() +
                ", endedWithPunishment=" + wasEndedWithPunishment() +
                '}';
    }

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
