package net.njay.dynamicdatabase.util;

/**
 * Enum to represent the visibility of an object.
 *
 * @author NathanTheBook
 */
public enum Visibility {
    PUBLIC("public", "Public"),
    PRIVATE("private", "Private"),
    STAFF_ONLY("staff-only", "Staff Only");

    public String dbName;
    public String displayName;

    Visibility(String dbName, String displayName) {
        this.dbName = dbName;
        this.displayName = displayName;
    }
}
