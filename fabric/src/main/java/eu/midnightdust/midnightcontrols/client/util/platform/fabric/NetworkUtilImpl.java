package eu.midnightdust.midnightcontrols.client.util.platform.fabric;

import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.common.CustomPayloadC2SPacket;

import static eu.midnightdust.midnightcontrols.client.MidnightControlsClient.client;

/**
 * Implementation of fabric methods for
 * @see eu.midnightdust.midnightcontrols.client.util.platform.NetworkUtil
 */
public class NetworkUtilImpl {
    private static final ClientPlayNetworkHandler handler = client.getNetworkHandler();

    public static void sendPacketC2S(Packet<?> packet) {
        if (handler != null)
            handler.sendPacket(packet);
    }
    public static void sendPayloadC2S(CustomPayload payload) {
        if (handler != null && client.world != null)
            handler.sendPacket(new CustomPayloadC2SPacket(payload));
    }
}