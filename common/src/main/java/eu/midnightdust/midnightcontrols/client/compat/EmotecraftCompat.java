package eu.midnightdust.midnightcontrols.client.compat;

import eu.midnightdust.midnightcontrols.client.controller.InputManager;
import eu.midnightdust.midnightcontrols.client.mixin.MouseAccessor;
import io.github.kosmx.emotes.arch.screen.ingame.FastMenuScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.input.MouseInput;
import org.joml.Vector2i;
import org.lwjgl.glfw.GLFW;

public class EmotecraftCompat {
    private static final MinecraftClient client = MinecraftClient.getInstance();

    public static void openEmotecraftScreen(Screen parent) {
        client.setScreen(new FastMenuScreen(parent));
    }
    public static boolean isEmotecraftScreen(Screen screen) {
        return screen instanceof FastMenuScreen;
    }

    static int prevIndex = -1;
    public static void handleEmoteSelector(int index) {
        try {
            if (client.currentScreen instanceof FastMenuScreen) {
                boolean stickReleased = index == -1 && prevIndex != -1;
                var pos = calcMousePos(stickReleased ? prevIndex : index);
                InputManager.queueMousePosition(pos.x, pos.y);
                InputManager.INPUT_MANAGER.updateMousePosition(client);

                if (stickReleased) {
                    ((MouseAccessor) client.mouse).midnightcontrols$onMouseButton(client.getWindow().getHandle(), new MouseInput(GLFW.GLFW_MOUSE_BUTTON_LEFT, 0), GLFW.GLFW_PRESS);
                    prevIndex = -1;
                }
                else prevIndex = index;
            } else prevIndex = -1;
        } catch (Exception ignored) {}
    }
    public static Vector2i calcMousePos(int index) {
        int x = client.getWindow().getWidth() / 2;
        int y = client.getWindow().getHeight() / 2;
        switch (index) {
            case 0, 3, 5 -> x -= 275;
            case 2, 4, 7 -> x += 275;
        }
        switch (index) {
            case 0, 1, 2 -> y -= 275;
            case 5, 6, 7 -> y += 275;
        }
        return new Vector2i(x, y);
    }
}
