package eu.midnightdust.midnightcontrols.client.util.storage;

import eu.midnightdust.midnightcontrols.client.enums.ButtonState;

import static org.lwjgl.glfw.GLFW.GLFW_GAMEPAD_BUTTON_DPAD_LEFT;
import static org.lwjgl.glfw.GLFW.GLFW_GAMEPAD_BUTTON_DPAD_UP;

public class ButtonStorage {
    public final int button;
    public final ButtonState state;

    public ButtonStorage(int button, ButtonState state) {
        this.button = button;
        this.state = state;
    }
    public boolean isDpad() {
        return button >= GLFW_GAMEPAD_BUTTON_DPAD_UP && button <= GLFW_GAMEPAD_BUTTON_DPAD_LEFT;
    }
}
