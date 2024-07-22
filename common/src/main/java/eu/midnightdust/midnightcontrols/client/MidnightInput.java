/*
 * Copyright © 2021 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of midnightcontrols.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package eu.midnightdust.midnightcontrols.client;

import com.google.common.collect.ImmutableSet;
import eu.midnightdust.lib.util.PlatformFunctions;
import eu.midnightdust.midnightcontrols.client.compat.EmotecraftCompat;
import eu.midnightdust.midnightcontrols.client.compat.LibGuiCompat;
import eu.midnightdust.midnightcontrols.client.compat.MidnightControlsCompat;
import eu.midnightdust.midnightcontrols.client.compat.YACLCompat;
import eu.midnightdust.midnightcontrols.client.mixin.AdvancementsScreenAccessor;
import eu.midnightdust.midnightcontrols.client.mixin.CreativeInventoryScreenAccessor;
import eu.midnightdust.midnightcontrols.client.mixin.MouseAccessor;
import eu.midnightdust.midnightcontrols.client.util.InventoryUtil;
import eu.midnightdust.midnightcontrols.client.util.storage.AxisStorage;
import eu.midnightdust.midnightcontrols.client.util.storage.ButtonStorage;
import net.minecraft.client.gui.screen.option.KeybindsScreen;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.client.gui.widget.PressableWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.entity.vehicle.BoatEntity;
import org.thinkingstudio.obsidianui.widget.AbstractSpruceWidget;
import org.thinkingstudio.obsidianui.widget.container.SpruceEntryListWidget;
import eu.midnightdust.midnightcontrols.MidnightControls;
import eu.midnightdust.midnightcontrols.client.controller.ButtonBinding;
import eu.midnightdust.midnightcontrols.client.controller.Controller;
import eu.midnightdust.midnightcontrols.client.controller.InputManager;
import eu.midnightdust.midnightcontrols.client.enums.CameraMode;
import eu.midnightdust.midnightcontrols.client.gui.RingScreen;
import eu.midnightdust.midnightcontrols.client.touch.gui.TouchscreenOverlay;
import eu.midnightdust.midnightcontrols.client.gui.widget.ControllerControlsWidget;
import eu.midnightdust.midnightcontrols.client.ring.RingPage;
import eu.midnightdust.midnightcontrols.client.util.HandledScreenAccessor;
import eu.midnightdust.midnightcontrols.client.util.MathUtil;
import org.thinkingstudio.obsidianui.navigation.NavigationDirection;
import org.thinkingstudio.obsidianui.screen.SpruceScreen;
import org.thinkingstudio.obsidianui.widget.AbstractSprucePressableButtonWidget;
import org.thinkingstudio.obsidianui.widget.SpruceElement;
import org.thinkingstudio.obsidianui.widget.SpruceLabelWidget;
import org.thinkingstudio.obsidianui.widget.container.SpruceParentWidget;
import eu.midnightdust.midnightcontrols.client.enums.ButtonState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.ParentElement;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.advancement.AdvancementTab;
import net.minecraft.client.gui.screen.advancement.AdvancementsScreen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.MerchantScreen;
import net.minecraft.client.gui.screen.ingame.StonecutterScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerServerListWidget;
import net.minecraft.client.gui.screen.world.WorldListWidget;
import net.minecraft.text.TranslatableTextContent;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWGamepadState;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static eu.midnightdust.midnightcontrols.client.MidnightControlsClient.client;
import static org.lwjgl.glfw.GLFW.*;

/**
 * Represents the midnightcontrols' input handler.
 *
 * @author Motschen, LambdAurora
 * @version 1.10.0
 * @since 1.0.0
 */
public class MidnightInput {
    public static final Map<Integer, Integer> BUTTON_COOLDOWNS = new HashMap<>();
    // Cooldowns
    public int actionGuiCooldown = 0;
    public int joystickCooldown = 0;
    public boolean ignoreNextARelease = false;
    public boolean ignoreNextXRelease = false;
    private double targetYaw = 0.0;
    private double targetPitch = 0.0;
    private float prevXAxis = 0.f;
    private float prevYAxis = 0.f;
    public float mouseSpeedX = 0.f;
    public float mouseSpeedY = 0.f;
    public int inventoryInteractionCooldown = 0;
    public int screenCloseCooldown = 0;

    private ControllerControlsWidget controlsInput = null;

    public MidnightInput() {}

    /**
     * This method is called every Minecraft tick.
     */
    public void tick() {
        this.targetYaw = 0.F;
        this.targetPitch = 0.F;

        // Handles the key bindings.
        if (MidnightControlsClient.BINDING_LOOK_UP.isPressed()) {
            this.handleFlatLook(new AxisStorage(GLFW_GAMEPAD_AXIS_RIGHT_Y, -0.8F, 0d));
        } else if (MidnightControlsClient.BINDING_LOOK_DOWN.isPressed()) {
            this.handleFlatLook(new AxisStorage(GLFW_GAMEPAD_AXIS_RIGHT_Y, 0.8F, 0d));
        }
        if (MidnightControlsClient.BINDING_LOOK_LEFT.isPressed()) {
            this.handleFlatLook(new AxisStorage(GLFW_GAMEPAD_AXIS_RIGHT_X, -0.8F, 0d));
        } else if (MidnightControlsClient.BINDING_LOOK_RIGHT.isPressed()) {
            this.handleFlatLook(new AxisStorage(GLFW_GAMEPAD_AXIS_RIGHT_X, 0.8F, 0d));
        }

        InputManager.INPUT_MANAGER.tick();
    }

