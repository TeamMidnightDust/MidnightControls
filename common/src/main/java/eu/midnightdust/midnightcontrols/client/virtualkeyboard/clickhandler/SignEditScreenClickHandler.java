package eu.midnightdust.midnightcontrols.client.virtualkeyboard.clickhandler;

import eu.midnightdust.midnightcontrols.client.util.AbstractSignEditScreenAccessor;
import eu.midnightdust.midnightcontrols.client.virtualkeyboard.gui.VirtualKeyboardScreen;
import net.minecraft.client.gui.screen.ingame.SignEditScreen;

import static eu.midnightdust.midnightcontrols.client.MidnightControlsClient.client;

public class SignEditScreenClickHandler extends AbstractScreenClickHandler<SignEditScreen>  {
    @Override
    public void handle(SignEditScreen screen, double mouseX, double mouseY) {
        // don't open the keyboard if a UI element was clicked
        if(screen.hoveredElement(mouseX, mouseY).isPresent()) {
            return;
        }

        var accessor = (AbstractSignEditScreenAccessor) screen;

        StringBuilder linesToString = new StringBuilder();
        String[] messages = accessor.midnightcontrols$getMessages();
        for (int i = 0; i < Math.min(4, messages.length); i++) {
            String line = messages[i];
            linesToString.append(line);
            if (!line.isEmpty() && i < 3) linesToString.append("\n");
        }

        VirtualKeyboardScreen virtualKeyboardScreen = new VirtualKeyboardScreen(linesToString.toString(), (text) -> {
            client.setScreen(screen);
            String[] lines = text.split("\n");
            for (int i = 0; i < 4; i++) accessor.midnightcontrols$setMessage(i, lines.length > i ? lines[i] : "");
            accessor.midnightcontrols$writeToBlockEntity();
        }, true);

        client.setScreen(virtualKeyboardScreen);
    }
}
