package eu.midnightdust.midnightcontrols.client.util.storage;

import eu.midnightdust.midnightcontrols.client.MidnightControlsConfig;
import eu.midnightdust.midnightcontrols.client.controller.ButtonBinding;
import eu.midnightdust.midnightcontrols.client.enums.ButtonState;
import org.lwjgl.glfw.GLFW;

import static eu.midnightdust.midnightcontrols.client.MidnightInput.BUTTON_COOLDOWNS;
import static eu.midnightdust.midnightcontrols.client.controller.InputManager.STATES;
import static org.lwjgl.glfw.GLFW.GLFW_GAMEPAD_AXIS_LEFT_TRIGGER;
import static org.lwjgl.glfw.GLFW.GLFW_GAMEPAD_AXIS_LEFT_X;
import static org.lwjgl.glfw.GLFW.GLFW_GAMEPAD_AXIS_LEFT_Y;
import static org.lwjgl.glfw.GLFW.GLFW_GAMEPAD_AXIS_RIGHT_TRIGGER;

public class AxisStorage {
    public int axis, state, asButtonState;
    public float value, absValue;
    public double deadZone;
    public Polarity polarity;
    public boolean isTrigger;

    // Only used for camera handling
    public AxisStorage(int axis, float value, int state) {
        this.axis = axis;
        this.value = value;
        this.state = state;
        this.deadZone = isLeftAxis() ? MidnightControlsConfig.leftDeadZone : MidnightControlsConfig.rightDeadZone;
        boolean currentPlusState = value > deadZone;
        boolean currentMinusState = value < -deadZone;
        this.polarity = currentPlusState ? AxisStorage.Polarity.PLUS : currentMinusState ? AxisStorage.Polarity.MINUS : AxisStorage.Polarity.ZERO;
    }

    public AxisStorage(int axis, float value, float absValue, int state) {
        this.axis = axis;
        this.value = value;
        this.absValue = absValue;
        this.state = state;
        this.deadZone = isLeftAxis() ? MidnightControlsConfig.leftDeadZone : MidnightControlsConfig.rightDeadZone;
        this.asButtonState = value > .5f ? 1 : (value < -.5f ? 2 : 0);

        if (axis == GLFW_GAMEPAD_AXIS_LEFT_TRIGGER || axis == GLFW_GAMEPAD_AXIS_RIGHT_TRIGGER
                || axis == ButtonBinding.controller2Button(GLFW.GLFW_GAMEPAD_AXIS_LEFT_TRIGGER) || axis == ButtonBinding.controller2Button(GLFW.GLFW_GAMEPAD_AXIS_RIGHT_TRIGGER)) {
            this.isTrigger = true;
            if (asButtonState == 2) {
                this.asButtonState = 0;
            }
            else {
                // Fixes Triggers not working correctly on some controllers
                if (MidnightControlsConfig.triggerFix) {
                    this.value = 1.0f;
                    this.absValue = 1.0f;
                    this.state = 1;
                    this.asButtonState = 1;
                }
            }
        }
        boolean currentPlusState = value > deadZone;
        boolean currentMinusState = value < -deadZone;
        if (isTrigger) currentMinusState = false;
        else if (!MidnightControlsConfig.analogMovement && isLeftAxis()) {
            currentPlusState = asButtonState == 1;
            currentMinusState = asButtonState == 2;
        }
        this.polarity = currentPlusState ? AxisStorage.Polarity.PLUS : currentMinusState ? AxisStorage.Polarity.MINUS : AxisStorage.Polarity.ZERO;
    }

    public void setupButtonStates() {
        var posButton = ButtonBinding.axisAsButton(axis, true);
        var negButton = ButtonBinding.axisAsButton(axis, false);
        var previousPlusState = STATES.getOrDefault(posButton, ButtonState.NONE);
        var previousMinusState = STATES.getOrDefault(negButton, ButtonState.NONE);

        if (polarity.isPositive() != previousPlusState.isPressed()) {
            STATES.put(posButton, polarity.isPositive() ? ButtonState.PRESS : ButtonState.RELEASE);
            if (polarity.isPositive())
                BUTTON_COOLDOWNS.put(posButton, 5);
        } else if (polarity.isPositive()) {
            STATES.put(posButton, ButtonState.REPEAT);
            if (BUTTON_COOLDOWNS.getOrDefault(posButton, 0) == 0) {
                BUTTON_COOLDOWNS.put(posButton, 5);
            }
        }

        if (polarity.isNegative() != previousMinusState.isPressed()) {
            STATES.put(negButton, polarity.isNegative() ? ButtonState.PRESS : ButtonState.RELEASE);
            if (polarity.isNegative())
                BUTTON_COOLDOWNS.put(negButton, 5);
        } else if (polarity.isNegative()) {
            STATES.put(negButton, ButtonState.REPEAT);
            if (BUTTON_COOLDOWNS.getOrDefault(negButton, 0) == 0) {
                BUTTON_COOLDOWNS.put(negButton, 5);
            }
        }
    }
    public boolean isLeftAxis() {
        return axis == GLFW_GAMEPAD_AXIS_LEFT_X || axis == GLFW_GAMEPAD_AXIS_LEFT_Y || axis == GLFW_GAMEPAD_AXIS_LEFT_TRIGGER;
    }
    public boolean isRightAxis() {
        return !isLeftAxis();
    }

    public enum Polarity {
        MINUS, ZERO, PLUS;
        public boolean isPositive() {
            return this == PLUS;
        }
        public boolean isNegative() {
            return this == MINUS;
        }
    }
}
