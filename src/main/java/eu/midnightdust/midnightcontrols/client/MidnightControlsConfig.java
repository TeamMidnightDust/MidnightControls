/*
 * Copyright © 2021 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of midnightcontrols.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package eu.midnightdust.midnightcontrols.client;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.GlDebugInfo;
import eu.midnightdust.lib.config.MidnightConfig;
import eu.midnightdust.midnightcontrols.ControlsMode;
import eu.midnightdust.midnightcontrols.MidnightControlsFeature;
import eu.midnightdust.midnightcontrols.client.controller.ButtonBinding;
import eu.midnightdust.midnightcontrols.client.controller.Controller;
import eu.midnightdust.midnightcontrols.client.controller.InputManager;
import eu.midnightdust.midnightcontrols.client.enums.CameraMode;
import eu.midnightdust.midnightcontrols.client.enums.ControllerType;
import eu.midnightdust.midnightcontrols.client.enums.HudSide;
import eu.midnightdust.midnightcontrols.client.enums.VirtualMouseSkin;
import eu.midnightdust.midnightcontrols.client.gui.RingScreen;
import eu.midnightdust.midnightcontrols.client.touch.TouchMode;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.advancement.AdvancementsScreen;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.*;
import java.util.regex.Pattern;

import static org.lwjgl.glfw.GLFW.*;

/**
 * Represents MidnightControls configuration.
 */
