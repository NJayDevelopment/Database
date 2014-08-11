package us.nsakt.dynamicdatabase.serverinterconnect.listeners;

import com.sk89q.minecraft.util.commands.ChatColor;
import event.Event;
import event.EventHandler;
import event.Listener;
import net.njay.serverinterconnect.event.PacketRecievedEvent;
import org.bukkit.Bukkit;
import us.nsakt.dynamicdatabase.DynamicDatabasePlugin;
import us.nsakt.dynamicdatabase.daos.DAOService;
import us.nsakt.dynamicdatabase.documents.ServerDocument;
import us.nsakt.dynamicdatabase.serverinterconnect.packets.AdminChatPacket;
import us.nsakt.dynamicdatabase.util.BroadcastUtils;

/**
 * Listener fot AdminChat packets.
 *
 * @author NathanTheBook
 */
public class AdminChatListener implements Listener {
    public AdminChatListener() {
        Event.addListener(this);
    }

    @EventHandler
    public void onAdminChat(PacketRecievedEvent e) {
        if (!(e.getPacket() instanceof AdminChatPacket)) return;
        AdminChatPacket packet = (AdminChatPacket) e.getPacket();
        if (packet.getServer().equals(DynamicDatabasePlugin.getInstance().getCurrentServerDocument().getObjectId()))
            return;
        ServerDocument server = DAOService.getServers().findOne(ServerDocument.MongoFields.id.fieldName, packet.getServer());
        StringBuilder message = new StringBuilder();
        message.append(BroadcastUtils.generateCrossServerPrefix(server));
        message.append(BroadcastUtils.generatePrefix("A")).append(" ");
        message.append(ChatColor.DARK_AQUA).append(packet.getPlayer()).append(ChatColor.RESET).append(": ").append(packet.getMessage());
        Bukkit.broadcast(message.toString(), "adminchat.see");
    }
}