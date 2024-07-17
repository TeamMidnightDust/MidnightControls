package eu.midnightdust.midnightcontrols.client.util.storage;

import eu.midnightdust.midnightcontrols.client.MidnightControlsConfig;
import eu.midnightdust.midnightcontrols.client.controller.ButtonBinding;
import org.lwjgl.glfw.GLFW;

import static org.lwjgl.glfw.GLFW.GLFW_GAMEPAD_AXIS_LEFT_TRIGGER;
import static org.lwjgl.glfw.GLFW.GLFW_GAMEPAD_AXIS_LEFT_X;
import static org.lwjgl.glfw.GLFW.GLFW_GAMEPAD_AXIS_LEFT_Y;
import static org.lwjgl.glfw.GLFW.GLFW_GAMEPAD_AXIS_RIGHT_TRIGGER;

public class AxisStorage {
    public int axis, state, asButtonState;
    public float value, absValue;
    public double deadZone;
    public Polarity polarity;

    // Only used for camera handling
    public AxisStorage(int axis, float value, int state) {
        this.axis = axis;
        this.value = value;
        this.state = state;
    }

    public AxisStorage(int axis, float value, float absValue, int state) {
        this.axis = axis;
        this.value = value;
        this.absValue = absValue;
        this.state = state;
        deadZone = getDeadZoneValue(axis);
        asButtonState = value > .5f ? 1 : (value < -.5f ? 2 : 0);

        if (axis == GLFW_GAMEPAD_AXIS_LEFT_TRIGGER || axis == GLFW_GAMEPAD_AXIS_RIGHT_TRIGGER
                || axis == ButtonBinding.controller2Button(GLFW.GLFW_GAMEPAD_AXIS_LEFT_TRIGGER)
                || axis == ButtonBinding.controller2Button(GLFW.GLFW_GAMEPAD_AXIS_RIGHT_TRIGGER)) {
            if (asButtonState == 2) {
                asButtonState = 0;
            }
            else {
                // Fixes Triggers not working correctly on some controllers
                if (MidnightControlsConfig.triggerFix) {
                    this.value = 1.0f;
                    this.absValue = 1.0f;
                    this.state = 1;
                    this.asButtonState = 1;
                }
                //if (MidnightControlsConfig.debug) System.out.println(axis + " "+ value + " " + absValue + " " + state);
            }
        }
    }
    private static double getDeadZoneValue(int axis) {
        return (axis == GLFW_GAMEPAD_AXIS_LEFT_X || axis == GLFW_GAMEPAD_AXIS_LEFT_Y) ? MidnightControlsConfig.leftDeadZone : MidnightControlsConfig.rightDeadZone;
    }
    public enum Polarity {
        MINUS, ZERO, PLUS;
    }
}