public class MidnightControlsConfig extends MidnightConfig {
    public static final String CONTROLLER = "controller";
    public static final String TOUCH = "touch";
    public static final String GAMEPLAY = "gameplay";
    public static final String SCREENS = "screens";
    public static final String VISUAL = "visual";
    public static final String MISC = "misc";
    public static boolean isEditing = false;
    @Hidden @Entry public static int configVersion = 2;
    // General
    @Entry(category = CONTROLLER, name = "midnightcontrols.menu.controls_mode") public static ControlsMode controlsMode = ControlsMode.DEFAULT;
    @Entry(category = CONTROLLER, name = "midnightcontrols.menu.auto_switch_mode") public static boolean autoSwitchMode = true;
    @Entry(category = MISC, name = "Debug") public static boolean debug = false;
    // HUD
    @Entry(category = VISUAL, name = "midnightcontrols.menu.hud_enable") public static boolean hudEnable = true;
    @Entry(category = VISUAL, name = "midnightcontrols.menu.hud_side") public static HudSide hudSide = HudSide.LEFT;
    @Entry(category = SCREENS, name = "midnightcontrols.menu.move_chat") public static boolean moveChat = false;
    // Gameplay
    @Entry(category = GAMEPLAY, name = "midnightcontrols.menu.analog_movement") public static boolean analogMovement = true;
    @Entry(category = GAMEPLAY, name = "midnightcontrols.menu.double_tap_to_sprint") public static boolean doubleTapToSprint = true;
    @Entry(category = GAMEPLAY, name = "midnightcontrols.menu.controller_toggle_sneak") public static boolean controllerToggleSneak = MinecraftClient.getInstance().options.getSneakToggled().getValue();
    @Entry(category = GAMEPLAY, name = "midnightcontrols.menu.controller_toggle_sprint") public static boolean controllerToggleSprint = MinecraftClient.getInstance().options.getSprintToggled().getValue();
    @Entry(category = GAMEPLAY, name = "midnightcontrols.menu.fast_block_placing") public static boolean fastBlockPlacing = false; // Disabled by default as this behaviour can be considered cheating on multiplayer servers.
    @Entry(category = GAMEPLAY, name = "midnightcontrols.menu.fly_drifting") public static boolean flyDrifting = true; // Enabled by default as disabling this behaviour can be considered cheating on multiplayer servers. It can also conflict with some other mods.
    @Entry(category = GAMEPLAY, name = "midnightcontrols.menu.fly_drifting_vertical") public static boolean verticalFlyDrifting = true; // Enabled by default as disabling this behaviour can be considered cheating on multiplayer servers.
    @Entry(category = GAMEPLAY, name = "midnightcontrols.menu.reacharound.horizontal") public static boolean horizontalReacharound = false; // Disabled by default as this behaviour can be considered cheating on multiplayer servers.
    @Entry(category = GAMEPLAY, name = "midnightcontrols.menu.reacharound.vertical") public static boolean verticalReacharound = false; // Disabled by default as this behaviour can be considered cheating on multiplayer servers.
    @Entry(category = VISUAL, name = "Reacharound Outline") public static boolean shouldRenderReacharoundOutline = true;
    @Entry(category = VISUAL, name = "Reacharound Outline Color", isColor = true) public static String reacharoundOutlineColorHex = "#ffffff";
    @Entry(category = VISUAL, name = "Reacharound Outline Alpha", isSlider = true, min = 0, max = 255) public static int reacharoundOutlineColorAlpha = 102;
    @Entry(category = CONTROLLER, name = "midnightcontrols.menu.right_dead_zone", isSlider = true, min = 0.05, max = 1) public static double rightDeadZone = 0.25;
    @Entry(category = CONTROLLER, name = "midnightcontrols.menu.left_dead_zone", isSlider = true, min = 0.05, max = 1) public static double leftDeadZone = 0.25;
    @Entry(category = CONTROLLER, name = "midnightcontrols.menu.invert_right_y_axis") public static boolean invertRightYAxis = false;
    @Entry(category = CONTROLLER, name = "midnightcontrols.menu.invert_right_x_axis") public static boolean invertRightXAxis = false;
    @Entry(category = CONTROLLER, name = "midnightcontrols.menu.rotation_speed", isSlider = true, min = 0, max = 100, precision = 10) public static double rotationSpeed = 35.0; //used for x-axis, name kept for compatibility
    @Entry(category = CONTROLLER, name = "midnightcontrols.menu.y_axis_rotation_speed", isSlider = true, min = 0, max = 100, precision = 10) public static double yAxisRotationSpeed = rotationSpeed;
    @Entry(category = CONTROLLER, name = "midnightcontrols.menu.camera_mode") public static CameraMode cameraMode = CameraMode.FLAT;
    @Entry(category = SCREENS, name = "midnightcontrols.menu.mouse_speed", isSlider = true, min = 0, max = 150, precision = 10) public static double mouseSpeed = 25.0;
    @Entry(category = SCREENS, name = "midnightcontrols.menu.joystick_as_mouse") public static boolean joystickAsMouse = false;
    @Entry(category = SCREENS, name = "midnightcontrols.menu.eye_tracker_as_mouse") public static boolean eyeTrackerAsMouse = false;
    @Entry(category = SCREENS, name = "midnightcontrols.menu.eye_tracker_deadzone", isSlider = true, min = 0, max = 0.4) public static double eyeTrackerDeadzone = 0.05;
    @Entry(category = CONTROLLER, name = "midnightcontrols.menu.unfocused_input") public static boolean unfocusedInput = false;
    @Entry(category = SCREENS, name = "midnightcontrols.menu.virtual_mouse") public static boolean virtualMouse = false;
    @Entry(category = SCREENS, name = "midnightcontrols.menu.virtual_mouse.skin") public static VirtualMouseSkin virtualMouseSkin = VirtualMouseSkin.DEFAULT_LIGHT;
    @Entry(category = SCREENS, name = "midnightcontrols.menu.hide_cursor") public static boolean hideNormalMouse = false;
    @Entry(category = CONTROLLER, name = "Controller ID") @Hidden public static Object controllerID = 0;
    @Entry(category = CONTROLLER, name = "2nd Controller ID") @Hidden public static Object secondControllerID = -1;
    @Entry(category = VISUAL, name = "midnightcontrols.menu.controller_type") public static ControllerType controllerType = ControllerType.DEFAULT;
    @Entry(category = SCREENS, name = "Mouse screens") public static List<String> mouseScreens = Lists.newArrayList("net.minecraft.client.gui.screen.advancement",
            "net.minecraft.class_457", "net.minecraft.class_408", "net.minecraft.class_3872", "me.flashyreese.mods.reeses_sodium_options.client.gui", "dev.emi.emi.screen",
            "hardcorequesting.client.interfaces.GuiQuestBook", "hardcorequesting.client.interfaces.GuiReward", "hardcorequesting.client.interfaces.EditTrackerScreen",
            "me.shedaniel.clothconfig2.gui.ClothConfigScreen", "com.mamiyaotaru.voxelmap.gui.GuiWaypoints", "com.mamiyaotaru.voxelmap.gui.GuiPersistentMap");
    @Entry(category = SCREENS, name = "Arrow screens") public static List<String> arrowScreens = Lists.newArrayList(ChatScreen.class.getCanonicalName());
    @Entry(category = SCREENS, name = "WASD screens") public static List<String> wasdScreens = Lists.newArrayList("com.ultreon.devices.core.Laptop");
    @Entry(category = TOUCH, name = "Screens with close button") public static List<String> closeButtonScreens = Lists.newArrayList(ChatScreen.class.getCanonicalName(), AdvancementsScreen.class.getCanonicalName(), RingScreen.class.getCanonicalName());
    @Entry(category = TOUCH, name = "midnightcontrols.menu.touch_with_controller") public static boolean touchInControllerMode = false;
    @Entry(category = TOUCH, name = "midnightcontrols.menu.touch_speed", isSlider = true, min = 0, max = 150, precision = 10) public static double touchSpeed = 50.0;
    @Entry(category = TOUCH, name = "midnightcontrols.menu.invert_touch") public static boolean invertTouch = false;
    @Entry(category = TOUCH, name = "midnightcontrols.menu.touch_mode") public static TouchMode touchMode = TouchMode.CROSSHAIR;
    @Entry(category = TOUCH, name = "midnightcontrols.menu.touch_break_delay", isSlider = true, min = 50, max = 500) public static int touchBreakDelay = 120;
    @Entry(category = TOUCH, name = "midnightcontrols.menu.touch_transparency", isSlider = true, min = 0, max = 100) public static int touchTransparency = 75;
    @Entry(category = TOUCH, name = "Touch Outline Color", isColor = true) public static String touchOutlineColorHex = "#ffffff";
    @Entry(category = TOUCH, name = "Touch Outline Alpha", isSlider = true, min = 0, max = 255) public static int touchOutlineColorAlpha = 150;
    @Entry(category = TOUCH, name = "Left Touch button bindings") public static List<String> leftTouchBinds = Lists.newArrayList("debug_screen", "screenshot","toggle_perspective");
    @Entry(category = TOUCH, name = "Right Touch button bindings") public static List<String> rightTouchBinds = Lists.newArrayList("screenshot","toggle_perspective", "use");

