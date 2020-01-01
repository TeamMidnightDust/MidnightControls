/*
 * Copyright © 2020 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of LambdaControls.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package me.lambdaurora.lambdacontrols;

import com.electronwill.nightconfig.core.file.FileConfig;
import me.lambdaurora.lambdacontrols.controller.ButtonBinding;
import me.lambdaurora.lambdacontrols.controller.Controller;
import me.lambdaurora.lambdacontrols.controller.InputManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.Arrays;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.lwjgl.glfw.GLFW.GLFW_GAMEPAD_AXIS_LEFT_X;
import static org.lwjgl.glfw.GLFW.GLFW_GAMEPAD_AXIS_LEFT_Y;

/**
 * Represents LambdaControls configuration.
 */
public class LambdaControlsConfig
{
    private static final ControlsMode   DEFAULT_CONTROLS_MODE    = ControlsMode.DEFAULT;
    private static final boolean        DEFAULT_AUTO_SWITCH_MODE = false;
    private static final boolean        DEFAULT_HUD_ENABLE       = true;
    private static final HudSide        DEFAULT_HUD_SIDE         = HudSide.LEFT;
    private static final ControllerType DEFAULT_CONTROLLER_TYPE  = ControllerType.DEFAULT;
    private static final double         DEFAULT_DEAD_ZONE        = 0.25;
    private static final double         DEFAULT_ROTATION_SPEED   = 40.0;
    private static final double         DEFAULT_MOUSE_SPEED      = 25.0;

    private static final Pattern BUTTON_BINDING_PATTERN = Pattern.compile("(-?\\d+)\\+?");

    private final FileConfig     config = FileConfig.builder("config/lambdacontrols.toml").concurrent().defaultResource("/config.toml").build();
    private final LambdaControls mod;
    private       ControlsMode   controls_mode;
    private       ControllerType controller_type;
    // HUD settings.
    private       boolean        hud_enable;
    private       HudSide        hud_side;
    // Controller settings
    private       double         dead_zone;
    private       double         rotation_speed;
    private       double         mouse_speed;

    public LambdaControlsConfig(@NotNull LambdaControls mod)
    {
        this.mod = mod;
    }

    /**
     * Loads the configuration
     */
    public void load()
    {
        this.config.load();
        this.check_and_fix();
        this.mod.log("Configuration loaded.");
        this.controls_mode = ControlsMode.by_id(this.config.getOrElse("controls", DEFAULT_CONTROLS_MODE.get_name())).orElse(DEFAULT_CONTROLS_MODE);
        // HUD settings.
        this.hud_enable = this.config.getOrElse("hud.enable", DEFAULT_HUD_ENABLE);
        this.hud_side = HudSide.by_id(this.config.getOrElse("hud.side", DEFAULT_HUD_SIDE.get_name())).orElse(DEFAULT_HUD_SIDE);
        // Controller settings.
        this.controller_type = ControllerType.by_id(this.config.getOrElse("controller.type", DEFAULT_CONTROLLER_TYPE.get_name())).orElse(DEFAULT_CONTROLLER_TYPE);
        this.dead_zone = this.config.getOrElse("controller.dead_zone", DEFAULT_DEAD_ZONE);
        this.rotation_speed = this.config.getOrElse("controller.rotation_speed", DEFAULT_ROTATION_SPEED);
        this.mouse_speed = this.config.getOrElse("controller.mouse_speed", DEFAULT_MOUSE_SPEED);
        // Controller controls.
        InputManager.load_button_bindings(this);
    }

    /**
     * Saves the configuration.
     */
    public void save()
    {
        this.config.set("controller.dead_zone", this.dead_zone);
        this.config.set("controller.rotation_speed", this.rotation_speed);
        this.config.set("controller.mouse_speed", this.mouse_speed);
        this.config.save();
        this.mod.log("Configuration saved.");
    }

    public void check_and_fix()
    {
        InputManager.stream_bindings().forEach(binding -> {
            String path = "controller.controls." + binding.get_name();
            Object raw = this.config.getRaw(path);
            if (raw instanceof Number) {
                this.mod.warn("Invalid data at \"" + path + "\", fixing...");
                this.config.set(path, String.valueOf(raw));
            }
        });
    }

    /**
     * Resets the configuration to default values.
     */
    public void reset()
    {
        this.set_controls_mode(DEFAULT_CONTROLS_MODE);
        this.set_auto_switch_mode(DEFAULT_AUTO_SWITCH_MODE);
        this.set_hud_enabled(DEFAULT_HUD_ENABLE);
        this.set_hud_side(DEFAULT_HUD_SIDE);
        this.set_controller_type(DEFAULT_CONTROLLER_TYPE);
        this.set_dead_zone(DEFAULT_DEAD_ZONE);
        this.set_rotation_speed(DEFAULT_ROTATION_SPEED);
        this.set_mouse_speed(DEFAULT_MOUSE_SPEED);

        InputManager.stream_bindings().forEach(binding -> this.set_button_binding(binding, binding.get_default_button()));
    }

