package eu.midnightdust.midnightcontrols.packet;

import eu.midnightdust.midnightcontrols.MidnightControlsConstants;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record HelloPayload(String version, String controlsMode) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<HelloPayload> PACKET_ID = new CustomPacketPayload.Type<>(MidnightControlsConstants.HELLO_CHANNEL);
    public static final StreamCodec<RegistryFriendlyByteBuf, HelloPayload> codec = StreamCodec.ofMember(HelloPayload::write, HelloPayload::read);

    public static HelloPayload read(RegistryFriendlyByteBuf buf) {
        return new HelloPayload(buf.readUtf(32), buf.readUtf(32));
    }

    public void write(RegistryFriendlyByteBuf buf) {
        buf.writeUtf(version, 32).writeUtf(controlsMode, 32);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return PACKET_ID;
    }
}