    @Entry @Hidden public static Map<String, String> BINDING = new HashMap<>();

    private static final Pattern BUTTON_BINDING_PATTERN = Pattern.compile("(-?\\d+)\\+?");
    @Deprecated @Hidden @Entry public static double[] maxAnalogValues = new double[]{1, 1, 1, 1};
    @Entry(category = CONTROLLER, name = "Max analog value: Left X", isSlider = true, min = .25f, max = 1.f) public static double maxAnalogValueLeftX = maxAnalogValues[0];
    @Entry(category = CONTROLLER, name = "Max analog value: Left Y", isSlider = true, min = .25f, max = 1.f) public static double maxAnalogValueLeftY = maxAnalogValues[1];
    @Entry(category = CONTROLLER, name = "Max analog value: Right X", isSlider = true, min = .25f, max = 1.f) public static double maxAnalogValueRightX = maxAnalogValues[2];
    @Entry(category = CONTROLLER, name = "Max analog value: Right Y", isSlider = true, min = .25f, max = 1.f) public static double maxAnalogValueRightY = maxAnalogValues[3];
    @Entry(category = CONTROLLER, name = "Trigger button fix") public static boolean triggerFix = true;
    @Entry(category = CONTROLLER, name = "Excluded Keybindings") public static List<String> excludedKeybindings = Lists.newArrayList("key.forward", "key.left", "key.back", "key.right", "key.jump", "key.sneak", "key.sprint", "key.inventory",
            "key.swapOffhand", "key.drop", "key.use", "key.attack", "key.chat", "key.playerlist", "key.screenshot", "key.togglePerspective", "key.smoothCamera", "key.fullscreen", "key.saveToolbarActivator", "key.loadToolbarActivator",
            "key.pickItem", "key.hotbar.1", "key.hotbar.2", "key.hotbar.3", "key.hotbar.4", "key.hotbar.5", "key.hotbar.6", "key.hotbar.7", "key.hotbar.8", "key.hotbar.9");
    @Entry(category = GAMEPLAY, name = "Enable Hints") public static boolean enableHints = true;
    @Entry(category = SCREENS, name = "Enable Shortcut in Controls Options") public static boolean shortcutInControls = true;
    @Entry(category = MISC, name = "Ring Bindings (WIP)") public static List<String> ringBindings = new ArrayList<>();
    @Entry(category = MISC, name = "Ignored Unbound Keys") public static List<String> ignoredUnboundKeys = Lists.newArrayList("inventorytabs.key.next_tab");
    @Entry @Hidden public static Map<String, Map<String, String>> controllerBindingProfiles = new HashMap<>();
    private static Map<String, String> currentBindingProfile = new HashMap<>();
    private static Controller prevController;

