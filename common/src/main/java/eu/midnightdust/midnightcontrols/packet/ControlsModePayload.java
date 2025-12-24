package eu.midnightdust.midnightcontrols.packet;

import eu.midnightdust.midnightcontrols.MidnightControlsConstants;
import java.util.Objects;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

public record ControlsModePayload(String controlsMode) implements CustomPacketPayload {
    public static final Type<@NotNull ControlsModePayload> PACKET_ID = new Type<>(MidnightControlsConstants.CONTROLS_MODE_CHANNEL);
    public static final StreamCodec<@NotNull RegistryFriendlyByteBuf, @NotNull ControlsModePayload> codec = StreamCodec.ofMember(ControlsModePayload::write, ControlsModePayload::read);

    public static ControlsModePayload read(RegistryFriendlyByteBuf buf) {
        return new ControlsModePayload(buf.readUtf(32));
    }

    public void write(RegistryFriendlyByteBuf buf) {
        Objects.requireNonNull(controlsMode, "Controls mode cannot be null.");
        buf.writeUtf(controlsMode, 32);
    }

    @Override
    public @NotNull Type<? extends @NotNull CustomPacketPayload> type() {
        return PACKET_ID;
    }
}