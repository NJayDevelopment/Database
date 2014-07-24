package us.nsakt.dynamicdatabase.tasks.runners;

import org.mongodb.morphia.Datastore;
import us.nsakt.dynamicdatabase.documents.PunishmentDocument;

public class PunishmentTask extends Task {

    private PunishmentDocument punishment;

    public PunishmentTask(Datastore store, PunishmentDocument punishment) {
        super(store);
        this.punishment = punishment;
    }

    public PunishmentDocument getPunishment() {
        return punishment;
    }

    @Override
    public void run() {

    }
}
