/*
 * Copyright © 2021 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of midnightcontrols.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package eu.midnightdust.midnightcontrols.client.ring;

import com.electronwill.nightconfig.core.Config;
import eu.midnightdust.midnightcontrols.client.util.KeyBindingAccessor;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class KeyBindingRingAction extends RingAction {
    public static final Factory FACTORY = new Factory();
    public final KeyBinding binding;

    public KeyBindingRingAction(@NotNull Config config, @NotNull KeyBinding binding) {
        super(config);
        this.binding = binding;
    }

    @Override
    public @NotNull String getName() {
        return this.binding.getTranslationKey();
    }

    @Override
    public void onAction(@NotNull RingButtonMode mode) {
        KeyBindingAccessor accessor = (KeyBindingAccessor) this.binding;
        switch (mode) {
            case PRESS, HOLD -> accessor.midnightcontrols$handlePressState(this.activated);
            case TOGGLE -> {
                accessor.midnightcontrols$handlePressState(!this.binding.isPressed());
                this.activated = !this.binding.isPressed();
            }
        }
    }

    @Override
    public void drawIcon(@NotNull MatrixStack matrices, @NotNull TextRenderer textRenderer, int x, int y, boolean hovered) {
        drawCenteredText(matrices, textRenderer, new TranslatableText(this.getName()), x + 25, y + 25 - textRenderer.fontHeight / 2, 0xffffff);
    }

    protected static class Factory implements RingAction.Factory {
        @Override
        public @NotNull Supplier<RingAction> newFromGui(@NotNull Screen screen) {
            return () -> null;
        }

        @Override
        public @Nullable RingAction parse(@NotNull Config config) {
            return null;
        }
    }
}
