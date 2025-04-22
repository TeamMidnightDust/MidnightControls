package eu.midnightdust.midnightcontrols.client.virtualkeyboard.clickhandler;

import eu.midnightdust.midnightcontrols.client.mixin.AbstractSignEditScreenAccessor;
import net.minecraft.client.gui.screen.ingame.SignEditScreen;

public class SignEditScreenHandler extends AbstractScreenClickHandler<SignEditScreen>  {
    @Override
    public void handle(SignEditScreen screen, double mouseX, double mouseY) {
        // don't open the keyboard if a UI element was clicked
        if(screen.hoveredElement(mouseX, mouseY).isPresent()) {
            return;
        }

        var accessor = (AbstractSignEditScreenAccessor) screen;
    }
}