    /**
     * Loads the configuration
     */
    public static void load() {
        MidnightControlsConfig.init("midnightcontrols", MidnightControlsConfig.class);
        MidnightControlsClient.get().log("Configuration loaded.");
        // Controller controls.
        InputManager.loadButtonBindings();
    }

    /**
     * Saves the configuration.
     */
    public static void save() {
        MidnightControlsConfig.write("midnightcontrols");
        MidnightControlsClient.get().log("Configuration saved.");
        MidnightControlsFeature.refreshEnabled();
    }
    public static void updateBindingsForController(Controller controller) {
        if (controller.isConnected() && controller.isGamepad() && controllerBindingProfiles.containsKey(controller.getGuid()))
            currentBindingProfile = controllerBindingProfiles.get(controller.getGuid());
        else currentBindingProfile = Maps.newHashMap(BINDING);
        InputManager.loadButtonBindings();
    }
    public static Map<String, String> getBindingsForController() {
        return currentBindingProfile;
    }
    /**
     * Gets the used controller.
     *
     * @return the controller
     */
    public static Controller getController() {
        var raw = MidnightControlsConfig.controllerID;
        Controller controller = Controller.byId(GLFW.GLFW_JOYSTICK_1);
        if (raw instanceof Number) {
            controller = Controller.byId(((Number) raw).intValue());
        } else if (raw instanceof String) {
            controller = Controller.byGuid((String) raw).orElse(Controller.byId(GLFW.GLFW_JOYSTICK_1));
        }
        if ((!controller.isConnected() || !controller.isGamepad()) && MidnightControlsConfig.autoSwitchMode && !isEditing) {
            for (int i = 0; i < GLFW.GLFW_JOYSTICK_LAST; ++i) {
                Controller gamepad = Controller.byId(i);
                if (gamepad.isConnected() && gamepad.isGamepad()) {
                    controller = gamepad;
                    i = GLFW_JOYSTICK_LAST;
                }
            }
        }
        if (controller.isConnected() && controller.isGamepad() && MidnightControlsConfig.autoSwitchMode && !isEditing) MidnightControlsConfig.controlsMode = ControlsMode.CONTROLLER;
        if (prevController != controller) updateBindingsForController(controller);
        prevController = controller;
        return controller;
    }

    /**
     * Sets the used controller.
     *
     * @param controller the controller
     */
    public static void setController(Controller controller) {
        MidnightControlsConfig.controllerID = controller.id();
        MidnightControlsConfig.write("midnightcontrols");
    }

    /**
     * Gets the second controller (for Joy-Con supports).
     *
     * @return the second controller
     */
    public static Optional<Controller> getSecondController() {
        var raw = MidnightControlsConfig.secondControllerID;
        if (raw instanceof Number) {
            if (((Number) raw).intValue() == -1)
                return Optional.empty();
            return Optional.of(Controller.byId(((Number) raw).intValue()));
        } else if (raw instanceof String) {
            return Optional.of(Controller.byGuid((String) raw).orElse(Controller.byId(GLFW.GLFW_JOYSTICK_1)));
        }
        return Optional.empty();
    }

    /**
     * Sets the second controller.
     *
     * @param controller the second controller
     */
    public static void setSecondController(@Nullable Controller controller) {
        MidnightControlsConfig.secondControllerID = controller == null ? -1 : controller.id();
    }
    /**
     * Gets the right X axis sign.
     *
     * @return the right X axis sign
     */
    public static double getRightXAxisSign() {
        return MidnightControlsConfig.invertRightXAxis ? -1.0 : 1.0;
    }

