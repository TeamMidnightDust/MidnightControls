/*
 * Copyright © 2020 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of LambdaControls.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package me.lambdaurora.lambdacontrols.client.gui;

import me.lambdaurora.lambdacontrols.ControlsMode;
import me.lambdaurora.lambdacontrols.client.LambdaControlsClient;
import me.lambdaurora.lambdacontrols.client.controller.Controller;
import me.lambdaurora.spruceui.SpruceButtonWidget;
import me.lambdaurora.spruceui.SpruceLabelWidget;
import me.lambdaurora.spruceui.Tooltip;
import me.lambdaurora.spruceui.option.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.options.ControlsOptionsScreen;
import net.minecraft.client.gui.widget.ButtonListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.options.CyclingOption;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.options.Option;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

/**
 * Represents the LambdaControls settings screen.
 */
public class LambdaControlsSettingsScreen extends Screen
{
    public static final String               GAMEPAD_TOOL_URL             = "http://generalarcade.com/gamepadtool/";
    final               LambdaControlsClient mod;
    private final       Screen               parent;
    private final       boolean              hide_controls;
    // General options
    private final       Option               auto_switch_mode_option;
    private final       Option               rotation_speed_option;
    private final       Option               mouse_speed_option;
    private final       Option               reset_option;
    // Gameplay options
    private final       Option               front_block_placing_option;
    // Controller options
    private final       Option               controller_option;
    private final       Option               second_controller_option;
    private final       Option               controller_type_option;
    private final       Option               dead_zone_option;
    private final       Option               inverts_right_x_axis;
    private final       Option               inverts_right_y_axis;
    // Hud options
    private final       Option               hud_enable_option;
    private final       Option               hud_side_option;
    private final       String               controller_mappings_url_text = I18n.translate("lambdacontrols.controller.mappings.2", Formatting.GOLD.toString(), GAMEPAD_TOOL_URL, Formatting.RESET.toString());
    private             ButtonListWidget     list;
    private             SpruceLabelWidget    gamepad_tool_url_label;

