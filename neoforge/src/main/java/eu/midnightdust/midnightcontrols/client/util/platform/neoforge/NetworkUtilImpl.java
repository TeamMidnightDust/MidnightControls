package eu.midnightdust.midnightcontrols.client.util.platform.neoforge;

import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.network.packet.Packet;
import net.neoforged.neoforge.network.PacketDistributor;

import static eu.midnightdust.midnightcontrols.client.MidnightControlsClient.client;

/**
 * Implementation of neoforge methods for
 * @see eu.midnightdust.midnightcontrols.client.util.platform.NetworkUtil
 */
public class NetworkUtilImpl {
    private static final ClientPlayNetworkHandler handler = client.getNetworkHandler();

    public static void sendPacketC2S(Packet<?> packet) {
        if (handler != null)
            handler.send(packet);
    }
    public static void sendPayloadC2S(CustomPayload payload) {
        if (handler != null && client.world != null && handler.hasChannel(payload.getId().id()))
            PacketDistributor.sendToServer(payload);
    }
}