    /**
     * Gets the controls mode from the configuration.
     *
     * @return The controls mode.
     */
    public @NotNull ControlsMode get_controls_mode()
    {
        return this.controls_mode;
    }

    /**
     * Sets the controls mode in the configuration.
     *
     * @param controls_mode The controls mode.
     */
    public void set_controls_mode(@NotNull ControlsMode controls_mode)
    {
        this.controls_mode = controls_mode;
        this.config.set("controls", controls_mode.get_name());
    }

    /**
     * Returns whether the auto switch mode is enabled or not.
     *
     * @return True if the auto switch mode is enabled, else false.
     */
    public boolean has_auto_switch_mode()
    {
        return this.config.getOrElse("auto_switch_mode", DEFAULT_AUTO_SWITCH_MODE);
    }

    /**
     * Sets whether the auto switch mode is enabled or not.
     *
     * @param auto_switch_mode True if the auto switch mode is enabled, else false.
     */
    public void set_auto_switch_mode(boolean auto_switch_mode)
    {
        this.config.set("auto_switch_mode", auto_switch_mode);
    }

    /**
     * Returns whether the HUD is enabled.
     *
     * @return True if the HUD is enabled, else false.
     */
    public boolean is_hud_enabled()
    {
        return this.hud_enable;
    }

    /**
     * Sets whether the HUD is enabled.
     *
     * @param enable True if the HUD is enabled, else false.
     */
    public void set_hud_enabled(boolean enable)
    {
        this.hud_enable = enable;
        this.config.set("hud.enable", this.hud_enable);
    }

    /**
     * Gets the HUD side from the configuration.
     *
     * @return The HUD side.
     */
    public @NotNull HudSide get_hud_side()
    {
        return this.hud_side;
    }

    /**
     * Sets the HUD side in the configuration.
     *
     * @param hud_side The HUD side.
     */
    public void set_hud_side(@NotNull HudSide hud_side)
    {
        this.hud_side = hud_side;
        this.config.set("hud.side", hud_side.get_name());
    }

    /**
     * Gets the used controller.
     *
     * @return The used controller.
     */
    public @NotNull Controller get_controller()
    {
        Object raw = this.config.getRaw("controller.id");
        if (raw instanceof Number) {
            return Controller.by_id((Integer) raw);
        } else if (raw instanceof String) {
            return Controller.by_guid((String) raw).orElse(Controller.by_id(GLFW.GLFW_JOYSTICK_1));
        }
        return Controller.by_id(GLFW.GLFW_JOYSTICK_1);
    }

    /**
     * Sets the used controller.
     *
     * @param controller The used controller.
     */
    public void set_controller(@NotNull Controller controller)
    {
        this.config.set("controller.id", controller.get_id());
    }

    /**
     * Gets the second controller (for Joy-Con supports).
     *
     * @return The second controller.
     */
    public @NotNull Optional<Controller> get_second_controller()
    {
        Object raw = this.config.getRaw("controller.id2");
        if (raw instanceof Number) {
            if ((int) raw == -1)
                return Optional.empty();
            return Optional.of(Controller.by_id((Integer) raw));
        } else if (raw instanceof String) {
            return Optional.of(Controller.by_guid((String) raw).orElse(Controller.by_id(GLFW.GLFW_JOYSTICK_1)));
        }
        return Optional.empty();
    }

    /**
     * Sets the second controller.
     *
     * @param controller The second controller.
     */
    public void set_second_controller(@Nullable Controller controller)
    {
        this.config.set("controller.id2", controller == null ? -1 : controller.get_id());
    }

    /**
     * Gets the controller's type.
     *
     * @return The controller's type.
     */
    public @NotNull ControllerType get_controller_type()
    {
        return this.controller_type;
    }

    /**
     * Sets the controller's type.
     *
     * @param controller_type The controller's type.
     */
    public void set_controller_type(@NotNull ControllerType controller_type)
    {
        this.controller_type = controller_type;
        this.config.set("controller.type", controller_type.get_name());
    }

    /**
     * Gets the controller's dead zone from the configuration.
     *
     * @return The controller's dead zone value.
     */
    public double get_dead_zone()
    {
        return this.dead_zone;
    }

    /**
     * Sets the controller's dead zone in the configuration.
     *
     * @param dead_zone The new controller's dead zone value.
     */
    public void set_dead_zone(double dead_zone)
    {
        this.dead_zone = dead_zone;
    }

    /**
     * Gets the controller's rotation speed.
     *
     * @return The rotation speed.
     */
    public double get_rotation_speed()
    {
        return this.rotation_speed;
    }

    /**
     * Sets the controller's rotation speed.
     *
     * @param rotation_speed The rotation speed.
     */
    public void set_rotation_speed(double rotation_speed)
    {
        this.rotation_speed = rotation_speed;
    }

    /**
     * Gets the controller's mouse speed.
     *
     * @return The mouse speed.
     */
    public double get_mouse_speed()
    {
        return this.mouse_speed;
    }

    /**
     * Sets the controller's mouse speed.
     *
     * @param mouse_speed The mouse speed.
     */
    public void set_mouse_speed(double mouse_speed)
    {
        this.mouse_speed = mouse_speed;
    }

