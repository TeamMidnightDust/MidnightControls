package eu.midnightdust.midnightcontrols.packet;

import eu.midnightdust.midnightcontrols.MidnightControlsConstants;
import eu.midnightdust.midnightcontrols.MidnightControlsFeature;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record FeaturePayload(MidnightControlsFeature... features) implements CustomPacketPayload {
    public static final Type<FeaturePayload> PACKET_ID = new Type<>(MidnightControlsConstants.FEATURE_CHANNEL);
    public static final StreamCodec<RegistryFriendlyByteBuf, FeaturePayload> codec = StreamCodec.ofMember(FeaturePayload::write, FeaturePayload::read);

    public static FeaturePayload read(RegistryFriendlyByteBuf buf) {
        int featureLength = buf.readVarInt();
        MidnightControlsFeature[] receivedFeatures = new MidnightControlsFeature[featureLength];
        for (int i = 0; i < featureLength; i++) {
            var name = buf.readUtf(64);
            boolean allowed = buf.readBoolean();
            var feature = MidnightControlsFeature.fromName(name);
            if (feature.isPresent()) {
                feature.get().setAllowed(allowed);
                receivedFeatures[i] = feature.get();
            }
        }
        return new FeaturePayload(receivedFeatures);
    }

    public void write(RegistryFriendlyByteBuf buf) {
        if (features.length == 0)
            throw new IllegalArgumentException("At least one feature must be provided.");

        buf.writeVarInt(features.length);
        for (var feature : features) {
            buf.writeUtf(feature.getName(), 64);
            buf.writeBoolean(feature.isAllowed());
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return PACKET_ID;
    }
}