/*
 * Copyright © 2021 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of midnightcontrols.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package eu.midnightdust.midnightcontrols.client;

import com.mojang.blaze3d.platform.InputConstants;
import eu.midnightdust.lib.util.PlatformFunctions;
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
import eu.midnightdust.midnightcontrols.client.gui.RingScreen;
import eu.midnightdust.midnightcontrols.client.touch.gui.TouchscreenOverlay;
import eu.midnightdust.midnightcontrols.client.mixin.KeyBindingIDAccessor;
import eu.midnightdust.midnightcontrols.client.ring.ButtonBindingRingAction;
import eu.midnightdust.midnightcontrols.client.ring.MidnightRing;
import eu.midnightdust.midnightcontrols.client.util.platform.NetworkUtil;
import eu.midnightdust.midnightcontrols.client.virtualkeyboard.MouseClickInterceptor;
import eu.midnightdust.midnightcontrols.client.touch.TouchInput;
import eu.midnightdust.midnightcontrols.packet.ControlsModePayload;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicReference;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

/**
 * Represents the midnightcontrols client mod.
 *
 * @author Motschen, LambdAurora
 * @version 1.10.0
 * @since 1.1.0
 */
public class MidnightControlsClient extends MidnightControls {
    public static boolean lateInitDone = false;
    public static final KeyMapping.Category MIDNIGHTCONTROLS_CATEGORY = KeyMapping.Category.register(Identifier.fromNamespaceAndPath("midnightcontrols", "keybinds"));
    public static final KeyMapping BINDING_LOOK_UP = InputManager.makeKeyBinding(id("look_up"),
            InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_KP_8, MIDNIGHTCONTROLS_CATEGORY);
    public static final KeyMapping BINDING_LOOK_RIGHT = InputManager.makeKeyBinding(id("look_right"),
            InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_KP_6, MIDNIGHTCONTROLS_CATEGORY);
    public static final KeyMapping BINDING_LOOK_DOWN = InputManager.makeKeyBinding(id("look_down"),
            InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_KP_2, MIDNIGHTCONTROLS_CATEGORY);
    public static final KeyMapping BINDING_LOOK_LEFT = InputManager.makeKeyBinding(id("look_left"),
            InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_KP_4, MIDNIGHTCONTROLS_CATEGORY);
    public static final KeyMapping BINDING_RING = InputManager.makeKeyBinding(id("ring"),
            InputConstants.Type.KEYSYM, InputConstants.UNKNOWN.getValue(), MIDNIGHTCONTROLS_CATEGORY);
    public static final Identifier CONTROLLER_BUTTONS = id("textures/gui/controller_buttons.png");
    public static final Identifier CONTROLLER_EXPANDED = id("textures/gui/controller_expanded.png");
    public static final Identifier CONTROLLER_AXIS = id("textures/gui/controller_axis.png");
    public static final File MAPPINGS_FILE = new File("config/gamecontrollercustommappings.txt");
    public static Minecraft client = Minecraft.getInstance();
    public static final MidnightInput input = new MidnightInput();
    public static final MidnightRing ring = new MidnightRing();
    public static final MidnightReacharound reacharound = new MidnightReacharound();
    public static final MouseClickInterceptor clickInterceptor = new MouseClickInterceptor();
    public static boolean isWayland;
    private static ControlsMode previousControlsMode;

