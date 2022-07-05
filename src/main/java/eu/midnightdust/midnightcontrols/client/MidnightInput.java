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
import dev.lambdaurora.spruceui.widget.container.SpruceEntryListWidget;
import eu.midnightdust.midnightcontrols.MidnightControls;
import eu.midnightdust.midnightcontrols.client.compat.EMICompat;
import eu.midnightdust.midnightcontrols.client.compat.MidnightControlsCompat;
import eu.midnightdust.midnightcontrols.client.compat.SodiumCompat;
import eu.midnightdust.midnightcontrols.client.controller.ButtonBinding;
import eu.midnightdust.midnightcontrols.client.controller.Controller;
import eu.midnightdust.midnightcontrols.client.controller.InputManager;
import eu.midnightdust.midnightcontrols.client.gui.TouchscreenOverlay;
import eu.midnightdust.midnightcontrols.client.gui.widget.ControllerControlsWidget;
import eu.midnightdust.midnightcontrols.client.mixin.AdvancementsScreenAccessor;
import eu.midnightdust.midnightcontrols.client.mixin.CreativeInventoryScreenAccessor;
import eu.midnightdust.midnightcontrols.client.mixin.EntryListWidgetAccessor;
import eu.midnightdust.midnightcontrols.client.util.HandledScreenAccessor;
import eu.midnightdust.midnightcontrols.client.util.MouseAccessor;
import dev.lambdaurora.spruceui.navigation.NavigationDirection;
import dev.lambdaurora.spruceui.screen.SpruceScreen;
import dev.lambdaurora.spruceui.widget.AbstractSprucePressableButtonWidget;
import dev.lambdaurora.spruceui.widget.SpruceElement;
import dev.lambdaurora.spruceui.widget.SpruceLabelWidget;
import dev.lambdaurora.spruceui.widget.container.SpruceParentWidget;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.ParentElement;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.advancement.AdvancementTab;
import net.minecraft.client.gui.screen.advancement.AdvancementsScreen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.MerchantScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerServerListWidget;
import net.minecraft.client.gui.screen.world.WorldListWidget;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.client.gui.widget.PressableWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.math.MathHelper;
import org.aperlambda.lambdacommon.utils.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWGamepadState;

import java.util.*;

import static org.lwjgl.glfw.GLFW.*;

/**
 * Represents the midnightcontrols' input handler.
 *
 * @author LambdAurora
 * @version 1.7.0
 * @since 1.0.0
 */
public class MidnightInput {
    private static final Map<Integer, Integer> BUTTON_COOLDOWNS = new HashMap<>();
    // Cooldowns
    public int actionGuiCooldown = 0;
    public boolean ignoreNextARelease = false;
    private double targetYaw = 0.0;
    private double targetPitch = 0.0;
    private float prevXAxis = 0.f;
    private float prevYAxis = 0.f;
    private int targetMouseX = 0;
    private int targetMouseY = 0;
    private float mouseSpeedX = 0.f;
    private float mouseSpeedY = 0.f;
    public int inventoryInteractionCooldown = 0;

    private ControllerControlsWidget controlsInput = null;

    public MidnightInput() {}

    /**
     * This method is called every Minecraft tick.
     *
     * @param client the client instance
     */
    public void tick(@NotNull MinecraftClient client) {
        this.targetYaw = 0.F;
        this.targetPitch = 0.F;

        // Handles the key bindings.
        if (MidnightControlsClient.BINDING_LOOK_UP.isPressed()) {
            this.handleLook(client, GLFW_GAMEPAD_AXIS_RIGHT_Y, 0.8F, 2);
        } else if (MidnightControlsClient.BINDING_LOOK_DOWN.isPressed()) {
            this.handleLook(client, GLFW_GAMEPAD_AXIS_RIGHT_Y, 0.8F, 1);
        }
        if (MidnightControlsClient.BINDING_LOOK_LEFT.isPressed()) {
            this.handleLook(client, GLFW_GAMEPAD_AXIS_RIGHT_X, 0.8F, 2);
        } else if (MidnightControlsClient.BINDING_LOOK_RIGHT.isPressed()) {
            this.handleLook(client, GLFW_GAMEPAD_AXIS_RIGHT_X, 0.8F, 1);
        }

        InputManager.INPUT_MANAGER.tick(client);
    }

