/*
 * Copyright © 2021 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of midnightcontrols.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package eu.midnightdust.midnightcontrols.client.gui;

import eu.midnightdust.midnightcontrols.client.MidnightControlsClient;
import eu.midnightdust.midnightcontrols.client.ring.RingPage;
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
    protected final MidnightControlsClient mod;

    public RingScreen() {
        super(new TranslatableText("midnightcontrols.menu.title.ring"));
        this.mod = MidnightControlsClient.get();
    }

    @Override
    public boolean shouldPause() {
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
        /*if (midnightcontrolsClient.BINDING_RING.matchesMouse(button)) {
            this.onClose();
            return true;
        }*/
        return false;
    }
}
