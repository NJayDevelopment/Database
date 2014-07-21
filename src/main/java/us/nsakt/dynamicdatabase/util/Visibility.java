package us.nsakt.dynamicdatabase.util;

/**
 * Class to represent the visibility of something.
 */
public enum Visibility {
    PUBLIC("public", "Public"),
    PRIVATE("private", "Private"),
    STAFF_ONLY("staff-only", "Staff Only");


    public String dbName;
    public String displayName;

    /**
     * Default constructor
     *
     * @param dbName name for database reference
     */
    Visibility(String dbName, String displayName) {
        this.dbName = dbName;
        this.displayName = displayName;
    }
}
