package eu.midnightdust.midnightcontrols.client.virtualkeyboard.clickhandler;

import eu.midnightdust.midnightcontrols.client.mixin.BookEditScreenAccessor;
import eu.midnightdust.midnightcontrols.client.mixin.BookSigningScreenAccessor;
import eu.midnightdust.midnightcontrols.client.virtualkeyboard.gui.VirtualKeyboardScreen;
import net.minecraft.client.gui.screen.ingame.BookEditScreen;
import net.minecraft.client.gui.screen.ingame.BookSigningScreen;
import net.minecraft.client.gui.widget.EditBoxWidget;

import static eu.midnightdust.midnightcontrols.client.MidnightControlsClient.client;

public class BookEditScreenClickHandler extends AbstractScreenClickHandler<BookEditScreen> {
    @Override
    public void handle(BookEditScreen screen, double mouseX, double mouseY) {
        // don't open the keyboard if a UI element was clicked
        if(screen.hoveredElement(mouseX, mouseY).isPresent() && !(screen.hoveredElement(mouseX, mouseY).get() instanceof EditBoxWidget)) {
            return;
        }

        var accessor = (BookEditScreenAccessor) screen;

        VirtualKeyboardScreen virtualKeyboardScreen = new VirtualKeyboardScreen(accessor.midnightcontrols$getEditBox().getText(), (text) -> {
            client.setScreen(screen);
            accessor.midnightcontrols$getEditBox().setText(text);
        }, true);

        client.setScreen(virtualKeyboardScreen);
    }
    public static class Signing extends AbstractScreenClickHandler<BookSigningScreen> {
        @Override
        public void handle(BookSigningScreen screen, double mouseX, double mouseY) {
            // don't open the keyboard if a UI element was clicked
            if(screen.hoveredElement(mouseX, mouseY).isPresent()) {
                return;
            }

            var accessor = (BookSigningScreenAccessor) screen;

            VirtualKeyboardScreen virtualKeyboardScreen = new VirtualKeyboardScreen(accessor.midnightcontrols$getBookTitleTextField().getText(), (text) -> {
                client.setScreen(screen);
                accessor.midnightcontrols$getBookTitleTextField().setText(text);
            }, false);

            client.setScreen(virtualKeyboardScreen);
        }
    }
}
