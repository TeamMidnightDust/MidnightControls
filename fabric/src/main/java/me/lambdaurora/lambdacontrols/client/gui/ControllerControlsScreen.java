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
import me.lambdaurora.spruceui.SpruceTexts;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.options.ControlsOptionsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;
import org.aperlambda.lambdacommon.utils.function.Predicates;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
        this.addButton(new SpruceButtonWidget(this.width / 2 - 155, 18, this.hideSettings ? 310 : 150, 20,
                new TranslatableText("lambdacontrols.menu.keyboard_controls"),
                btn -> this.client.openScreen(new ControlsOptionsScreen(this, this.client.options))));
        if (!this.hideSettings)
            this.addButton(new SpruceButtonWidget(this.width / 2 - 155 + 160, 18, 150, 20,
                    SpruceTexts.MENU_OPTIONS,
                    btn -> this.client.openScreen(new LambdaControlsSettingsScreen(this, true))));
        this.bindingsListWidget = new ControlsListWidget(this, this.client);
        this.children.add(this.bindingsListWidget);
        this.resetButton = this.addButton(new ButtonWidget(this.width / 2 - 155, this.height - 29, 150, 20,
                SpruceTexts.CONTROLS_RESET_ALL,
                btn -> InputManager.streamBindings().collect(Collectors.toSet()).forEach(binding -> this.mod.config.setButtonBinding(binding, binding.getDefaultButton()))));
        this.addButton(new ButtonWidget(this.width / 2 - 155 + 160, this.height - 29, 150, 20,
                SpruceTexts.GUI_DONE,
                btn -> this.client.openScreen(this.parent)));
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta)
    {
        this.renderBackground(matrices);
        this.bindingsListWidget.render(matrices, mouseX, mouseY, delta);
        drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 8, 16777215);
        this.resetButton.active = InputManager.streamBindings().anyMatch(Predicates.not(ButtonBinding::isDefault));
        super.render(matrices, mouseX, mouseY, delta);
    }
}
