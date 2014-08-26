package net.njay.dynamicdatabase.tasks;

import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.joda.time.DateTime;
import net.njay.dynamicdatabase.Config;
import net.njay.dynamicdatabase.ConfigEnforcer;
import net.njay.dynamicdatabase.Debug;
import net.njay.dynamicdatabase.MongoExecutionService;
import net.njay.dynamicdatabase.daos.DAOService;
import net.njay.dynamicdatabase.daos.Punishments;
import net.njay.dynamicdatabase.documents.PunishmentDocument;
import net.njay.dynamicdatabase.tasks.core.SaveTask;
import net.njay.dynamicdatabase.tasks.core.base.DBCallBack;
import net.njay.dynamicdatabase.util.NsaktException;

import java.util.List;

/**
 * Basic Utility class to perform action related to punishment documents.
 *
 * @author NathanTheBook
 */
public class PunishmentTasks {
    private static Punishments getDao() {
        return DAOService.getPunishments();
    }

    /**
     * Punish a UUID based on prior punishments.
     * <p/>
     * ----------| CALLBACK INFORMATION |----------
     * The callback is passed one object(s), the PunishmentDocument that was saved.
     *
     * @param punishmentDocument Already formatted document with. (Expiration and type will be overwritten)
     * @param onFinish           Action to be performed when the punishment is saved
     */
    public static void punish(final PunishmentDocument punishmentDocument, final DBCallBack onFinish) {
        try {
            ConfigEnforcer.Documents.Punishments.ensureEnabled();
        } catch (NsaktException e) {
        }
        SaveTask task = new SaveTask(getDao().getDatastore(), punishmentDocument) {
            @Override
            public void run() {
                int totalPunishments = (getDao().getAllPunishmentsOfType(punishmentDocument.getPunished(), PunishmentDocument.PunishmentType.KICK).size()) + (getDao().getAllPunishmentsOfType(punishmentDocument.getPunished(), PunishmentDocument.PunishmentType.BAN).size());
                PunishmentDocument.PunishmentType type;
                Debug.log(Debug.LogLevel.INFO, Integer.toString(totalPunishments));
                switch (totalPunishments) {
                    case 0:
                        type = PunishmentDocument.PunishmentType.KICK;
                        punishmentDocument.setType(type);
                        break;
                    case 1:
                        type = PunishmentDocument.PunishmentType.BAN;
                        punishmentDocument.setExpires(new DateTime().plus(Config.Documents.Punishments.Types.Bans.defBanTime).toDate());
                        punishmentDocument.setType(type);
                        break;
                    default:
                        type = PunishmentDocument.PunishmentType.BAN;
                        punishmentDocument.setExpires(null);
                        punishmentDocument.setType(type);
                }
                getDao().save(punishmentDocument);
                onFinish.call(punishmentDocument);
            }
        };
        MongoExecutionService.getExecutorService().execute(task);
    }

    public static void checkPunishmentsOnLogin(final AsyncPlayerPreLoginEvent event) {
        try {
            ConfigEnforcer.Documents.Punishments.ensureEnabled();
        } catch (NsaktException e) {
        }
        List<PunishmentDocument> affective = DAOService.getPunishments().getAllAffectivePunishments(event.getUniqueId());
        if (affective == null || affective.isEmpty()) return;
        PunishmentDocument latest = affective.get(0);
        event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, latest.generateKickMessage());
    }
}
