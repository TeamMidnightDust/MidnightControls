/*
 * Copyright © 2021 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of midnightcontrols.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package eu.midnightdust.midnightcontrols.client;

import eu.midnightdust.lib.config.MidnightConfig;
import eu.midnightdust.midnightcontrols.ControlsMode;
import eu.midnightdust.midnightcontrols.MidnightControlsFeature;
import eu.midnightdust.midnightcontrols.client.controller.ButtonBinding;
import eu.midnightdust.midnightcontrols.client.controller.Controller;
import eu.midnightdust.midnightcontrols.client.controller.InputManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.*;
import java.util.regex.Pattern;

import static org.lwjgl.glfw.GLFW.GLFW_GAMEPAD_AXIS_LEFT_X;
import static org.lwjgl.glfw.GLFW.GLFW_GAMEPAD_AXIS_LEFT_Y;

/**
 * Represents MidnightControls configuration.
 */
public class MidnightControlsConfig extends MidnightConfig {
    // General
    @Entry public static ControlsMode controlsMode = ControlsMode.DEFAULT;
    @Entry public static boolean autoSwitchMode = false;
    @Entry public static boolean debug = false;
    // HUD
    @Entry public static boolean hudEnable = true;
    @Entry public static boolean hudAlwaysShow = true; // Enabled by default so that users migrating from LambdaControls will have a consistent experience.
    @Entry public static HudSide hudSide = HudSide.LEFT;
    // Gameplay
    @Entry public static boolean analogMovement = true;
    @Entry public static boolean fastBlockPlacing = true;
    @Entry public static boolean flyDrifting = false;
    @Entry public static boolean verticalFlyDrifting = true;
    @Entry public static boolean horizontalReacharound = false;
    @Entry public static boolean verticalReacharound = false;
    @Entry public static boolean shouldRenderReacharoundOutline = true;
    @Entry public static int[] reacharoundOutlineColor = new int[]{255, 255, 255, 102};
    // Controller
    @Entry public static ControllerType controllerType = ControllerType.DEFAULT;
    @Entry public static double rightDeadZone = 0.25;
    @Entry public static double leftDeadZone = 0.25;
    @Entry public static boolean invertRightYAxis = false;
    @Entry public static boolean invertRightXAxis = false;
    @Entry public static double DEFAULT_MAX_VALUE = 1;
    @Entry public static double rotationSpeed = 40.0;
    @Entry public static double mouseSpeed = 25.0;
    @Entry public static boolean unfocusedInput = false;
    @Entry public static boolean virtualMouse = false;
    @Entry public static VirtualMouseSkin virtualMouseSkin = VirtualMouseSkin.DEFAULT_LIGHT;
//    @Entry public static List<Pages> ringPages = new ArrayList<String>();
//    @Entry public static double maxAnalog1 = 1;
//    @Entry public static double maxAnalog2 = 1;
//    @Entry public static double maxAnalog3 = 1;
//    @Entry public static double maxAnalog4 = 1;
    @Entry public static Object controllerID = 0;
    @Entry public static Object secondControllerID = -1;
    @Entry public static Map<String, String> BINDINGS = Map.of();

    private static final Pattern BUTTON_BINDING_PATTERN = Pattern.compile("(-?\\d+)\\+?");
    // Gameplay.
    // Controller settings
    @Entry public static double[] maxAnalogValues = new double[]{DEFAULT_MAX_VALUE, DEFAULT_MAX_VALUE, DEFAULT_MAX_VALUE, DEFAULT_MAX_VALUE};

    /**
     * Loads the configuration
     */
    public static void load() {
        MidnightControlsConfig.init("midnightcontrols", MidnightControlsConfig.class);
        MidnightControlsClient.get().log("Configuration loaded.");
        // Controller controls.
        InputManager.loadButtonBindings();
        //this.mod.ring.load(this.config);
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
        if (raw instanceof Number) {
            return Controller.byId(((Number) raw).intValue());
        } else if (raw instanceof String) {
            return Controller.byGuid((String) raw).orElse(Controller.byId(GLFW.GLFW_JOYSTICK_1));
        }
        return Controller.byId(GLFW.GLFW_JOYSTICK_1);
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
            return DEFAULT_MAX_VALUE;
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
}
