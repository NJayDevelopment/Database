package net.njay.dynamicdatabase;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoException;
import com.mongodb.ServerAddress;
import net.njay.dynamicdatabase.config.ConfigurationContainer;
import net.njay.dynamicdatabase.config.provider.ConfigurationProvider;
import net.njay.dynamicdatabase.config.DebuggingServiceConfiguration;
import net.njay.dynamicdatabase.config.MongoConfiguration;
import net.njay.dynamicdatabase.config.provider.YAMLConfigurationProvider;
import net.njay.dynamicdatabase.module.loader.ExternalModuleLoader;
import net.njay.dynamicdatabase.util.ExceptionHandler;
import net.njay.dynamicdatabase.util.MongoExecutionService;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.reflections.Reflections;

import java.io.File;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Project Main Class
 * <p/>
 * ---------------| Project Information |---------------
 * <p/>
 * Project Lead: Austin Mayes
 * With Help From: skipperguy12 & gcflames5 (Nick)
 * <p/>
 * Description:
 * This project is meant to serve as an easy way to store various documents in a Mongo database.
 * The project strives to be easily configurable and extendable.
 * This project is built with performance in mind.
 */
public class DynamicDatabase {
    private static DynamicDatabase instance;
    private Datastore mainStore;
    private Map<Class<? extends Document>, Datastore> datastores = Maps.newHashMap();
    private Thread mainThread;
    private DebuggingService debuggingService;
    private ConfigurationContainer mainConfig;
    private Reflections reflections;
    private ProjectConfiguration projectConfiguration;

    private ExternalModuleLoader loader;

    /**
     * Get an instance of the plugin.
     */
    public static DynamicDatabase getInstance() {
        if (instance == null) instance = new DynamicDatabase();
        return instance;
    }

    private static void setInstance(DynamicDatabase instance) {
        DynamicDatabase.instance = instance;
    }

    /**
     * Run this to setup the project and connect to mongo.
     * This is required before any mongo operations can be ran.
     */
    public void init(ProjectConfiguration configuration) {
        this.projectConfiguration = configuration;
        this.reflections = new Reflections("net.njay.dynamicdatabase");
        setInstance(this);
        setMainThread(Thread.currentThread());
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler());
        setupConfig();
        setupDebugging();
        setupMongo();
        MongoExecutionService.createExecutorService();
    }

    // Load the configuration service
    private void setupConfig() {
        List<ConfigurationProvider> providers = Lists.newArrayList();
        providers.add(new YAMLConfigurationProvider(new File(this.projectConfiguration.getMainConfigurationContainer(), "config.yml")));
        this.setMainConfig(new ConfigurationContainer(providers, this.projectConfiguration.getProjectConfig()));
    }

    // Load the debugging service
    public void setupDebugging() {
        this.setDebuggingService(new DebuggingService(Logger.getGlobal()));
        List<Class<?>> classList = new ArrayList<Class<?>>();
        DebuggingServiceConfiguration config = ((DebuggingServiceConfiguration) (this.getMainConfig().getAllocatedClasses().get("debug")));
        for (String classname : config.allowedChannels) {
            try {
                Class<?> act = Class.forName(classname);
                classList.add(act);
            } catch (ClassNotFoundException e) {
                getDebuggingService().log(DebuggingService.LogLevel.SEVERE, "Specified class (" + classname + ") was not found. Full package name required.");
            }
        }
        getDebuggingService().addFilter(DebuggingService.FilterMode.valueOf(config.filterMode), classList);
    }

    /**
     * Unload all external jars and disconnect from mongo
     */
    public void shutdown() {
        getLoader().disableAll();
        MongoExecutionService.destroyExecutorService(false);
        setInstance(null);
    }

    // Connect to mongo.
    private void setupMongo() {
        MongoConfiguration mongoConfig = ((MongoConfiguration) (this.getMainConfig().getAllocatedClasses().get("mongo")));
        MongoClient mongo = null;
        MongoClientOptions clientOptions = MongoClientOptions.builder().connectionsPerHost(10).build();
        try {
            List<ServerAddress> addresses = Lists.newArrayList();
            for (String address : mongoConfig.hostnames) {
                Iterator<String> addressSplitter = Splitter.on(":").limit(2).split(address).iterator();
                addresses.add(new ServerAddress(addressSplitter.next(), addressSplitter.hasNext() ? Integer.parseInt(addressSplitter.next()) : 27017));
            }
            if (addresses.size() == 1) mongo = new MongoClient(addresses.get(0), clientOptions);
            else if (addresses.size() > 1) mongo = new MongoClient(addresses, clientOptions);
            else throw new MongoException("Unable to connect to any Mongo instance!");
        } catch (UnknownHostException e) {
            getDebuggingService().log(e);
        }
        DB database = mongo.getDB(mongoConfig.database);
        if (mongoConfig.useAthentication && !database.authenticate(mongoConfig.username, mongoConfig.password.toCharArray())) {
            throw new MongoException("Could not authenticate to database " + database.getName());
        }
        setupMorphia(mongo);
    }

    // Map every class in the documents package to morphia.
    private void setupMorphia(MongoClient mongo) {
        Morphia morphia = new Morphia();
        setMainStore(morphia.createDatastore(mongo, this.getMainConfig().getAllocatedClasses().containsKey("mongo") ? ((MongoConfiguration) (this.getMainConfig().getAllocatedClasses().get("mongo"))).database : "database"));
        // fixClassLoader(morphia);
        morphia.getMapper().getOptions().setStoreEmpties(this.projectConfiguration.storeEmpties());
        morphia.getMapper().getOptions().setActLikeSerializer(this.projectConfiguration.actLikeSerializer());
        morphia.getMapper().getOptions().setIgnoreFinals(this.projectConfiguration.ignoreFinals());
        morphia.getMapper().getOptions().setStoreNulls(this.projectConfiguration.storeNulls());
        registerModules(mongo, morphia);
    }

    /*
    // Fix the morphia class loader (This was for Bukkit).
    private void fixClassLoader(Morphia morphia) {
        morphia.getMapper().getOptions().objectFactory = new DefaultCreator() {
            @Override
            protected ClassLoader getClassLoaderForClass() {
                return DynamicDatabasePlugin.getInstance().getClassLoader();
            }
        };
    }
    */

    private void registerModules(MongoClient mongo, Morphia morphia) {
        File modulesFolder = this.projectConfiguration.getModulesContainer();

        setLoader(new ExternalModuleLoader(morphia, mongo, modulesFolder));
        getLoader().load();
    }

    public Datastore getMainStore() {
        return mainStore;
    }

    public void setMainStore(Datastore mainStore) {
        this.mainStore = mainStore;
    }

    public Map<Class<? extends Document>, Datastore> getDatastores() {
        return datastores;
    }

    public void setDatastores(Map<Class<? extends Document>, Datastore> datastores) {
        this.datastores = datastores;
    }

    public Thread getMainThread() {
        return mainThread;
    }

    public void setMainThread(Thread mainThread) {
        this.mainThread = mainThread;
    }

    public DebuggingService getDebuggingService() {
        return debuggingService;
    }

    public void setDebuggingService(DebuggingService debuggingService) {
        this.debuggingService = debuggingService;
    }

    public ExternalModuleLoader getLoader() {
        return loader;
    }

    public void setLoader(ExternalModuleLoader loader) {
        this.loader = loader;
    }

    public ConfigurationContainer getMainConfig() {
        return mainConfig;
    }

    public void setMainConfig(ConfigurationContainer mainConfig) {
        this.mainConfig = mainConfig;
    }

    public Reflections getReflections() {
        return reflections;
    }
}
