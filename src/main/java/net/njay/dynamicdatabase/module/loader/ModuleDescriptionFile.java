package net.njay.dynamicdatabase.module.loader;

import com.google.common.collect.ImmutableList;
import org.bukkit.plugin.InvalidDescriptionException;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;

import java.io.InputStream;
import java.io.Reader;
import java.util.List;
import java.util.Map;

public class ModuleDescriptionFile {

    private static final Yaml yaml = new Yaml(new SafeConstructor());
    private String main = null;
    private List<String> depend = null;
    private List<String> softDepend = null;

    public ModuleDescriptionFile(final InputStream stream) throws InvalidDescriptionException {
        loadMap(asMap(yaml.load(stream)));
    }

    public ModuleDescriptionFile(final Reader reader) throws InvalidDescriptionException {
        loadMap(asMap(yaml.load(reader)));
    }


    private void loadMap(Map<?, ?> map) throws InvalidDescriptionException {

        try {
            main = map.get("main").toString();
        } catch (NullPointerException ex) {
            throw new InvalidDescriptionException(ex, "main is not defined");
        } catch (ClassCastException ex) {
            throw new InvalidDescriptionException(ex, "main is of wrong type");
        }

        if (map.get("depend") != null) {
            ImmutableList.Builder<String> dependBuilder = ImmutableList.<String>builder();
            try {
                for (Object dependency : (Iterable<?>) map.get("depend")) {
                    dependBuilder.add(dependency.toString());
                }
            } catch (ClassCastException ex) {
                throw new InvalidDescriptionException(ex, "depend is of wrong type");
            } catch (NullPointerException e) {
                throw new InvalidDescriptionException(e, "invalid dependency format");
            }
            depend = dependBuilder.build();
        }

        if (map.get("softdepend") != null) {
            ImmutableList.Builder<String> softDependBuilder = ImmutableList.<String>builder();
            try {
                for (Object dependency : (Iterable<?>) map.get("softdepend")) {
                    softDependBuilder.add(dependency.toString());
                }
            } catch (ClassCastException ex) {
                throw new InvalidDescriptionException(ex, "softdepend is of wrong type");
            } catch (NullPointerException ex) {
                throw new InvalidDescriptionException(ex, "invalid soft-dependency format");
            }
            softDepend = softDependBuilder.build();
        }


    }

    private Map<?, ?> asMap(Object object) throws InvalidDescriptionException {
        if (object instanceof Map) {
            return (Map<?, ?>) object;
        }
        throw new InvalidDescriptionException(object + " is not properly structured.");
    }

    public List<String> getDepend() {
        return depend;
    }

    public String getMain() {
        return main;
    }

}
