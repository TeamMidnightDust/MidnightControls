package eu.midnightdust.midnightcontrols.client.util.storage;

import static org.lwjgl.glfw.GLFW.GLFW_GAMEPAD_BUTTON_DPAD_LEFT;
import static org.lwjgl.glfw.GLFW.GLFW_GAMEPAD_BUTTON_DPAD_UP;

public class ButtonStorage {
    public final int button, action;
    public final boolean state;

    public ButtonStorage(int button, int action, boolean state) {
        this.button = button;
        this.action = action;
        this.state = state;
    }
    public boolean isDpad() {
        return button >= GLFW_GAMEPAD_BUTTON_DPAD_UP && button <= GLFW_GAMEPAD_BUTTON_DPAD_LEFT;
    }
}
