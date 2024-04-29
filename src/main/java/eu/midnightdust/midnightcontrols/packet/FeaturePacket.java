package eu.midnightdust.midnightcontrols.packet;

import eu.midnightdust.midnightcontrols.MidnightControlsConstants;
import eu.midnightdust.midnightcontrols.MidnightControlsFeature;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public record FeaturePacket(MidnightControlsFeature... features) implements CustomPayload {
    public static final Id<FeaturePacket> PACKET_ID = new Id<>(MidnightControlsConstants.FEATURE_CHANNEL);
    public static final PacketCodec<RegistryByteBuf, FeaturePacket> codec = PacketCodec.of(FeaturePacket::write, FeaturePacket::read);

    public static FeaturePacket read(RegistryByteBuf buf) {
        int featureLength = buf.readVarInt();
        MidnightControlsFeature[] receivedFeatures = new MidnightControlsFeature[featureLength];
        for (int i = 0; i < featureLength; i++) {
            var name = buf.readString(64);
            boolean allowed = buf.readBoolean();
            var feature = MidnightControlsFeature.fromName(name);
            if (feature.isPresent()) {
                feature.get().setAllowed(allowed);
                receivedFeatures[i] = feature.get();
            }
        }
        return new FeaturePacket(receivedFeatures);
    }

    public void write(RegistryByteBuf buf) {
        if (features.length == 0)
            throw new IllegalArgumentException("At least one feature must be provided.");

        buf.writeVarInt(features.length);
        for (var feature : features) {
            buf.writeString(feature.getName(), 64);
            buf.writeBoolean(feature.isAllowed());
        }
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }
}