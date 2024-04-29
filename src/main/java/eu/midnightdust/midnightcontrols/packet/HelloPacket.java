package eu.midnightdust.midnightcontrols.packet;

import eu.midnightdust.midnightcontrols.MidnightControlsConstants;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public record HelloPacket(String version, String controlsMode) implements CustomPayload {
    public static final CustomPayload.Id<HelloPacket> PACKET_ID = new CustomPayload.Id<>(MidnightControlsConstants.HELLO_CHANNEL);
    public static final PacketCodec<RegistryByteBuf, HelloPacket> codec = PacketCodec.of(HelloPacket::write, HelloPacket::read);

    public static HelloPacket read(RegistryByteBuf buf) {
        return new HelloPacket(buf.readString(32), buf.readString(32));
    }

    public void write(RegistryByteBuf buf) {
        buf.writeString(version, 32).writeString(controlsMode, 32);
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }
}