package us.nsakt.dynamicdatabase.util;

/**
 * Created by Nick on 7/5/14.
 */
public enum LogLevel {
    INFO(0), WARNING(1), SEVERE(2);

    int level;

    LogLevel(int level) {
        this.level = level;
    }

    public int getLevel() {
        return this.level;
    }
}