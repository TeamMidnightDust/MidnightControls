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
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.aperlambda.lambdacommon.utils.Nameable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

/**
 * Represents a ring action.
 *
 * @author LambdAurora
 * @version 1.5.0
 * @since 1.4.0
 */
public abstract class RingAction {
    protected boolean activated = false;

    public RingAction() {
    }

    /**
     * Returns whether the action is activated or not.
     *
     * @return true if the action is activated, else false
     */
    public boolean isActivated() {
        return this.activated;
    }

    public void activate(@NotNull RingButtonMode mode) {
        this.activated = !this.activated;

        this.onAction(mode);
    }

    public abstract void onAction(@NotNull RingButtonMode mode);

    public void render(@NotNull DrawContext context, @NotNull TextRenderer textRenderer, int x, int y, boolean hovered, int index) {
        context.fill(x, y, x + MidnightRing.ELEMENT_SIZE, y + MidnightRing.ELEMENT_SIZE, hovered || RingPage.selected == index ? 0xbb777777 : 0xbb000000);
        drawIcon(context, textRenderer, x, y, hovered);
    }

    public abstract void drawIcon(@NotNull DrawContext context, @NotNull TextRenderer textRenderer, int x, int y, boolean hovered);

    /**
     * Represents a factory for {@link RingAction}.
     *
     * @version 1.4.3
     * @since 1.4.3
     */
    public interface Factory {
        @NotNull Supplier<RingAction> newFromGui(@NotNull Screen screen);

        @Nullable RingAction parse(@NotNull Gson config);
    }
}
