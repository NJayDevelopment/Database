package net.njay.dynamicdatabase.config;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.njay.dynamicdatabase.DynamicDatabase;
import net.njay.dynamicdatabase.config.provider.ConfigurationProvider;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Class to hold multiple {@link net.njay.dynamicdatabase.config.Configuration}s.
 * Also allocates sections of a config to their proper sub-classes.
 *
 * @author Austin Mayes
 */
public class ConfigurationContainer {

    private HashMap<String, Object> contents = Maps.newHashMap();
    private List<ConfigurationProvider> providers = Lists.newArrayList();
    private Map<String, Class<? extends Configuration>> valuedClasses = Maps.newHashMap();
    private Map<String, Configuration> allocatedClasses = Maps.newHashMap();
    private Configuration mainConfig;

    public ConfigurationContainer(List<ConfigurationProvider> providers, Configuration mainConfig) {
        this.providers = providers;
        this.mainConfig = mainConfig;

        for (ConfigurationProvider provider : this.providers) {
            this.contents.putAll(provider.getConfigContents());
        }

        registerClasses();
        allocateSections();
    }

    private void registerClasses() {
        Set<Class<? extends Configuration>> classes = DynamicDatabase.getInstance().getReflections().getSubTypesOf(Configuration.class);
        for (Class<? extends Configuration> clazz : classes) {
            ConfigurationDefinition annotation = clazz.getAnnotation(ConfigurationDefinition.class);
            if (annotation == null) continue;
            this.valuedClasses.put(annotation.value(), clazz);
        }
    }

    private void allocateSections() {
        for (Map.Entry<String, Object> entry : this.contents.entrySet()) {
            String name = entry.getKey();
            Object section = entry.getValue();
            if (!(section instanceof Map)) mainConfig.contents.put(name, section);
            Class<? extends Configuration> matchingSectionClass = this.valuedClasses.get(name);
            if (matchingSectionClass == null) continue;
            try {
                // Add to an already created config's contents and then re-add to the class map.
                // This allows for multiple sections with the same throughout the config, which ill all be treated as one section.
                if (this.allocatedClasses.get(name) != null) {
                    Configuration configuration = this.allocatedClasses.get(name);
                    this.allocatedClasses.remove(name);
                    HashMap<String, Object> configContents = configuration.getContents();
                    configContents.putAll((HashMap<String, Object>) section);
                    Constructor constructor = matchingSectionClass.getDeclaredConstructor(HashMap.class);
                    Configuration newInstance = (Configuration) constructor.newInstance((HashMap<String, Object>) section);
                    this.allocatedClasses.put(name, newInstance);
                    continue;
                }
                Constructor constructor = matchingSectionClass.getDeclaredConstructor(HashMap.class);
                Configuration configuration = (Configuration) constructor.newInstance((HashMap<String, Object>) section);
                this.allocatedClasses.put(name, configuration);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public HashMap<String, Object> getContents() {
        return contents;
    }

    public List<ConfigurationProvider> getProviders() {
        return providers;
    }

    public Map<String, Class<? extends Configuration>> getValuedClasses() {
        return valuedClasses;
    }

    public Map<String, Configuration> getAllocatedClasses() {
        return allocatedClasses;
    }
}
