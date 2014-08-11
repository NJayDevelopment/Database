package us.nsakt.dynamicdatabase.commands;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import us.nsakt.dynamicdatabase.Config;
import us.nsakt.dynamicdatabase.DynamicDatabasePlugin;
import us.nsakt.dynamicdatabase.serverinterconnect.ConnectionManager;
import us.nsakt.dynamicdatabase.serverinterconnect.packets.AdminChatPacket;
import us.nsakt.dynamicdatabase.util.BroadcastUtils;

/**
 * Command class for sending admin chat messages
 *
 * @author NathanTheBook
 */
public class AdminChatCommand {

    @Command(
            aliases = {"a", "adminchat"},
            desc = "Speak in admin chat",
            min = 1,
            max = -1,
            anyFlags = true
    )
    @CommandPermissions("dynamicdb.adminchat.send")
    public static void adminChat(CommandContext args, CommandSender sender) throws CommandException {
        String message = BroadcastUtils.generateAdminChatMessage(sender.getName(), args.getJoinedStrings(0));
        Bukkit.broadcast(message, "dynamicdb.adminchat.receive");
        Bukkit.getConsoleSender().sendMessage(message);
        if (Config.CrossServer.enabled && Config.CrossServer.AdminChat.enabled && Config.CrossServer.AdminChat.send) {
            AdminChatPacket packet = new AdminChatPacket(DynamicDatabasePlugin.getInstance().getCurrentServerDocument().getObjectId(), DynamicDatabasePlugin.getInstance().getCurrentServerDocument().getCluster().getObjectId(), sender.getName(), args.getJoinedStrings(0));
            ConnectionManager.sendPacket(packet);
        }
    }
}
