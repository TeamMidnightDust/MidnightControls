/*
 * Copyright © 2020 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of LambdaControls.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package me.lambdaurora.lambdacontrols.client.gui.widget;

import me.lambdaurora.lambdacontrols.client.LambdaControlsClient;
import me.lambdaurora.lambdacontrols.client.controller.ButtonBinding;
import me.lambdaurora.lambdacontrols.client.controller.InputManager;
import me.lambdaurora.spruceui.Position;
import me.lambdaurora.spruceui.SpruceTexts;
import me.lambdaurora.spruceui.widget.SpruceButtonWidget;
import me.lambdaurora.spruceui.widget.container.SpruceContainerWidget;
import net.minecraft.client.gui.screen.options.ControlsOptionsScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;
import org.aperlambda.lambdacommon.utils.function.Predicates;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents the controls screen.
 */
public class ControllerControlsWidget extends SpruceContainerWidget {
    final LambdaControlsClient mod;
    private ControlsListWidget bindingsListWidget;
    private SpruceButtonWidget resetButton;
    public ButtonBinding focusedBinding;
    public boolean waiting = false;
    public List<Integer> currentButtons = new ArrayList<>();

    public ControllerControlsWidget(Position position, int width, int height) {
        super(position, width, height);
        this.mod = LambdaControlsClient.get();

        this.init();
    }

    protected void init() {
        this.addChild(new SpruceButtonWidget(Position.of(this, this.width / 2 - 155, 18), 310, 20,
                new TranslatableText("lambdacontrols.menu.keyboard_controls"),
                btn -> this.client.openScreen(new ControlsOptionsScreen(null, this.client.options))));
        this.bindingsListWidget = new ControlsListWidget(Position.of(this, 0, 43), this.width, this.height - 43 - 43, this);
        this.addChild(this.bindingsListWidget);
        this.addChild(this.resetButton = new SpruceButtonWidget(Position.of(this, this.width / 2 - 155, this.height - 29), 150, 20,
                SpruceTexts.CONTROLS_RESET_ALL,
                btn -> InputManager.streamBindings().collect(Collectors.toSet()).forEach(binding -> this.mod.config.setButtonBinding(binding, binding.getDefaultButton()))));
    }

    @Override
    public void renderWidget(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        drawCenteredText(matrices, this.client.textRenderer, new TranslatableText("lambdacontrols.menu.title.controller_controls"), this.width / 2, 8, 16777215);
        this.resetButton.setActive(InputManager.streamBindings().anyMatch(Predicates.not(ButtonBinding::isDefault)));
        super.renderWidget(matrices, mouseX, mouseY, delta);
    }
}
