package net.njay.dynamicdatabase.serverinterconnect.packets;

import net.njay.serverinterconnect.packet.Packet;
import org.bson.types.ObjectId;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Packet to represent an admin chat message.
 *
 * @author NathanTheBook
 */
public class AdminChatPacket extends DynamicDBPacket {
    private String player;
    private String message;

    public AdminChatPacket() {
    }

    public AdminChatPacket(ObjectId server, ObjectId cluster, String player, String message) {
        super(server, cluster);
        this.player = player;
        this.message = message;
    }

    @Override
    public void readPacketContent(DataInputStream input) throws IOException {
        server = new ObjectId(Packet.readBytes(input));
        cluster = new ObjectId(Packet.readBytes(input));
        player = Packet.readString(input);
        message = Packet.readString(input);
    }

    @Override
    public void writePacketContent(DataOutputStream output) throws IOException {
        Packet.writeByteArray(output, server.toByteArray());
        Packet.writeByteArray(output, cluster.toByteArray());
        Packet.writeString(player, output);
        Packet.writeString(message, output);
    }

    public String getPlayer() {
        return player;
    }

    public String getMessage() {
        return message;
    }
}