package us.nsakt.dynamicdatabase.tasks.runners;

import org.mongodb.morphia.Datastore;
import us.nsakt.dynamicdatabase.documents.UserDocument;

public class UserTask extends Task {

    private UserDocument user;

    public UserTask(Datastore store, UserDocument user) {
        super(store);
        this.user = user;
    }

    public UserDocument getUser() {
        return user;
    }

    @Override
    public void run() {

    }
}
