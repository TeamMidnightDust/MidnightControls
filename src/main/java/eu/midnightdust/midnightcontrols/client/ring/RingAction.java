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
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
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
public abstract class RingAction extends DrawableHelper implements Nameable {
    protected Config config;
    protected boolean activated = false;

    public RingAction(@NotNull Config config) {
        this.config = config;
    }

    /**
     * Gets the text name of the ring action.
     *
     * @return the text name
     */
    public Text getTextName() {
        return Text.translatable(this.getName());
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

    public void render(@NotNull MatrixStack matrices, @NotNull TextRenderer textRenderer, int x, int y, boolean hovered) {
        fill(matrices, x, y, x + MidnightRing.ELEMENT_SIZE, y + MidnightRing.ELEMENT_SIZE, hovered ? 0xbb777777 : 0xbb000000);
        drawIcon(matrices, textRenderer, x, y, hovered);
    }

    public abstract void drawIcon(@NotNull MatrixStack matrices, @NotNull TextRenderer textRenderer, int x, int y, boolean hovered);

    /**
     * Represents a factory for {@link RingAction}.
     *
     * @version 1.4.3
     * @since 1.4.3
     */
    public interface Factory {
        @NotNull Supplier<RingAction> newFromGui(@NotNull Screen screen);

        @Nullable RingAction parse(@NotNull Config config);
    }
}
