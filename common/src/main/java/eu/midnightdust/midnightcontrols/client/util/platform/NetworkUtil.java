package eu.midnightdust.midnightcontrols.client.util.platform;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.network.packet.Packet;

public class NetworkUtil {
    @ExpectPlatform
    public static void sendPacketC2S(Packet<?> packet) {
        throw new AssertionError();
    }
    @ExpectPlatform
    public static void sendPayloadC2S(CustomPayload payload) {
        throw new AssertionError();
    }
}