    /**
     * This method is called every Minecraft tick for controller input update.
     */
    public void tickController() {
        BUTTON_COOLDOWNS.entrySet().stream().filter(entry -> entry.getValue() > 0)
                .forEach(entry -> BUTTON_COOLDOWNS.put(entry.getKey(), entry.getValue() - 1));
        // Decreases the cooldown for GUI actions.
        if (this.actionGuiCooldown > 0)
            --this.actionGuiCooldown;
        if (this.screenCloseCooldown > 0)
            --this.screenCloseCooldown;
        if (this.joystickCooldown > 0)
            --this.joystickCooldown;

        InputManager.updateStates();

        var controller = MidnightControlsConfig.getController();

        if (controller.isConnected()) {
            var state = controller.getState();
            this.fetchButtonInput(state, false);
            this.fetchTriggerInput(state, false);
        }
        MidnightControlsConfig.getSecondController().filter(Controller::isConnected)
                .ifPresent(joycon -> {
                    var state = joycon.getState();
                    this.fetchButtonInput(state, true);
                    this.fetchTriggerInput(state, true);
                });

        boolean allowInput = this.controlsInput == null || this.controlsInput.focusedBinding == null;

        if (allowInput)
            InputManager.updateBindings();

        if (this.controlsInput != null) {
            InputManager.STATES.forEach((num, button) -> {
                if (button.isPressed()) System.out.println(num);
            });
        }
        if (this.controlsInput != null && InputManager.STATES.int2ObjectEntrySet().parallelStream().map(Map.Entry::getValue).allMatch(ButtonState::isUnpressed)) {
            if (MidnightControlsConfig.debug) MidnightControls.log("Starting MidnightInput Button Edit");
            if (this.controlsInput.focusedBinding != null && !this.controlsInput.waiting) {
                int[] buttons = new int[this.controlsInput.currentButtons.size()];
                for (int i = 0; i < this.controlsInput.currentButtons.size(); i++)
                    buttons[i] = this.controlsInput.currentButtons.get(i);
                this.controlsInput.finishBindingEdit(buttons);
                this.controlsInput = null;
            }
        }

        if (this.inventoryInteractionCooldown > 0)
            this.inventoryInteractionCooldown--;
    }
    /**
     * This method is called 1000 times a second for smooth joystick input
     */
    public void tickJoysticks() {
        var controller = MidnightControlsConfig.getController();

        if (controller.isConnected()) {
            this.fetchJoystickInput(controller.getState(), false);
        }
        MidnightControlsConfig.getSecondController().filter(Controller::isConnected)
                .ifPresent(joycon -> {
                    this.fetchJoystickInput(joycon.getState(), true);
                });
    }

    /**
     * This method is called before the screen is rendered.
     *
     * @param screen the screen to render
     */
    public void onPreRenderScreen(@NotNull Screen screen) {
        if (!isScreenInteractive(screen)) {
            InputManager.INPUT_MANAGER.updateMousePosition(client);
        }
    }

    /**
     * This method is called to update the camera
     */
    public void updateCamera() {
        if (!(client.currentScreen == null || client.currentScreen instanceof TouchscreenOverlay))
            return;

        var player = client.player;
        if (player == null)
            return;

        if (this.targetYaw != 0.f || this.targetPitch != 0.f) {
            float rotationYaw = (float) (client.player.prevYaw + (this.targetYaw * 0.175));
            float rotationPitch = (float) (client.player.prevPitch + (this.targetPitch * 0.175));
            client.player.prevYaw = rotationYaw;
            client.player.prevPitch = MathHelper.clamp(rotationPitch, -90.f, 90.f);
            client.player.setYaw(rotationYaw);
            client.player.setPitch(MathHelper.clamp(rotationPitch, -90.f, 90.f));
            if (client.player.isRiding() && client.player.getVehicle() != null) {
                client.player.getVehicle().onPassengerLookAround(client.player);
            }
            client.getTutorialManager().onUpdateMouse(this.targetPitch, this.targetYaw);
        }
    }

    /**
     * This method is called when a Screen is opened.
     *
     * @param windowWidth the window width
     * @param windowHeight the window height
     */
    public void onScreenOpen(int windowWidth, int windowHeight) {
        if (client.currentScreen == null) {
            this.mouseSpeedX = this.mouseSpeedY = 0.0F;
            InputManager.INPUT_MANAGER.resetMousePosition(windowWidth, windowHeight);
        } else if (isScreenInteractive(client.currentScreen) && MidnightControlsConfig.virtualMouse) {
            ((MouseAccessor) client.mouse).midnightcontrols$onCursorPos(client.getWindow().getHandle(), 0, 0);
            InputManager.INPUT_MANAGER.resetMouseTarget(client);
        }
        this.inventoryInteractionCooldown = 5;
    }

