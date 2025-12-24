package eu.midnightdust.midnightcontrols.neoforge;

import eu.midnightdust.midnightcontrols.MidnightControls;
import eu.midnightdust.midnightcontrols.client.MidnightControlsClient;
import eu.midnightdust.midnightcontrols.client.MidnightControlsConfig;
import eu.midnightdust.midnightcontrols.client.MidnightControlsReloadListener;
import eu.midnightdust.midnightcontrols.client.util.platform.NetworkUtil;
import eu.midnightdust.midnightcontrols.packet.ControlsModePayload;
import eu.midnightdust.midnightcontrols.packet.HelloPayload;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.event.AddPackFindersEvent;

import static eu.midnightdust.midnightcontrols.MidnightControls.id;
import static eu.midnightdust.midnightcontrols.MidnightControlsConstants.NAMESPACE;
import static eu.midnightdust.midnightcontrols.client.MidnightControlsClient.BINDING_LOOK_DOWN;
import static eu.midnightdust.midnightcontrols.client.MidnightControlsClient.BINDING_LOOK_LEFT;
import static eu.midnightdust.midnightcontrols.client.MidnightControlsClient.BINDING_LOOK_RIGHT;
import static eu.midnightdust.midnightcontrols.client.MidnightControlsClient.BINDING_LOOK_UP;
import static eu.midnightdust.midnightcontrols.client.MidnightControlsClient.BINDING_RING;
import static eu.midnightdust.midnightcontrols.client.MidnightControlsClient.client;
import static eu.midnightdust.midnightcontrols.client.MidnightControlsClient.clickInterceptor;


@Mod(value = NAMESPACE, dist = Dist.CLIENT)
public class MidnightControlsClientNeoforge {
    public MidnightControlsClientNeoforge() {
        MidnightControlsClient.initClient();
    }

    @EventBusSubscriber(modid = NAMESPACE, value = Dist.CLIENT)
    public static class ClientEvents {
        @SubscribeEvent
        public static void registerKeybinding(RegisterKeyMappingsEvent event) {
            event.register(BINDING_RING);
            event.register(BINDING_LOOK_UP);
            event.register(BINDING_LOOK_DOWN);
            event.register(BINDING_LOOK_LEFT);
            event.register(BINDING_LOOK_RIGHT);
        }
        @SubscribeEvent
        public static void addPackFinders(AddPackFindersEvent event) {
            if (false) { // TODO: Switch to stonecutter build system to be able to test packs in dev environment
                event.addPackFinders(id("bedrock"), PackType.CLIENT_RESOURCES, Component.literal("midnightcontrols/bedrock"), PackSource.BUILT_IN, false, Pack.Position.TOP);
                event.addPackFinders(id("legacy"), PackType.CLIENT_RESOURCES, Component.literal("midnightcontrols/legacy"), PackSource.BUILT_IN, false, Pack.Position.TOP);
            }
        }
        @SubscribeEvent
        public static void onResourceReload(AddClientReloadListenersEvent event) {
            event.addListener(id("keyboard-layouts"), MidnightControlsReloadListener.INSTANCE);
        }
    }

    @EventBusSubscriber(modid = NAMESPACE, value = Dist.CLIENT)
    public static class ClientGameEvents {
        @SubscribeEvent
        public static void sendPacketOnLogin(ClientPlayerNetworkEvent.LoggingIn event) {
            var version = ModList.get().getModFileById(NAMESPACE).versionString();
            var controlsMode = MidnightControlsConfig.controlsMode.getName();
            NetworkUtil.sendPayloadC2S(new HelloPayload(version, controlsMode));
            NetworkUtil.sendPayloadC2S(new ControlsModePayload(controlsMode));
        }
        @SubscribeEvent
        public static void onDisconnect(ClientPlayerNetworkEvent.LoggingOut event) {
            MidnightControlsClient.onLeave();
        }
        @SubscribeEvent
        public static void startClientTick(ClientTickEvent.Pre event) {
            MidnightControlsClient.onTick(client);
        }
        @SubscribeEvent
        public static void onMouseButtonPressed(ScreenEvent.MouseButtonPressed.Pre event) {
            if (MidnightControlsConfig.virtualKeyboard && !event.isCanceled()) {
                Screen screen = event.getScreen();
                clickInterceptor.intercept(screen, event.getMouseButtonEvent());
            }
        }
    }
}
