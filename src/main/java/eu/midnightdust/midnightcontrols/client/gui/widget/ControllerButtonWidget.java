/*
 * Copyright © 2021 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of midnightcontrols.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package eu.midnightdust.midnightcontrols.client.gui.widget;

import dev.lambdaurora.spruceui.Position;
import dev.lambdaurora.spruceui.SpruceTexts;
import dev.lambdaurora.spruceui.render.SpruceGuiGraphics;
import dev.lambdaurora.spruceui.widget.AbstractSpruceIconButtonWidget;
import eu.midnightdust.midnightcontrols.client.controller.ButtonBinding;
import eu.midnightdust.midnightcontrols.client.gui.MidnightControlsRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
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
                (length > 0 ? ButtonBinding.getLocalizedButtonName(binding.getButton()[0]) : Component.literal("<>")));
    }

    @Override
    public Component getMessage() {
        if (this.binding.getButton().length > 1)
            return Component.empty();
        return super.getMessage();
    }

    @Override
    protected int renderIcon(SpruceGuiGraphics spruceGuiGraphics, int mouseX, int mouseY, float delta) {
        int x = this.getX();
        if (this.binding.getButton().length > 1) {
            x += (this.width / 2 - this.iconWidth / 2) - 4;
        }
        var size = MidnightControlsRenderer.drawButton(spruceGuiGraphics.vanilla(), x, this.getY(), this.binding, Minecraft.getInstance());
        this.iconWidth = size.length();
        return size.height();
    }
}