    public void beginControlsInput(ControllerControlsWidget widget) {
        this.controlsInput = widget;
        if (widget != null) {
            this.controlsInput.currentButtons.clear();
            this.controlsInput.waiting = true;
        }
    }

    private void fetchButtonInput(@NotNull GLFWGamepadState gamepadState, boolean leftJoycon) {
        var buffer = gamepadState.buttons();
        for (int i = 0; i < buffer.limit(); i++) {
            int btn = leftJoycon ? ButtonBinding.controller2Button(i) : i;
            boolean pressed = buffer.get() == (byte) 1;
            var state = ButtonState.NONE;
            var previousState = InputManager.STATES.getOrDefault(btn, ButtonState.NONE);

            if (pressed != previousState.isPressed()) {
                state = pressed ? ButtonState.PRESS : ButtonState.RELEASE;
                this.handleButton(new ButtonStorage(btn, state));
                if (pressed)
                    BUTTON_COOLDOWNS.put(btn, 5);
            } else if (pressed) {
                state = ButtonState.REPEAT;
                if (BUTTON_COOLDOWNS.getOrDefault(btn, 0) == 0) {
                    BUTTON_COOLDOWNS.put(btn, 5);
                    this.handleButton(new ButtonStorage(btn, state));
                }
            }

            InputManager.STATES.put(btn, state);
        }
    }
    final MathUtil.PolarUtil polarUtil = new MathUtil.PolarUtil();

    private void fetchJoystickInput(@NotNull GLFWGamepadState gamepadState, boolean leftJoycon) {
        var buffer = gamepadState.axes();

        polarUtil.calculate(buffer.get(GLFW_GAMEPAD_AXIS_LEFT_X), buffer.get(GLFW_GAMEPAD_AXIS_LEFT_Y), 1, MidnightControlsConfig.leftDeadZone);
        float leftX = polarUtil.polarX;
        float leftY = polarUtil.polarY;
        polarUtil.calculate(buffer.get(GLFW_GAMEPAD_AXIS_RIGHT_X), buffer.get(GLFW_GAMEPAD_AXIS_RIGHT_Y), 1, MidnightControlsConfig.rightDeadZone);
        float rightX = polarUtil.polarX;
        float rightY = polarUtil.polarY;

        boolean isRadialMenu = client.currentScreen instanceof RingScreen || (EmotecraftCompat.isPresent() && EmotecraftCompat.isEmotecraftScreen(client.currentScreen));

        if (!isRadialMenu) {
            for (int i = 0; i < GLFW_GAMEPAD_AXIS_LEFT_TRIGGER; i++) {
                int axis = leftJoycon ? ButtonBinding.controller2Button(i) : i;
                float value = buffer.get(i);

                switch (i) {
                    case GLFW_GAMEPAD_AXIS_LEFT_X -> {
                        if (MidnightControlsConfig.analogMovement) value = leftX;
                    }
                    case GLFW_GAMEPAD_AXIS_LEFT_Y -> {
                        if (MidnightControlsConfig.analogMovement) value = leftY;
                    }
                    case GLFW_GAMEPAD_AXIS_RIGHT_X -> value = rightX;
                    case GLFW_GAMEPAD_AXIS_RIGHT_Y -> value = rightY;
                }

                if (i == GLFW.GLFW_GAMEPAD_AXIS_LEFT_Y)
                    value *= -1.0F;

                this.handleJoystickAxis(new AxisStorage(axis, value));
            }
        }
        else {
            boolean leftStickActive = leftX != 0 || leftY != 0;
            handleRadialMenu(leftStickActive ? leftX : rightX, leftStickActive ? leftY : rightY);
        }
    }

    private void fetchTriggerInput(@NotNull GLFWGamepadState gamepadState, boolean leftJoycon) {
        var buffer = gamepadState.axes();

        for (int i = GLFW_GAMEPAD_AXIS_LEFT_TRIGGER; i <= GLFW_GAMEPAD_AXIS_RIGHT_TRIGGER; i++) {
            int axis = leftJoycon ? ButtonBinding.controller2Button(i) : i;
            float value = buffer.get(i);

            this.handleTriggerAxis(new AxisStorage(axis, value, MidnightControlsConfig.triggerDeadZone));
        }
    }

