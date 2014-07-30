package us.nsakt.dynamicdatabase.commands;

import com.google.common.collect.Lists;
import com.sk89q.minecraft.util.commands.ChatColor;
import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import com.sk89q.util.StringUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import us.nsakt.dynamicdatabase.Debug;
import us.nsakt.dynamicdatabase.DynamicDatabasePlugin;
import us.nsakt.dynamicdatabase.MongoExecutionService;
import us.nsakt.dynamicdatabase.daos.DAOGetter;
import us.nsakt.dynamicdatabase.documents.ServerDocument;
import us.nsakt.dynamicdatabase.documents.UserDocument;
import us.nsakt.dynamicdatabase.tasks.core.base.DBCallback;
import us.nsakt.dynamicdatabase.util.StringUtils;

import java.util.List;
import java.util.UUID;

/**
 * Basic commands that players can perform to get server information.
 *
 * @author NathanTheBook
 */
public class PlayerCommands {

    @Command(
            aliases = {"staff", "onlinestaff", "ops", "mods", "onlinemods"},
            desc = "Speak in admin chat",
            min = 0,
            max = 1,
            flags = "a"
    )
    @CommandPermissions("dynamicdb.staff.list")
    public static void staffList(final CommandContext args, final CommandSender sender) throws CommandException {
        Runnable commandRunner = new Runnable() {
            @Override
            public void run() {
                final StringBuilder message = new StringBuilder();
                if (args.hasFlag('a')) {
                    sender.sendMessage("Gi");
                    List<ServerDocument> servers = Lists.newArrayList();

                    message.append(StringUtils.padMessage("Online Staff [All Servers]", ChatColor.STRIKETHROUGH + "-", ChatColor.GREEN, ChatColor.DARK_AQUA));
                    message.append("\n");
                    if (servers.isEmpty()) {
                        sender.sendMessage(ChatColor.RED + "There are no servers in the database, or you just don't have permissions to see any of them.");
                        return;
                    }
                    for (ServerDocument document : servers) {
                        if (document.equals(DynamicDatabasePlugin.getInstance().getCurrentServerDocument())) {
                            message.append(ChatColor.GOLD).append(ChatColor.BOLD).append("Current Server").append(ChatColor.RESET).append(": ");
                            List<String> players = Lists.newArrayList();
                            for (UserDocument user : document.getOnlineStaff()) {
                                players.add(Bukkit.getPlayer(user.getUuid()).getDisplayName());
                            }
                            message.append(StringUtils.listToEnglishCompound(players));
                            message.append("\n");
                        } else {
                            message.append(ChatColor.DARK_AQUA).append(document.getName()).append(ChatColor.RESET).append(": ");
                            List<String> players = Lists.newArrayList();
                            for (UserDocument user : document.getOnlineStaff()) {
                                players.add(user.getLastUsername());
                            }
                            message.append(StringUtils.listToEnglishCompound(players));
                            message.append("\n");
                        }
                    }
                    sender.sendMessage(message.toString());
                }
                else if (args.argsLength() > 0) {
                    ServerDocument serverDocument = new DAOGetter().getServers().getDatastore().find(ServerDocument.class).field(ServerDocument.MongoFields.CLUSTER.fieldName).equal(DynamicDatabasePlugin.getInstance().getCurrentServerDocument().getCluster()).field(ServerDocument.MongoFields.NAME.fieldName).equal(args.getString(0)).get();
                    if (serverDocument == null) sender.sendMessage(ChatColor.RED + "Server not found!" + ChatColor.AQUA + "Use " + ChatColor.GOLD + "/servers " + ChatColor.AQUA + "for a list of all servers.");
                    message.append(StringUtils.padMessage("Online Staff [" + serverDocument.getName() + "]", ChatColor.STRIKETHROUGH + "-", ChatColor.GREEN, ChatColor.DARK_AQUA));
                    message.append("\n");
                    message.append(ChatColor.DARK_AQUA).append(serverDocument.getName()).append(ChatColor.RESET).append(": ");
                    if (serverDocument.getOnlineStaff().isEmpty()) { message.append(ChatColor.RED).append("No staff online!"); sender.sendMessage(message.toString()); return; }
                    List<String> players = Lists.newArrayList();
                    for (UserDocument user : serverDocument.getOnlineStaff()) {
                        players.add(user.getLastUsername());
                    }
                    message.append(StringUtils.listToEnglishCompound(players));
                    sender.sendMessage(message.toString());
                }
                else {
                    ServerDocument serverDocument = DynamicDatabasePlugin.getInstance().getCurrentServerDocument();
                    message.append(StringUtils.padMessage("Online Staff", "-", ChatColor.GREEN, ChatColor.DARK_AQUA));
                    if (serverDocument.getOnlineStaff().isEmpty()) { message.append("\n").append(ChatColor.RED).append("No staff online!"); sender.sendMessage(message.toString()); return; }
                    List<String> players = Lists.newArrayList();
                    for (UserDocument user : serverDocument.getOnlineStaff()) {
                        players.add(Bukkit.getPlayer(user.getUuid()).getDisplayName());
                    }
                    message.append("\n").append(StringUtils.listToEnglishCompound(players));
                    sender.sendMessage(message.toString());
                }
            }
        };
        MongoExecutionService.getExecutorService().submit(commandRunner);
    }
}