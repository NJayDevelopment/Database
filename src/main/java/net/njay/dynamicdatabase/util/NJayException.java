package net.njay.dynamicdatabase.util;

import net.njay.dynamicdatabase.Document;
import org.mongodb.morphia.annotations.Entity;

/**
 * A handy little exception class.
 *
 * @author Austin Mayes
 */
public class NJayException extends Exception {
    public NJayException() {
        super();
    }

    public NJayException(String message) {
        super(message);
    }

    public NJayException(Exception e) {
        super(e);
    }

    public NJayException(Exception e, String message) {
        super(message, e);
    }

    public NJayException(String message, Exception e) {
        super(message, e);
    }

    public NJayException(Document document) {
        super(document.getClass().getAnnotation(Entity.class).value());
    }
}
