package us.nsakt.dynamicdatabase;

import com.google.common.collect.Lists;
import com.mongodb.*;
import com.sk89q.bukkit.util.CommandsManagerRegistration;
import com.sk89q.minecraft.util.commands.*;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.mapping.DefaultCreator;
import us.nsakt.dynamicdatabase.util.LanguageFile;

import java.io.File;
import java.net.UnknownHostException;
import java.util.List;

/**
 * Main Bukkit class
 */
public class DynamicDatabasePlugin extends JavaPlugin {
    // Singleton instance
    private static DynamicDatabasePlugin instance;
    // Datastore for Morphia
    private Datastore datastore;
    //sk89q's command framework CommandsManager
    private CommandsManager<CommandSender> commands;
    // The mainThread thread
    private Thread mainThread;

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
        QueryExecutor.createExecutorService();

        this.getConfig().options().copyDefaults(true);
        this.saveConfig();

        setupConfig();

        registerListeners();

        // Debugging
        if (Config.Debug.hijackPrintStream) Debug.replaceMainOutChannel();
        for (String channel : Config.Debug.allowedChannels)
            Debug.allow(channel);

        // Mongo
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
            Debug.EXCEPTION.debug(e);
        }

        DB database = mongo.getDB(Config.Mongo.database);

        if (Config.Mongo.Authentication.useAthentication && !database.authenticate(Config.Mongo.Authentication.username, Config.Mongo.Authentication.password.toCharArray())) {
            throw new MongoException("Could not authenticate to database " + database.getName());
        }

        // Morphia
        Morphia morphia = new Morphia();
        morphia.map(); // Mapping of all morphia documents
        // Hacky ClassLoader fix.
        morphia.getMapper().getOptions().objectFactory = new DefaultCreator() {
            @Override
            protected ClassLoader getClassLoaderForClass(String clazz, DBObject object) {
                return DynamicDatabasePlugin.getInstance().getClassLoader();
            }
        };
        datastore = morphia.createDatastore(mongo, Config.Mongo.database);

    }

    public void onDisable() {
        instance = null;
        QueryExecutor.destroyExecutorService(false);
    }

    // Sets up the Config for language
    private void setupConfig() {
        for (String string : Config.Languages.supportedLanguages) {
            File file = new File(string + ".lang");
            if (!file.exists()) continue;

            LanguageFile.LanguageEnum languageInfo = LanguageFile.getByAbbreviation(string);
            LanguageFile language = new LanguageFile(languageInfo);
            Config.getLanguageMap().put(languageInfo, language);
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
    public Datastore getDatastore() {
        return datastore;
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

}
