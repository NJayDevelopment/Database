package net.njay.dynamicdatabase.module.loader;

import com.google.common.collect.Lists;
import com.mongodb.MongoClient;
import net.njay.dynamicdatabase.DebuggingService;
import net.njay.dynamicdatabase.Document;
import net.njay.dynamicdatabase.DynamicDatabase;
import net.njay.dynamicdatabase.module.ExternalModule;
import net.njay.dynamicdatabase.util.NJayException;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.annotations.Entity;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLClassLoader;
import java.util.List;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

/**
 * Class to handle the loading and unloading of external modules.
 *
 * @author skipperguy12 (Base Class)
 * @author Nick (Database Conversion)
 */
public class ExternalModuleLoader {

    private Morphia morphia;
    private MongoClient mongo;
    private File directory;

    private List<ExternalModuleInfo> loadedModules;

    public ExternalModuleLoader(Morphia morphia, MongoClient mongo, File directory) {
        this.morphia = morphia;
        this.mongo = mongo;
        this.directory = directory;
        this.loadedModules = Lists.newArrayList();

        if (!directory.exists()) {
            if (directory.mkdirs()) {
                DynamicDatabase.getInstance().getDebuggingService().log(DebuggingService.LogLevel.INFO, "Modules folder did not exist, created one at " + directory.getPath());
            } else {
                DynamicDatabase.getInstance().getDebuggingService().log(DebuggingService.LogLevel.SEVERE, "Failed to create plugins folder at " + directory.getPath());
                return;
            }
        }
    }

    public void load() {
        for (File f : directory.listFiles()) {
            if (f.getName().endsWith(".jar")) {
                ExternalModuleInfo moduleInfo = load(f);
                if (moduleInfo.getDocs() != null && !moduleInfo.getDocs().isEmpty()) {
                    for (Class<? extends Document> document : moduleInfo.getDocs()) {
                        Datastore store = morphia.createDatastore(mongo, document.getAnnotation(Entity.class).value());
                        DynamicDatabase.getInstance().getDatastores().put(document, store);
                        store.ensureIndexes();
                    }
                }
                try {
                    moduleInfo.getModuleInstance().onEnable();
                } catch (Exception e) {
                    e.printStackTrace();
                    System.err.println("There was an error while enabling " + f.getName() + "! (Is it up to date?)");
                }
                loadedModules.add(moduleInfo);
            }
        }
    }

    public ExternalModuleInfo load(File file) {
        JarFile jar;
        try {
            jar = new JarFile(file, true);
        } catch (IOException ex) {
            DynamicDatabase.getInstance().getDebuggingService().log(DebuggingService.LogLevel.SEVERE, "Could not load plugin at " + file.getPath() + ": " + ExceptionUtils.getMessage(ex));
            return null;
        }

        ZipEntry entry = jar.getEntry("module.yml");
        ModuleDescriptionFile descriptionFile;
        try {
            InputStream stream = jar.getInputStream(entry);

            descriptionFile = new ModuleDescriptionFile(stream);

        } catch (IOException ex) {
            DynamicDatabase.getInstance().getDebuggingService().log(DebuggingService.LogLevel.SEVERE, "Could not load module at " + file.getPath() + ": " + ExceptionUtils.getMessage(ex));
            return null;
        } catch (NJayException ex) {
            DynamicDatabase.getInstance().getDebuggingService().log(DebuggingService.LogLevel.SEVERE, "Could not load module at " + file.getPath() + ": " + ExceptionUtils.getMessage(ex));
            return null;
        }

        URLClassLoader loader;
        try {
            loader = ModuleClassLoader.addFile(file);
        } catch (IOException ex) {
            DynamicDatabase.getInstance().getDebuggingService().log(DebuggingService.LogLevel.SEVERE, "Invalid module.yml for " + file.getPath() + ": Could not load " + file.getName() + " into the classpath");
            return null;
        }


        Class<?> externalModule;
        try {
            externalModule = Class.forName(descriptionFile.getMain());
        } catch (ClassNotFoundException ex) {
            DynamicDatabase.getInstance().getDebuggingService().log(DebuggingService.LogLevel.SEVERE, "Invalid module.yml for " + file.getPath() + ": " + descriptionFile.getMain() + " does not exist");
            return null;
        }

        if (!ExternalModule.class.isAssignableFrom(externalModule)) {
            DynamicDatabase.getInstance().getDebuggingService().log(DebuggingService.LogLevel.SEVERE, "Invalid module.yml for " + file.getPath() + ": " + descriptionFile.getMain() + " is not assignable from " + ExternalModule.class.getSimpleName());
            return null;
        }

        try {
            ExternalModule externalModuleInstance = (ExternalModule) externalModule.newInstance();
            return new ExternalModuleInfo(externalModuleInstance, externalModuleInstance.getDocuments());
        } catch (Exception ex) {
            DynamicDatabase.getInstance().getDebuggingService().log(DebuggingService.LogLevel.SEVERE, "Failed to load " + file.getPath() + ": " + ExceptionUtils.getMessage(ex));
        }

        return null;
    }

    public void disableAll() {
        for (ExternalModuleInfo module : loadedModules)
            module.getModuleInstance().onDisable();
    }

    class ExternalModuleInfo {
        private ExternalModule moduleInstance;
        private List<Class<? extends Document>> docs;

        public ExternalModuleInfo(ExternalModule moduleInstance, List<Class<? extends Document>> docs) {
            this.moduleInstance = moduleInstance;
            this.docs = docs;
        }

        public ExternalModule getModuleInstance() {
            return this.moduleInstance;
        }

        public List<Class<? extends Document>> getDocs() {
            return this.docs;
        }
    }

}
