package net.njay.dynamicdatabase.config;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@ConfigurationDefinition("mongo")
public class MongoConfiguration extends Configuration {

    public final List<String> hostnames = get("hostnames", Arrays.asList("localhost:27017"));
    public final String database = get("database", "nsakt_database");
    public final boolean useAthentication = get("authentication.use-authentication", false);
    public final String username = get("authentication.username", "username");
    public final String password = get("authentication.password", "password");

    public MongoConfiguration(ConfigurationContainer configurationContainer) {
        super(configurationContainer.getContents());
    }

    public MongoConfiguration(HashMap<String, Object> contents) {
        super(contents);
    }

}
