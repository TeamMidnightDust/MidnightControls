/*
 * Copyright © 2020 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of LambdaControls.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package me.lambdaurora.lambdacontrols.client.gui;

import me.lambdaurora.lambdacontrols.LambdaControls;
import me.lambdaurora.lambdacontrols.client.controller.ButtonBinding;
import me.lambdaurora.spruceui.AbstractIconButtonWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import org.aperlambda.lambdacommon.utils.Pair;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a controller button widget.
 */
public class ControllerButtonWidget extends AbstractIconButtonWidget
{
    private ButtonBinding binding;
    private int           iconWidth;

    public ControllerButtonWidget(int x, int y, int width, @NotNull ButtonBinding binding, @NotNull PressAction action)
    {
        super(x, y, width, 20, ButtonBinding.getLocalizedButtonName(binding.getButton()[0]), action);
        this.binding = binding;
    }

    public void update()
    {
        int length = binding.getButton().length;
        this.setMessage(this.binding.isNotBound() ? LambdaControls.NOT_BOUND_TEXT :
                (length > 0 ? ButtonBinding.getLocalizedButtonName(binding.getButton()[0]) : new LiteralText("<>")));
    }

    @Override
    public Text getMessage()
    {
        if (this.binding.getButton().length > 1)
            return LiteralText.EMPTY;
        return super.getMessage();
    }

    @Override
    protected int renderIcon(MatrixStack matrices, int mouseX, int mouseY, float delta, int x, int y)
    {
        if (this.binding.getButton().length > 1) {
            x += (this.width / 2 - this.iconWidth / 2) - 4;
        }
        Pair<Integer, Integer> size = LambdaControlsRenderer.drawButton(matrices, x, y, this.binding, MinecraftClient.getInstance());
        this.iconWidth = size.key;
        return size.value;
    }
}