    public void handleButton(ButtonStorage storage) {
        if (this.controlsInput != null && this.controlsInput.focusedBinding != null) {
            if (storage.state == ButtonState.PRESS && !this.controlsInput.currentButtons.contains(storage.button)) {
                this.controlsInput.currentButtons.add(storage.button);

                var buttons = new int[this.controlsInput.currentButtons.size()];
                for (int i = 0; i < this.controlsInput.currentButtons.size(); i++)
                    buttons[i] = this.controlsInput.currentButtons.get(i);
                this.controlsInput.focusedBinding.setButton(buttons);

                this.controlsInput.waiting = false;
            }
            return;
        }

        if (client.currentScreen != null && storage.state.isPressed() && storage.button == GLFW_GAMEPAD_BUTTON_Y &&
                MidnightControlsConfig.arrowScreens.contains(client.currentScreen.getClass().getCanonicalName())) {
            pressKeyboardKey(client, GLFW.GLFW_KEY_ENTER);
            this.screenCloseCooldown = 5;
        }
        else if (storage.state.isPressed()) {
            if (client.currentScreen != null && storage.isDpad() && this.actionGuiCooldown == 0) {
                switch (storage.button) {
                    case GLFW_GAMEPAD_BUTTON_DPAD_UP -> this.changeFocus(client.currentScreen, NavigationDirection.UP);
                    case GLFW_GAMEPAD_BUTTON_DPAD_DOWN -> this.changeFocus(client.currentScreen, NavigationDirection.DOWN);
                    case GLFW_GAMEPAD_BUTTON_DPAD_LEFT -> this.handleLeftRight(client.currentScreen, false);
                    case GLFW_GAMEPAD_BUTTON_DPAD_RIGHT -> this.handleLeftRight(client.currentScreen, true);
                }
                if (MidnightControlsConfig.wasdScreens.contains(client.currentScreen.getClass().getCanonicalName())) {
                    switch (storage.button) {
                        case GLFW_GAMEPAD_BUTTON_DPAD_UP -> pressKeyboardKey(client, GLFW.GLFW_KEY_W);
                        case GLFW_GAMEPAD_BUTTON_DPAD_DOWN -> pressKeyboardKey(client, GLFW.GLFW_KEY_S);
                        case GLFW_GAMEPAD_BUTTON_DPAD_LEFT -> pressKeyboardKey(client, GLFW.GLFW_KEY_A);
                        case GLFW_GAMEPAD_BUTTON_DPAD_RIGHT -> pressKeyboardKey(client, GLFW.GLFW_KEY_D);
                    }
                }
                return;
            }
        }
        else {
            if (storage.button == GLFW.GLFW_GAMEPAD_BUTTON_A && client.currentScreen != null) {
                if (this.actionGuiCooldown == 0) {
                    var focused = client.currentScreen.getFocused();
                    if (focused != null && isScreenInteractive(client.currentScreen)) {
                        if (this.handleAButton(client.currentScreen, focused)) {
                            this.actionGuiCooldown = 5; // Set the cooldown to 5 ticks to avoid unintended button presses.
                            return;
                        }
                    }
                    else if (PlatformFunctions.isModLoaded("libgui")) LibGuiCompat.handlePress(client.currentScreen);
                }
            }
        }

        if (storage.button == GLFW.GLFW_GAMEPAD_BUTTON_A && client.currentScreen != null && !isScreenInteractive(client.currentScreen)
                && this.actionGuiCooldown == 0) {
            if (client.currentScreen instanceof HandledScreen<?> handledScreen && ((HandledScreenAccessor) handledScreen).midnightcontrols$getSlotAt(
                    client.mouse.getX() * (double) client.getWindow().getScaledWidth() / (double) client.getWindow().getWidth(),
                    client.mouse.getY() * (double) client.getWindow().getScaledHeight() / (double) client.getWindow().getHeight()) != null) return;
            if (!this.ignoreNextARelease && client.currentScreen != null) {
                var accessor = (MouseAccessor) client.mouse;
                accessor.midnightcontrols$onCursorPos(client.getWindow().getHandle(), client.mouse.getX(), client.mouse.getY());
                switch (storage.state) {
                    // Button pressed
                    case PRESS -> accessor.midnightcontrols$onMouseButton(client.getWindow().getHandle(), GLFW_MOUSE_BUTTON_LEFT, 1, 0);
                    case RELEASE -> { // Button released
                        accessor.midnightcontrols$onMouseButton(client.getWindow().getHandle(), GLFW_MOUSE_BUTTON_LEFT, 0, 0);
                        client.currentScreen.setDragging(false);
                    }
                    case REPEAT -> client.currentScreen.setDragging(true); // Button held down / dragging
                }
                this.screenCloseCooldown = 5;
            } else {
                this.ignoreNextARelease = false;
            }
        }
        else if (storage.button == GLFW.GLFW_GAMEPAD_BUTTON_X && client.currentScreen != null && !isScreenInteractive(client.currentScreen)
                && this.actionGuiCooldown == 0) {
            double mouseX = client.mouse.getX() * (double) client.getWindow().getScaledWidth() / (double) client.getWindow().getWidth();
            double mouseY = client.mouse.getY() * (double) client.getWindow().getScaledHeight() / (double) client.getWindow().getHeight();
            if (client.currentScreen instanceof HandledScreen<?> handledScreen && ((HandledScreenAccessor) handledScreen).midnightcontrols$getSlotAt(
                    mouseX, mouseY) != null) return;
            if (!this.ignoreNextXRelease && client.currentScreen != null) {
                if (storage.state == ButtonState.PRESS) client.currentScreen.mouseClicked(mouseX, mouseY, GLFW.GLFW_MOUSE_BUTTON_2);
                else if (storage.state == ButtonState.RELEASE) client.currentScreen.mouseReleased(mouseX, mouseY, GLFW.GLFW_MOUSE_BUTTON_2);
                this.screenCloseCooldown = 5;
            } else {
                this.ignoreNextXRelease = false;
            }
        }
    }
    private void handleTriggerAxis(AxisStorage storage) {
        storage.setupButtonStates();
    }