    public LambdaControlsSettingsScreen(Screen parent, @NotNull GameOptions options, boolean hide_controls)
    {
        super(new TranslatableText("lambdacontrols.title.settings"));
        this.mod = LambdaControlsClient.get();
        this.parent = parent;
        this.hide_controls = hide_controls;
        // General options
        this.auto_switch_mode_option = new SpruceBooleanOption("lambdacontrols.menu.auto_switch_mode", game_options -> this.mod.config.has_auto_switch_mode(),
                (game_options, new_value) -> this.mod.config.set_auto_switch_mode(new_value), new TranslatableText("lambdacontrols.tooltip.auto_switch_mode"));
        this.rotation_speed_option = new SpruceDoubleOption("lambdacontrols.menu.rotation_speed", 0.0, 50.0, 0.5F, game_options -> this.mod.config.get_rotation_speed(),
                (game_options, new_value) -> {
                    synchronized (this.mod.config) {
                        this.mod.config.set_rotation_speed(new_value);
                    }
                }, (game_options, option) -> option.getDisplayPrefix() + option.get(options),
                new TranslatableText("lambdacontrols.tooltip.rotation_speed"));
        this.mouse_speed_option = new SpruceDoubleOption("lambdacontrols.menu.mouse_speed", 0.0, 50.0, 0.5F, game_options -> this.mod.config.get_mouse_speed(),
                (game_options, new_value) -> {
                    synchronized (this.mod.config) {
                        this.mod.config.set_mouse_speed(new_value);
                    }
                }, (game_options, option) -> option.getDisplayPrefix() + option.get(options),
                new TranslatableText("lambdacontrols.tooltip.mouse_speed"));
        this.reset_option = new SpruceResetOption(btn -> {
            this.mod.config.reset();
            MinecraftClient client = MinecraftClient.getInstance();
            this.init(client, client.getWindow().getScaledWidth(), client.getWindow().getScaledHeight());
        });
        // Gameplay options
        this.front_block_placing_option = new SpruceBooleanOption("lambdacontrols.menu.front_block_placing", game_options -> this.mod.config.has_front_block_placing(),
                (game_options, new_value) -> this.mod.config.set_front_block_placing(new_value), new TranslatableText("lambdacontrols.tooltip.front_block_placing"));
        // Controller options
        this.controller_option = new CyclingOption("lambdacontrols.menu.controller", (game_options, amount) -> {
            int current_id = this.mod.config.get_controller().get_id();
            current_id += amount;
            if (current_id > GLFW.GLFW_JOYSTICK_LAST)
                current_id = GLFW.GLFW_JOYSTICK_1;
            this.mod.config.set_controller(Controller.by_id(current_id));
        }, (game_options, option) -> {
            String controller_name = this.mod.config.get_controller().get_name();
            if (!this.mod.config.get_controller().is_connected())
                return option.getDisplayPrefix() + Formatting.RED + controller_name;
            else if (!this.mod.config.get_controller().is_gamepad())
                return option.getDisplayPrefix() + Formatting.GOLD + controller_name;
            else
                return option.getDisplayPrefix() + controller_name;
        });
        this.second_controller_option = new SpruceCyclingOption("lambdacontrols.menu.controller2",
                (game_options, amount) -> {
                    int current_id = this.mod.config.get_second_controller().map(Controller::get_id).orElse(-1);
                    current_id += amount;
                    if (current_id > GLFW.GLFW_JOYSTICK_LAST)
                        current_id = -1;
                    this.mod.config.set_second_controller(current_id == -1 ? null : Controller.by_id(current_id));
                }, (game_options, option) -> this.mod.config.get_second_controller().map(controller -> {
            String controller_name = controller.get_name();
            if (!controller.is_connected())
                return option.getDisplayPrefix() + Formatting.RED + controller_name;
            else if (!controller.is_gamepad())
                return option.getDisplayPrefix() + Formatting.GOLD + controller_name;
            else
                return option.getDisplayPrefix() + controller_name;
        }).orElse(option.getDisplayPrefix() + Formatting.RED + I18n.translate("options.off")),
                new TranslatableText("lambdacontrols.tooltip.controller2"));
        this.controller_type_option = new SpruceCyclingOption("lambdacontrols.menu.controller_type",
                (game_options, amount) -> this.mod.config.set_controller_type(this.mod.config.get_controller_type().next()),
                (game_options, option) -> option.getDisplayPrefix() + this.mod.config.get_controller_type().get_translated_name(),
                new TranslatableText("lambdacontrols.tooltip.controller_type"));
        this.dead_zone_option = new SpruceDoubleOption("lambdacontrols.menu.dead_zone", 0.05, 1.0, 0.05F, game_options -> this.mod.config.get_dead_zone(),
                (game_options, new_value) -> {
                    synchronized (this.mod.config) {
                        this.mod.config.set_dead_zone(new_value);
                    }
                }, (game_options, option) -> {
            String value = String.valueOf(option.get(options));
            return option.getDisplayPrefix() + value.substring(0, Math.min(value.length(), 5));
        }, new TranslatableText("lambdacontrols.tooltip.dead_zone"));
        this.inverts_right_x_axis = new SpruceBooleanOption("lambdacontrols.menu.invert_right_x_axis", game_options -> this.mod.config.does_invert_right_x_axis(),
                (game_options, new_value) -> {
                    synchronized (this.mod.config) {
                        this.mod.config.set_invert_right_x_axis(new_value);
                    }
                }, null);
        this.inverts_right_y_axis = new SpruceBooleanOption("lambdacontrols.menu.invert_right_y_axis", game_options -> this.mod.config.does_invert_right_y_axis(),
                (game_options, new_value) -> {
                    synchronized (this.mod.config) {
                        this.mod.config.set_invert_right_y_axis(new_value);
                    }
                }, null);
        // HUD options
        this.hud_enable_option = new SpruceBooleanOption("lambdacontrols.menu.hud_enable", (game_options) -> this.mod.config.is_hud_enabled(),
                (game_options, new_value) -> this.mod.config.set_hud_enabled(new_value), new TranslatableText("lambdacontrols.tooltip.hud_enable"));
        this.hud_side_option = new SpruceCyclingOption("lambdacontrols.menu.hud_side",
                (game_options, amount) -> this.mod.config.set_hud_side(this.mod.config.get_hud_side().next()),
                (game_options, option) -> option.getDisplayPrefix() + this.mod.config.get_hud_side().get_translated_name(),
                new TranslatableText("lambdacontrols.tooltip.hud_side"));
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
        SpruceButtonWidget controls_mode_btn = new SpruceButtonWidget(this.width / 2 - 155, 18, this.hide_controls ? 310 : 150, button_height,
                I18n.translate("lambdacontrols.menu.controls_mode") + ": " + I18n.translate(this.mod.config.get_controls_mode().get_translation_key()),
                btn -> {
                    ControlsMode next = this.mod.config.get_controls_mode().next();
                    btn.setMessage(I18n.translate("lambdacontrols.menu.controls_mode") + ": " + I18n.translate(next.get_translation_key()));
                    this.mod.config.set_controls_mode(next);
                    this.mod.config.save();
                });
        controls_mode_btn.set_tooltip(new TranslatableText("lambdacontrols.tooltip.controls_mode"));
        this.addButton(controls_mode_btn);
        if (!this.hide_controls)
            this.addButton(new ButtonWidget(this.width / 2 - 155 + 160, 18, 150, button_height, I18n.translate("options.controls"),
                    btn -> {
                        if (this.mod.config.get_controls_mode() == ControlsMode.CONTROLLER)
                            this.minecraft.openScreen(new LambdaControlsControlsScreen(this, true));
                        else
                            this.minecraft.openScreen(new ControlsOptionsScreen(this, this.minecraft.options));
                    }));

        this.list = new ButtonListWidget(this.minecraft, this.width, this.height, 43, this.height - 29 - this.get_text_height(), 25);
        // General options
        this.list.addSingleOptionEntry(new SpruceSeparatorOption("lambdacontrols.menu.title.general", true, null));
        this.list.addOptionEntry(this.rotation_speed_option, this.mouse_speed_option);
        this.list.addSingleOptionEntry(this.auto_switch_mode_option);
        // Gameplay options
        this.list.addSingleOptionEntry(new SpruceSeparatorOption("lambdacontrols.menu.title.gameplay", true, null));
        this.list.addSingleOptionEntry(this.front_block_placing_option);
        // Controller options
        this.list.addSingleOptionEntry(new SpruceSeparatorOption("lambdacontrols.menu.title.controller", true, null));
        this.list.addSingleOptionEntry(this.controller_option);
        this.list.addSingleOptionEntry(this.second_controller_option);
        this.list.addOptionEntry(this.controller_type_option, this.dead_zone_option);
        this.list.addOptionEntry(this.inverts_right_x_axis, this.inverts_right_y_axis);
        this.list.addSingleOptionEntry(new ReloadControllerMappingsOption());
        // HUD options
        this.list.addSingleOptionEntry(new SpruceSeparatorOption("lambdacontrols.menu.title.hud", true, null));
        this.list.addOptionEntry(this.hud_enable_option, this.hud_side_option);
        this.children.add(this.list);

        this.gamepad_tool_url_label = new SpruceLabelWidget(this.width / 2, this.height - 29 - (5 + this.font.fontHeight) * 2, this.controller_mappings_url_text, this.width,
                label -> Util.getOperatingSystem().open(GAMEPAD_TOOL_URL), true);
        this.gamepad_tool_url_label.set_tooltip(new TranslatableText("chat.link.open"));
        this.children.add(this.gamepad_tool_url_label);

        this.addButton(this.reset_option.createButton(this.minecraft.options, this.width / 2 - 155, this.height - 29, 150));
        this.addButton(new ButtonWidget(this.width / 2 - 155 + 160, this.height - 29, 150, button_height, I18n.translate("gui.done"),
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
        this.gamepad_tool_url_label.render(mouse_x, mouse_y, delta);
        this.drawCenteredString(this.font, I18n.translate("lambdacontrols.controller.mappings.3", Formatting.GREEN.toString(), Formatting.RESET.toString()), this.width / 2, this.height - 29 - (5 + this.font.fontHeight), 10526880);

        Tooltip.render_all();
    }
}
