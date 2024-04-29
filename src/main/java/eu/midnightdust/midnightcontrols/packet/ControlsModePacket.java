package eu.midnightdust.midnightcontrols.packet;

import eu.midnightdust.midnightcontrols.MidnightControlsConstants;
import eu.midnightdust.midnightcontrols.client.MidnightControlsConfig;
import io.netty.buffer.Unpooled;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

import java.util.Objects;
import java.util.Optional;

public record ControlsModePacket(String controlsMode) implements CustomPayload {
    public static final Id<ControlsModePacket> PACKET_ID = new Id<>(MidnightControlsConstants.CONTROLS_MODE_CHANNEL);
    public static final PacketCodec<RegistryByteBuf, ControlsModePacket> codec = PacketCodec.of(ControlsModePacket::write, ControlsModePacket::read);

    public static ControlsModePacket read(RegistryByteBuf buf) {
        return new ControlsModePacket(buf.readString(32));
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