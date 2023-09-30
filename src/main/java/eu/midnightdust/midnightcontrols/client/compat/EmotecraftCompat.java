package eu.midnightdust.midnightcontrols.client.compat;

import eu.midnightdust.midnightcontrols.client.controller.InputManager;
import io.github.kosmx.emotes.arch.gui.EmoteMenuImpl;
import io.github.kosmx.emotes.arch.gui.screen.ingame.FastChosseScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;

public class EmotecraftCompat {
    private static final MinecraftClient client = MinecraftClient.getInstance();
    public static void openEmotecraftScreen(Screen parent) {
        client.setScreen(new EmoteMenuImpl(parent));
    }
    public static boolean isEmotecraftScreen(Screen screen) {
        return screen instanceof FastChosseScreen;
    }
    public static void handleEmoteSelector(int index) {

        if (client.currentScreen instanceof FastChosseScreen) {
            int x = client.getWindow().getWidth() / 2;
            int y = client.getWindow().getHeight() / 2;
            if (index == 0) InputManager.queueMousePosition(x-200, y-200);
            if (index == 1) InputManager.queueMousePosition(x, y-200);
            if (index == 2) InputManager.queueMousePosition(x+200, y-200);
            if (index == 3) InputManager.queueMousePosition(x-200, y);
            if (index == 4) InputManager.queueMousePosition(x+200, y);
            if (index == 5) InputManager.queueMousePosition(x-200, y+200);
            if (index == 6) InputManager.queueMousePosition(x, y+200);
            if (index == 7) InputManager.queueMousePosition(x+200, y+200);

            InputManager.INPUT_MANAGER.updateMousePosition(client);
        }
    }
}
