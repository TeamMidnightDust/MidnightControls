/*
 * Copyright © 2020 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of LambdaControls.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package me.lambdaurora.lambdacontrols.client.gui;

import me.lambdaurora.lambdacontrols.client.LambdaControlsClient;
import me.lambdaurora.lambdacontrols.client.controller.ButtonBinding;
import me.lambdaurora.lambdacontrols.client.controller.InputManager;
import me.lambdaurora.spruceui.SpruceButtonWidget;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.options.ControlsOptionsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.TranslatableText;
import org.aperlambda.lambdacommon.utils.function.Predicates;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the controls screen.
 */
public class ControllerControlsScreen extends Screen
{
    private final Screen               parent;
    final         LambdaControlsClient mod;
    private final boolean              hideSettings;
    private       ControlsListWidget   bindingsListWidget;
    private       ButtonWidget         resetButton;
    public        ButtonBinding        focusedBinding;
    public        boolean              waiting        = false;
    public        List<Integer>        currentButtons = new ArrayList<>();

    public ControllerControlsScreen(@NotNull Screen parent, boolean hideSettings)
    {
        super(new TranslatableText("lambdacontrols.menu.title.controller_controls"));
        this.parent = parent;
        this.mod = LambdaControlsClient.get();
        this.hideSettings = hideSettings;
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
        this.addButton(new SpruceButtonWidget(this.width / 2 - 155, 18, this.hideSettings ? 310 : 150, 20, I18n.translate("lambdacontrols.menu.keyboard_controls"),
                btn -> this.minecraft.openScreen(new ControlsOptionsScreen(this, this.minecraft.options))));
        if (!this.hideSettings)
            this.addButton(new SpruceButtonWidget(this.width / 2 - 155 + 160, 18, 150, 20, I18n.translate("menu.options"),
                    btn -> this.minecraft.openScreen(new LambdaControlsSettingsScreen(this, true))));
        this.bindingsListWidget = new ControlsListWidget(this, this.minecraft);
        this.children.add(this.bindingsListWidget);
        this.resetButton = this.addButton(new ButtonWidget(this.width / 2 - 155, this.height - 29, 150, 20, I18n.translate("controls.resetAll"),
                btn -> InputManager.streamBindings().forEach(binding -> this.mod.config.setButtonBinding(binding, binding.getDefaultButton()))));
        this.addButton(new ButtonWidget(this.width / 2 - 155 + 160, this.height - 29, 150, 20, I18n.translate("gui.done"),
                btn -> this.minecraft.openScreen(this.parent)));
    }

    @Override
    public void render(int mouseX, int mouseY, float delta)
    {
        this.renderBackground();
        this.bindingsListWidget.render(mouseX, mouseY, delta);
        this.drawCenteredString(this.font, this.title.asFormattedString(), this.width / 2, 8, 16777215);
        this.resetButton.active = InputManager.streamBindings().anyMatch(Predicates.not(ButtonBinding::isDefault));
        super.render(mouseX, mouseY, delta);
    }
}
