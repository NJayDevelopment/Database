package us.nsakt.dynamicdatabase.tasks.runners;

import org.mongodb.morphia.Datastore;
import us.nsakt.dynamicdatabase.documents.GroupDocument;

public class GroupTask extends Task {

    private GroupDocument group;

    public GroupTask(Datastore store, GroupDocument groupDocument) {
        super(store);
        this.group = groupDocument;
    }

    public GroupDocument getGroup() {
        return group;
    }

    @Override
    public void run() {

    }
}
