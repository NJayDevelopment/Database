package us.nsakt.dynamicdatabase.tasks;

import org.joda.time.Duration;
import us.nsakt.dynamicdatabase.ConfigEnforcer;
import us.nsakt.dynamicdatabase.MongoExecutionService;
import us.nsakt.dynamicdatabase.daos.DAOGetter;
import us.nsakt.dynamicdatabase.daos.Punishments;
import us.nsakt.dynamicdatabase.documents.PunishmentDocument;
import us.nsakt.dynamicdatabase.tasks.core.SaveTask;
import us.nsakt.dynamicdatabase.tasks.core.base.DBCallback;
import us.nsakt.dynamicdatabase.util.NsaktException;

import java.util.UUID;

/**
 * Basic Utility class to perform action related to punishment documents.
 *
 * @author NathanTheBook
 */
public class PunishmentTasks {
    private static Punishments getDao() {
        return new DAOGetter().getPunishments();
    }

    /**
     * Punish a UUID based on prior punishments.
     * <p/>
     * ----------| CALLBACK INFORMATION |----------
     * The callback is passed two objects, the UUID that is being punished, and the PunishmentDocument that was saved.
     *
     * @param uuid               UUID to punish
     * @param punishmentDocument Already formatted document with. (Expiration and type will be overwritten)
     * @param onFinish           Action to be performed when the punishment is saved
     */
    public static void punish(final UUID uuid, final PunishmentDocument punishmentDocument, final DBCallback onFinish) {
        try {
            ConfigEnforcer.Documents.Punishments.ensureEnabled();
        } catch (NsaktException e) {
        }
        SaveTask task = new SaveTask(getDao().getDatastore(), punishmentDocument) {
            @Override
            public void run() {
                int totalPunishments = (getDao().getAllPunishmentsOfType(uuid, PunishmentDocument.PunishmentType.KICK).size() + getDao().getAllPunishmentsOfType(uuid, PunishmentDocument.PunishmentType.BAN).size());
                PunishmentDocument.PunishmentType type;
                switch (totalPunishments) {
                    case 0:
                        type = PunishmentDocument.PunishmentType.KICK;
                        punishmentDocument.setType(type);
                    case 1:
                        type = PunishmentDocument.PunishmentType.BAN;
                        punishmentDocument.setExpires(Duration.standardDays(10));
                        punishmentDocument.setType(type);
                    default:
                        type = PunishmentDocument.PunishmentType.BAN;
                        punishmentDocument.setType(type);
                }
                getDao().save(punishmentDocument);
                onFinish.call(uuid, punishmentDocument);
            }
        };
        MongoExecutionService.getExecutorService().execute(task);
    }
}
