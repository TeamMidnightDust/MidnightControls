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
import eu.midnightdust.lib.config.MidnightConfig;
import eu.midnightdust.midnightcontrols.ControlsMode;
import eu.midnightdust.midnightcontrols.MidnightControlsFeature;
import eu.midnightdust.midnightcontrols.client.controller.ButtonBinding;
import eu.midnightdust.midnightcontrols.client.controller.Controller;
import eu.midnightdust.midnightcontrols.client.controller.InputManager;
import net.minecraft.client.MinecraftClient;
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
    public static boolean isEditing = false;
    @Hidden @Entry public static int configVersion = 1;
    // General
    @Entry(name = "midnightcontrols.menu.controls_mode") public static ControlsMode controlsMode = ControlsMode.DEFAULT;
    @Entry(name = "midnightcontrols.menu.auto_switch_mode") public static boolean autoSwitchMode = true;
    @Entry(name = "Debug") public static boolean debug = false;
    // HUD
    @Entry(name = "midnightcontrols.menu.hud_enable") public static boolean hudEnable = true;
    @Entry(name = "midnightcontrols.menu.hud_side") public static HudSide hudSide = HudSide.LEFT;
    // Gameplay
    @Entry(name = "midnightcontrols.menu.analog_movement") public static boolean analogMovement = true;
    @Entry(name = "midnightcontrols.menu.double_tap_to_sprint") public static boolean doubleTapToSprint = true;
    @Entry(name = "midnightcontrols.menu.fast_block_placing") public static boolean fastBlockPlacing = false; // Disabled by default as this behaviour can be considered cheating on multiplayer servers.
    @Entry(name = "midnightcontrols.menu.fly_drifting") public static boolean flyDrifting = true; // Enabled by default as disabling this behaviour can be considered cheating on multiplayer servers. It can also conflict with some other mods.
    @Entry(name = "midnightcontrols.menu.fly_drifting_vertical") public static boolean verticalFlyDrifting = true; // Enabled by default as disabling this behaviour can be considered cheating on multiplayer servers.
    @Entry(name = "midnightcontrols.menu.reacharound.horizontal") public static boolean horizontalReacharound = false; // Disabled by default as this behaviour can be considered cheating on multiplayer servers.
    @Entry(name = "midnightcontrols.menu.reacharound.vertical") public static boolean verticalReacharound = false; // Disabled by default as this behaviour can be considered cheating on multiplayer servers.
    @Entry(name = "Reacharound Outline") public static boolean shouldRenderReacharoundOutline = true;
    @Entry(name = "Reacharound Outline Color (WIP)", isColor = true) public static String reacharoundOutlineColorHex = "#ffffff";
    @Entry(name = "Reacharound Outline Alpha", min = 0, max = 255) public static int reacharoundOutlineColorAlpha = 102;
    @Entry(name = "midnightcontrols.menu.right_dead_zone") public static double rightDeadZone = 0.25;
    @Entry(name = "midnightcontrols.menu.left_dead_zone") public static double leftDeadZone = 0.25;
    @Entry(name = "midnightcontrols.menu.invert_right_y_axis") public static boolean invertRightYAxis = false;
    @Entry(name = "midnightcontrols.menu.invert_right_x_axis") public static boolean invertRightXAxis = false;
    @Entry(name = "midnightcontrols.menu.rotation_speed") public static double rotationSpeed = 40.0; //used for x-axis, name kept for compatability
    @Entry(name = "midnightcontrols.menu.y_axis_rotation_speed") public static double yAxisRotationSpeed = rotationSpeed;
    @Entry(name = "midnightcontrols.menu.mouse_speed") public static double mouseSpeed = 25.0;
    @Entry(name = "midnightcontrols.menu.unfocused_input") public static boolean unfocusedInput = false;
    @Entry(name = "midnightcontrols.menu.virtual_mouse") public static boolean virtualMouse = false;
    @Entry(name = "midnightcontrols.menu.virtual_mouse.skin") public static VirtualMouseSkin virtualMouseSkin = VirtualMouseSkin.DEFAULT_LIGHT;
    @Entry(name = "Controller ID") public static Object controllerID = 0;
    @Entry(name = "2nd Controller ID") public static Object secondControllerID = -1;
    @Entry(name = "midnightcontrols.menu.controller_type") public static ControllerType controllerType = ControllerType.DEFAULT;
    @Entry(name = "Mouse screens") public static List<String> mouseScreens = Lists.newArrayList("me.jellysquid.mods.sodium.client.gui",
            "net.coderbot.iris.gui", "net.minecraft.client.gui.screen.advancement", "net.minecraft.client.gui.screen.pack.PackScreen", "net.minecraft.class_5375",
            "net.minecraft.class_457", "net.minecraft.class_408", "me.flashyreese.mods.reeses_sodium_options.client.gui", "dev.emi.emi.screen",
            "hardcorequesting.client.interfaces.GuiQuestBook", "hardcorequesting.client.interfaces.GuiReward", "hardcorequesting.client.interfaces.EditTrackerScreen");
    @Entry(name = "Keybindings") public static Map<String, String> BINDINGS = new HashMap<>();

    private static final Pattern BUTTON_BINDING_PATTERN = Pattern.compile("(-?\\d+)\\+?");
    @Entry(name = "Max analog values") public static double[] maxAnalogValues = new double[]{1, 1, 1, 1};
    @Entry(name = "Trigger button fix") public static boolean triggerFix = true;
    @Entry(name = "Enable Hints") public static boolean enableHints = true;
    @Entry(name = "Enable Shortcut in Controls Options") public static boolean shortcutInControls = true;
    @Entry(name = "Ring Bindings (WIP)") public static List<String> ringBindings = new ArrayList<>();
    @Entry(name = "Ignored Unbound Keys") public static List<String> ignoredUnboundKeys = Lists.newArrayList("inventorytabs.key.next_tab");

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
        if (axis >= MidnightControlsConfig.maxAnalogValues.length)
            return 1;
        return MidnightControlsConfig.maxAnalogValues[axis];
    }

    public static void setAxisMaxValue(int axis, double value) {
        if (axis < MidnightControlsConfig.maxAnalogValues.length)
            MidnightControlsConfig.maxAnalogValues[axis] = value;
    }

    /**
     * Loads the button binding from configuration.
     *
     * @param button the button binding
     */
    public static void loadButtonBinding(@NotNull ButtonBinding button) {
        button.setButton(button.getDefaultButton());
        var code = MidnightControlsConfig.BINDINGS.getOrDefault("controller.controls." + button.getName(), button.getButtonCode());

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
            MidnightControlsConfig.BINDINGS.put("controller.controls." + button.getName(), button.getButtonCode());
        }
    }

    private static boolean checkValidity(@NotNull ButtonBinding binding, @NotNull String input, String group) {
        if (group == null) {
            MidnightControlsClient.get().warn("Malformed config value \"" + input + "\" for binding \"" + binding.getName() + "\".");
            MidnightControlsConfig.BINDINGS.put("controller.controls." + binding.getName(), binding.getButtonCode());
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
        MidnightControlsConfig.BINDINGS.put("controller.controls." + binding.getName(), binding.getButtonCode());
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
        mouseScreens = Lists.newArrayList("me.jellysquid.mods.sodium.client.gui", "net.coderbot.iris.gui", "net.minecraft.client.gui.screen.advancement", "net.minecraft.client.gui.screen.pack.PackScreen", "net.minecraft.class_5375", "net.minecraft.class_457", "net.minecraft.class_408", "me.flashyreese.mods.reeses_sodium_options.client.gui", "dev.emi.emi.screen");
        BINDINGS = new HashMap<>();
        maxAnalogValues = new double[]{1, 1, 1, 1};
        triggerFix = true;
        enableHints = true;
        shortcutInControls = true;
        ringBindings = new ArrayList<>();
        ignoredUnboundKeys = Lists.newArrayList("inventorytabs.key.next_tab");
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
        else if (controller.contains("steam deck")) return ControllerType.STEAM_DECK;
        else if (controller.contains("steam")) return ControllerType.STEAM_CONTROLLER;
        else if (controller.contains("dualsense")) return ControllerType.DUALSENSE;
        else if (controller.contains("dualshock") || controller.contains("ps4")  || controller.contains("sony")) return ControllerType.DUALSHOCK;
        else if (controller.contains("switch") || controller.contains("joy-con")  || controller.contains("wii") || controller.contains("nintendo")) return ControllerType.SWITCH;
        else if (controller.contains("ouya")) return ControllerType.OUYA;
        else return ControllerType.DEFAULT;
    }
}