    /**
     * Returns whether the right X axis is inverted or not.
     *
     * @return True if the right X axis is inverted, else false.
     */
    public boolean does_invert_right_x_axis()
    {
        return this.config.getOrElse("controller.invert_right_x_axis", false);
    }

    /**
     * Sets whether the right X axis is inverted or not.
     *
     * @param invert True if the right X axis is inverted, else false.
     */
    public void set_invert_right_x_axis(boolean invert)
    {
        this.config.set("controller.invert_right_x_axis", invert);
    }

    /**
     * Returns whether the right Y axis is inverted or not.
     *
     * @return True if the right Y axis is inverted, else false.
     */
    public boolean does_invert_right_y_axis()
    {
        return this.config.getOrElse("controller.invert_right_y_axis", false);
    }

    /**
     * Sets whether the right Y axis is inverted or not.
     *
     * @param invert True if the right Y axis is inverted, else false.
     */
    public void set_invert_right_y_axis(boolean invert)
    {
        this.config.set("controller.invert_right_y_axis", invert);
    }

    /**
     * Gets the right X axis sign.
     *
     * @return The right X axis sign.
     */
    public double get_right_x_axis_sign()
    {
        return this.does_invert_right_x_axis() ? -1.0 : 1.0;
    }

    /**
     * Gets the right Y axis sign.
     *
     * @return The right Y axis sign.
     */
    public double get_right_y_axis_sign()
    {
        return this.does_invert_right_y_axis() ? -1.0 : 1.0;
    }

    /**
     * Loads the button binding from configuration.
     *
     * @param button The button binding.
     */
    public void load_button_binding(@NotNull ButtonBinding button)
    {
        button.set_button(button.get_default_button());
        String button_code = this.config.getOrElse("controller.controls." + button.get_name(), button.get_button_code());

        Matcher matcher = BUTTON_BINDING_PATTERN.matcher(button_code);

        try {
            int[] buttons = new int[1];
            int count = 0;
            while (matcher.find()) {
                count++;
                if (count > buttons.length)
                    buttons = Arrays.copyOf(buttons, count);
                String current;
                if (!this.check_validity(button, button_code, current = matcher.group(1)))
                    return;
                buttons[count - 1] = Integer.parseInt(current);
            }
            if (count == 0) {
                this.mod.warn("Malformed config value \"" + button_code + "\" for binding \"" + button.get_name() + "\".");
                this.set_button_binding(button, new int[]{-1});
            }

            button.set_button(buttons);
        } catch (Exception e) {
            this.mod.warn("Malformed config value \"" + button_code + "\" for binding \"" + button.get_name() + "\".");
            this.config.set("controller.controls." + button.get_name(), button.get_button_code());
        }
    }

    private boolean check_validity(@NotNull ButtonBinding binding, @NotNull String input, String group)
    {
        if (group == null) {
            this.mod.warn("Malformed config value \"" + input + "\" for binding \"" + binding.get_name() + "\".");
            this.config.set("controller.controls." + binding.get_name(), binding.get_button_code());
            return false;
        }
        return true;
    }

    /**
     * Sets the button binding in configuration.
     *
     * @param binding The button binding.
     * @param button  The button.
     */
    public void set_button_binding(@NotNull ButtonBinding binding, int[] button)
    {
        binding.set_button(button);
        this.config.set("controller.controls." + binding.get_name(), binding.get_button_code());
    }

    public boolean is_back_button(int btn, boolean is_btn, int state)
    {
        if (!is_btn && state == 0)
            return false;
        return ButtonBinding.axis_as_button(GLFW_GAMEPAD_AXIS_LEFT_Y, false) == ButtonBinding.axis_as_button(btn, state == 1);
    }

    public boolean is_forward_button(int btn, boolean is_btn, int state)
    {
        if (!is_btn && state == 0)
            return false;
        return ButtonBinding.axis_as_button(GLFW_GAMEPAD_AXIS_LEFT_Y, true) == ButtonBinding.axis_as_button(btn, state == 1);
    }

    public boolean is_left_button(int btn, boolean is_btn, int state)
    {
        if (!is_btn && state == 0)
            return false;
        return ButtonBinding.axis_as_button(GLFW_GAMEPAD_AXIS_LEFT_X, false) == ButtonBinding.axis_as_button(btn, state == 1);
    }

    public boolean is_right_button(int btn, boolean is_btn, int state)
    {
        if (!is_btn && state == 0)
            return false;
        return ButtonBinding.axis_as_button(GLFW_GAMEPAD_AXIS_LEFT_X, true) == ButtonBinding.axis_as_button(btn, state == 1);
    }

    /**
     * Returns whether the specified axis is an axis used for movements.
     *
     * @param axis The axis index.
     * @return True if the axis is used for movements, else false.
     */
    public boolean is_movement_axis(int axis)
    {
        return axis == GLFW_GAMEPAD_AXIS_LEFT_Y || axis == GLFW_GAMEPAD_AXIS_LEFT_X;
    }
}