    private void handleJoystickAxis(AxisStorage storage) {
        storage.setupButtonStates();

        this.handleMovement(storage);

        if (this.handleScreenScrolling(client.currentScreen, storage)) return;

        storage.absValue = (float) MathHelper.clamp(storage.absValue / MidnightControlsConfig.getAxisMaxValue(storage.axis), 0.f, 1.f);
        if (client.currentScreen == null) {
            // Handles the look direction.
            this.handleLook(storage);
        } else {
            boolean allowMouseControl = true;

            if (this.actionGuiCooldown == 0 && MidnightControlsConfig.isMovementAxis(storage.axis) && isScreenInteractive(client.currentScreen)) {
                if (MidnightControlsConfig.isForwardButton(storage.axis, false, storage.buttonState)) {
                    allowMouseControl = this.changeFocus(client.currentScreen, NavigationDirection.UP);
                } else if (MidnightControlsConfig.isBackButton(storage.axis, false, storage.buttonState)) {
                    allowMouseControl = this.changeFocus(client.currentScreen, NavigationDirection.DOWN);
                } else if (MidnightControlsConfig.isLeftButton(storage.axis, false, storage.buttonState)) {
                    allowMouseControl = this.handleLeftRight(client.currentScreen, false);
                } else if (MidnightControlsConfig.isRightButton(storage.axis, false, storage.buttonState)) {
                    allowMouseControl = this.handleLeftRight(client.currentScreen, true);
                }
            }

            float movementX = 0.f;
            float movementY = 0.f;

            if (MidnightControlsConfig.isBackButton(storage.axis, false, (storage.value > 0 ? ButtonState.PRESS : ButtonState.RELEASE))) {
                movementY = storage.absValue;
            } else if (MidnightControlsConfig.isForwardButton(storage.axis, false, (storage.value > 0 ? ButtonState.PRESS : ButtonState.RELEASE))) {
                movementY = -storage.absValue;
            } else if (MidnightControlsConfig.isLeftButton(storage.axis, false, (storage.value > 0 ? ButtonState.PRESS : ButtonState.RELEASE))) {
                movementX = -storage.absValue;
            } else if (MidnightControlsConfig.isRightButton(storage.axis, false, (storage.value > 0 ? ButtonState.PRESS : ButtonState.RELEASE))) {
                movementX = storage.absValue;
            }

            if (client.currentScreen != null && allowMouseControl) {
                boolean moving = movementY != 0 || movementX != 0;
                if (moving) {
                /*
                    Updates the target mouse position when the initial movement stick movement is detected.
                    It prevents the cursor to jump to the old target mouse position if the user moves the cursor with the mouse.
                 */
                    if (Math.abs(prevXAxis) < storage.deadZone && Math.abs(prevYAxis) < storage.deadZone) {
                        InputManager.INPUT_MANAGER.resetMouseTarget(client);
                    }

                    this.mouseSpeedX = movementX;
                    this.mouseSpeedY = movementY;
                } else {
                    this.mouseSpeedX = 0.f;
                    this.mouseSpeedY = 0.f;
                }

                if (Math.abs(this.mouseSpeedX) >= .05f || Math.abs(this.mouseSpeedY) >= .05f) {
                    InputManager.queueMoveMousePosition(
                            this.mouseSpeedX * MidnightControlsConfig.mouseSpeed,
                            this.mouseSpeedY * MidnightControlsConfig.mouseSpeed
                    );
                }

                InventoryUtil.moveMouseToClosestSlot(client.currentScreen);
            }

            this.prevXAxis = movementX;
            this.prevYAxis = movementY;
        }
    }

    private void handleMovement(AxisStorage storage) {
        float axisValue = storage.absValue;
        if (!MidnightControlsConfig.analogMovement || (client.player != null && client.player.getVehicle() instanceof BoatEntity)) {
            axisValue = (float) (storage.absValue - storage.deadZone);
            axisValue /= (float) (1.0 - storage.deadZone);
            axisValue *= (float) storage.deadZone;
        }

        axisValue = (float) Math.min(axisValue / MidnightControlsConfig.getAxisMaxValue(storage.axis), 1);
        if (!MidnightControlsCompat.handleMovement(storage, axisValue)) {
            InputManager.BUTTON_VALUES.put(ButtonBinding.axisAsButton(storage.axis, true), storage.polarity == AxisStorage.Polarity.PLUS ? axisValue : 0.f);
            InputManager.BUTTON_VALUES.put(ButtonBinding.axisAsButton(storage.axis, false), storage.polarity == AxisStorage.Polarity.MINUS ? axisValue : 0.f);
        }
    }

