package us.nsakt.dynamicdatabase.serverinterconnect.packets;

import net.njay.serverinterconnect.packet.Packet;
import org.bson.types.ObjectId;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Base packet class that provides base data.
 *
 * @author NathanTheBook
 */
public class DynamicDBPacket extends Packet {
    protected ObjectId server;
    protected ObjectId cluster;

    public DynamicDBPacket() {
    }

    public DynamicDBPacket(ObjectId server, ObjectId cluster) {
        this.server = server;
        this.cluster = cluster;
    }

    @Override
    public void readPacketContent(DataInputStream input) throws IOException {
        server = new ObjectId(Packet.readBytes(input));
        cluster = new ObjectId(Packet.readBytes(input));
    }

    @Override
    public void writePacketContent(DataOutputStream output) throws IOException {
        Packet.writeByteArray(output, server.toByteArray());
        Packet.writeByteArray(output, cluster.toByteArray());
    }

    public ObjectId getServer() {
        return server;
    }

    public ObjectId getCluster() {
        return cluster;
    }
}
