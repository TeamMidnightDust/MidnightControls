package eu.midnightdust.midnightcontrols.fabric;

//? fabric {
import eu.midnightdust.midnightcontrols.MidnightControlsConstants;
import eu.midnightdust.midnightcontrols.client.MidnightControlsClient;
import eu.midnightdust.midnightcontrols.client.MidnightControlsConfig;
import eu.midnightdust.midnightcontrols.client.MidnightControlsReloadListener;
import eu.midnightdust.midnightcontrols.fabric.event.MouseClickListener;
import eu.midnightdust.midnightcontrols.packet.ControlsModePayload;
import eu.midnightdust.midnightcontrols.packet.FeaturePayload;
import eu.midnightdust.midnightcontrols.packet.HelloPayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
//~ if >=26.1 'net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper' -> 'net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper'
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.fabricmc.fabric.api.resource.v1.pack.PackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.server.packs.PackType;

import java.util.Optional;

import static eu.midnightdust.midnightcontrols.MidnightControls.id;
import static eu.midnightdust.midnightcontrols.client.MidnightControlsClient.BINDING_LOOK_DOWN;
import static eu.midnightdust.midnightcontrols.client.MidnightControlsClient.BINDING_LOOK_LEFT;
import static eu.midnightdust.midnightcontrols.client.MidnightControlsClient.BINDING_LOOK_RIGHT;
import static eu.midnightdust.midnightcontrols.client.MidnightControlsClient.BINDING_LOOK_UP;
import static eu.midnightdust.midnightcontrols.client.MidnightControlsClient.BINDING_RING;

public class MidnightControlsClientFabric implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        //~ if >=26.1 'KeyBindingHelper.registerKeyBinding' -> 'KeyMappingHelper.registerKeyMapping' {
        KeyMappingHelper.registerKeyMapping(BINDING_LOOK_UP);
        KeyMappingHelper.registerKeyMapping(BINDING_LOOK_RIGHT);
        KeyMappingHelper.registerKeyMapping(BINDING_LOOK_DOWN);
        KeyMappingHelper.registerKeyMapping(BINDING_LOOK_LEFT);
        KeyMappingHelper.registerKeyMapping(BINDING_RING);
        //~}
        ClientPlayNetworking.registerGlobalReceiver(ControlsModePayload.PACKET_ID, (payload, context) ->
                context.responseSender().sendPacket(new ControlsModePayload(MidnightControlsConfig.controlsMode.getName())));
        ClientPlayNetworking.registerGlobalReceiver(FeaturePayload.PACKET_ID, ((payload, context) -> {}));

        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            var version = "";
            Optional<ModContainer> container;
            if ((container = FabricLoader.getInstance().getModContainer(MidnightControlsConstants.NAMESPACE)).isPresent()) {
                version = container.get().getMetadata().getVersion().getFriendlyString();
            }
            var controlsMode = MidnightControlsConfig.controlsMode.getName();
            sender.sendPacket(new HelloPayload(version, controlsMode));
            sender.sendPacket(new ControlsModePayload(controlsMode));
            MidnightControlsClient.onJoinServer();
        });
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> MidnightControlsClient.onLeaveServer());

        ClientTickEvents.START_CLIENT_TICK.register(MidnightControlsClient::onTick);
        ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
            ScreenMouseEvents.allowMouseClick(screen).register(new MouseClickListener(screen));
        });

        FabricLoader.getInstance().getModContainer(MidnightControlsConstants.NAMESPACE).ifPresent(modContainer -> {
            ResourceLoader.registerBuiltinPack(id("bedrock"), modContainer, PackActivationType.NORMAL);
            ResourceLoader.registerBuiltinPack(id("legacy"), modContainer, PackActivationType.NORMAL);
        });
        MidnightControlsClient.initClient();

        //~ if >=26.1 '.registerReloader' -> '.registerReloadListener'
        ResourceLoader.get(PackType.CLIENT_RESOURCES).registerReloadListener(id("keyboard_layouts"), MidnightControlsReloadListener.INSTANCE);
    }
}
//?}