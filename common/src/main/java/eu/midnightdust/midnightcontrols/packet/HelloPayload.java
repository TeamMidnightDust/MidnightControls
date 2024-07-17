package eu.midnightdust.midnightcontrols.packet;

import eu.midnightdust.midnightcontrols.MidnightControlsConstants;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public record HelloPayload(String version, String controlsMode) implements CustomPayload {
    public static final CustomPayload.Id<HelloPayload> PACKET_ID = new CustomPayload.Id<>(MidnightControlsConstants.HELLO_CHANNEL);
    public static final PacketCodec<RegistryByteBuf, HelloPayload> codec = PacketCodec.of(HelloPayload::write, HelloPayload::read);

    public static HelloPayload read(RegistryByteBuf buf) {
        return new HelloPayload(buf.readString(32), buf.readString(32));
    }

    public void write(RegistryByteBuf buf) {
        buf.writeString(version, 32).writeString(controlsMode, 32);
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }
}