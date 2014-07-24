package us.nsakt.dynamicdatabase.tasks.runners;

import org.mongodb.morphia.Datastore;
import us.nsakt.dynamicdatabase.documents.PunishmentDocument;

/**
 * Class to represent a runnable that is meant to interact with a document.
 */
public class PunishmentTask extends Task {

    private PunishmentDocument punishment;

    /**
     * Constructor
     *
     * @param store      The document's datastore
     * @param punishment The main document to interact with
     */
    public PunishmentTask(Datastore store, PunishmentDocument punishment) {
        super(store);
        this.punishment = punishment;
    }

    /**
     * Get the main document to be interacted with
     *
     * @return the main document
     */
    public PunishmentDocument getPunishment() {
        return punishment;
    }

    /**
     * Called when the task is ran, needs to be overridden.
     */
    @Override
    public void run() {

    }
}
