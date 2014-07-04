package us.nsakt.dynamicdatabase.documents;

import org.mongodb.morphia.annotations.Embedded;

/**
 * Base class for all Embedded Mongo Documents
 * <p/>
 * Subclasses will not have an Id, see {@link org.mongodb.morphia.annotations.Reference}
 */
@Embedded
public abstract class EmbeddedDocument {

    /**
     * Default constructor for EmbeddedDocument
     */
    public EmbeddedDocument() {
    }

}
