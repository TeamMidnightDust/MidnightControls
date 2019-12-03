/*
 * Copyright © 2019 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of LambdaControls.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package me.lambdaurora.lambdacontrols.gui;

import me.lambdaurora.lambdacontrols.ControlsMode;
import me.lambdaurora.lambdacontrols.HudSide;
import me.lambdaurora.lambdacontrols.LambdaControls;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.controls.ControlsOptionsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.options.DoubleOption;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.options.Option;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.TranslatableText;
import org.jetbrains.annotations.NotNull;

/**
 * Represents the LambdaControls settings screen.
 */
public class LambdaControlsSettingsScreen extends Screen
{
    private final LambdaControls mod;
    private final Screen         parent;
    private final GameOptions    options;
    private final Option         dead_zone_option;
    private final Option         rotation_speed_option;
    private final Option         mouse_speed_option;

    public LambdaControlsSettingsScreen(Screen parent, @NotNull GameOptions options)
    {
        super(new TranslatableText("lambdacontrols.title.settings"));
        this.mod = LambdaControls.get();
        this.parent = parent;
        this.options = options;
        this.dead_zone_option = new DoubleOption("lambdacontrols.menu.dead_zone", 0.05, 1.0, 0.05F, game_options -> this.mod.config.get_dead_zone(),
                (game_options, new_value) -> {
                    synchronized (this.mod.config) {
                        this.mod.config.set_dead_zone(new_value);
                    }
                }, (game_options, option) -> {
            String value = String.valueOf(option.get(options));
            return option.getDisplayPrefix() + value.substring(0, value.length() > 5 ? 5 : value.length());
        });
        this.rotation_speed_option = new DoubleOption("lambdacontrols.menu.rotation_speed", 0.0, 50.0, 0.5F, game_options -> this.mod.config.get_rotation_speed(),
                (game_options, new_value) -> {
                    synchronized (this.mod.config) {
                        this.mod.config.set_rotation_speed(new_value);
                    }
                }, (game_options, option) -> option.getDisplayPrefix() + option.get(options));
        this.mouse_speed_option = new DoubleOption("lambdacontrols.menu.mouse_speed", 0.0, 50.0, 0.5F, game_options -> this.mod.config.get_mouse_speed(),
                (game_options, new_value) -> {
                    synchronized (this.mod.config) {
                        this.mod.config.set_mouse_speed(new_value);
                    }
                }, (game_options, option) -> option.getDisplayPrefix() + option.get(options));
    }

    @Override
    public void removed()
    {
        this.mod.config.save();
        super.removed();
    }

    @Override
    public void onClose()
    {
        this.mod.config.save();
        super.onClose();
    }

    @Override
    protected void init()
    {
        super.init();
        int y = 18;
        int button_height = 20, spacing = 5;
        this.addButton(new ButtonWidget(this.width / 2 - 155, y, 150, button_height, I18n.translate("lambdacontrols.menu.controls_mode") + ": " + this.mod.config.get_controls_mode().get_translated_name(),
                btn -> {
                    ControlsMode next = this.mod.config.get_controls_mode().next();
                    btn.setMessage(I18n.translate("lambdacontrols.menu.controls_mode") + ": " + next.get_translated_name());
                    this.mod.config.set_controls_mode(next);
                    this.mod.config.save();
                }));
        this.addButton(new ButtonWidget(this.width / 2 - 155 + 160, y, 150, button_height, I18n.translate("options.controls"),
                btn -> this.minecraft.openScreen(new ControlsOptionsScreen(this, this.options))));
        this.addButton(new ButtonWidget(this.width / 2 - 155, (y += spacing + button_height), 150, button_height, I18n.translate("lambdacontrols.menu.hud_side") + ": " + this.mod.config.get_hud_side().get_translated_name(),
                btn -> {
                    HudSide next = this.mod.config.get_hud_side().next();
                    btn.setMessage(I18n.translate("lambdacontrols.menu.hud_side") + ": " + next.get_translated_name());
                    this.mod.config.set_hud_side(next);
                    this.mod.config.save();
                }));
        this.addButton(new ButtonWidget(this.width / 2 - 155 + 160, y, 150, button_height, I18n.translate("lambdacontrols.menu.controller_controls"),
                btn -> this.minecraft.openScreen(new ControlsOptionsScreen(this, this.options))));
        this.addButton(this.dead_zone_option.createButton(this.options, this.width / 2 - 155, (y += spacing + button_height), 150));
        this.addButton(this.rotation_speed_option.createButton(this.options, this.width / 2 - 155 + 160, y, 150));
        this.addButton(this.mouse_speed_option.createButton(this.options, this.width / 2 - 155, (y += spacing + button_height), 150));
        this.addButton(new ButtonWidget(this.width / 2 - 155 + 160, this.height - 29, 150, button_height, I18n.translate("gui.done"), (buttonWidget) -> {
            this.minecraft.openScreen(this.parent);
        }));
    }

    @Override
    public void render(int mouseX, int mouseY, float delta)
    {
        this.renderBackground();
        super.render(mouseX, mouseY, delta);
    }
}
