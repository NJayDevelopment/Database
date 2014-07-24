package us.nsakt.dynamicdatabase.datastores;

import org.joda.time.Duration;
import org.mongodb.morphia.Datastore;
import us.nsakt.dynamicdatabase.DynamicDatabasePlugin;
import us.nsakt.dynamicdatabase.documents.PunishmentDocument;

import java.util.List;
import java.util.UUID;

public class Punishments {

    private static Datastore datastore = DynamicDatabasePlugin.getInstance().getDatastores().get(PunishmentDocument.class);

    public static Datastore getDatastore() {
        return datastore;
    }

    /**
     * Get all punishments issued to a UUID
     *
     * @param uuid UUID to search for
     * @return all punishments issues to the UUID
     */
    public static List<PunishmentDocument> getAllPunishments(UUID uuid) {
        return datastore.find(PunishmentDocument.class).field(PunishmentDocument.MongoFields.PUNISHED.fieldName).equal(uuid).asList();
    }

    /**
     * Get all punishments issued to a UUID of a certain type
     *
     * @param uuid UUID to search for
     * @param type PunishmentType to search for
     * @return all punishments issued to the UUID of that type
     */
    public static List<PunishmentDocument> getAllPunishmentsOfType(UUID uuid, PunishmentDocument.PunishmentType type) {
        return datastore.find(PunishmentDocument.class).field(PunishmentDocument.MongoFields.PUNISHED.fieldName).equal(uuid).field(PunishmentDocument.MongoFields.TYPE.fieldName).equal(type).asList();
    }

    /**
     * Punish a user.
     * This will take into account a user's other punishments and issue an appropriate punishment based on history.
     * This will either produce a kick, ban, or permanent ban.
     * The default ban time is 10 days.
     * This will also save the document
     *
     * @param uuid               UUID to punish
     * @param punishmentDocument Already generated document to set the type and expiration date for.
     */
    public static void punish(UUID uuid, PunishmentDocument punishmentDocument) {
        PunishmentDocument.PunishmentType type = PunishmentDocument.PunishmentType.UNKNOW;
        List<PunishmentDocument> kicks = getAllPunishmentsOfType(uuid, PunishmentDocument.PunishmentType.KICK);
        List<PunishmentDocument> bans = getAllPunishmentsOfType(uuid, PunishmentDocument.PunishmentType.BAN);

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
        datastore.save(punishmentDocument);
    }
}
