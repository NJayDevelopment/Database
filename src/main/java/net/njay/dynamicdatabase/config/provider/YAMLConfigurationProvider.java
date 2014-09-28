package net.njay.dynamicdatabase.config.provider;

import com.google.common.collect.Maps;
import net.njay.dynamicdatabase.DebuggingService;
import net.njay.dynamicdatabase.DynamicDatabase;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Map;

/**
 * Class to represent a YAML file that provides configuration options.
 *
 * @author Austin Mayes
 */
public class YAMLConfigurationProvider implements ConfigurationProvider {
    private final File configSource;
    private Map<String, Object> configContents = Maps.newHashMap();

    public YAMLConfigurationProvider(File configSource) {
        this.configSource = configSource;
        try {
            InputStream input = new FileInputStream(configSource);
            Yaml yaml = new Yaml();
            this.configContents = (Map<String, Object>) yaml.load(input);
        } catch (FileNotFoundException e) {
            DynamicDatabase.getInstance().getDebuggingService().log(DebuggingService.LogLevel.SEVERE, "Unable to load config!");
        }
    }

    @Override
    public Map<String, Object> getConfigContents() {
        return configContents;
    }

    @Override
    public File getConfigSource() {
        return configSource;
    }

    @Override
    public String toString() {
        return "ConfigProvider{" +
                "configContents=" + configContents +
                ", configSource=" + configSource +
                '}';
    }
}
