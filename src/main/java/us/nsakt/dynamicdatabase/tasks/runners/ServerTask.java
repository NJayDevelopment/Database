package us.nsakt.dynamicdatabase.tasks.runners;

import org.mongodb.morphia.Datastore;
import us.nsakt.dynamicdatabase.documents.ServerDocument;

public class ServerTask extends Task {

    private ServerDocument server;

    public ServerTask(Datastore store, ServerDocument punishment) {
        super(store);
        this.server = punishment;
    }

    public ServerDocument getServer() {
        return server;
    }

    @Override
    public void run() {

    }
}
