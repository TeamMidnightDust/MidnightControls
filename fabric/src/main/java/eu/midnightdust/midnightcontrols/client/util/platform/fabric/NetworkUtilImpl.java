package eu.midnightdust.midnightcontrols.client.util.platform.fabric;

import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import static eu.midnightdust.midnightcontrols.client.MidnightControlsClient.client;

/**
 * Implementation of fabric methods for
 * @see eu.midnightdust.midnightcontrols.client.util.platform.NetworkUtil
 */
public class NetworkUtilImpl {
    private static final ClientPacketListener handler = client.getConnection();

    public static void sendPacketC2S(Packet<?> packet) {
        if (handler != null)
            handler.send(packet);
    }
    public static void sendPayloadC2S(CustomPacketPayload payload) {
        if (handler != null && client.level != null)
            handler.send(new ServerboundCustomPayloadPacket(payload));
    }
}