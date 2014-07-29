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

import java.io.File;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Plugin main class
 * <p/>
 * ---------------| Project Information |---------------
 * <p/>
 * Project Lead: NathanTheBook
 * With Help From: skipperguy12 & gcflames5 (Nick)
 * <p/>
 * Description:
 * This plugin is meant to server as an easy way to store various documents in a Mongo database.
 * The plugin strives to be easily configurable and extendable.
 * This plugin is built with performance in mind.
 * This is plugin is also UUID ready and fully supports users with multiple names.
 */
public class DynamicDatabasePlugin extends JavaPlugin {
    private static DynamicDatabasePlugin instance;
    private CommandsManager<CommandSender> commands;
    private Datastore mainStore;
    private Map<Class<? extends Document>, Datastore> datastores = Maps.newHashMap();
    private Thread mainThread;
    private ServerDocument currentServerDocument;

    /**
     * Get an instance of the plugin.
     */
    public static DynamicDatabasePlugin getInstance() {
        if (instance == null) instance = new DynamicDatabasePlugin();
        return instance;
    }

    /**
     * Get the plugin's main thread
     */
    public Thread getMainThread() {
        return mainThread;
    }

    @Override
    public void onEnable() {
        instance = this;
        mainThread = Thread.currentThread();
        setupConfig();
        setupDebugging();
        setupMongo();
        MongoExecutionService.createExecutorService();
        Config.Tasks.convertAllNamesToClusters();
        setupServer();
        registerListeners();
        setupCommands();
    }

    // Load the debugging service
    public void setupDebugging() {
        for (String classname : Config.Debug.allowedChannels) {
            Debug.filter(classname);
        }
    }

    // Check if the server is in the database. If not, throw a warning.
    public void setupServer() {
        ServerDocument serverDocument = getDatastores().get(ServerDocument.class).createQuery(ServerDocument.class).filter("_id", ObjectId.massageToObjectId(Config.Mongo.serverId)).get();
        if (serverDocument == null) Debug.log(Debug.LogLevel.SEVERE, "Server not found in database!");
        this.currentServerDocument = serverDocument;
    }

    @Override
    public void onDisable() {
        instance = null;
        MongoExecutionService.destroyExecutorService(false);
    }

    // Load the plugin configuration and language file
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

    // Connect to mongo.
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

    // Map every class in the documents package to morphia.
    private void setupMorphia(MongoClient mongo) {
        Morphia morphia = new Morphia();
        morphia.mapPackage("us.nsakt.dynamicdatabase.documents", true);
        mainStore = morphia.createDatastore(mongo, Config.Mongo.database);
        fixClassLoader(morphia);
        setupDataStores(mongo, morphia);
    }

    // Fix the morphia class loader.
    private void fixClassLoader(Morphia morphia) {
        morphia.getMapper().getOptions().objectFactory = new DefaultCreator() {
            @Override
            protected ClassLoader getClassLoaderForClass() {
                return DynamicDatabasePlugin.getInstance().getClassLoader();
            }
        };
    }

    // Create a map of datastores.
    private void setupDataStores(MongoClient mongo, Morphia morphia) {
        Reflections reflections = new Reflections("us.nsakt.dynamicdatabase");
        Set<Class<? extends Document>> classes = reflections.getSubTypesOf(Document.class);
        for (Class<? extends Document> doc : classes) {
            Datastore store = morphia.createDatastore(mongo, doc.getAnnotation(Entity.class).value());
            datastores.put(doc, store);
            store.ensureIndexes();
        }
    }

    // Setup the command framework.
    private void setupCommands() {
        this.commands = new CommandsManager<CommandSender>() {
            @Override
            public boolean hasPermission(CommandSender sender, String perm) {
                return sender instanceof ConsoleCommandSender || sender.hasPermission(perm);
            }
        };
        CommandsManagerRegistration cmdRegister = new CommandsManagerRegistration(this, this.commands);
    }

    // Register the plugin listeners. DO NOT PUT PACKET LISTENERS HERE!
    private void registerListeners() {
    }

    // Utility to register a bukkit event listener.
    private void registerEvents(Listener listener) {
        Bukkit.getPluginManager().registerEvents(listener, this);
    }

    /**
     * Get the plugin's main datastore. (The one it initially connected to)
     */
    public Datastore getAuthDatastore() {
        return mainStore;
    }

    /**
     * Get all other datastores in a hashmap with their corresponding documents.
     */
    public Map<Class<? extends Document>, Datastore> getDatastores() {
        return datastores;
    }

    // Pass commands to the framework
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

    /**
     * Get the ServerDocument connected to the current server.
     */
    public ServerDocument getCurrentServerDocument() {
        return this.currentServerDocument;
    }
}
