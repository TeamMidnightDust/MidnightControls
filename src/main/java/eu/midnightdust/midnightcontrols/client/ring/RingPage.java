/*
 * Copyright © 2021 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of midnightcontrols.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package eu.midnightdust.midnightcontrols.client.ring;

import eu.midnightdust.midnightcontrols.client.MidnightControlsClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a ring page.
 *
 * @author LambdAurora
 * @version 1.5.0
 * @since 1.4.0
 */
public class RingPage {
    public static final RingPage DEFAULT = new RingPage("Default");

    public final String name;
    public static int selected = -1;
    public RingAction[] actions = new RingAction[8];

    public RingPage(@NotNull String name) {
        this.name = name;
        for (int i = 0; i < 8; i++) {
            this.actions[i] = null;
        }
    }

    /**
     * Renders the ring page.
     *
     * @param context the draw context
     * @param width the screen width
     * @param height the screen height
     * @param mouseX the mouse X-coordinate
     * @param mouseY the mouse Y-coordinate
     * @param tickDelta the tick delta
     */
    public void render(@NotNull DrawContext context, @NotNull TextRenderer textRenderer, int width, int height, int mouseX, int mouseY, float tickDelta) {
        int centerX = width / 2;
        int centerY = height / 2;
        if (MidnightControlsClient.get().ring.getMaxPages() > 1) context.drawCenteredTextWithShadow(textRenderer, name, centerX, 5, 0xffffff);

        int offset = MidnightRing.ELEMENT_SIZE + (MidnightRing.ELEMENT_SIZE / 2) + 5;

        int y = centerY - offset;
        int x = centerX - offset;
        for (int i = 0; i < 3; i++) {
            var ringAction = this.actions[i];
            if (ringAction != null)
                ringAction.render(context, textRenderer, x, y, isHovered(x, y, mouseX, mouseY), i);
            x += MidnightRing.ELEMENT_SIZE + 5;
        }
        y += MidnightRing.ELEMENT_SIZE + 5;
        x = centerX - offset;
        for (int i = 3; i < 5; i++) {
            var ringAction = this.actions[i];
            if (ringAction != null)
                ringAction.render(context, textRenderer, x, y, isHovered(x, y, mouseX, mouseY), i);
            x += (MidnightRing.ELEMENT_SIZE + 5) * 2;
        }
        y += MidnightRing.ELEMENT_SIZE + 5;
        x = centerX - offset;
        for (int i = 5; i < 8; i++) {
            var ringAction = this.actions[i];
            if (ringAction != null)
                ringAction.render(context, textRenderer, x, y, isHovered(x, y, mouseX, mouseY), i);
            x += MidnightRing.ELEMENT_SIZE + 5;
        }
    }

    private static boolean isHovered(int x, int y, int mouseX, int mouseY) {
        return mouseX >= x && mouseY >= y && mouseX <= x + MidnightRing.ELEMENT_SIZE && mouseY <= y + MidnightRing.ELEMENT_SIZE && selected < 0;
    }
    /**
     * Renders the ring page.
     *
     * @param width the screen width
     * @param height the screen height
     * @param mouseX the mouse X-coordinate
     * @param mouseY the mouse Y-coordinate
     */
    public boolean onClick(int width, int height, int mouseX, int mouseY) {
        int centerX = width / 2;
        int centerY = height / 2;

        int offset = MidnightRing.ELEMENT_SIZE + (MidnightRing.ELEMENT_SIZE / 2) + 5;

        int y = centerY - offset;
        int x = centerX - offset;
        for (int i = 0; i < 3; i++) {
            var ringAction = this.actions[i];
            if (ringAction != null && isHovered(x,y,mouseX,mouseY)) {
                ringAction.activate(RingButtonMode.PRESS);
                return true;
            }
            x += MidnightRing.ELEMENT_SIZE + 5;
        }
        y += MidnightRing.ELEMENT_SIZE + 5;
        x = centerX - offset;
        for (int i = 3; i < 5; i++) {
            var ringAction = this.actions[i];
            if (ringAction != null && isHovered(x,y,mouseX,mouseY)) {
                ringAction.activate(RingButtonMode.PRESS);
                return true;
            }
            x += (MidnightRing.ELEMENT_SIZE + 5) * 2;
        }
        y += MidnightRing.ELEMENT_SIZE + 5;
        x = centerX - offset;
        for (int i = 5; i < 8; i++) {
            var ringAction = this.actions[i];
            if (ringAction != null && isHovered(x,y,mouseX,mouseY)) {
                ringAction.activate(RingButtonMode.PRESS);
                return true;
            }
            x += MidnightRing.ELEMENT_SIZE + 5;
        }
        return false;
    }
}
