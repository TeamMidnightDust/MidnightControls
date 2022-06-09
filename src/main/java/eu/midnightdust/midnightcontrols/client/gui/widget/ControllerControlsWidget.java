/*
 * Copyright © 2021 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of midnightcontrols.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package eu.midnightdust.midnightcontrols.client.gui.widget;

import eu.midnightdust.midnightcontrols.client.MidnightControlsClient;
import eu.midnightdust.midnightcontrols.client.MidnightControlsConfig;
import eu.midnightdust.midnightcontrols.client.controller.ButtonBinding;
import eu.midnightdust.midnightcontrols.client.controller.InputManager;
import dev.lambdaurora.spruceui.Position;
import dev.lambdaurora.spruceui.SpruceTexts;
import dev.lambdaurora.spruceui.widget.SpruceButtonWidget;
import dev.lambdaurora.spruceui.widget.container.SpruceContainerWidget;
import net.minecraft.client.gui.screen.option.ControlsOptionsScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import org.aperlambda.lambdacommon.utils.function.Predicates;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents the controls screen.
 */
public class ControllerControlsWidget extends SpruceContainerWidget {
    final MidnightControlsClient mod;
    private ControlsListWidget bindingsListWidget;
    private SpruceButtonWidget resetButton;
    public ButtonBinding focusedBinding;
    public boolean waiting = false;
    public List<Integer> currentButtons = new ArrayList<>();

    public ControllerControlsWidget(Position position, int width, int height) {
        super(position, width, height);
        this.mod = MidnightControlsClient.get();

        this.init();
    }

    protected void init() {
        this.addChild(new SpruceButtonWidget(Position.of(this, this.width / 2 - 155, 18), 310, 20,
                Text.translatable("midnightcontrols.menu.keyboard_controls"),
                btn -> this.client.setScreen(new ControlsOptionsScreen(null, this.client.options))));
        this.bindingsListWidget = new ControlsListWidget(Position.of(this, 0, 43), this.width, this.height - 43 - 35, this);
        this.addChild(this.bindingsListWidget);
        this.addChild(this.resetButton = new SpruceButtonWidget(Position.of(this, this.width / 2 - 155, this.height - 29), 150, 20,
                SpruceTexts.CONTROLS_RESET_ALL,
                btn -> InputManager.streamBindings().collect(Collectors.toSet()).forEach(binding -> MidnightControlsConfig.setButtonBinding(binding, binding.getDefaultButton()))));
    }

    @Override
    public void renderWidget(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        drawCenteredText(matrices, this.client.textRenderer, Text.translatable("midnightcontrols.menu.title.controller_controls"),
                this.getX() + this.width / 2, this.getY() + 4, 16777215);
        this.resetButton.setActive(InputManager.streamBindings().anyMatch(Predicates.not(ButtonBinding::isDefault)));
        super.renderWidget(matrices, mouseX, mouseY, delta);
    }

    public void finishBindingEdit(int... buttons) {
        if (this.focusedBinding == null) return;
        MidnightControlsConfig.setButtonBinding(this.focusedBinding, buttons);
        this.focusedBinding = null;
    }
}
