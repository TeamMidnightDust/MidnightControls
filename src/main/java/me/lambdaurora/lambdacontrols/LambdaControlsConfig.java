/*
 * Copyright © 2019 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of LambdaControls.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package me.lambdaurora.lambdacontrols;

import com.electronwill.nightconfig.core.file.FileConfig;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.options.KeyBinding;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.lwjgl.glfw.GLFW.*;

/**
 * Represents LambdaControls configuration.
 */
public class LambdaControlsConfig
{
    private final FileConfig              config              = FileConfig.builder("config/lambdacontrols.toml").concurrent().defaultResource("/config.toml").build();
    private final Map<String, KeyBinding> keybinding_mappings = new HashMap<>();
    private final LambdaControls          mod;
    private       ControlsMode            controls_mode;
    private       ControllerType          controller_type;
    // HUD settings.
    private       boolean                 hud_enable;
    private       HudSide                 hud_side;
    // Controller settings
    private       double                  dead_zone;
    private       double                  rotation_speed;
    private       double                  mouse_speed;
    // Controller controls
    private       String                  back_button;
    private       String                  forward_button;
    private       String                  left_button;
    private       String                  right_button;

    public LambdaControlsConfig(@NotNull LambdaControls mod)
    {
        this.mod = mod;
    }

    public void load()
    {
        this.keybinding_mappings.clear();
        this.config.load();
        this.mod.log("Configuration loaded.");
        this.controls_mode = ControlsMode.by_id(this.config.getOrElse("controls", "default")).orElse(ControlsMode.DEFAULT);
        // HUD settings.
        this.hud_enable = this.config.getOrElse("hud.enable", true);
        this.hud_side = HudSide.by_id(this.config.getOrElse("hud.side", "left")).orElse(HudSide.LEFT);
        // Controller settings.
        this.controller_type = ControllerType.by_id(this.config.getOrElse("controller.type", "default")).orElse(ControllerType.DEFAULT);
        this.dead_zone = this.config.getOrElse("controller.dead_zone", 0.25);
        this.rotation_speed = this.config.getOrElse("controller.rotation_speed", 40.0);
        this.mouse_speed = this.config.getOrElse("controller.mouse_speed", 25.0);
        // Controller controls.
        this.back_button = this.config.getOrElse("controller.controls.back", "none").toLowerCase();
        this.forward_button = this.config.getOrElse("controller.controls.forward", "none").toLowerCase();
        this.left_button = this.config.getOrElse("controller.controls.left", "none").toLowerCase();
        this.right_button = this.config.getOrElse("controller.controls.right", "none").toLowerCase();
    }

    public void init_keybindings(GameOptions options)
    {
        this.keybinding_mappings.put("axis_" + GLFW_GAMEPAD_AXIS_RIGHT_TRIGGER + "+", options.keyAttack);
        this.keybinding_mappings.put("axis_" + GLFW_GAMEPAD_AXIS_LEFT_TRIGGER + "+", options.keyUse);
        this.keybinding_mappings.put("axis_" + GLFW_GAMEPAD_AXIS_LEFT_X + "+", options.keyRight);
        this.keybinding_mappings.put("axis_" + GLFW_GAMEPAD_AXIS_LEFT_X + "-", options.keyLeft);
        this.keybinding_mappings.put("axis_" + GLFW_GAMEPAD_AXIS_LEFT_Y + "+", options.keyBack);
        this.keybinding_mappings.put("axis_" + GLFW_GAMEPAD_AXIS_LEFT_Y + "-", options.keyForward);
        this.keybinding_mappings.put("button_" + GLFW_GAMEPAD_BUTTON_A, options.keyJump);
        this.keybinding_mappings.put("button_" + GLFW_GAMEPAD_BUTTON_B, options.keyDrop);
        this.keybinding_mappings.put("button_" + GLFW_GAMEPAD_BUTTON_X, options.keySwapHands);
        this.keybinding_mappings.put("button_" + GLFW_GAMEPAD_BUTTON_Y, options.keyInventory);
        this.keybinding_mappings.put("button_" + GLFW_GAMEPAD_BUTTON_BACK, options.keyPlayerList);
        this.keybinding_mappings.put("button_" + GLFW_GAMEPAD_BUTTON_GUIDE, options.keyScreenshot);
        this.keybinding_mappings.put("button_" + GLFW_GAMEPAD_BUTTON_RIGHT_THUMB, options.keySneak);
        this.keybinding_mappings.put("button_" + GLFW_GAMEPAD_BUTTON_LEFT_THUMB, options.keySprint);
        this.keybinding_mappings.put("button_" + GLFW_GAMEPAD_BUTTON_DPAD_UP, options.keyTogglePerspective);
    }

    public void save()
    {
        this.config.set("controller.dead_zone", this.dead_zone);
        this.config.set("controller.rotation_speed", this.rotation_speed);
        this.config.set("controller.mouse_speed", this.mouse_speed);
        this.config.save();
        this.mod.log("Configuration saved.");
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
     * Returns the keybindings.
     *
     * @return The keybindings.
     */
    public @NotNull Map<String, KeyBinding> get_keybindings()
    {
        return this.keybinding_mappings;
    }

    public Optional<KeyBinding> get_keybind(@NotNull String id)
    {
        return Optional.ofNullable(this.keybinding_mappings.get(id));
    }

    public String get_back_button()
    {
        return this.back_button;
    }

    public String get_forward_button()
    {
        return this.forward_button;
    }

    public String get_left_button()
    {
        return this.left_button;
    }

    public String get_right_button()
    {
        return this.right_button;
    }

    public boolean is_back_button(int btn, boolean is_btn, int state)
    {
        if (!is_btn && state == 0)
            return false;
        return this.get_back_button().equals((is_btn ? "button_" : "axe_") + btn + (is_btn ? "" : (state == 1 ? "+" : "-")));
    }

    public boolean is_forward_button(int btn, boolean is_btn, int state)
    {
        if (!is_btn && state == 0)
            return false;
        return this.get_forward_button().equals((is_btn ? "button_" : "axe_") + btn + (is_btn ? "" : (state == 1 ? "+" : "-")));
    }

    public boolean is_left_button(int btn, boolean is_btn, int state)
    {
        if (!is_btn && state == 0)
            return false;
        return this.get_left_button().equals((is_btn ? "button_" : "axe_") + btn + (is_btn ? "" : (state == 1 ? "+" : "-")));
    }

    public boolean is_right_button(int btn, boolean is_btn, int state)
    {
        if (!is_btn && state == 0)
            return false;
        return this.get_right_button().equals((is_btn ? "button_" : "axe_") + btn + (is_btn ? "" : (state == 1 ? "+" : "-")));
    }

    /**
     * Returns whether the specified axis is an axis used for movements.
     *
     * @param i The axis index.
     * @return True if the axis is used for movements, else false.
     */
    public boolean is_movement_axis(int i)
    {
        return this.get_forward_button().startsWith("axe_" + i) || this.get_back_button().startsWith("axe_" + i) || this.get_left_button().startsWith("axe_" + i)
                || this.get_right_button().startsWith("axe_" + i);
    }
}