    private boolean handleScreenScrolling(Screen screen, AxisStorage storage) {
        // @TODO allow rebinding to left stick
        int preferredAxis = true ? GLFW_GAMEPAD_AXIS_RIGHT_Y : GLFW_GAMEPAD_AXIS_LEFT_Y;

        if (this.controlsInput != null && this.controlsInput.focusedBinding != null) {
            if (storage.buttonState != ButtonState.NONE && !this.controlsInput.currentButtons.contains(ButtonBinding.axisAsButton(storage.axis, storage.buttonState == ButtonState.PRESS))) {

                this.controlsInput.currentButtons.add(ButtonBinding.axisAsButton(storage.axis, storage.buttonState == ButtonState.PRESS));

                int[] buttons = new int[this.controlsInput.currentButtons.size()];
                for (int i = 0; i < this.controlsInput.currentButtons.size(); i++)
                    buttons[i] = this.controlsInput.currentButtons.get(i);
                this.controlsInput.focusedBinding.setButton(buttons);

                this.controlsInput.waiting = false;
            }
            return true;
        } else if (storage.absValue >= storage.deadZone) {
            if (screen instanceof CreativeInventoryScreen creativeInventoryScreen) {
                if (storage.axis == preferredAxis) {
                    var accessor = (CreativeInventoryScreenAccessor) creativeInventoryScreen;
                    if (accessor.midnightcontrols$hasScrollbar() && storage.absValue >= storage.deadZone) {
                        creativeInventoryScreen.mouseScrolled(0.0, 0.0, 0, -storage.value);
                    }
                    return true;
                }
            } else if (screen instanceof MerchantScreen || screen instanceof StonecutterScreen) {
                if (storage.axis == preferredAxis) {
                    screen.mouseScrolled(0.0, 0.0, 0, -(storage.value * 1.5f));
                    return true;
                }
            } else if (screen instanceof AdvancementsScreen advancementsScreen) {
                if (storage.axis == GLFW_GAMEPAD_AXIS_RIGHT_X || storage.axis == GLFW_GAMEPAD_AXIS_RIGHT_Y) {
                    var accessor = (AdvancementsScreenAccessor) advancementsScreen;
                    AdvancementTab tab = accessor.getSelectedTab();
                    tab.move(storage.axis == GLFW_GAMEPAD_AXIS_RIGHT_X ? -storage.value * 5.0 : 0.0, storage.axis == GLFW_GAMEPAD_AXIS_RIGHT_Y ? -storage.value * 5.0 : 0.0);
                    return true;
                }
            } else if (screen != null) {
                if (storage.axis == preferredAxis && !handleListWidgetScrolling(screen.children(), storage.value)) {
                    screen.mouseScrolled(0.0, 0.0, 0, -(storage.value * 1.5f));
                } else if (isScreenInteractive(screen)) {
                    if (joystickCooldown == 0) {
                        switch (storage.axis) {
                            case GLFW_GAMEPAD_AXIS_LEFT_Y -> {
                                this.changeFocus(screen, storage.value > 0 ? NavigationDirection.UP : NavigationDirection.DOWN);
                                joystickCooldown = 4;
                            }
                            case GLFW_GAMEPAD_AXIS_LEFT_X -> {
                                this.handleLeftRight(screen, storage.value > 0);
                                joystickCooldown = 4;
                            }
                        }
                    }
                    return true;
                }
            }
        }
        return false;
    }
    public void handleRadialMenu(float x, float y) {
        int index = -1;
        float border = 0.3f;
        if (x < -border) {
            index = 3;
            if (y < -border) index = 0;
            else if (y > border) index = 5;
        } else if (x > border) {
            index = 4;
            if (y < -border) index = 2;
            else if (y > border) index = 7;
        } else {
            if (y < -border) index = 1;
            else if (y > border) index = 6;
        }
        if (client.currentScreen instanceof RingScreen && index > -1) RingPage.selected = index;
        if (EmotecraftCompat.isPresent() && EmotecraftCompat.isEmotecraftScreen(client.currentScreen)) EmotecraftCompat.handleEmoteSelector(index);
    }

    public boolean handleListWidgetScrolling(List<? extends Element> children, float value) {
        return children.stream().filter(element -> element instanceof SpruceEntryListWidget)
                .map(element -> (SpruceEntryListWidget<?>) element)
                .filter(AbstractSpruceWidget::isFocusedOrHovered)
                .anyMatch(element -> {
                    element.mouseScrolled(0.0, 0.0, 0, -value);
                    return true;
                }) ||
                children.stream().filter(element -> element instanceof EntryListWidget)
                    .map(element -> (EntryListWidget<?>) element)
                    .filter(element -> element.getType().isFocused())
                    .anyMatch(element -> {
                        element.mouseScrolled(0.0, 0.0, 0, -value);
                        return true;
                    });
    }

