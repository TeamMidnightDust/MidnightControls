/*
 * Copyright © 2021 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of midnightcontrols.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package eu.midnightdust.midnightcontrols.client;

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
import net.minecraft.client.gui.screen.Screen;
import org.thinkingstudio.obsidianui.hud.HudManager;
import eu.midnightdust.midnightcontrols.client.touch.TouchInput;
import eu.midnightdust.midnightcontrols.client.util.RainbowColor;
import eu.midnightdust.midnightcontrols.packet.ControlsModePayload;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

import java.io.File;
import java.util.Objects;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Represents the midnightcontrols client mod.
 *
 * @author LambdAurora
 * @version 1.7.0
 * @since 1.1.0
 */
public class MidnightControlsClient extends MidnightControls {
    public static boolean lateInitDone = false;
    public static final KeyBinding BINDING_LOOK_UP = InputManager.makeKeyBinding(id("look_up"),
            InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_KP_8, "key.categories.movement");
    public static final KeyBinding BINDING_LOOK_RIGHT = InputManager.makeKeyBinding(id("look_right"),
            InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_KP_6, "key.categories.movement");
    public static final KeyBinding BINDING_LOOK_DOWN = InputManager.makeKeyBinding(id("look_down"),
            InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_KP_2, "key.categories.movement");
    public static final KeyBinding BINDING_LOOK_LEFT = InputManager.makeKeyBinding(id("look_left"),
            InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_KP_4, "key.categories.movement");
    public static final KeyBinding BINDING_RING = InputManager.makeKeyBinding(id("ring"),
            InputUtil.Type.KEYSYM, InputUtil.UNKNOWN_KEY.getCode(), "key.categories.misc");
    public static final Identifier CONTROLLER_BUTTONS = id("textures/gui/controller_buttons.png");
    public static final Identifier CONTROLLER_EXPANDED = id("textures/gui/controller_expanded.png");
    public static final Identifier CONTROLLER_AXIS = id("textures/gui/controller_axis.png");
    public static final Identifier WAYLAND_CURSOR_TEXTURE_LIGHT = id("cursor/light/mouse_pointer");
    public static final Identifier WAYLAND_CURSOR_TEXTURE_DARK = id("cursor/dark/mouse_pointer");
    public static final File MAPPINGS_FILE = new File("config/gamecontrollercustommappings.txt");
    public static final MinecraftClient client = MinecraftClient.getInstance();
    public static final MidnightInput input = new MidnightInput();
    public static final MidnightRing ring = new MidnightRing();
    public static final MidnightReacharound reacharound = new MidnightReacharound();
    public static boolean isWayland;
    private static MidnightControlsHud hud;
    private static ControlsMode previousControlsMode;

