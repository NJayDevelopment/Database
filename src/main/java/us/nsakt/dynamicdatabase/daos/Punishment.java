package us.nsakt.dynamicdatabase.daos;

import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.dao.BasicDAO;
import us.nsakt.dynamicdatabase.documents.PunishmentDocument;

import java.util.List;
import java.util.UUID;

/**
 * Data access object to represent the punishments datastore.
 */
public class Punishment extends BasicDAO<PunishmentDocument, ObjectId> {

    /**
     * Constructor
     *
     * @param document  Document class to represent
     * @param datastore Datastore that contains the objects
     */
    public Punishment(Class<PunishmentDocument> document, Datastore datastore) {
        super(document, datastore);
    }

    /**
     * Get all punishments issued to a UUID
     *
     * @param uuid UUID to search for
     * @return all punishments issues to the UUID
     */
    public List<PunishmentDocument> getAllPunishments(UUID uuid) {
        return getDatastore().find(PunishmentDocument.class).field(PunishmentDocument.MongoFields.PUNISHED.fieldName).equal(uuid).asList();
    }

    /**
     * Get all punishments issued to a UUID of a certain type
     *
     * @param uuid UUID to search for
     * @param type PunishmentType to search for
     * @return all punishments issued to the UUID of that type
     */
    public List<PunishmentDocument> getAllPunishmentsOfType(UUID uuid, PunishmentDocument.PunishmentType type) {
        return getDatastore().find(PunishmentDocument.class).field(PunishmentDocument.MongoFields.PUNISHED.fieldName).equal(uuid).field(PunishmentDocument.MongoFields.TYPE.fieldName).equal(type).asList();
    }
}
