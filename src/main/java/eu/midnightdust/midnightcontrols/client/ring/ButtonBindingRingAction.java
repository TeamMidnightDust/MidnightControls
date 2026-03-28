/*
 * Copyright © 2021 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of midnightcontrols.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package eu.midnightdust.midnightcontrols.client.ring;

import com.google.gson.Gson;
import eu.midnightdust.midnightcontrols.client.enums.ButtonState;
import eu.midnightdust.midnightcontrols.client.controller.ButtonBinding;
import eu.midnightdust.midnightcontrols.client.util.KeyBindingAccessor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

public class ButtonBindingRingAction extends RingAction {
    public static final eu.midnightdust.midnightcontrols.client.ring.ButtonBindingRingAction.Factory FACTORY = new eu.midnightdust.midnightcontrols.client.ring.ButtonBindingRingAction.Factory();
    public final ButtonBinding binding;

    public ButtonBindingRingAction(@NotNull ButtonBinding binding) {
        super();
        this.binding = binding;
    }

    public @NotNull String getName() {
        return this.binding.getTranslationKey();
    }

    @Override
    public void onAction(@NotNull RingButtonMode mode) {
        binding.handle(Minecraft.getInstance(), 1.0f, ButtonState.PRESS);
        if (binding.asKeyBinding().isPresent()) {
            binding.asKeyBinding().get().setDown(true);
            ((KeyBindingAccessor)binding.asKeyBinding().get()).midnightcontrols$press();
        }
    }

    @Override
    //~ if >= 26.1 'GuiGraphics' -> 'GuiGraphicsExtractor'
    public void drawIcon(@NotNull GuiGraphicsExtractor context, @NotNull Font textRenderer, int x, int y, boolean hovered) {
        List<FormattedCharSequence> lines = textRenderer.split(Component.translatable(this.getName()), MidnightRing.ELEMENT_SIZE);
        for (int i = 0; i < lines.size(); ++i) {
            context.centeredText(textRenderer, lines.get(i), x + MidnightRing.ELEMENT_SIZE / 2, y + MidnightRing.ELEMENT_SIZE / 2 - textRenderer.lineHeight / 2 * (lines.size()-1) - textRenderer.lineHeight / 2 + textRenderer.lineHeight * i, 0xffffff);
        }
    }

    protected static class Factory implements RingAction.Factory {
        @Override
        public @NotNull Supplier<RingAction> newFromGui(@NotNull Screen screen) {
            return () -> null;
        }

        @Override
        public @Nullable RingAction parse(@NotNull Gson config) {
            return null;
        }
    }
}