    public static void initClient() {
        ring.registerAction("buttonbinding", ButtonBindingRingAction.FACTORY);

        final MinecraftClient client = MinecraftClient.getInstance();
        int delay = 0; // delay for 0 sec.
        int period = 1; // repeat every 0.001 sec. (100 times a second)
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                input.updateCamera(client);
            }
        }, delay, period);

        HudManager.register(hud = new MidnightControlsHud());
        isWayland = GLFW.glfwGetVersionString().contains("Wayland");
    }

    /**
     * This method is called when Minecraft is initializing.
     */
    public static void onMcInit(@NotNull MinecraftClient client) {
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
        hud.setVisible(MidnightControlsConfig.hudEnable);
        Controller.updateMappings();
        try {
            GLFW.glfwSetJoystickCallback((jid, event) -> {
                if (event == GLFW.GLFW_CONNECTED) {
                    var controller = Controller.byId(jid);
                    client.getToastManager().add(new SystemToast(SystemToast.Type.PERIODIC_NOTIFICATION, Text.translatable("midnightcontrols.controller.connected", jid),
                            Text.literal(controller.getName())));
                } else if (event == GLFW.GLFW_DISCONNECTED) {
                    client.getToastManager().add(new SystemToast(SystemToast.Type.PERIODIC_NOTIFICATION, Text.translatable("midnightcontrols.controller.disconnected", jid),
                            null));
                }

                switchControlsMode();
            });
        } catch (Exception e) {e.fillInStackTrace();}

        MidnightControlsCompat.init();
    }
    /**
     * This method is called to initialize keybindings
     */
    public static void initKeybindings() {
        if (lateInitDone) return;
        if (KeyBindingIDAccessor.getKEYS_BY_ID() == null || KeyBindingIDAccessor.getKEYS_BY_ID().isEmpty()) return;
        if (PlatformFunctions.isModLoaded("voxelmap") && !KeyBindingIDAccessor.getKEYS_BY_ID().containsKey("key.minimap.toggleingamewaypoints")) return;
        if (PlatformFunctions.isModLoaded("wynntils") && KeyBindingIDAccessor.getKEYS_BY_ID().entrySet().stream().noneMatch(b -> Objects.equals(b.getValue().getCategory(), "Wynntils"))) return;
        for (int i = 0; i < KeyBindingIDAccessor.getKEYS_BY_ID().size(); ++i) {
            KeyBinding keyBinding = KeyBindingIDAccessor.getKEYS_BY_ID().entrySet().stream().toList().get(i).getValue();
            if (MidnightControlsConfig.excludedKeybindings.stream().noneMatch(excluded -> keyBinding.getTranslationKey().startsWith(excluded))) {
                if (!keyBinding.getTranslationKey().contains(MidnightControlsConstants.NAMESPACE)) {
                    AtomicReference<ButtonCategory> category = new AtomicReference<>();
                    InputManager.streamCategories().forEach(buttonCategory -> {
                        if (buttonCategory.getIdentifier().equals(Identifier.of("minecraft", keyBinding.getCategory())))
                            category.set(buttonCategory);
                    });
                    if (category.get() == null) {
                        category.set(new ButtonCategory(Identifier.of("minecraft", keyBinding.getCategory())));
                        InputManager.registerCategory(category.get());
                    }
                    ButtonBinding buttonBinding = new ButtonBinding.Builder(keyBinding.getTranslationKey()).category(category.get()).linkKeybind(keyBinding).register();
                    if (MidnightControlsConfig.debug) {
                        MidnightControls.log(keyBinding.getTranslationKey());
                        MidnightControls.log(String.valueOf(buttonBinding));
                    }
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
    public static void onTick(@NotNull MinecraftClient client) {
        initKeybindings();
        input.tick(client);
        reacharound.tick(client);
        if (MidnightControlsConfig.controlsMode == ControlsMode.CONTROLLER && (client.isWindowFocused() || MidnightControlsConfig.unfocusedInput))
            input.tickController(client);

        if (BINDING_RING.wasPressed()) {
            ring.loadFromUnbound();
            client.setScreen(new RingScreen());
        }
        if (client.world != null && MidnightControlsConfig.enableHints && !MidnightControlsConfig.autoSwitchMode && MidnightControlsConfig.controlsMode == ControlsMode.DEFAULT && MidnightControlsConfig.getController().isGamepad()) {
            client.getToastManager().add(SystemToast.create(client, SystemToast.Type.PERIODIC_NOTIFICATION, Text.translatable("midnightcontrols.controller.tutorial.title"),
                    Text.translatable("midnightcontrols.controller.tutorial.description", Text.translatable("options.title"), Text.translatable("controls.title"),
                            Text.translatable("midnightcontrols.menu.title.controller"))));
            MidnightControlsConfig.enableHints = false;
            MidnightControlsConfig.save();
        }
        RainbowColor.tick();
        TouchInput.tick();
    }
    /**
     * Called when opening a screen.
     */
    public static void onScreenOpen(Screen screen) {
        if (screen == null && MidnightControlsConfig.controlsMode == ControlsMode.TOUCHSCREEN) {
            screen = new TouchscreenOverlay();
            screen.init(client, client.getWindow().getScaledWidth(), client.getWindow().getScaledHeight());
            client.skipGameRender = false;
            client.currentScreen = screen;
        } else if (screen != null) {
            MidnightControlsClient.input.onScreenOpen(client, client.getWindow().getWidth(), client.getWindow().getHeight());
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
        hud.setVisible(enabled);
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
