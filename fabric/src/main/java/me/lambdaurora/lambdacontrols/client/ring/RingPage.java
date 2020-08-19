/*
 * Copyright © 2020 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of LambdaControls.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package me.lambdaurora.lambdacontrols.client.ring;

import me.lambdaurora.lambdacontrols.client.LambdaControlsClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a ring page.
 *
 * @author LambdAurora
 * @version 1.4.3
 * @since 1.4.0
 */
public class RingPage extends DrawableHelper
{
    private RingAction[] actions = new RingAction[8];

    public RingPage()
    {
        for (int i = 0; i < 8; i++) {
            this.actions[i] = new DummyRingAction();
        }
        this.actions[1] = new KeyBindingRingAction(LambdaControlsClient.BINDING_LOOK_UP);
        this.actions[3] = new KeyBindingRingAction(LambdaControlsClient.BINDING_LOOK_LEFT);
        this.actions[4] = new KeyBindingRingAction(LambdaControlsClient.BINDING_LOOK_RIGHT);
        this.actions[6] = new KeyBindingRingAction(LambdaControlsClient.BINDING_LOOK_DOWN);
    }

    /**
     * Renders the ring page.
     *
     * @param matrices  The matrices.
     * @param width     The screen width.
     * @param height    The screen height.
     * @param mouseX    The mouse X-coordinate.
     * @param mouseY    The mouse Y-coordinate.
     * @param tickDelta The tick delta.
     */
    public void render(@NotNull MatrixStack matrices, @NotNull TextRenderer textRenderer, int width, int height, int mouseX, int mouseY, float tickDelta)
    {
        int centerX = width / 2;
        int centerY = height / 2;

        int offset = LambdaRing.ELEMENT_SIZE + (LambdaRing.ELEMENT_SIZE / 2) + 5;

        int y = centerY - offset;
        int x = centerX - offset;
        for (int i = 0; i < 3; i++) {
            RingAction ringAction = this.actions[i];
            if (ringAction != null)
                ringAction.render(matrices, textRenderer, x, y, isHovered(x, y, mouseX, mouseY));
            x += 55;
        }
        y += 55;
        x = centerX - offset;
        for (int i = 3; i < 5; i++) {
            RingAction ringAction = this.actions[i];
            if (ringAction != null)
                ringAction.render(matrices, textRenderer, x, y, isHovered(x, y, mouseX, mouseY));
            x += 55 * 2;
        }
        y += 55;
        x = centerX - offset;
        for (int i = 5; i < 8; i++) {
            RingAction ringAction = this.actions[i];
            if (ringAction != null)
                ringAction.render(matrices, textRenderer, x, y, isHovered(x, y, mouseX, mouseY));
            x += 55;
        }
    }

    private static boolean isHovered(int x, int y, int mouseX, int mouseY)
    {
        return mouseX >= x && mouseY >= y && mouseX <= x + LambdaRing.ELEMENT_SIZE && mouseY <= y + LambdaRing.ELEMENT_SIZE;
    }
}
