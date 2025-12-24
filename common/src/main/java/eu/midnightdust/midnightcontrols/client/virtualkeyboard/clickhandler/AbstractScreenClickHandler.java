package eu.midnightdust.midnightcontrols.client.virtualkeyboard.clickhandler;

import net.minecraft.client.gui.screens.Screen;

public abstract class AbstractScreenClickHandler<T extends Screen> {
    public abstract void handle(T screen, double mouseX, double mouseY);
}