    /**
     * Gets the right Y axis sign.
     *
     * @return the right Y axis sign
     */
    public static double getRightYAxisSign() {
        return MidnightControlsConfig.invertRightYAxis ? -1.0 : 1.0;
    }

    public static double getAxisMaxValue(int axis) {
        return switch (axis) {
            case GLFW_GAMEPAD_AXIS_LEFT_X -> MidnightControlsConfig.maxAnalogValueLeftX;
            case GLFW_GAMEPAD_AXIS_LEFT_Y -> MidnightControlsConfig.maxAnalogValueLeftY;
            case GLFW_GAMEPAD_AXIS_RIGHT_X -> MidnightControlsConfig.maxAnalogValueRightX;
            default -> MidnightControlsConfig.maxAnalogValueRightY;
        };
    }

    public static void setAxisMaxValue(int axis, double value) {
        switch (axis) {
            case GLFW_GAMEPAD_AXIS_LEFT_X -> MidnightControlsConfig.maxAnalogValueLeftX = value;
            case GLFW_GAMEPAD_AXIS_LEFT_Y -> MidnightControlsConfig.maxAnalogValueLeftY = value;
            case GLFW_GAMEPAD_AXIS_RIGHT_X -> MidnightControlsConfig.maxAnalogValueRightX = value;
            default -> MidnightControlsConfig.maxAnalogValueRightY = value;
        };
    }

    /**
     * Loads the button binding from configuration.
     *
     * @param button the button binding
     */
    public static void loadButtonBinding(@NotNull ButtonBinding button) {
        button.setButton(button.getDefaultButton());
        var code = getBindingsForController().getOrDefault("controller.controls." + button.getName(), button.getButtonCode());

        var matcher = BUTTON_BINDING_PATTERN.matcher(code);

        try {
            var buttons = new int[1];
            int count = 0;
            while (matcher.find()) {
                count++;
                if (count > buttons.length)
                    buttons = Arrays.copyOf(buttons, count);
                String current;
                if (!MidnightControlsConfig.checkValidity(button, code, current = matcher.group(1)))
                    return;
                buttons[count - 1] = Integer.parseInt(current);
            }
            if (count == 0) {
                MidnightControlsClient.get().warn("Malformed config value \"" + code + "\" for binding \"" + button.getName() + "\".");
                MidnightControlsConfig.setButtonBinding(button, new int[]{-1});
            }

            button.setButton(buttons);
        } catch (Exception e) {
            MidnightControlsClient.get().warn("Malformed config value \"" + code + "\" for binding \"" + button.getName() + "\".");
            setButtonBinding(button, button.getButton());
        }
    }

    private static boolean checkValidity(@NotNull ButtonBinding binding, @NotNull String input, String group) {
        if (group == null) {
            MidnightControlsClient.get().warn("Malformed config value \"" + input + "\" for binding \"" + binding.getName() + "\".");
            setButtonBinding(binding, binding.getButton());
            return false;
        }
        return true;
    }

    /**
     * Sets the button binding in configuration.
     *
     * @param binding the button binding
     * @param button the button
     */
    public static void setButtonBinding(@NotNull ButtonBinding binding, int[] button) {
        binding.setButton(button);
        getBindingsForController().put("controller.controls." + binding.getName(), binding.getButtonCode());
        if (controllerBindingProfiles.containsKey(getController().getGuid())) controllerBindingProfiles.get(getController().getGuid()).put("controller.controls." + binding.getName(), binding.getButtonCode());
        else BINDING.put("controller.controls." + binding.getName(), binding.getButtonCode());
    }

    public static boolean isBackButton(int btn, boolean isBtn, int state) {
        if (!isBtn && state == 0)
            return false;
        return ButtonBinding.axisAsButton(GLFW_GAMEPAD_AXIS_LEFT_Y, false) == ButtonBinding.axisAsButton(btn, state == 1);
    }

    public static boolean isForwardButton(int btn, boolean isBtn, int state) {
        if (!isBtn && state == 0)
            return false;
        return ButtonBinding.axisAsButton(GLFW_GAMEPAD_AXIS_LEFT_Y, true) == ButtonBinding.axisAsButton(btn, state == 1);
    }

    public static boolean isLeftButton(int btn, boolean isBtn, int state) {
        if (!isBtn && state == 0)
            return false;
        return ButtonBinding.axisAsButton(GLFW_GAMEPAD_AXIS_LEFT_X, false) == ButtonBinding.axisAsButton(btn, state == 1);
    }

