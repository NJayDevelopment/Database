package net.njay.dynamicdatabase.config;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@ConfigurationDefinition("debug")
public class DebuggingServiceConfiguration extends Configuration {

    public final String filterMode = get("filterMode", "BLACKLIST");
    public final List<String> allowedChannels = get("channels", Arrays.asList());

    public DebuggingServiceConfiguration(ConfigurationContainer configurationContainer) {
        super(configurationContainer.getContents());
    }
    public DebuggingServiceConfiguration(HashMap<String, Object> contents) {
        super(contents);
    }
}
