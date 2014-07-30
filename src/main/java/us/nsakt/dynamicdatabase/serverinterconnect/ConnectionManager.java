package us.nsakt.dynamicdatabase.serverinterconnect;

import net.njay.serverinterconnect.client.TcpClientManager;
import net.njay.serverinterconnect.packet.Packet;
import org.reflections.Reflections;
import us.nsakt.dynamicdatabase.Debug;
import us.nsakt.dynamicdatabase.serverinterconnect.packets.DynamicDBPacket;

import java.io.IOException;
import java.util.Set;

/**
 * Class to manage the connection with the ServerInterconnect server.
 *
 * @author NathanTheBook
 */
public class ConnectionManager {

    public static TcpClientManager manager;

    /**
     * Initialize the connection to the interconnect server.
     *
     * @param address Address of the server
     * @param port    Port of the server
     */
    public static void enable(String address, int port) {
        manager = new TcpClientManager(address, port);
        try {
            manager.initialize();
        } catch (IOException e) {
            Debug.log(e);
        }
    }

    /**
     * Send  a packet to the interconnect server.
     *
     * @param packet Packet to send
     */
    public static void sendPacket(Packet packet) {
        manager.getConnection().sendPacket(packet);
    }

    /**
     * Register all packets in the packets package.
     */
    public static void registerPackets() {
        Reflections reflections = new Reflections("us.nsakt.dynamicdatabase.serverinterconnect.packets");
        Set<Class<? extends DynamicDBPacket>> classes = reflections.getSubTypesOf(DynamicDBPacket.class);
        for (Class<? extends DynamicDBPacket> packet : classes) {
            Packet.registerPacket(packet);
        }
    }
}
