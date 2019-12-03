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

/**
 * Represents LambdaControls configuration.
 */
public class LambdaControlsConfig
{
    private final FileConfig              config              = FileConfig.builder("config/lambdacontrols.toml").concurrent().defaultResource("/config.toml").build();
    private final Map<String, KeyBinding> keybinding_mappings = new HashMap<>();
    private final LambdaControls          mod;
    private       ControlsMode            controls_mode;
    private       HudSide                 hud_side;
    // Controller settings
    private       double                  dead_zone;
    private       double                  rotation_speed;
    private       double                  mouse_speed;
    // Controller controls
    private       String                  b_button;
    private       String                  back_button;
    private       String                  dpad_up;
    private       String                  dpad_right;
    private       String                  dpad_down;
    private       String                  dpad_left;
    private       String                  forward_button;
    private       String                  inventory_button;
    private       String                  jump_button;
    private       String                  left_button;
    private       String                  right_button;
    private       String                  sneak_button;
    private       String                  x_button;

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
        this.hud_side = HudSide.by_id(this.config.getOrElse("hud.side", "left")).orElse(HudSide.LEFT);
        // Controller settings
        this.dead_zone = this.config.getOrElse("controller.dead_zone", 0.25);
        this.rotation_speed = this.config.getOrElse("controller.rotation_speed", 40.0);
        this.mouse_speed = this.config.getOrElse("controller.mouse_speed", 25.0);
        // Controller controls
        this.b_button = this.config.getOrElse("controller.controls.b", "none").toLowerCase();
        this.back_button = this.config.getOrElse("controller.controls.back", "none").toLowerCase();
        this.dpad_up = this.config.getOrElse("controller.controls.dpad_up", "none").toLowerCase();
        this.dpad_right = this.config.getOrElse("controller.controls.dpad_right", "none").toLowerCase();
        this.dpad_down = this.config.getOrElse("controller.controls.dpad_down", "none").toLowerCase();
        this.dpad_left = this.config.getOrElse("controller.controls.dpad_left", "none").toLowerCase();
        this.forward_button = this.config.getOrElse("controller.controls.forward", "none").toLowerCase();
        this.inventory_button = this.config.getOrElse("controller.controls.inventory", "none").toLowerCase();
        this.jump_button = this.config.getOrElse("controller.controls.jump", "none").toLowerCase();
        this.left_button = this.config.getOrElse("controller.controls.left", "none").toLowerCase();
        this.right_button = this.config.getOrElse("controller.controls.right", "none").toLowerCase();
        this.sneak_button = this.config.getOrElse("controller.controls.sneak", "none").toLowerCase();
        this.x_button = this.config.getOrElse("controller.controls.x", "none").toLowerCase();
    }

    public void init_keybindings(GameOptions options)
    {
        String str = this.config.getOrElse("controller.controls.attack", "none").toLowerCase();
        if (!str.equals("none"))
            this.keybinding_mappings.put(str, options.keyAttack);
        if (!this.back_button.equals("none"))
            this.keybinding_mappings.put(this.back_button, options.keyBack);
        if (!this.b_button.equals("none"))
            this.keybinding_mappings.put(this.b_button, options.keyDrop);
        if (!this.forward_button.equals("none"))
            this.keybinding_mappings.put(this.forward_button, options.keyForward);
        if (!this.inventory_button.equals("none"))
            this.keybinding_mappings.put(this.inventory_button, options.keyInventory);
        if (!this.jump_button.equals("none"))
            this.keybinding_mappings.put(this.jump_button, options.keyJump);
        if (!this.left_button.equals("none"))
            this.keybinding_mappings.put(this.left_button, options.keyLeft);
        if (!this.right_button.equals("none"))
            this.keybinding_mappings.put(this.right_button, options.keyRight);
        if (!this.sneak_button.equals("none"))
            this.keybinding_mappings.put(this.sneak_button, options.keySneak);
        str = this.config.getOrElse("controller.controls.sprint", "none").toLowerCase();
        if (!str.equals("none"))
            this.keybinding_mappings.put(str, options.keySprint);
        str = this.config.getOrElse("controller.controls.use", "none").toLowerCase();
        if (!str.equals("none"))
            this.keybinding_mappings.put(str, options.keyUse);
        if (!this.x_button.equals("none"))
            this.keybinding_mappings.put(this.x_button, options.keySwapHands);
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
        String guid = controller.get_guid();
        this.config.set("controller.id", guid.equals("") ? controller.get_id() : guid);
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

    /**
     * Returns the B button mapping.
     *
     * @return The B button mapping.
     */
    public String get_b_button()
    {
        return this.b_button;
    }

    public String get_back_button()
    {
        return this.back_button;
    }

    public String get_dpad_up()
    {
        return this.dpad_up;
    }

    public String get_dpad_right()
    {
        return this.dpad_right;
    }

    public String get_dpad_down()
    {
        return this.dpad_down;
    }

    public String get_dpad_left()
    {
        return this.dpad_left;
    }

    public String get_forward_button()
    {
        return this.forward_button;
    }

    public String get_hotbar_left_button()
    {
        return this.config.getOrElse("controller.controls.hotbar_left", "none").toLowerCase();
    }

    public String get_hotbar_right_button()
    {
        return this.config.getOrElse("controller.controls.hotbar_right", "none").toLowerCase();
    }

    /**
     * Gets the Inventory button mapping.
     *
     * @return The Inventory button mapping.
     */
    public String get_inventory_button()
    {
        return this.inventory_button;
    }

    public String get_jump_button()
    {
        return this.jump_button;
    }

    public String get_left_button()
    {
        return this.left_button;
    }

    public String get_right_button()
    {
        return this.right_button;
    }

    public String get_sneak_button()
    {
        return this.sneak_button;
    }

    public String get_start_button()
    {
        return this.config.getOrElse("controller.controls.start", "none").toLowerCase();
    }

    public String get_x_button()
    {
        return this.x_button;
    }

    public boolean is_b_button(int button)
    {
        return this.get_b_button().equals("button_" + button);
    }

    public boolean is_back_button(int btn, boolean is_btn, int state)
    {
        if (!is_btn && state == 0)
            return false;
        return this.get_back_button().equals((is_btn ? "button_" : "axe_") + btn + (is_btn ? "" : (state == 1 ? "+" : "-")));
    }

    public boolean is_dpad_up(int button)
    {
        return this.get_dpad_up().equals("button_" + button);
    }

    public boolean is_dpad_right(int button)
    {
        return this.get_dpad_right().equals("button_" + button);
    }

    public boolean is_dpad_down(int button)
    {
        return this.get_dpad_down().equals("button_" + button);
    }

    public boolean is_dpad_left(int button)
    {
        return this.get_dpad_left().equals("button_" + button);
    }

    public boolean is_forward_button(int btn, boolean is_btn, int state)
    {
        if (!is_btn && state == 0)
            return false;
        return this.get_forward_button().equals((is_btn ? "button_" : "axe_") + btn + (is_btn ? "" : (state == 1 ? "+" : "-")));
    }

    public boolean is_hotbar_left_button(int button)
    {
        return this.get_hotbar_left_button().equals("button_" + button);
    }

    public boolean is_hotbar_right_button(int button)
    {
        return this.get_hotbar_right_button().equals("button_" + button);
    }

    public boolean is_inventory_button(int btn)
    {
        return this.get_inventory_button().equals("button_" + btn);
    }

    public boolean is_jump_button(int btn)
    {
        return this.get_jump_button().equals("button_" + btn);
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

    public boolean is_sneak_button(int btn)
    {
        return this.get_sneak_button().equals("button_" + btn);
    }

    public boolean is_start_button(int btn)
    {
        return this.get_start_button().equals("button_" + btn);
    }

    public boolean is_x_button(int btn)
    {
        return this.get_x_button().equals("button_" + btn);
    }

    public String get_view_down_control()
    {
        return this.config.getOrElse("controller.controls.view_down", "none").toLowerCase();
    }

    public String get_view_left_control()
    {
        return this.config.getOrElse("controller.controls.view_left", "none").toLowerCase();
    }

    public String get_view_right_control()
    {
        return this.config.getOrElse("controller.controls.view_right", "none").toLowerCase();
    }

    public String get_view_up_control()
    {
        return this.config.getOrElse("controller.controls.view_up", "none").toLowerCase();
    }

    public boolean is_view_down_control(int axe, int state)
    {
        if (state == 0)
            return false;
        return this.get_view_down_control().contains(axe + (state == 1 ? "+" : "-"));
    }

    public boolean is_view_left_control(int axe, int state)
    {
        if (state == 0)
            return false;
        return this.get_view_left_control().endsWith(axe + (state == 1 ? "+" : "-"));
    }

    public boolean is_view_right_control(int axe, int state)
    {
        if (state == 0)
            return false;
        return this.get_view_right_control().contains(axe + (state == 1 ? "+" : "-"));
    }

    public boolean is_view_up_control(int axe, int state)
    {
        if (state == 0)
            return false;
        return this.get_view_up_control().contains(axe + (state == 1 ? "+" : "-"));
    }

    /**
     * Returns whether the specified axis is an axis used for look direction.
     *
     * @param i The axis index.
     * @return True if the axis is used for look direction, else false.
     */
    public boolean is_look_axis(int i)
    {
        return this.get_view_down_control().startsWith("axe_" + i) || this.get_view_left_control().startsWith("axe_" + i) || this.get_view_right_control().startsWith("axe_" + i)
                || this.get_view_up_control().startsWith("axe_" + i);
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
