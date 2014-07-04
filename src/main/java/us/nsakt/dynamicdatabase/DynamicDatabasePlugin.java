package us.nsakt.dynamicdatabase;

import com.mongodb.DBObject;
import com.mongodb.Mongo;
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

/**
 * Main Bukkit class
 */
public class DynamicDatabasePlugin extends JavaPlugin {
    // Singleton instance
    private static DynamicDatabasePlugin instance;
    // Datastore for Morphia
    private Datastore datastore;
    /**
     * sk89q's command framework CommandsManager
     */
    private CommandsManager<CommandSender> commands;

    /**
     * Gets the Singleton instance of DynamicDatabasePlugin
     *
     * @return Singleton instance of DynamicDatabasePlugin
     */
    public static DynamicDatabasePlugin getInstance() {
        if (instance == null) instance = new DynamicDatabasePlugin();
        return instance;
    }

    public void onEnable() {
        instance = this;

        this.getConfig().options().copyDefaults(true);
        this.saveConfig();

        setupConfig();

        registerListeners();

        // Debugging
        if (Config.Debug.hijackPrintStream) Debug.replaceMainOutChannel();
        for (String channel : Config.Debug.allowedChannels)
            Debug.allow(channel);

        // Mongo/Morphia
        Mongo mongo = null;
        try {
            mongo = new Mongo(Config.Mongo.Authentication.hostname, Config.Mongo.Authentication.port);
        } catch (UnknownHostException e) {
            Debug.EXCEPTION.debug(e);
        }

        Morphia morphia = new Morphia();
        morphia.map(); // Mapping of all morphia documents
        // Hacky ClassLoader fix.
        morphia.getMapper().getOptions().objectFactory = new DefaultCreator() {
            @Override
            protected ClassLoader getClassLoaderForClass(String clazz, DBObject object) {
                return DynamicDatabasePlugin.getInstance().getClassLoader();
            }
        };
        datastore = morphia.createDatastore(mongo, Config.Mongo.Authentication.database);
    }

    public void onDisable() {
        instance = null;
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
