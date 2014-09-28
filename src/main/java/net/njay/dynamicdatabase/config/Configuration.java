package net.njay.dynamicdatabase.config;

import com.google.common.base.Splitter;

import java.util.HashMap;
import java.util.List;

/**
 * Base class to represent a configuration.
 *
 * @author Austin Mayes
 */
public class Configuration {

    protected HashMap<String, Object> contents;

    public Configuration(HashMap<String, Object> contents) {
        this.contents = contents;
    }

    public HashMap<String, Object> getContents() {
        return contents;
    }

    /**
     * Generic way to get an Object from the config.
     *
     * @param path Path to the object
     * @param <T>  Type of the object to be retrieved.
     * @return The configuration object that resisded at the supplied path
     */
    @SuppressWarnings("unchecked")
    public <T> T get(String path) {
        return (T) this.get(path, null);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String path, Object def) {
        List<String> parts = (List) Splitter.on(".").split(path);
        for (int i = 0; i < parts.size(); i++) {
            String part = parts.get(i);
            if (parts.get(i + 1) == null) {
                return this.contents.get(part) == null ? (T) def : (T) this.contents.get(part);
            }
        }
        return (T) def;
    }
}
