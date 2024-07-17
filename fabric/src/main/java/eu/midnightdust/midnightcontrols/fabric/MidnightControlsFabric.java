package eu.midnightdust.midnightcontrols.fabric;

import eu.midnightdust.midnightcontrols.ControlsMode;
import eu.midnightdust.midnightcontrols.MidnightControls;
import eu.midnightdust.midnightcontrols.MidnightControlsFeature;
import eu.midnightdust.midnightcontrols.event.PlayerChangeControlsModeCallback;
import eu.midnightdust.midnightcontrols.packet.ControlsModePayload;
import eu.midnightdust.midnightcontrols.packet.FeaturePayload;
import eu.midnightdust.midnightcontrols.packet.HelloPayload;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

public class MidnightControlsFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        MidnightControls.init();
        PayloadTypeRegistry.playC2S().register(HelloPayload.PACKET_ID, HelloPayload.codec);
        PayloadTypeRegistry.playC2S().register(ControlsModePayload.PACKET_ID, ControlsModePayload.codec);
        PayloadTypeRegistry.playS2C().register(ControlsModePayload.PACKET_ID, ControlsModePayload.codec);
        PayloadTypeRegistry.playS2C().register(FeaturePayload.PACKET_ID, FeaturePayload.codec);

        ServerPlayNetworking.registerGlobalReceiver(HelloPayload.PACKET_ID, (payload, context) -> {
            ControlsMode.byId(payload.controlsMode())
                    .ifPresent(controlsMode -> PlayerChangeControlsModeCallback.EVENT.invoker().apply(context.player(), controlsMode));
            context.responseSender().sendPacket(new FeaturePayload(MidnightControlsFeature.HORIZONTAL_REACHAROUND));
        });
        ServerPlayNetworking.registerGlobalReceiver(ControlsModePayload.PACKET_ID,
                (payload, context) -> ControlsMode.byId(payload.controlsMode())
                        .ifPresent(controlsMode -> PlayerChangeControlsModeCallback.EVENT.invoker().apply(context.player(), controlsMode)));
    }
}
