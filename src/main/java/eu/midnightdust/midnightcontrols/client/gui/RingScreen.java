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
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

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
        super(Text.literal("midnightcontrols.menu.title.ring"));
        this.mod = MidnightControlsClient.get();
    }

    @Override
    protected void init() {
        super.init();
        if (mod.ring.getMaxPages() > 1) {
            this.addDrawableChild(ButtonWidget.builder(Text.of("◀"), button -> this.mod.ring.cyclePage(false)).dimensions(5, 5, 20, 20).build());
            this.addDrawableChild(ButtonWidget.builder(Text.of("▶"), button -> this.mod.ring.cyclePage(true)).dimensions(width - 25, 5, 20, 20).build());
        }
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        RingPage page = this.mod.ring.getCurrentPage();
        page.render(context, this.textRenderer, this.width, this.height, mouseX, mouseY, delta);
    }

    @Override
    public void close() {
        super.close();
        assert client != null;
        client.currentScreen = null;
        RingPage page = this.mod.ring.getCurrentPage();
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
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (mod.ring.getCurrentPage().onClick(width, height, (int) mouseX, (int) mouseY)) {
            this.close();
            return true;
        }
        return false;
    }
}
