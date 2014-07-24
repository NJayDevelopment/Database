package us.nsakt.dynamicdatabase.tasks;

import org.bukkit.Bukkit;
import org.joda.time.Duration;
import us.nsakt.dynamicdatabase.DynamicDatabasePlugin;
import us.nsakt.dynamicdatabase.daos.Punishments;
import us.nsakt.dynamicdatabase.documents.PunishmentDocument;
import us.nsakt.dynamicdatabase.tasks.runners.PunishmentTask;

import java.util.List;
import java.util.UUID;

/**
 * Different tasks for working with punishments.
 */
public class PunishmentTasks {

    Punishments punishments = new Punishments(PunishmentDocument.class, DynamicDatabasePlugin.getInstance().getDatastores().get(PunishmentDocument.class));

    /**
     * Get the document's relative data access object.
     *
     * @return the document's relative data access object.
     */
    private Punishments getDao() {
        return punishments;
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
    public void punish(final UUID uuid, final PunishmentDocument punishmentDocument) {
        PunishmentTask task = new PunishmentTask(getDao().getDatastore(), punishmentDocument) {
            @Override
            public void run() {
                PunishmentDocument.PunishmentType type = PunishmentDocument.PunishmentType.UNKNOW;
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
        Bukkit.getScheduler().runTaskAsynchronously(DynamicDatabasePlugin.getInstance(), task);
    }
}
