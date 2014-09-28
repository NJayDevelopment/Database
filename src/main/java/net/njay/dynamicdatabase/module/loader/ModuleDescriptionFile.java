package net.njay.dynamicdatabase.module.loader;

import com.google.common.collect.ImmutableList;
import net.njay.dynamicdatabase.util.NJayException;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;

import java.io.InputStream;
import java.io.Reader;
import java.util.List;
import java.util.Map;

/**
 * Class to represent a file that hold asic information about an external module.
 *
 * @author skipperguy12
 */
public class ModuleDescriptionFile {

    private static final Yaml yaml = new Yaml(new SafeConstructor());
    private String main = null;
    private List<String> depend = null;
    private List<String> softDepend = null;

    public ModuleDescriptionFile(final InputStream stream) throws NJayException {
        loadMap(asMap(yaml.load(stream)));
    }

    public ModuleDescriptionFile(final Reader reader) throws NJayException {
        loadMap(asMap(yaml.load(reader)));
    }


    private void loadMap(Map<?, ?> map) throws NJayException {

        try {
            main = map.get("main").toString();
        } catch (NullPointerException ex) {
            throw new NJayException(ex, "main is not defined");
        } catch (ClassCastException ex) {
            throw new NJayException(ex, "main is of wrong type");
        }

        if (map.get("depend") != null) {
            ImmutableList.Builder<String> dependBuilder = ImmutableList.<String>builder();
            try {
                for (Object dependency : (Iterable<?>) map.get("depend")) {
                    dependBuilder.add(dependency.toString());
                }
            } catch (ClassCastException ex) {
                throw new NJayException(ex, "depend is of wrong type");
            } catch (NullPointerException e) {
                throw new NJayException(e, "invalid dependency format");
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
                throw new NJayException(ex, "softdepend is of wrong type");
            } catch (NullPointerException ex) {
                throw new NJayException(ex, "invalid soft-dependency format");
            }
            softDepend = softDependBuilder.build();
        }


    }

    private Map<?, ?> asMap(Object object) throws NJayException {
        if (object instanceof Map) {
            return (Map<?, ?>) object;
        }
        throw new NJayException(object + " is not properly structured.");
    }

    public List<String> getDepend() {
        return depend;
    }

    public String getMain() {
        return main;
    }

}