    public boolean handleAButton(@NotNull Screen screen, @NotNull Element focused) {
        if (focused instanceof PressableWidget widget) {
            widget.playDownSound(MinecraftClient.getInstance().getSoundManager());
            widget.onPress();
            return true;
        } else if (focused instanceof AbstractSprucePressableButtonWidget widget) {
            widget.playDownSound();
            widget.onPress();
            return true;
        } else if (focused instanceof SpruceLabelWidget labelWidget) {
            labelWidget.onPress();
            return true;
        } else if (focused instanceof WorldListWidget list) {
            list.getSelectedAsOptional().ifPresent(WorldListWidget.WorldEntry::play);
            return true;
        } else if (focused instanceof MultiplayerServerListWidget list) {
            var entry = list.getSelectedOrNull();
            if (entry instanceof MultiplayerServerListWidget.LanServerEntry || entry instanceof MultiplayerServerListWidget.ServerEntry) {
                ((MultiplayerScreen) screen).select(entry);
                ((MultiplayerScreen) screen).connect();
            }
        } else if (focused instanceof SpruceParentWidget) {
            var childFocused = ((SpruceParentWidget<?>) focused).getFocused();
            if (childFocused != null)
                return this.handleAButton(screen, childFocused);
        } else if (focused instanceof ParentElement widget) {
            var childFocused = widget.getFocused();
            if (childFocused != null)
                return this.handleAButton(screen, childFocused);
        } else if (PlatformFunctions.isModLoaded("yet-another-config-lib") && YACLCompat.handleAButton(screen, focused)) {
            return true;
        }
        else pressKeyboardKey(screen, GLFW_KEY_ENTER);
        return false;
    }

    /**
     * Handles the left and right buttons.
     *
     * @param screen the current screen
     * @param right true if the right button is pressed, else false
     */
    private boolean handleLeftRight(@NotNull Screen screen, boolean right) {
        if (screen instanceof SpruceScreen spruceScreen) {
            spruceScreen.onNavigation(right ? NavigationDirection.RIGHT : NavigationDirection.LEFT, false);
            this.actionGuiCooldown = 5;
            return false;
        }
        if (PlatformFunctions.isModLoaded("yet-another-config-lib") && YACLCompat.handleLeftRight(screen, right)) {
            this.actionGuiCooldown = 5;
            return false;
        }
        var focused = screen.getFocused();
        if (focused != null)
            if (this.handleRightLeftElement(focused, right))
                return this.changeFocus(screen, right ? NavigationDirection.RIGHT : NavigationDirection.LEFT);
        return true;
    }

    private boolean handleRightLeftElement(@NotNull Element element, boolean right) {
        switch (element) {
            case SpruceElement spruceElement -> {
                if (spruceElement.requiresCursor())
                    return true;
                return !spruceElement.onNavigation(right ? NavigationDirection.RIGHT : NavigationDirection.LEFT, false);
            }
            case SliderWidget slider -> {
                if (slider.active) {
                    slider.keyPressed(right ? 262 : 263, 0, 0);
                    this.actionGuiCooldown = 2; // Prevent to press too quickly the focused element, so we have to skip 5 ticks.
                    return true;
                }
            }
//            case AlwaysSelectedEntryListWidget<?> alwaysSelectedEntryListWidget -> {
//                //TODO ((EntryListWidgetAccessor) element).midnightcontrols$moveSelection(right ? EntryListWidget.MoveDirection.DOWN : EntryListWidget.MoveDirection.UP);
//                return false;
//            }
            case ParentElement entryList -> {
                var focused = entryList.getFocused();
                if (focused == null)
                    return true;
                return this.handleRightLeftElement(focused, right);
            }
            default -> {
            }
        }
        return true;
    }
    private double prevX = 0;
    private double prevY = 0;
    private double xValue;
    private AxisStorage.Polarity xPolarity;

