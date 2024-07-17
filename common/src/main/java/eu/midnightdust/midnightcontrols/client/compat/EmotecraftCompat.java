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
            switch (index) {
                case 0, 3, 5 -> x -= 200;
                case 2, 4, 7 -> x += 200;
            }
            switch (index) {
                case 0, 1, 2 -> y -= 200;
                case 5, 6, 7 -> y += 200;
            }
            InputManager.queueMousePosition(x, y);

            InputManager.INPUT_MANAGER.updateMousePosition(client);
        }
    }
}
