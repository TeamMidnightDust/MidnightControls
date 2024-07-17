package eu.midnightdust.midnightcontrols.packet;

import eu.midnightdust.midnightcontrols.MidnightControlsConstants;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

import java.util.Objects;

public record ControlsModePayload(String controlsMode) implements CustomPayload {
    public static final Id<ControlsModePayload> PACKET_ID = new Id<>(MidnightControlsConstants.CONTROLS_MODE_CHANNEL);
    public static final PacketCodec<RegistryByteBuf, ControlsModePayload> codec = PacketCodec.of(ControlsModePayload::write, ControlsModePayload::read);

    public static ControlsModePayload read(RegistryByteBuf buf) {
        return new ControlsModePayload(buf.readString(32));
    }

    public void write(RegistryByteBuf buf) {
        Objects.requireNonNull(controlsMode, "Controls mode cannot be null.");
        buf.writeString(controlsMode, 32);
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }
}