package us.nsakt.dynamicdatabase;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoException;
import com.mongodb.ServerAddress;
import com.sk89q.bukkit.util.CommandsManagerRegistration;
import com.sk89q.minecraft.util.commands.ChatColor;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.minecraft.util.commands.CommandPermissionsException;
import com.sk89q.minecraft.util.commands.CommandUsageException;
import com.sk89q.minecraft.util.commands.CommandsManager;
import com.sk89q.minecraft.util.commands.MissingNestedCommandException;
import com.sk89q.minecraft.util.commands.WrappedCommandException;
import org.bson.types.ObjectId;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.mapping.DefaultCreator;
import org.reflections.Reflections;
import us.nsakt.dynamicdatabase.documents.Document;
import us.nsakt.dynamicdatabase.documents.ServerDocument;
import us.nsakt.dynamicdatabase.util.LanguageFile;
import us.nsakt.dynamicdatabase.util.LogLevel;

import java.io.File;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Main Bukkit class
 */
public class DynamicDatabasePlugin extends JavaPlugin {
    // Singleton instance
    private static DynamicDatabasePlugin instance;
    //sk89q's command framework CommandsManager
    private CommandsManager<CommandSender> commands;
    // The main DataStore
    private Datastore mainStore;
    // Datastores for Morphia
    private Map<Class<? extends Document>, Datastore> datastores = Maps.newHashMap();
    // The mainThread thread
    private Thread mainThread;
    private ServerDocument currentServerDocument;

    /**
     * Gets the Singleton instance of DynamicDatabasePlugin
     *
     * @return Singleton instance of DynamicDatabasePlugin
     */
    public static DynamicDatabasePlugin getInstance() {
        if (instance == null) instance = new DynamicDatabasePlugin();
        return instance;
    }

    /**
     * Gets the mainThread thread
     *
     * @return the mainThread thread
     */
    public Thread getMainThread() {
        return mainThread;
    }

    public void onEnable() {
        instance = this;
        mainThread = Thread.currentThread();
        setupConfig();
        setupDebugging();
        setupMongo();
        QueryExecutor.createExecutorService();
        ConfigEnforcer.ConfigTasks.convertAllNamesToClusters();
        setupServer();
        registerListeners();
        setupCommands();
    }

    public void setupDebugging() {
        for (String classname : Config.Debug.allowedChannels) {
            Debug.filter(classname);
        }
    }

    public void setupServer() {
        ServerDocument serverDocument = getDatastores().get(ServerDocument.class).createQuery(ServerDocument.class).filter("_id", ObjectId.massageToObjectId(Config.Mongo.serverId)).get();
        if (serverDocument == null) Debug.log(LogLevel.SEVERE, "Server not found in database!");
        this.currentServerDocument = serverDocument;
    }

    public void onDisable() {
        instance = null;
        QueryExecutor.destroyExecutorService(false);
    }

    // Sets up the Config for language
    private void setupConfig() {
        this.getConfig().options().copyDefaults(true);
        this.saveConfig();

        for (String string : Config.Languages.supportedLanguages) {
            File file = new File(string + ".lang");
            if (!file.exists()) continue;

            LanguageFile.LanguageEnum languageInfo = LanguageFile.getByAbbreviation(string);
            LanguageFile language = new LanguageFile(languageInfo);
            Config.getLanguageMap().put(languageInfo, language);
        }
    }

    //Establish connection to mongo
    private void setupMongo() {
        MongoClient mongo = null;
        MongoClientOptions clientOptions = MongoClientOptions.builder().connectionsPerHost(10).build();
        try {
            List<ServerAddress> addresses = Lists.newArrayList();
            for (String address : Config.Mongo.hostnames)
                addresses.add(new ServerAddress(address, Config.Mongo.port));
            if (addresses.size() == 1) mongo = new MongoClient(addresses.get(0), clientOptions);
            else if (addresses.size() > 1) mongo = new MongoClient(addresses, clientOptions);
            else throw new MongoException("Unable to connect to any Mongo instance!");
        } catch (UnknownHostException e) {
            Debug.log(e);
        }

        DB database = mongo.getDB(Config.Mongo.database);

        if (Config.Mongo.Authentication.useAthentication && !database.authenticate(Config.Mongo.Authentication.username, Config.Mongo.Authentication.password.toCharArray())) {
            throw new MongoException("Could not authenticate to database " + database.getName());
        }

        setupMorphia(mongo);
    }