    /**
     * This method is called every Minecraft tick for controller input update.
     *
     * @param client the client instance
     */
    public void tickController(@NotNull MinecraftClient client) {
        BUTTON_COOLDOWNS.entrySet().stream().filter(entry -> entry.getValue() > 0)
                .forEach(entry -> BUTTON_COOLDOWNS.put(entry.getKey(), entry.getValue() - 1));
        // Decreases the cooldown for GUI actions.
        if (this.actionGuiCooldown > 0)
            --this.actionGuiCooldown;

        InputManager.updateStates();

        var controller = MidnightControlsConfig.getController();

        if (controller.isConnected()) {
            var state = controller.getState();
            this.fetchButtonInput(client, state, false);
            this.fetchAxeInput(client, state, false);
        }
        MidnightControlsConfig.getSecondController().filter(Controller::isConnected)
                .ifPresent(joycon -> {
                    GLFWGamepadState state = joycon.getState();
                    this.fetchButtonInput(client, state, true);
                    this.fetchAxeInput(client, state, true);
                });

        boolean allowInput = this.controlsInput == null || this.controlsInput.focusedBinding == null;

        if (allowInput)
            InputManager.updateBindings(client);

        if (this.controlsInput != null
                && InputManager.STATES.int2ObjectEntrySet().parallelStream().map(Map.Entry::getValue).allMatch(ButtonState::isUnpressed)) {
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
     * This method is called before the screen is rendered.
     *
     * @param client the client instance
     * @param screen the screen to render
     */
    public void onPreRenderScreen(@NotNull MinecraftClient client, @NotNull Screen screen) {
        if (!isScreenInteractive(screen)) {
            InputManager.INPUT_MANAGER.updateMousePosition(client);
        }
    }

    /**
     * This method is called when Minecraft renders.
     *
     * @param client the client instance
     */
    public void onRender(float tickDelta, @NotNull MinecraftClient client) {
        if (!(client.currentScreen == null || client.currentScreen instanceof TouchscreenOverlay))
            return;

        var player = client.player;
        if (player == null)
            return;

        if (this.targetYaw != 0.f || this.targetPitch != 0.f) {
            float rotationYaw = (float) (player.prevYaw + (this.targetYaw / 0.10) * tickDelta);
            float rotationPitch = (float) (player.prevPitch + (this.targetPitch / 0.10) * tickDelta);
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
     * @param client the client instance
     * @param windowWidth the window width
     * @param windowHeight the window height
     */
    public void onScreenOpen(@NotNull MinecraftClient client, int windowWidth, int windowHeight) {
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

    private void fetchButtonInput(@NotNull MinecraftClient client, @NotNull GLFWGamepadState gamepadState, boolean leftJoycon) {
        var buffer = gamepadState.buttons();
        for (int i = 0; i < buffer.limit(); i++) {
            int btn = leftJoycon ? ButtonBinding.controller2Button(i) : i;
            boolean btnState = buffer.get() == (byte) 1;
            var state = ButtonState.NONE;
            var previousState = InputManager.STATES.getOrDefault(btn, ButtonState.NONE);

            if (btnState != previousState.isPressed()) {
                state = btnState ? ButtonState.PRESS : ButtonState.RELEASE;
                this.handleButton(client, btn, btnState ? 0 : 1, btnState);
                if (btnState)
                    BUTTON_COOLDOWNS.put(btn, 5);
            } else if (btnState) {
                state = ButtonState.REPEAT;
                if (BUTTON_COOLDOWNS.getOrDefault(btn, 0) == 0) {
                    BUTTON_COOLDOWNS.put(btn, 5);
                    this.handleButton(client, btn, 2, true);
                }
            }

            InputManager.STATES.put(btn, state);
        }
    }

    private void fetchAxeInput(@NotNull MinecraftClient client, @NotNull GLFWGamepadState gamepadState, boolean leftJoycon) {
        var buffer = gamepadState.axes();
        for (int i = 0; i < buffer.limit(); i++) {
            int axis = leftJoycon ? ButtonBinding.controller2Button(i) : i;
            float value = buffer.get();
            float absValue = Math.abs(value);

            if (i == GLFW.GLFW_GAMEPAD_AXIS_LEFT_Y)
                value *= -1.0F;

            int state = value > MidnightControlsConfig.rightDeadZone ? 1 : (value < -MidnightControlsConfig.rightDeadZone ? 2 : 0);
            this.handleAxe(client, axis, value, absValue, state);
        }
    }

    public void handleButton(@NotNull MinecraftClient client, int button, int action, boolean state) {
        if (this.controlsInput != null && this.controlsInput.focusedBinding != null) {
            if (action == 0 && !this.controlsInput.currentButtons.contains(button)) {
                this.controlsInput.currentButtons.add(button);

                var buttons = new int[this.controlsInput.currentButtons.size()];
                for (int i = 0; i < this.controlsInput.currentButtons.size(); i++)
                    buttons[i] = this.controlsInput.currentButtons.get(i);
                this.controlsInput.focusedBinding.setButton(buttons);

                this.controlsInput.waiting = false;
            }
            return;
        }

        if (action == 0 || action == 2) {
            if (client.currentScreen != null
                    && (button == GLFW.GLFW_GAMEPAD_BUTTON_DPAD_UP || button == GLFW.GLFW_GAMEPAD_BUTTON_DPAD_DOWN
                    || button == GLFW.GLFW_GAMEPAD_BUTTON_DPAD_LEFT || button == GLFW.GLFW_GAMEPAD_BUTTON_DPAD_RIGHT)) {
                if (this.actionGuiCooldown == 0) {
                    if (button == GLFW.GLFW_GAMEPAD_BUTTON_DPAD_UP) {
                        this.changeFocus(client.currentScreen, NavigationDirection.UP);
                    } else if (button == GLFW.GLFW_GAMEPAD_BUTTON_DPAD_DOWN) {
                        this.changeFocus(client.currentScreen, NavigationDirection.DOWN);
                    } else this.handleLeftRight(client.currentScreen, button != GLFW.GLFW_GAMEPAD_BUTTON_DPAD_LEFT);
                }
                return;
            }
        }

        if (action == 1) {
            if (button == GLFW.GLFW_GAMEPAD_BUTTON_A && client.currentScreen != null) {
                if (this.actionGuiCooldown == 0) {
                    var focused = client.currentScreen.getFocused();
                    if (focused != null && isScreenInteractive(client.currentScreen)) {
                        if (this.handleAButton(client.currentScreen, focused)) {
                            this.actionGuiCooldown = 5; // Prevent to press too quickly the focused element, so we have to skip 5 ticks.
                            return;
                        }
                    }
                }
            }

            if (button == GLFW.GLFW_GAMEPAD_BUTTON_B) {
                if (client.currentScreen != null && client.currentScreen.getClass() != TitleScreen.class) {
                    if (!MidnightControlsCompat.handleMenuBack(client, client.currentScreen))
                        if (!this.tryGoBack(client.currentScreen))
                            client.currentScreen.close();
                    return;
                }
            }
        }

        if (button == GLFW.GLFW_GAMEPAD_BUTTON_A && client.currentScreen != null && !isScreenInteractive(client.currentScreen)
                && this.actionGuiCooldown == 0) {
            if (client.currentScreen instanceof HandledScreen<?> handledScreen && ((HandledScreenAccessor) handledScreen).midnightcontrols$getSlotAt(
                    client.mouse.getX() * (double) client.getWindow().getScaledWidth() / (double) client.getWindow().getWidth(),
                    client.mouse.getY() * (double) client.getWindow().getScaledHeight() / (double) client.getWindow().getHeight()) != null) return;
            if (!this.ignoreNextARelease) {
                double mouseX = client.mouse.getX() * (double) client.getWindow().getScaledWidth() / (double) client.getWindow().getWidth();
                double mouseY = client.mouse.getY() * (double) client.getWindow().getScaledHeight() / (double) client.getWindow().getHeight();
                if (action == 0) {
                    Screen.wrapScreenError(() -> client.currentScreen.mouseClicked(mouseX, mouseY, GLFW.GLFW_MOUSE_BUTTON_1),
                            "mouseClicked event handler", client.currentScreen.getClass().getCanonicalName());
                } else if (action == 1) {
                    Screen.wrapScreenError(() -> client.currentScreen.mouseReleased(mouseX, mouseY, GLFW.GLFW_MOUSE_BUTTON_1),
                            "mouseReleased event handler", client.currentScreen.getClass().getCanonicalName());
                }
                //this.actionGuiCooldown = 5;
            } else {
                this.ignoreNextARelease = false;
            }
        }
    }
    /**

    /**
     * Tries to go back.
     *
     * @param screen the current screen
     * @return true if successful, else false
     */
    public boolean tryGoBack(@NotNull Screen screen) {
        var set = ImmutableSet.of("gui.back", "gui.done", "gui.cancel", "gui.toTitle", "gui.toMenu");
        return screen.children().stream().filter(element -> element instanceof PressableWidget)
                .map(element -> (PressableWidget) element)
                .filter(element -> element.getMessage() != null && element.getMessage().getContent() != null)
                .anyMatch(element -> {
                    if (set.stream().anyMatch(key -> element.getMessage().getContent().toString().equals(key))) {
                        element.onPress();
                        return true;
                    }
                    return false;
                });
    }

    private double getDeadZoneValue(int axis) {
        return (axis == GLFW_GAMEPAD_AXIS_LEFT_X || axis == GLFW_GAMEPAD_AXIS_LEFT_Y) ? MidnightControlsConfig.leftDeadZone
                : MidnightControlsConfig.rightDeadZone;
    }

    private void handleAxe(@NotNull MinecraftClient client, int axis, float value, float absValue, int state) {
        int asButtonState = value > .5f ? 1 : (value < -.5f ? 2 : 0);


        if (axis == GLFW_GAMEPAD_AXIS_LEFT_TRIGGER || axis == GLFW_GAMEPAD_AXIS_RIGHT_TRIGGER
                || axis == ButtonBinding.controller2Button(GLFW.GLFW_GAMEPAD_AXIS_LEFT_TRIGGER)
                || axis == ButtonBinding.controller2Button(GLFW.GLFW_GAMEPAD_AXIS_RIGHT_TRIGGER)) {
            if (asButtonState == 2) {
                asButtonState = 0;
            }
            else {
                // Fixes Triggers not working correctly on some controllers
                if (MidnightControlsConfig.triggerFix) {
                    value = 1.0f;
                    absValue = 1.0f;
                    state = 1;
                    asButtonState = 1;
                }
                if (MidnightControlsConfig.debug) System.out.println(axis + " "+ value + " " + absValue + " " + state);
            }
        }

        {
            boolean currentPlusState = asButtonState == 1;
            boolean currentMinusState = asButtonState == 2;
            var previousPlusState = InputManager.STATES.getOrDefault(ButtonBinding.axisAsButton(axis, true), ButtonState.NONE);
            var previousMinusState = InputManager.STATES.getOrDefault(ButtonBinding.axisAsButton(axis, false), ButtonState.NONE);

            if (currentPlusState != previousPlusState.isPressed()) {
                InputManager.STATES.put(ButtonBinding.axisAsButton(axis, true), currentPlusState ? ButtonState.PRESS : ButtonState.RELEASE);
                if (currentPlusState)
                    BUTTON_COOLDOWNS.put(ButtonBinding.axisAsButton(axis, true), 5);
            } else if (currentPlusState) {
                InputManager.STATES.put(ButtonBinding.axisAsButton(axis, true), ButtonState.REPEAT);
                if (BUTTON_COOLDOWNS.getOrDefault(ButtonBinding.axisAsButton(axis, true), 0) == 0) {
                    BUTTON_COOLDOWNS.put(ButtonBinding.axisAsButton(axis, true), 5);
                }
            }

            if (currentMinusState != previousMinusState.isPressed()) {
                InputManager.STATES.put(ButtonBinding.axisAsButton(axis, false), currentMinusState ? ButtonState.PRESS : ButtonState.RELEASE);
                if (currentMinusState)
                    BUTTON_COOLDOWNS.put(ButtonBinding.axisAsButton(axis, false), 5);
            } else if (currentMinusState) {
                InputManager.STATES.put(ButtonBinding.axisAsButton(axis, false), ButtonState.REPEAT);
                if (BUTTON_COOLDOWNS.getOrDefault(ButtonBinding.axisAsButton(axis, false), 0) == 0) {
                    BUTTON_COOLDOWNS.put(ButtonBinding.axisAsButton(axis, false), 5);
                }
            }

            double deadZone = this.getDeadZoneValue(axis);
            float axisValue = absValue < deadZone ? 0.f : (float) (absValue - deadZone);
            axisValue /= (1.0 - deadZone);

            axisValue = (float) Math.min(axisValue / MidnightControlsConfig.getAxisMaxValue(axis), 1);
            if (currentPlusState)
                InputManager.BUTTON_VALUES.put(ButtonBinding.axisAsButton(axis, true), axisValue);
            else
                InputManager.BUTTON_VALUES.put(ButtonBinding.axisAsButton(axis, true), 0.f);
            if (currentMinusState)
                InputManager.BUTTON_VALUES.put(ButtonBinding.axisAsButton(axis, false), axisValue);
            else
                InputManager.BUTTON_VALUES.put(ButtonBinding.axisAsButton(axis, false), 0.f);
        }

        double deadZone = this.getDeadZoneValue(axis);

        if (this.controlsInput != null && this.controlsInput.focusedBinding != null) {
            if (asButtonState != 0 && !this.controlsInput.currentButtons.contains(ButtonBinding.axisAsButton(axis, asButtonState == 1))) {

                this.controlsInput.currentButtons.add(ButtonBinding.axisAsButton(axis, asButtonState == 1));

                int[] buttons = new int[this.controlsInput.currentButtons.size()];
                for (int i = 0; i < this.controlsInput.currentButtons.size(); i++)
                    buttons[i] = this.controlsInput.currentButtons.get(i);
                this.controlsInput.focusedBinding.setButton(buttons);

                this.controlsInput.waiting = false;
            }
            return;
        } else if (client.currentScreen instanceof CreativeInventoryScreen creativeInventoryScreen) {
            if (axis == GLFW_GAMEPAD_AXIS_RIGHT_Y) {
                var accessor = (CreativeInventoryScreenAccessor) creativeInventoryScreen;
                // @TODO allow rebinding to left stick
                if (accessor.midnightcontrols$hasScrollbar() && absValue >= deadZone) {
                    creativeInventoryScreen.mouseScrolled(0.0, 0.0, -value);
                }
                return;
            }
        } else if (client.currentScreen instanceof MerchantScreen merchantScreen) {
            if (axis == GLFW_GAMEPAD_AXIS_RIGHT_Y) {
                // @TODO allow rebinding to left stick
                if (absValue >= deadZone) {
                    merchantScreen.mouseScrolled(0.0, 0.0, -(value * 1.5f));
                }
                return;
            }
        } else if (client.currentScreen instanceof AdvancementsScreen advancementsScreen) {
            if (axis == GLFW_GAMEPAD_AXIS_RIGHT_X || axis == GLFW_GAMEPAD_AXIS_RIGHT_Y) {
                var accessor = (AdvancementsScreenAccessor) advancementsScreen;
                if (absValue >= deadZone) {
                    AdvancementTab tab = accessor.getSelectedTab();
                    tab.move(axis == GLFW_GAMEPAD_AXIS_RIGHT_X ? -value * 5.0 : 0.0, axis == GLFW_GAMEPAD_AXIS_RIGHT_Y ? -value * 5.0 : 0.0);
                }
                return;
            }
        } else if (client.currentScreen != null) {
            if (axis == GLFW_GAMEPAD_AXIS_RIGHT_Y && absValue >= deadZone) {
                float finalValue = value;
                client.currentScreen.children().stream().filter(element -> element instanceof EntryListWidget)
                        .map(element -> (EntryListWidget<?>) element)
                        .filter(element -> element.getType().isFocused())
                        .anyMatch(element -> {
                            element.mouseScrolled(0.0, 0.0, -finalValue);
                            return true;
                        });
                client.currentScreen.children().stream().filter(element -> element instanceof SpruceEntryListWidget)
                        .map(element -> (SpruceEntryListWidget<?>) element)
                        .filter(element -> element.getType().isFocused())
                        .anyMatch(element -> {
                            MidnightControls.get().log(String.valueOf(finalValue));
                            element.mouseScrolled(0.0, 0.0, -finalValue);
                            return true;
                        });
                return;
            }
        }

        absValue -= deadZone;
        absValue /= (1.0 - deadZone);
        absValue = (float) MathHelper.clamp(absValue / MidnightControlsConfig.getAxisMaxValue(axis), 0.f, 1.f);
        if (client.currentScreen == null) {
            // Handles the look direction.
            this.handleLook(client, axis, absValue, state);
        } else {
            boolean allowMouseControl = true;

            if (this.actionGuiCooldown == 0 && MidnightControlsConfig.isMovementAxis(axis) && isScreenInteractive(client.currentScreen)) {
                if (MidnightControlsConfig.isForwardButton(axis, false, asButtonState)) {
                    allowMouseControl = this.changeFocus(client.currentScreen, NavigationDirection.UP);
                } else if (MidnightControlsConfig.isBackButton(axis, false, asButtonState)) {
                    allowMouseControl = this.changeFocus(client.currentScreen, NavigationDirection.DOWN);
                } else if (MidnightControlsConfig.isLeftButton(axis, false, asButtonState)) {
                    allowMouseControl = this.handleLeftRight(client.currentScreen, false);
                } else if (MidnightControlsConfig.isRightButton(axis, false, asButtonState)) {
                    allowMouseControl = this.handleLeftRight(client.currentScreen, true);
                }
            }

            float movementX = 0.f;
            float movementY = 0.f;

            if (MidnightControlsConfig.isBackButton(axis, false, (value > 0 ? 1 : 2))) {
                movementY = absValue;
            } else if (MidnightControlsConfig.isForwardButton(axis, false, (value > 0 ? 1 : 2))) {
                movementY = -absValue;
            } else if (MidnightControlsConfig.isLeftButton(axis, false, (value > 0 ? 1 : 2))) {
                movementX = -absValue;
            } else if (MidnightControlsConfig.isRightButton(axis, false, (value > 0 ? 1 : 2))) {
                movementX = absValue;
            }

            if (client.currentScreen != null && allowMouseControl) {
                boolean moving = movementY != 0 || movementX != 0;
                if (moving) {
                /*
                    Updates the target mouse position when the initial movement stick movement is detected.
                    It prevents the cursor to jump to the old target mouse position if the user moves the cursor with the mouse.
                 */
                    if (Math.abs(prevXAxis) < deadZone && Math.abs(prevYAxis) < deadZone) {
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

                this.moveMouseToClosestSlot(client, client.currentScreen);
            }

            this.prevXAxis = movementX;
            this.prevYAxis = movementY;
        }
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
        }
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
        var focused = screen.getFocused();
        if (focused != null)
            if (this.handleRightLeftElement(focused, right))
                return this.changeFocus(screen, right ? NavigationDirection.RIGHT : NavigationDirection.LEFT);
        return true;
    }

    private boolean handleRightLeftElement(@NotNull Element element, boolean right) {
        if (element instanceof SpruceElement spruceElement) {
            if (spruceElement.requiresCursor())
                return true;
            return !spruceElement.onNavigation(right ? NavigationDirection.RIGHT : NavigationDirection.LEFT, false);
        }
        if (element instanceof SliderWidget slider) {
            slider.keyPressed(right ? 262 : 263, 0, 0);
            this.actionGuiCooldown = 2; // Prevent to press too quickly the focused element, so we have to skip 5 ticks.
            return false;
        } else if (element instanceof AlwaysSelectedEntryListWidget) {
            ((EntryListWidgetAccessor) element).midnightcontrols$moveSelection(right ? EntryListWidget.MoveDirection.UP : EntryListWidget.MoveDirection.DOWN);
            return false;
        } else if (element instanceof ParentElement entryList) {
            var focused = entryList.getFocused();
            if (focused == null)
                return true;
            return this.handleRightLeftElement(focused, right);
        }
        return true;
    }

    /**
     * Handles the look direction input.
     *
     * @param client the client instance
     * @param axis the axis to change
     * @param value the value of the look
     * @param state the state
     */
    public void handleLook(@NotNull MinecraftClient client, int axis, float value, int state) {
        // Handles the look direction.
        if (client.player != null) {
            double powValue = Math.pow(value, 2.0);
            if (axis == GLFW_GAMEPAD_AXIS_RIGHT_Y) {
                if (state == 2) {
                    this.targetPitch = -MidnightControlsConfig.getRightYAxisSign() * (MidnightControlsConfig.yAxisRotationSpeed * powValue) * 0.11D;
                } else if (state == 1) {
                    this.targetPitch = MidnightControlsConfig.getRightYAxisSign() * (MidnightControlsConfig.yAxisRotationSpeed * powValue) * 0.11D;
                }
            }
            if (axis == GLFW_GAMEPAD_AXIS_RIGHT_X) {
                if (state == 2) {
                    this.targetYaw = -MidnightControlsConfig.getRightXAxisSign() * (MidnightControlsConfig.rotationSpeed * powValue) * 0.11D;
                } else if (state == 1) {
                    this.targetYaw = MidnightControlsConfig.getRightXAxisSign() * (MidnightControlsConfig.rotationSpeed * powValue) * 0.11D;
                }
            }
        }
    }

    private boolean changeFocus(@NotNull Screen screen, NavigationDirection direction) {
        if (!isScreenInteractive(screen) && !screen.getClass().getCanonicalName().contains("me.jellysquid.mods.sodium.client.gui")) return false;
        try {
            if (screen instanceof SpruceScreen spruceScreen) {
                if (spruceScreen.onNavigation(direction, false)) {
                    this.actionGuiCooldown = 5;
                }
                return false;
            }
            if (FabricLoader.getInstance().isModLoaded("sodium"))
                SodiumCompat.handleInput(screen, direction.isLookingForward());
            if (!screen.changeFocus(direction.isLookingForward())) {
                if (screen.changeFocus(direction.isLookingForward())) {
                    this.actionGuiCooldown = 5;
                    return false;
                }
                return true;
            } else {
                this.actionGuiCooldown = 5;
                return false;
            }
        } catch (Exception exception) {MidnightControls.get().warn("Unknown exception encountered while trying to change focus: "+exception);}
        return false;
    }

    public static boolean isScreenInteractive(@NotNull Screen screen) {
        return !(screen instanceof HandledScreen || MidnightControlsConfig.mouseScreens.stream().anyMatch(a -> screen.getClass().toString().contains(a))
                || (screen instanceof SpruceScreen && ((SpruceScreen) screen).requiresCursor())
                || MidnightControlsCompat.requireMouseOnScreen(screen));
    }

    // Inspired from https://github.com/MrCrayfish/Controllable/blob/1.14.X/src/main/java/com/mrcrayfish/controllable/client/ControllerInput.java#L686.
    private void moveMouseToClosestSlot(@NotNull MinecraftClient client, @Nullable Screen screen) {
        // Makes the mouse attracted to slots. This helps with selecting items when using a controller.
        if (screen instanceof HandledScreen inventoryScreen) {
            var accessor = (HandledScreenAccessor) inventoryScreen;
            int guiLeft = accessor.getX();
            int guiTop = accessor.getY();
            int mouseX = (int) (targetMouseX * (double) client.getWindow().getScaledWidth() / (double) client.getWindow().getWidth());
            int mouseY = (int) (targetMouseY * (double) client.getWindow().getScaledHeight() / (double) client.getWindow().getHeight());

            // Finds the closest slot in the GUI within 14 pixels.
            Optional<Pair<Slot, Double>> closestSlot = inventoryScreen.getScreenHandler().slots.parallelStream()
                    .map(slot -> {
                        int x = guiLeft + slot.x + 8;
                        int y = guiTop + slot.y + 8;

                        // Distance between the slot and the cursor.
                        double distance = Math.sqrt(Math.pow(x - mouseX, 2) + Math.pow(y - mouseY, 2));
                        return Pair.of(slot, distance);
                    }).filter(entry -> entry.value <= 14.0)
                    .min(Comparator.comparingDouble(p -> p.value));

            if (closestSlot.isPresent()) {
                var slot = closestSlot.get().key;
                if (slot.hasStack() || !client.player.getInventory().getMainHandStack().isEmpty()) {
                    int slotCenterXScaled = guiLeft + slot.x + 8;
                    int slotCenterYScaled = guiTop + slot.y + 8;
                    int slotCenterX = (int) (slotCenterXScaled / ((double) client.getWindow().getScaledWidth() / (double) client.getWindow().getWidth()));
                    int slotCenterY = (int) (slotCenterYScaled / ((double) client.getWindow().getScaledHeight() / (double) client.getWindow().getHeight()));
                    double deltaX = slotCenterX - targetMouseX;
                    double deltaY = slotCenterY - targetMouseY;

                    if (mouseX != slotCenterXScaled || mouseY != slotCenterYScaled) {
                        this.targetMouseX += deltaX * 0.75;
                        this.targetMouseY += deltaY * 0.75;
                    } else {
                        this.mouseSpeedX *= 0.3F;
                        this.mouseSpeedY *= 0.3F;
                    }
                    this.mouseSpeedX *= .75F;
                    this.mouseSpeedY *= .75F;
                } else {
                    this.mouseSpeedX *= .1F;
                    this.mouseSpeedY *= .1F;
                }
            } else {
                this.mouseSpeedX *= .3F;
                this.mouseSpeedY *= .3F;
            }
        } else {
            this.mouseSpeedX = 0.F;
            this.mouseSpeedY = 0.F;
        }
    }
}
