package us.nsakt.dynamicdatabase.daos;

import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.dao.BasicDAO;
import us.nsakt.dynamicdatabase.MongoExecutionService;
import us.nsakt.dynamicdatabase.documents.PunishmentDocument;
import us.nsakt.dynamicdatabase.tasks.core.QueryActionTask;
import us.nsakt.dynamicdatabase.tasks.core.base.DBCallback;

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
     * <p/>
     * ----------| CALLBACK INFORMATION |----------
     * The only object that is passed to the callback is a list of PunishmentDocuments.
     *
     * @param uuid     UUID to check punishments for.
     * @param callback Task to run on completion of the punishment query,
     */
    public void getAllPunishments(final UUID uuid, final DBCallback callback) {
        QueryActionTask task = new QueryActionTask(getDatastore(), getDatastore().createQuery(getEntityClazz())) {
            @Override
            public void run() {
                getQuery().field(PunishmentDocument.MongoFields.PUNISHED.fieldName).equal(uuid);
                callback.call(getQuery().asList());
            }
        };
        MongoExecutionService.getExecutorService().submit(task);
    }

    /**
     * Get all punishments that have been issued to a UUID (of a certain PunishmentType).
     * NOTE: For ban checks, callers need to check if the punishment is still active.
     * <p/>
     * ----------| CALLBACK INFORMATION |----------
     * The only object that is passed to the callback is a list of PunishmentDocuments.
     *
     * @param uuid     UUID to check punishments for.
     * @param type     Specific PunishmentType to search for
     * @param callback Task to run on completion of the punishment query,
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
        MongoExecutionService.getExecutorService().submit(task);
    }
}