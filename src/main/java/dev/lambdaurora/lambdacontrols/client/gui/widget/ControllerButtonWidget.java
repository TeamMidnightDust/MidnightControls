/*
 * Copyright © 2021 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of LambdaControls.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package dev.lambdaurora.lambdacontrols.client.gui.widget;

import dev.lambdaurora.lambdacontrols.client.controller.ButtonBinding;
import dev.lambdaurora.lambdacontrols.client.gui.LambdaControlsRenderer;
import me.lambdaurora.spruceui.Position;
import me.lambdaurora.spruceui.SpruceTexts;
import me.lambdaurora.spruceui.widget.AbstractSpruceIconButtonWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import org.aperlambda.lambdacommon.utils.Pair;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a controller button widget.
 */
public class ControllerButtonWidget extends AbstractSpruceIconButtonWidget {
    private ButtonBinding binding;
    private int iconWidth;

    public ControllerButtonWidget(Position position, int width, @NotNull ButtonBinding binding, @NotNull PressAction action) {
        super(position, width, 20, ButtonBinding.getLocalizedButtonName(binding.getButton()[0]), action);
        this.binding = binding;
    }

    public void update() {
        int length = binding.getButton().length;
        this.setMessage(this.binding.isNotBound() ? SpruceTexts.NOT_BOUND.copy() :
                (length > 0 ? ButtonBinding.getLocalizedButtonName(binding.getButton()[0]) : new LiteralText("<>")));
    }

    @Override
    public Text getMessage() {
        if (this.binding.getButton().length > 1)
            return LiteralText.EMPTY;
        return super.getMessage();
    }

    @Override
    protected int renderIcon(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        int x = this.getX();
        if (this.binding.getButton().length > 1) {
            x += (this.width / 2 - this.iconWidth / 2) - 4;
        }
        Pair<Integer, Integer> size = LambdaControlsRenderer.drawButton(matrices, x, this.getY(), this.binding, MinecraftClient.getInstance());
        this.iconWidth = size.key;
        return size.value;
    }
}
