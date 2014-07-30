package us.nsakt.dynamicdatabase.daos;

import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.dao.BasicDAO;
import org.mongodb.morphia.query.Query;
import us.nsakt.dynamicdatabase.documents.PunishmentDocument;

import java.util.List;
import java.util.UUID;

/**
 * A dynamic way to interact with all punishments.
 * This is the proper place to put find methods.
 *
 * @author NathanTheBook
 */
public class Punishments extends BasicDAO<PunishmentDocument, ObjectId> {
    /**
     * Default constructor for an instance of the DAO.
     * DAO needs to be initiated before any finder methods can be ran.
     *
     * @param datastore The datastore that the documents are stored in.
     */
    public Punishments(Datastore datastore) {
        super(datastore);
    }

    /**
     * Get all punishments that have been issued to a UUID.
     * NOTE: For ban checks, callers need to check if the punishment is still active.
     *
     * @param uuid UUID to check punishments for.
     */
    public List<PunishmentDocument> getAllPunishments(final UUID uuid) {
        Query<PunishmentDocument> query = getDatastore().createQuery(PunishmentDocument.class);
        query.field(PunishmentDocument.MongoFields.PUNISHED.fieldName).equal(uuid);
        return query.asList();
    }

    /**
     * Get all punishments that have been issued to a UUID (of a certain PunishmentType).
     * NOTE: For ban checks, callers need to check if the punishment is still active.
     *
     * @param uuid     UUID to check punishments for.
     * @param type     Specific PunishmentType to search for
     */
    public List<PunishmentDocument> getAllPunishmentsOfType(final UUID uuid, final PunishmentDocument.PunishmentType type) {
        Query<PunishmentDocument> query = getDatastore().createQuery(PunishmentDocument.class);
        query.field(PunishmentDocument.MongoFields.PUNISHED.fieldName).equal(uuid);
        query.field(PunishmentDocument.MongoFields.TYPE.fieldName).equal(type);
        return query.asList();
    }
}