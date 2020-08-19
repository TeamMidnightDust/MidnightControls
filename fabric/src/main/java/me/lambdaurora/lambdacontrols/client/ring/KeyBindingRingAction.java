/*
 * Copyright © 2020 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of LambdaControls.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package me.lambdaurora.lambdacontrols.client.ring;

import me.lambdaurora.lambdacontrols.client.util.KeyBindingAccessor;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;
import org.jetbrains.annotations.NotNull;

public class KeyBindingRingAction extends RingAction
{
    public final KeyBinding binding;

    public KeyBindingRingAction(@NotNull KeyBinding binding)
    {
        this.binding = binding;
    }

    @Override
    public @NotNull String getName()
    {
        return this.binding.getTranslationKey();
    }

    @Override
    public void onAction(@NotNull RingButtonMode mode)
    {
        KeyBindingAccessor accessor = (KeyBindingAccessor) this.binding;
        switch (mode) {
            case PRESS:
            case HOLD:
                accessor.lambdacontrols_handlePressState(this.activated);
                break;
            case TOGGLE:
                accessor.lambdacontrols_handlePressState(!this.binding.isPressed());
                this.activated = !this.binding.isPressed();
                break;
        }
    }

    @Override
    public void drawIcon(@NotNull MatrixStack matrices, @NotNull TextRenderer textRenderer, int x, int y, boolean hovered)
    {
        drawCenteredText(matrices, textRenderer, new TranslatableText(this.getName()), x + 25, y + 25 - textRenderer.fontHeight / 2, 0xffffff);
    }
}
