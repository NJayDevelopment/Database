package us.nsakt.dynamicdatabase.daos;

import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.dao.BasicDAO;
import us.nsakt.dynamicdatabase.QueryExecutor;
import us.nsakt.dynamicdatabase.documents.PunishmentDocument;
import us.nsakt.dynamicdatabase.tasks.core.QueryActionTask;
import us.nsakt.dynamicdatabase.tasks.core.base.DBCallback;

import java.util.UUID;

/**
 * Data access object to represent the punishments datastore.
 */
public class Punishments extends BasicDAO<PunishmentDocument, ObjectId> {

    /**
     * Constructor
     *
     * @param document  Document class to represent
     * @param datastore Datastore that contains the objects
     */
    public Punishments(Class<PunishmentDocument> document, Datastore datastore) {
        super(document, datastore);
    }

    /**
     * Get all punishments issued to a UUID
     *
     * @param uuid UUID to search for
     * @return all punishments issues to the UUID
     */
    public void getAllPunishments(final UUID uuid, final DBCallback callback) {
        QueryActionTask task = new QueryActionTask(getDatastore(), getDatastore().createQuery(getEntityClazz())) {
            @Override
            public void run() {
                getQuery().field(PunishmentDocument.MongoFields.PUNISHED.fieldName).equal(uuid);
                callback.call(getQuery().asList());
            }
        };
        QueryExecutor.getExecutorService().submit(task);
    }

    /**
     * Get all punishments issued to a UUID of a certain type
     *
     * @param uuid UUID to search for
     * @param type PunishmentType to search for
     * @return all punishments issued to the UUID of that type
     */
    public void getAllPunishmentsOfType(final UUID uuid, final PunishmentDocument.PunishmentType type, final DBCallback callback) {
        QueryActionTask task = new QueryActionTask(getDatastore(), getDatastore().createQuery(getEntityClazz())) {
            @Override
            public void run() {
                getQuery().field(PunishmentDocument.MongoFields.PUNISHED.fieldName).equal(uuid);
                getQuery().field(PunishmentDocument.MongoFields.TYPE.fieldName).equal(type);
                callback.call(getQuery().asList());
            }
        };
        QueryExecutor.getExecutorService().submit(task);
    }
}