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
    }

    public void init_keybindings(GameOptions options)
    {
        String str = this.config.getOrElse("controller.attack", "none").toLowerCase();
        if (!str.equals("none"))
            this.keybinding_mappings.put(str, options.keyAttack);
        str = this.config.getOrElse("controller.back", "none").toLowerCase();
        if (!str.equals("none"))
            this.keybinding_mappings.put(str, options.keyBack);
        str = this.config.getOrElse("controller.drop", "none").toLowerCase();
        if (!str.equals("none"))
            this.keybinding_mappings.put(str, options.keyDrop);
        str = this.config.getOrElse("controller.forward", "none").toLowerCase();
        if (!str.equals("none"))
            this.keybinding_mappings.put(str, options.keyForward);
        str = this.config.getOrElse("controller.inventory", "none").toLowerCase();
        if (!str.equals("none"))
            this.keybinding_mappings.put(str, options.keyInventory);
        str = this.config.getOrElse("controller.jump", "none").toLowerCase();
        if (!str.equals("none"))
            this.keybinding_mappings.put(str, options.keyJump);
        str = this.config.getOrElse("controller.left", "none").toLowerCase();
        if (!str.equals("none"))
            this.keybinding_mappings.put(str, options.keyLeft);
        str = this.config.getOrElse("controller.right", "none").toLowerCase();
        if (!str.equals("none"))
            this.keybinding_mappings.put(str, options.keyRight);
        str = this.config.getOrElse("controller.sneak", "none").toLowerCase();
        if (!str.equals("none"))
            this.keybinding_mappings.put(str, options.keySneak);
        str = this.config.getOrElse("controller.sprint", "none").toLowerCase();
        if (!str.equals("none"))
            this.keybinding_mappings.put(str, options.keySprint);
        str = this.config.getOrElse("controller.use", "none").toLowerCase();
        if (!str.equals("none"))
            this.keybinding_mappings.put(str, options.keyUse);
    }

    public void save()
    {
        this.config.save();
        this.mod.log("Configuration saved.");
    }

    /**
     * Returns the controls mode from the configuration.
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
     * Returns the HUD side from the configuration.
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

    public String get_hotbar_left_button()
    {
        return this.config.getOrElse("controller.hotbar_left", "none").toLowerCase();
    }

    public String get_hotbar_right_button()
    {
        return this.config.getOrElse("controller.hotbar_right", "none").toLowerCase();
    }

    public boolean is_hotbar_left_button(int button)
    {
        return this.get_hotbar_left_button().equals("button_" + button);
    }

    public boolean is_hotbar_right_button(int button)
    {
        return this.get_hotbar_right_button().equals("button_" + button);
    }

    public String get_view_down_control()
    {
        return this.config.getOrElse("controller.view_down", "none").toLowerCase();
    }

    public String get_view_left_control()
    {
        return this.config.getOrElse("controller.view_left", "none").toLowerCase();
    }

    public String get_view_right_control()
    {
        return this.config.getOrElse("controller.view_right", "none").toLowerCase();
    }

    public String get_view_up_control()
    {
        return this.config.getOrElse("controller.view_up", "none").toLowerCase();
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

    public boolean is_look_axis(int i)
    {
        return this.get_view_down_control().startsWith("axe_" + i) || this.get_view_left_control().startsWith("axe_" + i) || this.get_view_right_control().startsWith("axe_" + i)
                || this.get_view_up_control().startsWith("axe_" + i);
    }
}
