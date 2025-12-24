package eu.midnightdust.midnightcontrols.fabric;

//? fabric {
/*import eu.midnightdust.midnightcontrols.MidnightControlsConstants;
import eu.midnightdust.midnightcontrols.client.MidnightControlsClient;
import eu.midnightdust.midnightcontrols.client.MidnightControlsConfig;
import eu.midnightdust.midnightcontrols.client.MidnightControlsReloadListener;
import eu.midnightdust.midnightcontrols.fabric.event.MouseClickListener;
import eu.midnightdust.midnightcontrols.packet.ControlsModePayload;
import eu.midnightdust.midnightcontrols.packet.FeaturePayload;
import eu.midnightdust.midnightcontrols.packet.HelloPayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;

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
        KeyBindingHelper.registerKeyBinding(BINDING_LOOK_UP);
        KeyBindingHelper.registerKeyBinding(BINDING_LOOK_RIGHT);
        KeyBindingHelper.registerKeyBinding(BINDING_LOOK_DOWN);
        KeyBindingHelper.registerKeyBinding(BINDING_LOOK_LEFT);
        KeyBindingHelper.registerKeyBinding(BINDING_RING);
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
        });
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> MidnightControlsClient.onLeave());

        ClientTickEvents.START_CLIENT_TICK.register(MidnightControlsClient::onTick);
        ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
            ScreenMouseEvents.allowMouseClick(screen).register(new MouseClickListener(screen));
        });

        FabricLoader.getInstance().getModContainer(MidnightControlsConstants.NAMESPACE).ifPresent(modContainer -> {
            ResourceManagerHelper.registerBuiltinResourcePack(id("bedrock"), modContainer, ResourcePackActivationType.NORMAL);
            ResourceManagerHelper.registerBuiltinResourcePack(id("legacy"), modContainer, ResourcePackActivationType.NORMAL);
        });
        MidnightControlsClient.initClient();

        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
            @Override
            public Identifier getFabricId() {
                return id("keyboard_layouts");
            }
            @Override
            public void onResourceManagerReload(ResourceManager manager) {
                MidnightControlsReloadListener.INSTANCE.onResourceManagerReload(manager);
            }
        });
    }
}
*///?}