package net.njay.dynamicdatabase.config.provider;

import java.util.Map;

/**
 * Interface to represent something that provides configuration options.
 *
 * @author Austin Mayes
 */
public interface ConfigurationProvider {

    public Map<String, Object> getConfigContents();

    public <T> T getConfigSource();
}
