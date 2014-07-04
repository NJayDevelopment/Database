package us.nsakt.dynamicdatabase;

import com.google.common.collect.Maps;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import us.nsakt.dynamicdatabase.util.LanguageFile;
import us.nsakt.dynamicdatabase.util.config.ConfigAnnotation;
import us.nsakt.dynamicdatabase.util.config.ConfigStructure;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Class to interface with Bukkits Configuration system
 */
public class Config {

    // mapping of LanguageEnum to the LanguageFile
    private static HashMap<LanguageFile.LanguageEnum, LanguageFile> languageHashMap = Maps.newHashMap();

    /**
     * Gets the languages which are loaded
     *
     * @return the mapping of LanguageEnum to the LanguageFile
     */
    public static HashMap<LanguageFile.LanguageEnum, LanguageFile> getLanguageMap() {
        return languageHashMap;
    }

    /**
     * Gets the Bukkit Configuration
     *
     * @return the Configuration in use by Bukkit from the Main class
     */
    public static Configuration getBukkitConfig() {
        // Singleton instance of Main class
        DynamicDatabasePlugin plugin = DynamicDatabasePlugin.getInstance();
        // result from plugin
        FileConfiguration res = plugin.getConfig();

        // return res if not null, else return a brand new YamlConfiguration
        if (res != null) return res;
        else return new YamlConfiguration();
    }

    /**
     * Saves the Bukkit config
     */
    public static void saveBukkitConfig() {
        DynamicDatabasePlugin.getInstance().saveConfig();
    }

    /**
     * Gets an element form the Configuration file
     *
     * @param path path to element
     * @param <T>  type of element
     * @return found element
     */
    @SuppressWarnings("unchecked")
    public static <T> T get(String path) {
        return (T) getBukkitConfig().get(path);
    }

    /**
     * Gets an element form the Configuration file, returning def if not found
     *
     * @param path path to element
     * @param def  default value to return if element is not found
     * @param <T>  type of element
     * @return found element, def if null
     */
    @SuppressWarnings("unchecked")
    public static <T> T get(String path, Object def) {
        return (T) getBukkitConfig().get(path, def);
    }

    /**
     * Sets a value in the configuration at path
     *
     * @param path path to element
     * @param val  value to set at path
     */
    public static void set(String path, Object val) {
        getBukkitConfig().set(path, val);
    }


    @ConfigAnnotation(type = ConfigStructure.SECTION, desc = "Information for Mongo")
    public static class Mongo {

        @ConfigAnnotation(type = ConfigStructure.VARIABLE, desc = "The current Server's ObjectId, must be in database.")
        public static String serverId = get("mongo.server-id", "nil");


        @ConfigAnnotation(type = ConfigStructure.SECTION, desc = "Information for Mongo")
        public static class Authentication {
            @ConfigAnnotation(type = ConfigStructure.VARIABLE, desc = "The hostname to attempt to connect to", def = "localhost")
            public static String hostname = get("mongo.hostname", "localhost");

            @ConfigAnnotation(type = ConfigStructure.VARIABLE, desc = "The port to connect to", def = "27017")
            public static int port = get("mongo.port", 27017);

            @ConfigAnnotation(type = ConfigStructure.VARIABLE, desc = "The Mongo database to connect to", def = "nsakt_database")
            public static String database = get("mongo.database", "nsakt_database");
        }
    }

    @ConfigAnnotation(type = ConfigStructure.SECTION, desc = "Configurable settings on Languages handled by DynamicDatabase")
    public static class Languages {
        @ConfigAnnotation(type = ConfigStructure.VARIABLE, desc = "List of supported languages")
        public static List<String> supportedLanguages = get("languages.supported-languages", Arrays.asList("en_CA"));
    }

    @ConfigAnnotation(type = ConfigStructure.SECTION, desc = "Configurable debugging options")
    public static class Debug {
        @ConfigAnnotation(type = ConfigStructure.VARIABLE, desc = "Boolean to decide whether to use voodoo magic to take over the normal console to filter out specific channels")
        public static boolean hijackPrintStream = get("debug.hijack-printstream", true);

        @ConfigAnnotation(type = ConfigStructure.VARIABLE, desc = "Determines which channels are allowed to debug", def = "[\"Generic\", \"Morphia\", \"Exception\"]")
        public static List<String> allowedChannels = get("debug.allowedChannels", Arrays.asList("Generic", "Morphia", "Exception"));
    }
}