    //Initialize the morphia instance and handle any setup
    private void setupMorphia(MongoClient mongo) {
        Morphia morphia = new Morphia();
        morphia.mapPackage("us.nsakt.dynamicdatabase.documents", true);
        mainStore = morphia.createDatastore(mongo, Config.Mongo.database);

        fixClassLoader(morphia);
        setupDataStores(mongo, morphia);
    }

    //Fix the morphia classloader
    private void fixClassLoader(Morphia morphia) {
        morphia.getMapper().getOptions().objectFactory = new DefaultCreator() {
            @Override
            protected ClassLoader getClassLoaderForClass() {
                return DynamicDatabasePlugin.getInstance().getClassLoader();
            }
        };
    }

    //Set up multiple databases
    private void setupDataStores(MongoClient mongo, Morphia morphia) {
        Reflections reflections = new Reflections("us.nsakt.dynamicdatabase");
        Set<Class<? extends Document>> classes = reflections.getSubTypesOf(Document.class);
        for (Class<? extends Document> doc : classes) {
            Datastore store = morphia.createDatastore(mongo, doc.getAnnotation(Entity.class).value());
            datastores.put(doc, store);
            store.ensureIndexes();
        }
    }

    /**
     * sk89q's command framework method to setup commands from onEnable
     */
    private void setupCommands() {
        this.commands = new CommandsManager<CommandSender>() {
            @Override
            public boolean hasPermission(CommandSender sender, String perm) {
                return sender instanceof ConsoleCommandSender || sender.hasPermission(perm);
            }
        };
        CommandsManagerRegistration cmdRegister = new CommandsManagerRegistration(this, this.commands);
    }

    /**
     * Registers Listeners used by DynamicDatabasePlugin
     */
    private void registerListeners() {
    }

    /**
     * Registers a Bukkit Listener with Bukkit's PluginManager
     *
     * @param listener Listener to register
     */
    private void registerEvents(Listener listener) {
        Bukkit.getPluginManager().registerEvents(listener, this);
    }

    /**
     * Gets the Datastore in use
     *
     * @return the Datastore in use
     */
    public Datastore getAuthDatastore() {
        return mainStore;
    }

    /**
     * Gets the datastores map
     *
     * @return mapping of datastores
     */
    public Map<Class<? extends Document>, Datastore> getDatastores() {
        return datastores;
    }

    // Passes commands from Bukkit to sk89q
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        try {
            this.commands.execute(cmd.getName(), args, sender, sender);
        } catch (CommandPermissionsException e) {
            sender.sendMessage(ChatColor.RED + (sender instanceof Player ? Config.getLanguageMap().get(LanguageFile.getLocale((Player) sender)).get("sk89qCommands.noPermissionMessage") : Config.getLanguageMap().get(LanguageFile.LanguageEnum.ENGLISH).get("sk89qCommands.noPermissionMessage")));
        } catch (MissingNestedCommandException e) {
            sender.sendMessage(ChatColor.RED + e.getUsage());
        } catch (CommandUsageException e) {
            sender.sendMessage(ChatColor.RED + e.getMessage());
            sender.sendMessage(ChatColor.RED + e.getUsage());
        } catch (WrappedCommandException e) {
            if (e.getCause() instanceof NumberFormatException) {
                sender.sendMessage(ChatColor.RED + (sender instanceof Player ? Config.getLanguageMap().get(LanguageFile.getLocale((Player) sender)).get("sk89qCommands.intExpectedStringReceived") : Config.getLanguageMap().get(LanguageFile.LanguageEnum.ENGLISH).get("sk89qCommands.intExpectedStringReceived")));
            } else {
                sender.sendMessage(ChatColor.RED + (sender instanceof Player ? Config.getLanguageMap().get(LanguageFile.getLocale((Player) sender)).get("sk89qCommands.errorOccurred") : Config.getLanguageMap().get(LanguageFile.LanguageEnum.ENGLISH).get("sk89qCommands.errorOccurred")));
                e.printStackTrace();
            }
        } catch (CommandException e) {
            sender.sendMessage(ChatColor.RED + e.getMessage());
        }

        return true;
    }

    public ServerDocument getCurrentServerDocument() {
        return this.currentServerDocument;
    }
}
