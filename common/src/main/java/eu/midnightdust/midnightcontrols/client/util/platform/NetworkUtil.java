package eu.midnightdust.midnightcontrols.client.util.platform;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public class NetworkUtil {
    @ExpectPlatform
    public static void sendPacketC2S(Packet<?> packet) {
        throw new AssertionError();
    }
    @ExpectPlatform
    public static void sendPayloadC2S(CustomPacketPayload payload) {
        throw new AssertionError();
    }
}