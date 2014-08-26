package net.njay.dynamicdatabase.module.loader;

import com.google.common.collect.Lists;
import com.mongodb.MongoClient;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.InvalidDescriptionException;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.annotations.Entity;
import net.njay.dynamicdatabase.Debug;
import net.njay.dynamicdatabase.DynamicDatabasePlugin;
import net.njay.dynamicdatabase.documents.Document;
import net.njay.dynamicdatabase.module.ExternalModule;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLClassLoader;
import java.util.List;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

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
                Debug.log(Debug.LogLevel.INFO, "Modules folder did not exist, created one at " + directory.getPath());
            } else {
                Debug.log(Debug.LogLevel.SEVERE, "Failed to create plugins folder at " + directory.getPath());
                Bukkit.shutdown(); // panic
                return;
            }
        }
    }

    public void load() {
        for (File f : directory.listFiles()) {
            if (f.getName().endsWith(".jar")) {
                ExternalModuleInfo moduleInfo = load(f);
                for (Class<? extends Document> document : moduleInfo.getDocs()) {
                    Datastore store = morphia.createDatastore(mongo, document.getAnnotation(Entity.class).value());
                    DynamicDatabasePlugin.getInstance().getDatastores().put(document, store);
                    store.ensureIndexes();
                }
                try {
                    moduleInfo.getModuleInstance().onEnable();
                }catch (Exception e){
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
            Debug.log(Debug.LogLevel.SEVERE, "Could not load plugin at " + file.getPath() + ": " + ExceptionUtils.getMessage(ex));
            return null;
        }

        ZipEntry entry = jar.getEntry("module.yml");
        ModuleDescriptionFile descriptionFile;
        try {
            InputStream stream = jar.getInputStream(entry);

            descriptionFile = new ModuleDescriptionFile(stream);

        } catch (IOException ex) {
            Debug.log(Debug.LogLevel.SEVERE, "Could not load module at " + file.getPath() + ": " + ExceptionUtils.getMessage(ex));
            return null;
        } catch (InvalidDescriptionException ex) {
            Debug.log(Debug.LogLevel.SEVERE, "Could not load module at " + file.getPath() + ": " + ExceptionUtils.getMessage(ex));
            return null;
        }

        URLClassLoader loader;
        try {
            loader = ModuleClassLoader.addFile(file);
        } catch (IOException ex) {
            Debug.log(Debug.LogLevel.SEVERE, "Invalid module.yml for " + file.getPath() + ": Could not load " + file.getName() + " into the classpath");
            return null;
        }


        Class<?> externalModule;
        try {
            externalModule = Class.forName(descriptionFile.getMain());
        } catch (ClassNotFoundException ex) {
            Debug.log(Debug.LogLevel.SEVERE, "Invalid module.yml for " + file.getPath() + ": " + descriptionFile.getMain() + " does not exist");
            return null;
        }

        if (!ExternalModule.class.isAssignableFrom(externalModule)) {
            Debug.log(Debug.LogLevel.SEVERE, "Invalid module.yml for " + file.getPath() + ": " + descriptionFile.getMain() + " is not assignable from " + ExternalModule.class.getSimpleName());
            return null;
        }

        try {
            ExternalModule externalModuleInstance = (ExternalModule) externalModule.newInstance();
            return new ExternalModuleInfo(externalModuleInstance, externalModuleInstance.getDocuments());
        } catch (Exception ex) {
            Debug.log(Debug.LogLevel.SEVERE, "Failed to load " + file.getPath() + ": " + ExceptionUtils.getMessage(ex));
        }

        return null;
    }

    public void disableAll(){
        for (ExternalModuleInfo module : loadedModules)
            module.getModuleInstance().onDisable();
    }

    class ExternalModuleInfo {
        private ExternalModule moduleInstance;
        private List<Class<? extends Document>> docs;

        public ExternalModuleInfo(ExternalModule moduleInstance, List<Class<? extends Document>> docs){
            this.moduleInstance = moduleInstance;
            this.docs = docs;
        }

        public ExternalModule getModuleInstance(){
            return this.moduleInstance;
        }

        public List<Class<? extends Document>> getDocs(){
            return this.docs;
        }
    }

}
