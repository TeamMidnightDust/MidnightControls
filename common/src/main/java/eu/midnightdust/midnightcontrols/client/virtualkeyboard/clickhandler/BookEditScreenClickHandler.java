package eu.midnightdust.midnightcontrols.client.virtualkeyboard.clickhandler;

import eu.midnightdust.midnightcontrols.client.mixin.BookEditScreenAccessor;
import eu.midnightdust.midnightcontrols.client.mixin.BookSigningScreenAccessor;
import eu.midnightdust.midnightcontrols.client.virtualkeyboard.gui.VirtualKeyboardScreen;
import net.minecraft.client.gui.components.MultiLineEditBox;
import net.minecraft.client.gui.screens.inventory.BookEditScreen;
import net.minecraft.client.gui.screens.inventory.BookSignScreen;

import static eu.midnightdust.midnightcontrols.client.MidnightControlsClient.client;

public class BookEditScreenClickHandler extends AbstractScreenClickHandler<BookEditScreen> {
    @Override
    public void handle(BookEditScreen screen, double mouseX, double mouseY) {
        // don't open the keyboard if a UI element was clicked
        if(screen.getChildAt(mouseX, mouseY).isPresent() && !(screen.getChildAt(mouseX, mouseY).get() instanceof MultiLineEditBox)) {
            return;
        }

        var accessor = (BookEditScreenAccessor) screen;

        VirtualKeyboardScreen virtualKeyboardScreen = new VirtualKeyboardScreen(accessor.midnightcontrols$getEditBox().getValue(), (text) -> {
            client.setScreen(screen);
            accessor.midnightcontrols$getEditBox().setValue(text);
        }, true);

        client.setScreen(virtualKeyboardScreen);
    }
    public static class Signing extends AbstractScreenClickHandler<BookSignScreen> {
        @Override
        public void handle(BookSignScreen screen, double mouseX, double mouseY) {
            // don't open the keyboard if a UI element was clicked
            if(screen.getChildAt(mouseX, mouseY).isPresent()) {
                return;
            }

            var accessor = (BookSigningScreenAccessor) screen;

            VirtualKeyboardScreen virtualKeyboardScreen = new VirtualKeyboardScreen(accessor.midnightcontrols$getBookTitleTextField().getValue(), (text) -> {
                client.setScreen(screen);
                accessor.midnightcontrols$getBookTitleTextField().setValue(text);
            }, false);

            client.setScreen(virtualKeyboardScreen);
        }
    }
}
