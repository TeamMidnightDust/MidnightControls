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

/**
 * Stores information about the current axis state
 */
public class AxisStorage {
    public final int axis;
    public float value, absValue;
    public final double deadZone;
    public final Polarity polarity;
    public final ButtonState buttonState;

    /**
     * Create an AxisStorage instance with a configured deadzone (for left/right sticks)
     */
    public static AxisStorage of(int axis, float value) {
        return new AxisStorage(axis, value, isLeftAxis(axis) ? MidnightControlsConfig.leftDeadZone : MidnightControlsConfig.rightDeadZone);
    }
    /**
     * Create an AxisStorage instance with an arbitrary deadzone (for triggers and internal use)
     */
    public static AxisStorage of(int axis, float value, double deadZone) {
        return new AxisStorage(axis, value, deadZone);
    }

    private AxisStorage(int axis, float value, double deadZone) {
        this.axis = axis;
        this.deadZone = deadZone;

        if (isTrigger(axis)) {
            if (value < -.5f) {
                value = 0f;
            }
            else if (MidnightControlsConfig.triggerFix) { // Fixes Triggers not working correctly on some controllers
                value = 1.0f;
            }
        }

        this.value = value;
        this.buttonState = value > .5f ? ButtonState.PRESS : (value < -.5f ? ButtonState.RELEASE : ButtonState.NONE);
        this.absValue = Math.abs(value);
        boolean currentPlusState = value > deadZone;
        boolean currentMinusState = value < -deadZone;
        if (isTrigger(axis)) currentMinusState = false;
        else if (!MidnightControlsConfig.isAnalogMovementAllowed() && isLeftAxis(axis)) {
            currentPlusState = buttonState == ButtonState.PRESS;
            currentMinusState = buttonState == ButtonState.RELEASE;
        }
        this.polarity = currentPlusState ? AxisStorage.Polarity.PLUS : currentMinusState ? AxisStorage.Polarity.MINUS : AxisStorage.Polarity.ZERO;
    }

    /**
     * Returns the specified axis as a button.
     *
     * @param positive true if the axis part is positive, else false
     * @return the axis as a button
     */
    public int getButtonId(boolean positive) {
        return ButtonBinding.axisAsButton(axis, positive);
    }

    /**
     * For button bindings making use of joysticks, this method updates the button values according to the axis value.
     * Each axis has two according buttons, plus (down or right) and minus (up or left).
     */
    public void setupButtonStates() {
        var posButton = getButtonId(true);
        var negButton = getButtonId(false);
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

    public static boolean isLeftAxis(int axis) {
        return axis == GLFW_GAMEPAD_AXIS_LEFT_X || axis == GLFW_GAMEPAD_AXIS_LEFT_Y || axis == GLFW_GAMEPAD_AXIS_LEFT_TRIGGER;
    }

    public static boolean isTrigger(int axis) {
        return axis == GLFW_GAMEPAD_AXIS_LEFT_TRIGGER || axis == GLFW_GAMEPAD_AXIS_RIGHT_TRIGGER ||
               axis == ButtonBinding.controller2Button(GLFW.GLFW_GAMEPAD_AXIS_LEFT_TRIGGER) || axis == ButtonBinding.controller2Button(GLFW.GLFW_GAMEPAD_AXIS_RIGHT_TRIGGER);
    }

    public enum Polarity {
        MINUS(-1), ZERO(0), PLUS(1);

        public final int multiplier;
        Polarity(int multiplier) {
            this.multiplier = multiplier;
        }
        public boolean isPositive() {
            return this == PLUS;
        }
        public boolean isNegative() {
            return this == MINUS;
        }
    }
}
