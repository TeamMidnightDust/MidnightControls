package eu.midnightdust.midnightcontrols.client.virtualkeyboard.clickhandler;

import eu.midnightdust.midnightcontrols.client.mixin.BookEditScreenAccessor;
import eu.midnightdust.midnightcontrols.client.virtualkeyboard.gui.VirtualKeyboardScreen;
import net.minecraft.client.gui.screen.ingame.BookEditScreen;

import static eu.midnightdust.midnightcontrols.client.MidnightControlsClient.client;

public class BookEditScreenClickHandler extends AbstractScreenClickHandler<BookEditScreen> {
    @Override
    public void handle(BookEditScreen screen, double mouseX, double mouseY) {
        // don't open the keyboard if a UI element was clicked
        if(screen.hoveredElement(mouseX, mouseY).isPresent()) {
            return;
        }

        var accessor = (BookEditScreenAccessor) screen;

        VirtualKeyboardScreen virtualKeyboardScreen;
        if(accessor.midnightcontrols$isSigning()) {
            virtualKeyboardScreen = new VirtualKeyboardScreen(accessor.midnightcontrols$getTitle(), (text) -> {
                client.setScreen(screen);
                accessor.midnightcontrols$setTitle(text);
            }, true);
        }
        else {
            virtualKeyboardScreen = new VirtualKeyboardScreen(accessor.midnightcontrols$getCurrentPageContent(), (text) -> {
                client.setScreen(screen);
                accessor.midnightcontrols$setPageContent(text);
                accessor.midnightcontrols$getCurrentPageSelectionManager().putCursorAtEnd();
            }, true);
        }

        client.setScreen(virtualKeyboardScreen);
    }
}
