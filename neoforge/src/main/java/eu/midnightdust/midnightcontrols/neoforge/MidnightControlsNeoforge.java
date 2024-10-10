package eu.midnightdust.midnightcontrols.neoforge;

import eu.midnightdust.midnightcontrols.ControlsMode;
import eu.midnightdust.midnightcontrols.MidnightControls;
import eu.midnightdust.midnightcontrols.MidnightControlsFeature;
import eu.midnightdust.midnightcontrols.client.MidnightControlsConfig;
import eu.midnightdust.midnightcontrols.neoforge.event.PlayerChangeControlsModeEvent;
import eu.midnightdust.midnightcontrols.packet.ControlsModePayload;
import eu.midnightdust.midnightcontrols.packet.FeaturePayload;
import eu.midnightdust.midnightcontrols.packet.HelloPayload;
import net.minecraft.network.packet.c2s.common.CustomPayloadC2SPacket;
import net.minecraft.network.packet.s2c.common.CustomPayloadS2CPacket;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

import static eu.midnightdust.midnightcontrols.MidnightControlsConstants.NAMESPACE;

@Mod(value = NAMESPACE)
public class MidnightControlsNeoforge {
    public MidnightControlsNeoforge() {
        MidnightControls.init();
    }
    @EventBusSubscriber(modid = NAMESPACE, bus = EventBusSubscriber.Bus.MOD)
    public class CommonEvents {
        @SubscribeEvent
        public static void registerPayloads(RegisterPayloadHandlersEvent event) {
            PayloadRegistrar registrar = event.registrar("1").optional();
            registrar.playToServer(HelloPayload.PACKET_ID, HelloPayload.codec, (payload, context) -> {
                ControlsMode.byId(payload.controlsMode()).ifPresent(controlsMode -> new PlayerChangeControlsModeEvent(context.player(), controlsMode));
                context.connection().send(new CustomPayloadS2CPacket(new FeaturePayload(MidnightControlsFeature.HORIZONTAL_REACHAROUND)));
            });
            registrar.playBidirectional(ControlsModePayload.PACKET_ID, ControlsModePayload.codec, (payload, context) -> {
                if (context.flow().isServerbound()) ControlsMode.byId(payload.controlsMode()).ifPresent(controlsMode -> new PlayerChangeControlsModeEvent(context.player(), controlsMode));
                else context.connection().send(new CustomPayloadC2SPacket(new ControlsModePayload(MidnightControlsConfig.controlsMode.getName())));
            });
            registrar.playToClient(FeaturePayload.PACKET_ID, FeaturePayload.codec, (payload, context) -> {});
        }
    }
}