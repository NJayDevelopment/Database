package us.nsakt.dynamicdatabase.util;

/**
 * Class to represent the visibility of something.
 */
public enum Visibility {
    PUBLIC("public"),
    PRIVATE("private"),
    STAFF_ONLY("staff-only");


    public String dbName;

    /**
     * Default constructor
     *
     * @param dbName name for database reference
     */
    Visibility(String dbName) {
        this.dbName = dbName;
    }
}
