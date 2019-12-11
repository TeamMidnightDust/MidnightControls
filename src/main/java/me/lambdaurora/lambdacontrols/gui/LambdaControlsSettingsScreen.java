/*
 * Copyright © 2019 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of LambdaControls.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package me.lambdaurora.lambdacontrols.gui;

import me.lambdaurora.lambdacontrols.Controller;
import me.lambdaurora.lambdacontrols.ControlsMode;
import me.lambdaurora.lambdacontrols.LambdaControls;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.options.ControlsOptionsScreen;
import net.minecraft.client.gui.widget.ButtonListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.options.*;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

/**
 * Represents the LambdaControls settings screen.
 */
public class LambdaControlsSettingsScreen extends Screen
{
    final         LambdaControls   mod;
    private final Screen           parent;
    private final GameOptions      options;
    private final Option           controller_option;
    private final Option           controller_type_option;
    private final Option           hud_enable_option;
    private final Option           hud_side_option;
    private final Option           dead_zone_option;
    private final Option           rotation_speed_option;
    private final Option           mouse_speed_option;
    private       ButtonListWidget list;

    public LambdaControlsSettingsScreen(Screen parent, @NotNull GameOptions options)
    {
        super(new TranslatableText("lambdacontrols.title.settings"));
        this.mod = LambdaControls.get();
        this.parent = parent;
        this.options = options;
        this.controller_option = new CyclingOption("lambdacontrols.menu.controller", (game_options, amount) -> {
            int current_id = this.mod.config.get_controller().get_id();
            current_id += amount;
            if (current_id > GLFW.GLFW_JOYSTICK_LAST)
                current_id = GLFW.GLFW_JOYSTICK_1;
            this.mod.config.set_controller(Controller.by_id(current_id));
        }, (game_options, option) -> {
            String controller_name = this.mod.config.get_controller().get_name();
            if (controller_name.equals(String.valueOf(this.mod.config.get_controller().get_id())))
                return option.getDisplayPrefix() + Formatting.RED + controller_name;
            else
                return option.getDisplayPrefix() + controller_name;
        });
        this.controller_type_option = new CyclingOption("lambdacontrols.menu.controller_type",
                (game_options, amount) -> this.mod.config.set_controller_type(this.mod.config.get_controller_type().next()),
                (game_options, option) -> option.getDisplayPrefix() + this.mod.config.get_controller_type().get_translated_name());
        this.hud_enable_option = new BooleanOption("lambdacontrols.menu.hud_enable", (game_options) -> this.mod.config.is_hud_enabled(),
                (game_options, new_value) -> this.mod.config.set_hud_enabled(new_value));
        this.hud_side_option = new CyclingOption("lambdacontrols.menu.hud_side",
                (game_options, amount) -> this.mod.config.set_hud_side(this.mod.config.get_hud_side().next()),
                (game_options, option) -> option.getDisplayPrefix() + this.mod.config.get_hud_side().get_translated_name());
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

    private int get_text_height()
    {
        return (5 + this.font.fontHeight) * 3 + 5;
    }

    @Override
    protected void init()
    {
        super.init();
        int button_height = 20;
        this.addButton(new ButtonWidget(this.width / 2 - 155, 18, 150, button_height, I18n.translate("lambdacontrols.menu.controls_mode") + ": " + this.mod.config.get_controls_mode().get_translated_name(),
                btn -> {
                    ControlsMode next = this.mod.config.get_controls_mode().next();
                    btn.setMessage(I18n.translate("lambdacontrols.menu.controls_mode") + ": " + next.get_translated_name());
                    this.mod.config.set_controls_mode(next);
                    this.mod.config.save();
                }));
        this.addButton(new ButtonWidget(this.width / 2 - 155 + 160, 18, 150, button_height, I18n.translate("options.controls"),
                btn -> {
                    if (this.mod.config.get_controls_mode() == ControlsMode.CONTROLLER)
                        this.minecraft.openScreen(new LambdaControlsControlsScreen(this));
                    else
                        this.minecraft.openScreen(new ControlsOptionsScreen(this, this.options));
                }));

        this.list = new ButtonListWidget(this.minecraft, this.width, this.height, 43, this.height - 29 - this.get_text_height(), 25);
        this.list.addSingleOptionEntry(this.controller_option);
        this.list.addOptionEntry(this.controller_type_option, this.dead_zone_option);
        this.list.addOptionEntry(this.hud_enable_option, this.hud_side_option);
        this.list.addOptionEntry(this.rotation_speed_option, this.mouse_speed_option);
        this.children.add(this.list);

        this.addButton(new ButtonWidget(this.width / 2 - 155, this.height - 29, 300, button_height, I18n.translate("gui.done"),
                (buttonWidget) -> this.minecraft.openScreen(this.parent)));
    }

    @Override
    public void render(int mouse_x, int mouse_y, float delta)
    {
        this.renderBackground();
        this.list.render(mouse_x, mouse_y, delta);
        super.render(mouse_x, mouse_y, delta);
        this.drawCenteredString(this.font, I18n.translate("lambdacontrols.menu.title"), this.width / 2, 8, 16777215);
        this.drawCenteredString(this.font, I18n.translate("lambdacontrols.controller.mappings.1", Formatting.GREEN.toString(), Formatting.RESET.toString()), this.width / 2, this.height - 29 - (5 + this.font.fontHeight) * 3, 10526880);
        this.drawCenteredString(this.font, I18n.translate("lambdacontrols.controller.mappings.2", Formatting.GOLD.toString(), Formatting.RESET.toString()), this.width / 2, this.height - 29 - (5 + this.font.fontHeight) * 2, 10526880);
        this.drawCenteredString(this.font, I18n.translate("lambdacontrols.controller.mappings.3", Formatting.GREEN.toString(), Formatting.RESET.toString()), this.width / 2, this.height - 29 - (5 + this.font.fontHeight), 10526880);
    }
}
