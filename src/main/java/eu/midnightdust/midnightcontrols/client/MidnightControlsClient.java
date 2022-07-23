/*
 * Copyright © 2021 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of midnightcontrols.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package eu.midnightdust.midnightcontrols.client;

import dev.lambdaurora.spruceui.event.OpenScreenCallback;
import eu.midnightdust.midnightcontrols.ControlsMode;
import eu.midnightdust.midnightcontrols.MidnightControls;
import eu.midnightdust.midnightcontrols.MidnightControlsConstants;
import eu.midnightdust.midnightcontrols.MidnightControlsFeature;
import eu.midnightdust.midnightcontrols.client.compat.MidnightControlsCompat;
import eu.midnightdust.midnightcontrols.client.controller.ButtonBinding;
import eu.midnightdust.midnightcontrols.client.controller.ButtonCategory;
import eu.midnightdust.midnightcontrols.client.controller.Controller;
import eu.midnightdust.midnightcontrols.client.controller.InputManager;
import eu.midnightdust.midnightcontrols.client.gui.MidnightControlsHud;
import eu.midnightdust.midnightcontrols.client.gui.TouchscreenOverlay;
import eu.midnightdust.midnightcontrols.client.mixin.KeyBindingRegistryImplAccessor;
import eu.midnightdust.midnightcontrols.client.ring.KeyBindingRingAction;
import eu.midnightdust.midnightcontrols.client.ring.MidnightRing;
import dev.lambdaurora.spruceui.hud.HudManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

import java.io.File;

/**
 * Represents the midnightcontrols client mod.
 *
 * @author LambdAurora
 * @version 1.7.0
 * @since 1.1.0
 */
public class MidnightControlsClient extends MidnightControls implements ClientModInitializer {
    public static boolean lateInitDone = false;
    private static MidnightControlsClient INSTANCE;
    public static final KeyBinding BINDING_LOOK_UP = InputManager.makeKeyBinding(new Identifier(MidnightControlsConstants.NAMESPACE, "look_up"),
            InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_KP_8, "key.categories.movement");
    public static final KeyBinding BINDING_LOOK_RIGHT = InputManager.makeKeyBinding(new Identifier(MidnightControlsConstants.NAMESPACE, "look_right"),
            InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_KP_6, "key.categories.movement");
    public static final KeyBinding BINDING_LOOK_DOWN = InputManager.makeKeyBinding(new Identifier(MidnightControlsConstants.NAMESPACE, "look_down"),
            InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_KP_2, "key.categories.movement");
    public static final KeyBinding BINDING_LOOK_LEFT = InputManager.makeKeyBinding(new Identifier(MidnightControlsConstants.NAMESPACE, "look_left"),
            InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_KP_4, "key.categories.movement");
    public static final KeyBinding BINDING_RING = InputManager.makeKeyBinding(new Identifier(MidnightControlsConstants.NAMESPACE, "ring"),
            InputUtil.Type.KEYSYM, InputUtil.UNKNOWN_KEY.getCode(), "key.categories.misc");
    public static final Identifier CONTROLLER_BUTTONS = new Identifier(MidnightControlsConstants.NAMESPACE, "textures/gui/controller_buttons.png");
    public static final Identifier CONTROLLER_EXPANDED = new Identifier(MidnightControlsConstants.NAMESPACE, "textures/gui/controller_expanded.png");
    public static final Identifier CONTROLLER_AXIS = new Identifier(MidnightControlsConstants.NAMESPACE, "textures/gui/controller_axis.png");
    public static final Identifier CURSOR_TEXTURE = new Identifier(MidnightControlsConstants.NAMESPACE, "textures/gui/cursor.png");
    public final static File MAPPINGS_FILE = new File("config/gamecontrollercustommappings.txt");
    public final MidnightInput input = new MidnightInput();
    public final MidnightRing ring = new MidnightRing(this);
    public final MidnightReacharound reacharound = new MidnightReacharound();
    private MidnightControlsHud hud;
    private ControlsMode previousControlsMode;