    /**
     * Handles the look direction input.
     *
     * @param storage the state of the provided axis
     */
    public void handleLook(AxisStorage storage) {
        if (client.player == null || !(storage.axis == GLFW_GAMEPAD_AXIS_RIGHT_Y || storage.axis == GLFW_GAMEPAD_AXIS_RIGHT_X)) return;
        // Handles the look direction.
        if (MidnightControlsConfig.cameraMode == CameraMode.FLAT) handleFlatLook(storage);
        else handleAdaptiveLook(storage);
        MidnightControlsCompat.handleCamera(this.targetYaw, this.targetPitch);
    }
    private void handleFlatLook(AxisStorage storage) {
        if (storage.polarity != AxisStorage.Polarity.ZERO) {
            double rotation = Math.pow(storage.value, 2.0) * 0.11D * storage.polarity.multiplier;

            if (storage.axis == GLFW_GAMEPAD_AXIS_RIGHT_Y) this.targetPitch = rotation * MidnightControlsConfig.getRightYAxisSign() * MidnightControlsConfig.yAxisRotationSpeed / 2;
            else this.targetYaw = rotation * MidnightControlsConfig.getRightXAxisSign() * MidnightControlsConfig.rotationSpeed / 2;
        }
    }
    private void handleAdaptiveLook(AxisStorage storage) {
        if (storage.axis == GLFW_GAMEPAD_AXIS_RIGHT_X) {
            xValue = storage.value;
            xPolarity = storage.polarity;
        }
        else {
            double yStep = (MidnightControlsConfig.yAxisRotationSpeed / 50) * 0.6000000238418579 + 0.20000000298023224;
            double xStep = (MidnightControlsConfig.rotationSpeed / 50) * 0.6000000238418579 + 0.20000000298023224;
            float yValue = storage.value;
            AxisStorage.Polarity yPolarity = storage.polarity;

            double cursorDeltaX = 2 * xValue - this.prevX;
            double cursorDeltaY = 2 * yValue - this.prevY;
            boolean slowdown = client.options.getPerspective().isFirstPerson() && Objects.requireNonNull(client.player).isUsingSpyglass();
            double x = cursorDeltaX * xStep * (slowdown ? xStep : 1);
            double y = cursorDeltaY * yStep * (slowdown ? yStep : 1);

            double powXValue = Math.pow(x, 2.0);
            double powYValue = Math.pow(y, 2.0);

            if (xPolarity != AxisStorage.Polarity.ZERO) {
                double sign = MidnightControlsConfig.getRightXAxisSign() * MidnightControlsConfig.rotationSpeed;
                this.targetYaw = sign * powXValue * 0.11D * xPolarity.multiplier;
            }
            if (yPolarity != AxisStorage.Polarity.ZERO) {
                double sign = MidnightControlsConfig.getRightYAxisSign() * MidnightControlsConfig.yAxisRotationSpeed;
                this.targetPitch = sign * powYValue * 0.11D * yPolarity.multiplier;
            }

            this.prevY = yValue;
            this.prevX = xValue;
        }
    }
    public void handleTouchscreenLook(AxisStorage storage) {
        if (storage.polarity != AxisStorage.Polarity.ZERO) {
            double rotation = storage.value * 0.11D * MidnightControlsConfig.touchSpeed/5;

            if (storage.axis == GLFW_GAMEPAD_AXIS_RIGHT_Y) this.targetPitch = rotation;
            else this.targetYaw = rotation;
        }
    }

    private boolean changeFocus(@NotNull Screen screen, NavigationDirection direction) {
        if (!isScreenInteractive(screen) && !screen.getClass().getCanonicalName().contains("me.jellysquid.mods.sodium.client.gui")) return false;
        try {
            if (screen instanceof SpruceScreen spruceScreen) {
                if (spruceScreen.onNavigation(direction, false)) {
                    this.actionGuiCooldown = 5;
                }
                return true;
            }
            switch (direction) {
                case UP -> pressKeyboardKey(screen, GLFW.GLFW_KEY_UP);
                case DOWN -> pressKeyboardKey(screen, GLFW.GLFW_KEY_DOWN);
                case LEFT -> pressKeyboardKey(screen, GLFW.GLFW_KEY_LEFT);
                case RIGHT -> pressKeyboardKey(screen, GLFW.GLFW_KEY_RIGHT);
            }
            this.actionGuiCooldown = 5;
            return true;
        } catch (Exception exception) {MidnightControls.warn("Unknown exception encountered while trying to change focus: "+exception);}
        return false;
    }

    /**
     * Tries to go back.
     *
     * @param screen the current screen
     * @return true if successful, else false
     */
    public boolean tryGoBack(@NotNull Screen screen) {
        var set = ImmutableSet.of("gui.back", "gui.done", "gui.cancel", "gui.toTitle", "gui.toMenu");
        if (screen instanceof KeybindsScreen) return false;

        return screen.children().stream().filter(element -> element instanceof PressableWidget)
                .map(element -> (PressableWidget) element)
                .filter(element -> element.getMessage() != null && element.getMessage().getContent() != null)
                .anyMatch(element -> {
                    if (element.getMessage().getContent() instanceof TranslatableTextContent translatableText) {
                        if (set.stream().anyMatch(key -> translatableText.getKey().equals(key))) {
                            element.onPress();
                            return true;
                        }
                    }
                    return false;
                });
    }

    public static boolean isScreenInteractive(@NotNull Screen screen) {
        return !(screen instanceof HandledScreen || MidnightControlsConfig.joystickAsMouse || MidnightControlsConfig.mouseScreens.stream().anyMatch(a -> screen.getClass().toString().contains(a))
                || (screen instanceof SpruceScreen && ((SpruceScreen) screen).requiresCursor())
                || MidnightControlsCompat.requireMouseOnScreen(screen));
    }

    public void pressKeyboardKey(MinecraftClient client, int key) {
        client.keyboard.onKey(client.getWindow().getHandle(), key, 0, 1, 0);
    }
    public void pressKeyboardKey(Screen screen, int key) {
        screen.keyPressed(key, 0, 1);
    }
}
