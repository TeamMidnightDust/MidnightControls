/*
 * Copyright © 2019 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of LambdaControls.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package me.lambdaurora.lambdacontrols.gui;

import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

/**
 * Represents a touchscreen button widget.
 */
public class TouchscreenButtonWidget extends TexturedButtonWidget
{
    private final Consumer<Boolean> on_change_state;

    public TouchscreenButtonWidget(int x, int y, int width, int height, int u, int v, int hovered_v_offset, Identifier texture, @NotNull Consumer<Boolean> on_changed_state)
    {
        super(x, y, width, height, u, v, hovered_v_offset, texture, 256, 256, btn -> on_changed_state.accept(true));
        this.on_change_state = on_changed_state;
    }

    @Override
    public void onRelease(double mouseX, double mouseY)
    {
        super.onRelease(mouseX, mouseY);
        this.on_change_state.accept(false);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        if (this.active && this.visible) {
            if (this.isValidClickButton(button)) {
                boolean clicked = this.clicked(mouseX, mouseY);
                if (clicked) {
                    this.onClick(mouseX, mouseY);
                    return true;
                }
            }

            return false;
        } else
            return false;
    }

    @Override
    protected void onDrag(double mouseX, double mouseY, double deltaX, double deltaY)
    {
        super.onDrag(mouseX, mouseY, deltaX, deltaY);
        if (this.active && !this.isHovered)
            this.on_change_state.accept(false);
    }
}
