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
import eu.midnightdust.midnightcontrols.client.ButtonState;
import eu.midnightdust.midnightcontrols.client.controller.ButtonBinding;
import eu.midnightdust.midnightcontrols.client.util.KeyBindingAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.option.StickyKeyBinding;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

public class ButtonBindingRingAction extends RingAction {
    public static final Factory FACTORY = new Factory();
    public final ButtonBinding binding;

    public ButtonBindingRingAction(@NotNull ButtonBinding binding) {
        super();
        this.binding = binding;
    }

    @Override
    public @NotNull String getName() {
        return this.binding.getTranslationKey();
    }

    @Override
    public void onAction(@NotNull RingButtonMode mode) {
        binding.handle(MinecraftClient.getInstance(), 1.0f, ButtonState.PRESS);
        if (binding.asKeyBinding().isPresent()) {
            binding.asKeyBinding().get().setPressed(true);
            ((KeyBindingAccessor)binding.asKeyBinding().get()).midnightcontrols$press();
        }
    }

    @Override
    public void drawIcon(@NotNull DrawContext context, @NotNull TextRenderer textRenderer, int x, int y, boolean hovered) {
        List<OrderedText> lines = textRenderer.wrapLines(Text.translatable(this.getName()), MidnightRing.ELEMENT_SIZE);
        for (int i = 0; i < lines.size(); ++i) {
            context.drawCenteredTextWithShadow(textRenderer, lines.get(i), x + MidnightRing.ELEMENT_SIZE / 2, y + MidnightRing.ELEMENT_SIZE / 2 - textRenderer.fontHeight / 2 * (lines.size()-1) - textRenderer.fontHeight / 2 + textRenderer.fontHeight * i, 0xffffff);
        }
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
