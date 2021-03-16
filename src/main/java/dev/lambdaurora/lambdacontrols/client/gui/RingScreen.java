/*
 * Copyright © 2020 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of LambdaControls.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package dev.lambdaurora.lambdacontrols.client.gui;

import dev.lambdaurora.lambdacontrols.client.LambdaControlsClient;
import dev.lambdaurora.lambdacontrols.client.ring.RingPage;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;

/**
 * Represents the controls ring screen.
 *
 * @author LambdAurora
 * @version 1.4.3
 * @since 1.4.3
 */
public class RingScreen extends Screen {
    protected final LambdaControlsClient mod;

    public RingScreen() {
        super(new TranslatableText("lambdacontrols.menu.title.ring"));
        this.mod = LambdaControlsClient.get();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        super.render(matrices, mouseX, mouseY, delta);

        RingPage page = this.mod.ring.getCurrentPage();

        page.render(matrices, this.textRenderer, this.width, this.height, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        /*if (LambdaControlsClient.BINDING_RING.matchesMouse(button)) {
            this.onClose();
            return true;
        }*/
        return false;
    }
}
