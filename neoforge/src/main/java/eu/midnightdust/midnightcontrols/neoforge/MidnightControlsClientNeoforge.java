package eu.midnightdust.midnightcontrols.neoforge;

import eu.midnightdust.midnightcontrols.client.MidnightControlsClient;
import eu.midnightdust.midnightcontrols.client.MidnightControlsConfig;
import eu.midnightdust.midnightcontrols.client.util.platform.NetworkUtil;
import eu.midnightdust.midnightcontrols.packet.ControlsModePayload;
import eu.midnightdust.midnightcontrols.packet.HelloPayload;
import net.minecraft.resource.DirectoryResourcePack;
import net.minecraft.resource.ResourcePackInfo;
import net.minecraft.resource.ResourcePackPosition;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.resource.ResourcePackSource;
import net.minecraft.resource.ResourceType;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.event.AddPackFindersEvent;
import net.neoforged.neoforgespi.locating.IModFile;

import java.util.Optional;

import static eu.midnightdust.midnightcontrols.MidnightControls.id;
import static eu.midnightdust.midnightcontrols.MidnightControlsConstants.NAMESPACE;
import static eu.midnightdust.midnightcontrols.client.MidnightControlsClient.BINDING_LOOK_DOWN;
import static eu.midnightdust.midnightcontrols.client.MidnightControlsClient.BINDING_LOOK_LEFT;
import static eu.midnightdust.midnightcontrols.client.MidnightControlsClient.BINDING_LOOK_RIGHT;
import static eu.midnightdust.midnightcontrols.client.MidnightControlsClient.BINDING_LOOK_UP;
import static eu.midnightdust.midnightcontrols.client.MidnightControlsClient.BINDING_RING;
import static eu.midnightdust.midnightcontrols.client.MidnightControlsClient.client;

@Mod(value = NAMESPACE, dist = Dist.CLIENT)
public class MidnightControlsClientNeoforge {
    public MidnightControlsClientNeoforge() {
        MidnightControlsClient.initClient();
    }

    @EventBusSubscriber(modid = NAMESPACE, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public class ClientEvents {
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
            if (event.getPackType() == ResourceType.CLIENT_RESOURCES) {
                registerResourcePack(event, id("bedrock"), false);
                registerResourcePack(event, id("legacy"), false);
            }
        }
        private static void registerResourcePack(AddPackFindersEvent event, Identifier id, boolean alwaysEnabled) {
            event.addRepositorySource(((profileAdder) -> {
                IModFile file = ModList.get().getModFileById(id.getNamespace()).getFile();
                try {
                    ResourcePackProfile.PackFactory pack = new DirectoryResourcePack.DirectoryBackedFactory(file.findResource("resourcepacks/" + id.getPath()));
                    ResourcePackInfo info = new ResourcePackInfo(id.toString(), Text.of(id.getNamespace()+"/"+id.getPath()), ResourcePackSource.BUILTIN, Optional.empty());
                    ResourcePackProfile packProfile = ResourcePackProfile.create(info, pack, ResourceType.CLIENT_RESOURCES, new ResourcePackPosition(alwaysEnabled, ResourcePackProfile.InsertionPosition.TOP, false));
                    if (packProfile != null) {
                        profileAdder.accept(packProfile);
                    }
                } catch (NullPointerException e) {e.fillInStackTrace();}
            }));
        }
    }

    @EventBusSubscriber(modid = NAMESPACE, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
    public class ClientGameEvents {
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
    }
}
