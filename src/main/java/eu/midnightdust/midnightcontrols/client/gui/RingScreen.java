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
import eu.midnightdust.midnightcontrols.client.ring.RingButtonMode;
import eu.midnightdust.midnightcontrols.client.ring.RingPage;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

import static eu.midnightdust.midnightcontrols.client.MidnightControlsClient.ring;

/**
 * Represents the controls ring screen.
 *
 * @author LambdAurora
 * @version 1.4.3
 * @since 1.4.3
 */
public class RingScreen extends Screen {

    public RingScreen() {
        super(Component.literal("midnightcontrols.menu.title.ring"));
    }

    @Override
    protected void init() {
        super.init();
        if (ring.getMaxPages() > 1) {
            this.addRenderableWidget(Button.builder(Component.nullToEmpty("◀"), button -> ring.cyclePage(false)).bounds(5, 5, 20, 20).build());
            this.addRenderableWidget(Button.builder(Component.nullToEmpty("▶"), button -> ring.cyclePage(true)).bounds(width - 25, 5, 20, 20).build());
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta) {
        super.extractRenderState(context, mouseX, mouseY, delta);

        RingPage page = ring.getCurrentPage();
        page.extractRenderState(context, this.font, this.width, this.height, mouseX, mouseY, delta);
    }

    @Override
    public void onClose() {
        super.onClose();
        assert minecraft != null;
        minecraft.screen = null;
        RingPage page = ring.getCurrentPage();
        if (RingPage.selected >= 0 && page.actions[RingPage.selected] != null)
            page.actions[RingPage.selected].activate(RingButtonMode.PRESS);
        RingPage.selected = -1;
        this.removed();
    }
//    @Override
//    public boolean changeFocus(boolean lookForwards) {
//        if (lookForwards) {
//            if (RingPage.selected < 7) ++RingPage.selected;
//            else RingPage.selected = -1;
//        }
//        else  {
//            if (RingPage.selected > -1) --RingPage.selected;
//            else RingPage.selected = 7;
//        }
//        return true;
//    }

    @Override
    public boolean mouseReleased(MouseButtonEvent click) {
        if (ring.getCurrentPage().onClick(width, height, (int) click.x(), (int) click.y())) {
            this.onClose();
            return true;
        }
        return false;
    }
}