    /**
     * Initialize the mod's main client-side functionality
     */
    public static void initClient() {
        client = Minecraft.getInstance();
        ring.registerAction("buttonbinding", ButtonBindingRingAction.FACTORY);

        int delay = 0; // delay for 0 sec.
        int period = 1; // repeat every 0.001 sec. (1000 times a second)
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                MidnightControlsClient.onCameraTick();
            }
        }, delay, period);

        isWayland = GLFW.glfwGetVersionString().contains("Wayland");
    }

    /**
     * This method is called when Minecraft is initializing.
     */
    public static void onMcInit(@NotNull Minecraft client) {
        ButtonBinding.init(client.options);
        MidnightControlsConfig.load();
        if (MidnightControlsConfig.configVersion < 2) {
            MidnightControlsConfig.mouseScreens.remove("me.jellysquid.mods.sodium.client.gui");
            MidnightControlsConfig.mouseScreens.remove("net.coderbot.iris.gui");
            MidnightControlsConfig.mouseScreens.remove("net.minecraft.class_5375");
            MidnightControlsConfig.mouseScreens.remove("net.minecraft.client.gui.screen.pack.PackScreen");
            MidnightControlsConfig.configVersion = 2;
            MidnightControlsConfig.write(MidnightControlsConstants.NAMESPACE);
        }
        MidnightControlsHud.isVisible = MidnightControlsConfig.hudEnable;
        Controller.updateMappings();
        try {
            GLFW.glfwSetJoystickCallback(MidnightControlsClient::onControllerConnectionChanged);
        } catch (Exception e) {
            e.fillInStackTrace();
        }

        MidnightControlsCompat.init();
    }

    /**
     * Shows a toast popup to notify the user about an event.
     */
    private static void showToastMessage(Component title, Component description) {
        client.getToastManager().addToast(new SystemToast(SystemToast.SystemToastId.PERIODIC_NOTIFICATION, title, description));
    }

    /**
     * This callback is executed every time a controller is connected or disconnected.
     */
    private static void onControllerConnectionChanged(int jid, int event) {
        if (event == GLFW.GLFW_CONNECTED) {
            var controller = Controller.byId(jid);
            showToastMessage(Component.translatable("midnightcontrols.controller.connected", jid), Component.literal(controller.getName()));
        } else if (event == GLFW.GLFW_DISCONNECTED) {
            showToastMessage(Component.translatable("midnightcontrols.controller.disconnected", jid), null);
        }

        switchControlsMode();
    }

    /**
     * This method is called to initialize keybindings.
     * Due to being called every tick, we can delay keybind init by `return`-ing early, which might be required for some odd mods.
     */
    public static void initKeybindings() {
        if (lateInitDone) return;
        if (KeyBindingIDAccessor.getALL() == null || KeyBindingIDAccessor.getALL().isEmpty()) return;
        if (PlatformFunctions.isModLoaded("voxelmap") && !KeyBindingIDAccessor.getALL().containsKey("key.minimap.toggleingamewaypoints")) return;
        //if (PlatformFunctions.isModLoaded("wynntils") && KeyBindingIDAccessor.getALL().entrySet().stream().noneMatch(b -> Objects.equals(b.getValue().getCategory(), "Wynntils"))) return; // TODO: Check if this is still required. If so, it will need to be updated.
        for (KeyMapping keyBinding : KeyBindingIDAccessor.getALL().values()) {
            if (MidnightControlsConfig.excludedKeybindings.stream().noneMatch(excluded -> keyBinding.getName().startsWith(excluded)) && !keyBinding.getName().contains(MidnightControlsConstants.NAMESPACE)) {
                AtomicReference<ButtonCategory> category = new AtomicReference<>();
                InputManager.streamCategories().forEach(buttonCategory -> {
                    if (buttonCategory.getIdentifier().equals(keyBinding.getCategory().id()))
                        category.set(buttonCategory);
                });
                if (category.get() == null) {
                    category.set(new ButtonCategory(keyBinding.getCategory().id()));
                    InputManager.registerCategory(category.get());
                }
                ButtonBinding buttonBinding = new ButtonBinding.Builder(keyBinding.getName()).category(category.get()).linkKeybind(keyBinding).register();
                if (MidnightControlsConfig.debug) {
                    MidnightControls.log(keyBinding.getName());
                    MidnightControls.log(String.valueOf(buttonBinding));
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
    public static void onTick(@NotNull Minecraft client) {
        initKeybindings();
        input.tick();
        reacharound.tick();
        if (MidnightControlsConfig.controlsMode == ControlsMode.CONTROLLER && (client.isWindowActive() || MidnightControlsConfig.unfocusedInput))
            input.tickController();

        if (BINDING_RING.consumeClick()) {
            ring.loadFromUnbound();
            client.setScreen(new RingScreen());
        }
        if (client.level != null && MidnightControlsConfig.enableHints && !MidnightControlsConfig.autoSwitchMode && MidnightControlsConfig.controlsMode == ControlsMode.DEFAULT && MidnightControlsConfig.getController().isGamepad()) {
            showToastMessage(Component.translatable("midnightcontrols.controller.tutorial.title"),
                    Component.translatable("midnightcontrols.controller.tutorial.description",
                            Component.translatable("options.title"),
                            Component.translatable("controls.title"),
                            Component.translatable("midnightcontrols.menu.title.controller")
                    ));
            MidnightControlsConfig.enableHints = false;
            MidnightControlsConfig.save();
        }
        TouchInput.tick();
    }
    /**
     * This method is called every camera tick.
     */
    public static void onCameraTick() {
        try {
            if (lateInitDone && client.isRunning() && MidnightControlsConfig.controlsMode != ControlsMode.DEFAULT && (client.isWindowActive() || MidnightControlsConfig.unfocusedInput)) {
                if (MidnightControlsConfig.controlsMode == ControlsMode.CONTROLLER)
                    input.tickCameraStick();
                input.updateCamera();
            }
        } catch (Exception | Error e) {
            MidnightControls.logger.error("Exception encountered in camera loop: %s", e);
        }
    }

    /**
     * Called when opening a screen.
     */
    public static void onScreenOpen(Screen screen) {
        client = Minecraft.getInstance();
        if (screen == null && MidnightControlsConfig.controlsMode == ControlsMode.TOUCHSCREEN) {
            screen = new TouchscreenOverlay();
            screen.init(client.getWindow().getGuiScaledWidth(), client.getWindow().getGuiScaledHeight());
            client.noRender = false;
            client.screen = screen;
        } else if (screen != null) {
            MidnightControlsClient.input.onScreenOpen(client.getWindow().getScreenWidth(), client.getWindow().getScreenHeight());
        }
    }

    /**
     * Called when leaving a server.
     */
    public static void onLeave() {
        MidnightControlsFeature.resetAllAllowed();
    }

    /**
     * Switches the controls mode if the auto switch is enabled.
     */
    public static void switchControlsMode() {
        if (MidnightControlsConfig.autoSwitchMode) {
            if (MidnightControlsConfig.getController().isGamepad()) {
                previousControlsMode = MidnightControlsConfig.controlsMode;
                MidnightControlsConfig.controlsMode = ControlsMode.CONTROLLER;
            } else {
                if (previousControlsMode == null) {
                    previousControlsMode = ControlsMode.DEFAULT;
                }

                MidnightControlsConfig.controlsMode = previousControlsMode;
            }
            NetworkUtil.sendPayloadC2S(new ControlsModePayload(MidnightControlsConfig.controlsMode.getName()));
        }
    }

    /**
     * Sets whether the HUD is enabled or not.
     *
     * @param enabled true if the HUD is enabled, else false
     */
    public static void setHudEnabled(boolean enabled) {
        MidnightControlsConfig.hudEnable = enabled;
        MidnightControlsHud.isVisible = enabled;
    }

    private static final MidnightControlsClient INSTANCE = new MidnightControlsClient();
    /**
     * Gets the midnightcontrols client instance.
     *
     * @return the midnightcontrols client instance
     */
    @Deprecated
    public static MidnightControlsClient get() {
        return INSTANCE;
    }
}
