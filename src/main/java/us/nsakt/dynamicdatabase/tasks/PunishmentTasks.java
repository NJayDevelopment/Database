package us.nsakt.dynamicdatabase.tasks;

import org.joda.time.Duration;
import us.nsakt.dynamicdatabase.ConfigEnforcer;
import us.nsakt.dynamicdatabase.QueryExecutor;
import us.nsakt.dynamicdatabase.daos.DAOGetter;
import us.nsakt.dynamicdatabase.daos.Punishments;
import us.nsakt.dynamicdatabase.documents.PunishmentDocument;
import us.nsakt.dynamicdatabase.tasks.core.SaveTask;
import us.nsakt.dynamicdatabase.util.NsaktException;

import java.util.List;
import java.util.UUID;

/**
 * Different tasks for working with punishments.
 */
public class PunishmentTasks {

    /**
     * Get the document's relative data access object.
     *
     * @return the document's relative data access object.
     */
    private static Punishments getDao() {
        return new DAOGetter().getPunishments();
    }

    /**
     * Punish a user.
     * This will take into account a user's other punishments and issue an appropriate punishment based on history.
     * This will either produce a kick, ban, or permanent ban.
     * The default ban time is 10 days.
     * This will also save the document.
     *
     * @param uuid               UUID to punish
     * @param punishmentDocument Already generated document to set the type and expiration date for.
     */
    public static void punish(final UUID uuid, final PunishmentDocument punishmentDocument) {
        try {
            ConfigEnforcer.Documents.Punishments.ensureEnabled();
        } catch (NsaktException e) {
            // silence
        }
        SaveTask task = new SaveTask(getDao().getDatastore(), punishmentDocument) {
            @Override
            public void run() {
                PunishmentDocument.PunishmentType type = PunishmentDocument.PunishmentType.UNKNOWN;
                List<PunishmentDocument> kicks = getDao().getAllPunishmentsOfType(uuid, PunishmentDocument.PunishmentType.KICK);
                List<PunishmentDocument> bans = getDao().getAllPunishmentsOfType(uuid, PunishmentDocument.PunishmentType.BAN);

                int kickSize = kicks.size();
                int banSize = bans.size();
                int totalPunishments = kickSize + banSize;

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
            }
        };
        QueryExecutor.getExecutorService().submit(task);
    }
}
