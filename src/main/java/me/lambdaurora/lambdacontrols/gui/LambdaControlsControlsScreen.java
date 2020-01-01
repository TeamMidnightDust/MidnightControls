/*
 * Copyright © 2020 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of LambdaControls.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package me.lambdaurora.lambdacontrols.gui;

import me.lambdaurora.lambdacontrols.LambdaControls;
import me.lambdaurora.lambdacontrols.controller.ButtonBinding;
import me.lambdaurora.lambdacontrols.controller.InputManager;
import me.lambdaurora.spruceui.SpruceButtonWidget;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.options.ControlsOptionsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.TranslatableText;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * Represents the controls screen.
 */
public class LambdaControlsControlsScreen extends Screen
{
    private final Screen             parent;
    final         LambdaControls     mod;
    private final boolean            hide_settings;
    private       ControlsListWidget bindings_list_widget;
    private       ButtonWidget       reset_button;
    public        ButtonBinding      focused_binding;
    public        boolean            waiting         = false;
    public        List<Integer>      current_buttons = new ArrayList<>();

    public LambdaControlsControlsScreen(@NotNull Screen parent, boolean hide_settings)
    {
        super(new TranslatableText("lambdacontrols.menu.title.controller_controls"));
        this.parent = parent;
        this.mod = LambdaControls.get();
        this.hide_settings = hide_settings;
    }

    @Override
    public void removed()
    {
        this.mod.config.save();
        super.removed();
    }

    @Override
    protected void init()
    {
        this.addButton(new SpruceButtonWidget(this.width / 2 - 155, 18, this.hide_settings ? 310 : 150, 20, I18n.translate("lambdacontrols.menu.keyboard_controls"),
                btn -> this.minecraft.openScreen(new ControlsOptionsScreen(this, this.minecraft.options))));
        if (!this.hide_settings)
            this.addButton(new SpruceButtonWidget(this.width / 2 - 155 + 160, 18, 150, 20, I18n.translate("menu.options"),
                    btn -> this.minecraft.openScreen(new LambdaControlsSettingsScreen(this, this.minecraft.options, true))));
        this.bindings_list_widget = new ControlsListWidget(this, this.minecraft);
        this.children.add(this.bindings_list_widget);
        this.reset_button = this.addButton(new ButtonWidget(this.width / 2 - 155, this.height - 29, 150, 20, I18n.translate("controls.resetAll"),
                btn -> InputManager.stream_bindings().forEach(binding -> this.mod.config.set_button_binding(binding, binding.get_default_button()))));
        this.addButton(new ButtonWidget(this.width / 2 - 155 + 160, this.height - 29, 150, 20, I18n.translate("gui.done"),
                btn -> this.minecraft.openScreen(this.parent)));
    }

    // Replacement for Predicate#not as it is Java 11.
    private <T> Predicate<T> not(Predicate<T> target)
    {
        Objects.requireNonNull(target);
        return target.negate();
    }

    @Override
    public void render(int mouse_x, int mouse_y, float delta)
    {
        this.renderBackground();
        this.bindings_list_widget.render(mouse_x, mouse_y, delta);
        this.drawCenteredString(this.font, this.title.asFormattedString(), this.width / 2, 8, 16777215);
        this.reset_button.active = InputManager.stream_bindings().anyMatch(this.not(ButtonBinding::is_default));
        super.render(mouse_x, mouse_y, delta);
    }
}
