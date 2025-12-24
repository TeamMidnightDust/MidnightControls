package eu.midnightdust.midnightcontrols.client.util.platform;

import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import static eu.midnightdust.midnightcontrols.client.MidnightControlsClient.client;

public class NetworkUtil {
    private static final ClientPacketListener handler = client.getConnection();

    public static void sendPacketC2S(Packet<?> packet) {
        if (handler != null)
            handler.send(packet);
    }
    public static void sendPayloadC2S(CustomPacketPayload payload) {
        if (handler != null && client.level != null) {
            try {
                handler.send(new ServerboundCustomPayloadPacket(payload));
            } catch (Exception e) {
                e.fillInStackTrace();
            }
        }
    }
}