    public static boolean isRightButton(int btn, boolean isBtn, int state) {
        if (!isBtn && state == 0)
            return false;
        return ButtonBinding.axisAsButton(GLFW_GAMEPAD_AXIS_LEFT_X, true) == ButtonBinding.axisAsButton(btn, state == 1);
    }

    /**
     * Returns whether the specified axis is an axis used for movements.
     *
     * @param axis the axis index
     * @return true if the axis is used for movements, else false
     */
    public static boolean isMovementAxis(int axis) {
        return axis == GLFW_GAMEPAD_AXIS_LEFT_Y || axis == GLFW_GAMEPAD_AXIS_LEFT_X;
    }

    public static void reset() {
        controlsMode = ControlsMode.DEFAULT;
        autoSwitchMode = true;
        debug = false;
        hudEnable = true;
        hudSide = HudSide.LEFT;
        analogMovement = true;
        doubleTapToSprint = true;
        controllerToggleSneak = MinecraftClient.getInstance().options.getSneakToggled().getValue();
        controllerToggleSprint = MinecraftClient.getInstance().options.getSprintToggled().getValue();
        fastBlockPlacing = false;
        flyDrifting = true;
        verticalFlyDrifting = true;
        horizontalReacharound = false;
        verticalReacharound = false;
        shouldRenderReacharoundOutline = true;
        reacharoundOutlineColorHex = "#ffffff";
        reacharoundOutlineColorAlpha = 102;
        rightDeadZone = 0.25;
        leftDeadZone = 0.25;
        invertRightYAxis = false;
        invertRightXAxis = false;
        rotationSpeed = 40.0;
        yAxisRotationSpeed = rotationSpeed;
        mouseSpeed = 25.0;
        unfocusedInput = false;
        virtualMouse = false;
        virtualMouseSkin = VirtualMouseSkin.DEFAULT_LIGHT;
        controllerID = 0;
        secondControllerID = -1;
        controllerType = ControllerType.DEFAULT;
        mouseScreens = Lists.newArrayList("net.minecraft.client.gui.screen.advancement", "net.minecraft.class_457", "net.minecraft.class_408", "net.minecraft.class_3872", "me.flashyreese.mods.reeses_sodium_options.client.gui", "dev.emi.emi.screen", "me.shedaniel.clothconfig2.gui.ClothConfigScreen", "com.mamiyaotaru.voxelmap.gui.GuiWaypoints", "com.mamiyaotaru.voxelmap.gui.GuiPersistentMap");
        BINDING = new HashMap<>();
        maxAnalogValueLeftX = 1;
        maxAnalogValueLeftY = 1;
        maxAnalogValueRightX = 1;
        maxAnalogValueRightY = 1;
        triggerFix = true;
        enableHints = true;
        shortcutInControls = true;
        ringBindings = new ArrayList<>();
        ignoredUnboundKeys = Lists.newArrayList("inventorytabs.key.next_tab");
        controllerBindingProfiles = new HashMap<>();
    }

    /**
     * Gets the controller type from the controller's identifier.
     *
     * @return the controller name matches a type, else empty
     */
    public static @NotNull ControllerType matchControllerToType() {
        String controller = getController().getName().toLowerCase();
        if (controller.contains("xbox 360")) return ControllerType.XBOX_360;
        else if (controller.contains("xbox") || controller.contains("afterglow")) return ControllerType.XBOX;
        else if (controller.contains("steam") && GlDebugInfo.getCpuInfo().contains("AMD Custom APU")) return ControllerType.STEAM_DECK;
        else if (controller.contains("steam")) return ControllerType.STEAM_CONTROLLER;
        else if (controller.contains("dualsense") || controller.contains("ps5")) return ControllerType.DUALSENSE;
        else if (controller.contains("dualshock") || controller.contains("ps4")  || controller.contains("sony")) return ControllerType.DUALSHOCK;
        else if (controller.contains("switch") || controller.contains("joy-con")  || controller.contains("wii") || controller.contains("nintendo")) return ControllerType.SWITCH;
        else if (controller.contains("ouya")) return ControllerType.OUYA;
        else return ControllerType.DEFAULT;
    }
}
