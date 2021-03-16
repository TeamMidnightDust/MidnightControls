/*
 * Copyright © 2020 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of LambdaControls.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package dev.lambdaurora.lambdacontrols.client.ring;

import com.electronwill.nightconfig.core.Config;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.jetbrains.annotations.NotNull;

public class DummyRingAction extends RingAction {
    public DummyRingAction(@NotNull Config config) {
        super(config);
    }

    @Override
    public @NotNull String getName() {
        return "dummy";
    }

    @Override
    public void onAction(@NotNull RingButtonMode mode) {

    }

    @Override
    public void drawIcon(@NotNull MatrixStack matrices, @NotNull TextRenderer textRenderer, int x, int y, boolean hovered) {
        drawCenteredString(matrices, textRenderer, this.getName(), x + 25, y + 25 - textRenderer.fontHeight / 2, 0xffffff);
    }
}