    @Override
    public void onInitializeClient() {
        INSTANCE = this;
        KeyBindingHelper.registerKeyBinding(BINDING_LOOK_UP);
        KeyBindingHelper.registerKeyBinding(BINDING_LOOK_RIGHT);
        KeyBindingHelper.registerKeyBinding(BINDING_LOOK_DOWN);
        KeyBindingHelper.registerKeyBinding(BINDING_LOOK_LEFT);
        //KeyBindingHelper.registerKeyBinding(BINDING_RING);

        this.ring.registerAction("keybinding", KeyBindingRingAction.FACTORY);
        this.ring.load();

        ClientPlayNetworking.registerGlobalReceiver(CONTROLS_MODE_CHANNEL, (client, handler, buf, responseSender) ->
                responseSender.sendPacket(CONTROLS_MODE_CHANNEL, this.makeControlsModeBuffer(MidnightControlsConfig.controlsMode)));
        ClientPlayNetworking.registerGlobalReceiver(FEATURE_CHANNEL, (client, handler, buf, responseSender) -> {
            int features = buf.readVarInt();
            for (int i = 0; i < features; i++) {
                var name = buf.readString(64);
                boolean allowed = buf.readBoolean();
                MidnightControlsFeature.fromName(name).ifPresent(feature -> client.execute(() -> feature.setAllowed(allowed)));
            }
        });
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            sender.sendPacket(HELLO_CHANNEL, this.makeHello(MidnightControlsConfig.controlsMode));
            sender.sendPacket(CONTROLS_MODE_CHANNEL, this.makeControlsModeBuffer(MidnightControlsConfig.controlsMode));
        });
        ClientPlayConnectionEvents.DISCONNECT.register(this::onLeave);

        ClientTickEvents.START_CLIENT_TICK.register(this.reacharound::tick);
        ClientTickEvents.START_CLIENT_TICK.register(this::onTick);

        OpenScreenCallback.EVENT.register((client, screen) -> {
            if (screen == null && MidnightControlsConfig.controlsMode == ControlsMode.TOUCHSCREEN) {
                screen = new TouchscreenOverlay(this);
                screen.init(client, client.getWindow().getScaledWidth(), client.getWindow().getScaledHeight());
                client.skipGameRender = false;
                client.currentScreen = screen;
            } else if (screen != null) {
                this.input.onScreenOpen(client, client.getWindow().getWidth(), client.getWindow().getHeight());
            }
        });

        HudManager.register(this.hud = new MidnightControlsHud(this));
        FabricLoader.getInstance().getModContainer("midnightcontrols").ifPresent(modContainer -> {
            ResourceManagerHelper.registerBuiltinResourcePack(new Identifier("midnightcontrols","bedrock"), modContainer, ResourcePackActivationType.NORMAL);
            ResourceManagerHelper.registerBuiltinResourcePack(new Identifier("midnightcontrols","legacy"), modContainer, ResourcePackActivationType.NORMAL);
        });
    }

    /**
     * This method is called when Minecraft is initializing.
     */
    public void onMcInit(@NotNull MinecraftClient client) {
        ButtonBinding.init(client.options);
        MidnightControlsConfig.load();
        this.hud.setVisible(MidnightControlsConfig.hudEnable);
        Controller.updateMappings();
        GLFW.glfwSetJoystickCallback((jid, event) -> {
            if (event == GLFW.GLFW_CONNECTED) {
                var controller = Controller.byId(jid);
                client.getToastManager().add(new SystemToast(SystemToast.Type.TUTORIAL_HINT, Text.translatable("midnightcontrols.controller.connected", jid),
                        Text.literal(controller.getName())));
            } else if (event == GLFW.GLFW_DISCONNECTED) {
                client.getToastManager().add(new SystemToast(SystemToast.Type.TUTORIAL_HINT, Text.translatable("midnightcontrols.controller.disconnected", jid),
                        null));
            }

            this.switchControlsMode();
        });

        MidnightControlsCompat.init(this);
    }
    ButtonCategory category;
    /**
     * This method is called to initialize keybindings
     */
    public void initKeybindings() {
        if (lateInitDone) return;
        if (KeyBindingRegistryImplAccessor.getModdedKeyBindings() == null || KeyBindingRegistryImplAccessor.getModdedKeyBindings().isEmpty()) return;
        for (int i = 0; i < KeyBindingRegistryImplAccessor.getModdedKeyBindings().size(); ++i) {
            KeyBinding keyBinding = KeyBindingRegistryImplAccessor.getModdedKeyBindings().get(i);
            if (!keyBinding.getTranslationKey().contains("midnightcontrols") && !keyBinding.getTranslationKey().contains("ok_zoomer") && !keyBinding.getTranslationKey().contains("okzoomer")) {
                category = null;
                InputManager.streamCategories().forEach(buttonCategory -> {
                    if (buttonCategory.getIdentifier().equals(new org.aperlambda.lambdacommon.Identifier("minecraft", keyBinding.getCategory())))
                        category = buttonCategory;
                });
                if (category == null) {
                    category = new ButtonCategory(new org.aperlambda.lambdacommon.Identifier("minecraft", keyBinding.getCategory()));
                    InputManager.registerCategory(category);
                }
                ButtonBinding buttonBinding = new ButtonBinding.Builder(keyBinding.getTranslationKey()).category(category).linkKeybind(keyBinding).register();
                if (MidnightControlsConfig.debug) {
                    logger.info(keyBinding.getTranslationKey());
                    logger.info(buttonBinding);
                }
            }
        }
        InputManager.loadButtonBindings();
        lateInitDone = true;
    }

    /**
     * This method is called every Minecraft tick.
     *
     * @param client the client instance
     */
    public void onTick(@NotNull MinecraftClient client) {
        this.initKeybindings();
        this.input.tick(client);
        if (MidnightControlsConfig.controlsMode == ControlsMode.CONTROLLER && (client.isWindowFocused() || MidnightControlsConfig.unfocusedInput))
            this.input.tickController(client);

//        if (BINDING_RING.wasPressed()) {
//            client.setScreen(new RingScreen());
//        }
    }
    public void onRender(MinecraftClient client) {
        this.input.onRender(client);
    }

    /**
     * Called when leaving a server.
     */
    public void onLeave(ClientPlayNetworkHandler handler, MinecraftClient client) {
        MidnightControlsFeature.resetAllAllowed();
    }

    /**
     * Switches the controls mode if the auto switch is enabled.
     */
    public void switchControlsMode() {
        if (MidnightControlsConfig.autoSwitchMode) {
            if (MidnightControlsConfig.getController().isGamepad()) {
                this.previousControlsMode = MidnightControlsConfig.controlsMode;
                MidnightControlsConfig.controlsMode = ControlsMode.CONTROLLER;
            } else {
                if (this.previousControlsMode == null) {
                    this.previousControlsMode = ControlsMode.DEFAULT;
                }

                MidnightControlsConfig.controlsMode = this.previousControlsMode;
            }
        }
    }

    /**
     * Sets whether the HUD is enabled or not.
     *
     * @param enabled true if the HUD is enabled, else false
     */
    public void setHudEnabled(boolean enabled) {
        MidnightControlsConfig.hudEnable = enabled;
        this.hud.setVisible(enabled);
    }

    /**
     * Gets the midnightcontrols client instance.
     *
     * @return the midnightcontrols client instance
     */
    public static MidnightControlsClient get() {
        return INSTANCE;
    }
}
