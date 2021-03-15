/*
 * Copyright © 2020 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of LambdaControls.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package me.lambdaurora.lambdacontrols.client;

import com.google.common.collect.ImmutableSet;
import me.lambdaurora.lambdacontrols.client.compat.LambdaControlsCompat;
import me.lambdaurora.lambdacontrols.client.controller.ButtonBinding;
import me.lambdaurora.lambdacontrols.client.controller.Controller;
import me.lambdaurora.lambdacontrols.client.controller.InputManager;
import me.lambdaurora.lambdacontrols.client.gui.TouchscreenOverlay;
import me.lambdaurora.lambdacontrols.client.gui.widget.ControllerControlsWidget;
import me.lambdaurora.lambdacontrols.client.mixin.AdvancementsScreenAccessor;
import me.lambdaurora.lambdacontrols.client.mixin.CreativeInventoryScreenAccessor;
import me.lambdaurora.lambdacontrols.client.mixin.EntryListWidgetAccessor;
import me.lambdaurora.lambdacontrols.client.util.HandledScreenAccessor;
import me.lambdaurora.lambdacontrols.client.util.MouseAccessor;
import me.lambdaurora.spruceui.navigation.NavigationDirection;
import me.lambdaurora.spruceui.screen.SpruceScreen;
import me.lambdaurora.spruceui.widget.AbstractSprucePressableButtonWidget;
import me.lambdaurora.spruceui.widget.SpruceElement;
import me.lambdaurora.spruceui.widget.SpruceLabelWidget;
import me.lambdaurora.spruceui.widget.SpruceWidget;
import me.lambdaurora.spruceui.widget.container.SpruceParentWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.ParentElement;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.advancement.AdvancementTab;
import net.minecraft.client.gui.screen.advancement.AdvancementsScreen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerServerListWidget;
import net.minecraft.client.gui.screen.pack.PackScreen;
import net.minecraft.client.gui.screen.world.WorldListWidget;
import net.minecraft.client.gui.widget.AbstractPressableButtonWidget;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.MathHelper;
import org.aperlambda.lambdacommon.utils.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWGamepadState;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static me.lambdaurora.lambdacontrols.client.controller.ButtonBinding.axisAsButton;
import static me.lambdaurora.lambdacontrols.client.controller.InputManager.INPUT_MANAGER;
import static org.lwjgl.glfw.GLFW.*;

/**
 * Represents the LambdaControls' input handler.
 *
 * @author LambdAurora
 * @version 1.4.3
 * @since 1.0.0
 */
public class LambdaInput {
    private static final Map<Integer, Integer> BUTTON_COOLDOWNS = new HashMap<>();
    private final LambdaControlsConfig config;
    // Cooldowns
    private int actionGuiCooldown = 0;
    private boolean ignoreNextARelease = false;
    private double targetYaw = 0.0;
    private double targetPitch = 0.0;
    private float prevXAxis = 0.F;
    private float prevYAxis = 0.F;
    private int targetMouseX = 0;
    private int targetMouseY = 0;
    private float mouseSpeedX = 0.F;
    private float mouseSpeedY = 0.F;
    private int inventoryInteractionCooldown = 0;

    private ControllerControlsWidget controlsInput = null;

    public LambdaInput(@NotNull LambdaControlsClient mod) {
        this.config = mod.config;
    }

    /**
     * This method is called every Minecraft tick.
     *
     * @param client The client instance.
     */
    public void tick(@NotNull MinecraftClient client) {
        this.targetYaw = 0.F;
        this.targetPitch = 0.F;

        // Handles the key bindings.
        if (LambdaControlsClient.BINDING_LOOK_UP.isPressed()) {
            this.handleLook(client, GLFW_GAMEPAD_AXIS_RIGHT_Y, 0.8F, 2);
        } else if (LambdaControlsClient.BINDING_LOOK_DOWN.isPressed()) {
            this.handleLook(client, GLFW_GAMEPAD_AXIS_RIGHT_Y, 0.8F, 1);
        }
        if (LambdaControlsClient.BINDING_LOOK_LEFT.isPressed()) {
            this.handleLook(client, GLFW_GAMEPAD_AXIS_RIGHT_X, 0.8F, 2);
        } else if (LambdaControlsClient.BINDING_LOOK_RIGHT.isPressed()) {
            this.handleLook(client, GLFW_GAMEPAD_AXIS_RIGHT_X, 0.8F, 1);
        }

        INPUT_MANAGER.tick(client);
    }

    /**
     * This method is called every Minecraft tick for controller input update.
     *
     * @param client The client instance.
     */
    public void tickController(@NotNull MinecraftClient client) {
        BUTTON_COOLDOWNS.entrySet().stream().filter(entry -> entry.getValue() > 0).forEach(entry -> BUTTON_COOLDOWNS.put(entry.getKey(), entry.getValue() - 1));
        // Decreases the cooldown for GUI actions.
        if (this.actionGuiCooldown > 0)
            --this.actionGuiCooldown;

        InputManager.updateStates();

        Controller controller = this.config.getController();
        if (controller.isConnected()) {
            GLFWGamepadState state = controller.getState();
            this.fetchButtonInput(client, state, false);
            this.fetchAxeInput(client, state, false);
        }
        this.config.getSecondController().filter(Controller::isConnected)
                .ifPresent(joycon -> {
                    GLFWGamepadState state = joycon.getState();
                    this.fetchButtonInput(client, state, true);
                    this.fetchAxeInput(client, state, true);
                });

        boolean allowInput = true;

        if (this.controlsInput != null && this.controlsInput.focusedBinding != null)
            allowInput = false;

        if (allowInput)
            InputManager.updateBindings(client);

        if (this.controlsInput != null
                && InputManager.STATES.entrySet().parallelStream().map(Map.Entry::getValue).allMatch(ButtonState::isUnpressed)) {
            if (this.controlsInput.focusedBinding != null && !this.controlsInput.waiting) {
                int[] buttons = new int[this.controlsInput.currentButtons.size()];
                for (int i = 0; i < this.controlsInput.currentButtons.size(); i++)
                    buttons[i] = this.controlsInput.currentButtons.get(i);
                this.controlsInput.focusedBinding.setButton(buttons);
                this.controlsInput.focusedBinding = null;
                this.controlsInput = null;
            }
        }

        if (this.inventoryInteractionCooldown > 0)
            this.inventoryInteractionCooldown--;
    }

    /**
     * This method is called before the screen is rendered.
     *
     * @param client The client instance.
     * @param screen The screen to render.
     */
    public void onPreRenderScreen(@NotNull MinecraftClient client, @NotNull Screen screen) {
        if (!isScreenInteractive(screen)) {
            INPUT_MANAGER.updateMousePosition(client);
        }
    }

    /**
     * This method is called when Minecraft renders.
     *
     * @param client The client instance.
     */
    public void onRender(float tickDelta, @NotNull MinecraftClient client) {
        if (!(client.currentScreen == null || client.currentScreen instanceof TouchscreenOverlay))
            return;

        PlayerEntity player = client.player;
        if (player == null)
            return;

        if (this.targetYaw != 0F || this.targetPitch != 0F) {
            float rotationYaw = (float) (player.prevYaw + (this.targetYaw / 0.10) * tickDelta);
            float rotationPitch = (float) (player.prevPitch + (this.targetPitch / 0.10) * tickDelta);
            client.player.yaw = rotationYaw;
            client.player.pitch = MathHelper.clamp(rotationPitch, -90.F, 90.F);
            if (client.player.isRiding()) {
                client.player.getVehicle().copyPositionAndRotation(client.player);
            }
            client.getTutorialManager().onUpdateMouse(this.targetPitch, this.targetYaw);
        }
    }

    /**
     * This method is called when a Screen is opened.
     *
     * @param client The client instance.
     * @param windowWidth The window width.
     * @param windowHeight The window height.
     */
    public void onScreenOpen(@NotNull MinecraftClient client, int windowWidth, int windowHeight) {
        if (client.currentScreen == null) {
            this.mouseSpeedX = this.mouseSpeedY = 0.0F;
            INPUT_MANAGER.resetMousePosition(windowWidth, windowHeight);
        } else if (isScreenInteractive(client.currentScreen) && this.config.hasVirtualMouse()) {
            ((MouseAccessor) client.mouse).lambdacontrols_onCursorPos(client.getWindow().getHandle(), 0, 0);
            INPUT_MANAGER.resetMouseTarget(client);
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
        ByteBuffer buffer = gamepadState.buttons();
        for (int i = 0; i < buffer.limit(); i++) {
            int btn = leftJoycon ? ButtonBinding.controller2Button(i) : i;
            boolean btnState = buffer.get() == (byte) 1;
            ButtonState state = ButtonState.NONE;
            ButtonState previousState = InputManager.STATES.getOrDefault(btn, ButtonState.NONE);

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
        FloatBuffer buffer = gamepadState.axes();
        for (int i = 0; i < buffer.limit(); i++) {
            int axis = leftJoycon ? ButtonBinding.controller2Button(i) : i;
            float value = buffer.get();
            float absValue = Math.abs(value);

            if (i == GLFW.GLFW_GAMEPAD_AXIS_LEFT_Y)
                value *= -1.0F;

            int state = value > this.config.getDeadZone() ? 1 : (value < -this.config.getDeadZone() ? 2 : 0);
            this.handleAxe(client, axis, value, absValue, state);
        }
    }

    private void handleButton(@NotNull MinecraftClient client, int button, int action, boolean state) {
        if (this.controlsInput != null && this.controlsInput.focusedBinding != null) {
            if (action == 0 && !this.controlsInput.currentButtons.contains(button)) {
                this.controlsInput.currentButtons.add(button);

                int[] buttons = new int[this.controlsInput.currentButtons.size()];
                for (int i = 0; i < this.controlsInput.currentButtons.size(); i++)
                    buttons[i] = this.controlsInput.currentButtons.get(i);
                this.controlsInput.focusedBinding.setButton(buttons);

                this.controlsInput.waiting = false;
            }
            return;
        }

        if (action == 0 || action == 2) {
            if (client.currentScreen != null && isScreenInteractive(client.currentScreen)
                    && (button == GLFW.GLFW_GAMEPAD_BUTTON_DPAD_UP || button == GLFW.GLFW_GAMEPAD_BUTTON_DPAD_DOWN
                    || button == GLFW.GLFW_GAMEPAD_BUTTON_DPAD_LEFT || button == GLFW.GLFW_GAMEPAD_BUTTON_DPAD_RIGHT)) {
                if (this.actionGuiCooldown == 0) {
                    if (button == GLFW.GLFW_GAMEPAD_BUTTON_DPAD_UP) {
                        this.changeFocus(client.currentScreen, NavigationDirection.UP);
                    } else if (button == GLFW.GLFW_GAMEPAD_BUTTON_DPAD_DOWN) {
                        this.changeFocus(client.currentScreen, NavigationDirection.DOWN);
                    } else if (button == GLFW.GLFW_GAMEPAD_BUTTON_DPAD_LEFT) {
                        this.handleLeftRight(client.currentScreen, false);
                    } else {
                        this.handleLeftRight(client.currentScreen, true);
                    }
                }
                return;
            }
        }

        if (action == 1) {
            if (button == GLFW.GLFW_GAMEPAD_BUTTON_A && client.currentScreen != null) {
                if (this.actionGuiCooldown == 0) {
                    Element focused = client.currentScreen.getFocused();
                    if (focused != null && isScreenInteractive(client.currentScreen)) {
                        if (this.handleAButton(client.currentScreen, focused)) {
                            this.actionGuiCooldown = 5; // Prevent to press too quickly the focused element, so we have to skip 5 ticks.
                            return;
                        }
                    }
                }
            }

            if (this.handleInventory(client, button)) {
                this.ignoreNextARelease = true;
                return;
            }

            if (button == GLFW.GLFW_GAMEPAD_BUTTON_B) {
                if (client.currentScreen != null) {
                    if (!LambdaControlsCompat.handleMenuBack(client, client.currentScreen))
                        if (!this.tryGoBack(client.currentScreen))
                            client.currentScreen.onClose();
                    return;
                }
            }
        }

        if (button == GLFW.GLFW_GAMEPAD_BUTTON_A && client.currentScreen != null && !isScreenInteractive(client.currentScreen) && this.actionGuiCooldown == 0) {
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
                this.actionGuiCooldown = 5;
            } else {
                this.ignoreNextARelease = false;
            }
        }
    }

    /**
     * Handles inventory interaction.
     *
     * @param client The client instance.
     * @param button The button pressed.
     * @return True if an inventory interaction was done.
     */
    private boolean handleInventory(@NotNull MinecraftClient client, int button) {
        if (!(client.currentScreen instanceof HandledScreen))
            return false;

        if (client.interactionManager == null || client.player == null)
            return false;

        if (this.inventoryInteractionCooldown > 0)
            return true;

        if (button == GLFW.GLFW_GAMEPAD_BUTTON_B) {
            client.player.closeHandledScreen();
            return true;
        }

        double x = client.mouse.getX() * (double) client.getWindow().getScaledWidth() / (double) client.getWindow().getWidth();
        double y = client.mouse.getY() * (double) client.getWindow().getScaledHeight() / (double) client.getWindow().getHeight();

        HandledScreen screen = (HandledScreen) client.currentScreen;
        HandledScreenAccessor accessor = (HandledScreenAccessor) screen;
        Slot slot = ((HandledScreenAccessor) client.currentScreen).lambdacontrols_getSlotAt(x, y);

        int slotId;
        if (slot == null) {
            if (client.player.inventory.getCursorStack().isEmpty())
                return false;
            slotId = accessor.lambdacontrols_isClickOutsideBounds(x, y, accessor.getX(), accessor.getY(), GLFW_MOUSE_BUTTON_1) ? -999 : -1;
        } else {
            slotId = slot.id;
        }

        SlotActionType actionType = SlotActionType.PICKUP;
        int clickData = GLFW.GLFW_MOUSE_BUTTON_1;
        switch (button) {
            case GLFW_GAMEPAD_BUTTON_A:
                if (screen instanceof CreativeInventoryScreen)
                    if (((CreativeInventoryScreenAccessor) screen).lambdacontrols_isCreativeInventorySlot(slot))
                        actionType = SlotActionType.CLONE;
                if (slot != null && LambdaControlsCompat.streamCompatHandlers().anyMatch(handler -> handler.isCreativeSlot(screen, slot)))
                    actionType = SlotActionType.CLONE;
                break;
            case GLFW.GLFW_GAMEPAD_BUTTON_X:
                clickData = GLFW_MOUSE_BUTTON_2;
                break;
            case GLFW.GLFW_GAMEPAD_BUTTON_Y:
                actionType = SlotActionType.QUICK_MOVE;
                break;
            default:
                return false;
        }

        accessor.lambdacontrols_onMouseClick(slot, slotId, clickData, actionType);
        return true;
    }

    /**
     * Tries to go back.
     *
     * @param screen The current screen.
     * @return True if successful, else false.
     */
    public boolean tryGoBack(@NotNull Screen screen) {
        ImmutableSet<String> set = ImmutableSet.of("gui.back", "gui.done", "gui.cancel", "gui.toTitle", "gui.toMenu");
        return screen.children().stream().filter(element -> element instanceof AbstractPressableButtonWidget)
                .map(element -> (AbstractPressableButtonWidget) element)
                .filter(element -> element.getMessage() instanceof TranslatableText)
                .anyMatch(element -> {
                    if (set.stream().anyMatch(key -> key.equals(((TranslatableText) element.getMessage()).getKey()))) {
                        element.onPress();
                        return true;
                    }
                    return false;
                });
    }

    private void handleAxe(@NotNull MinecraftClient client, int axis, float value, float absValue, int state) {
        int asButtonState = value > 0.5F ? 1 : (value < -0.5F ? 2 : 0);

        if (axis == GLFW_GAMEPAD_AXIS_LEFT_TRIGGER || axis == GLFW_GAMEPAD_AXIS_RIGHT_TRIGGER || axis == ButtonBinding.controller2Button(GLFW.GLFW_GAMEPAD_AXIS_LEFT_TRIGGER) ||
                axis == ButtonBinding.controller2Button(GLFW.GLFW_GAMEPAD_AXIS_RIGHT_TRIGGER))
            if (asButtonState == 2)
                asButtonState = 0;

        {
            boolean currentPlusState = asButtonState == 1;
            boolean currentMinusState = asButtonState == 2;
            ButtonState previousPlusState = InputManager.STATES.getOrDefault(axisAsButton(axis, true), ButtonState.NONE);
            ButtonState previousMinusState = InputManager.STATES.getOrDefault(axisAsButton(axis, false), ButtonState.NONE);

            if (currentPlusState != previousPlusState.isPressed()) {
                InputManager.STATES.put(axisAsButton(axis, true), currentPlusState ? ButtonState.PRESS : ButtonState.RELEASE);
                if (currentPlusState)
                    BUTTON_COOLDOWNS.put(axisAsButton(axis, true), 5);
            } else if (currentPlusState) {
                InputManager.STATES.put(axisAsButton(axis, true), ButtonState.REPEAT);
                if (BUTTON_COOLDOWNS.getOrDefault(axisAsButton(axis, true), 0) == 0) {
                    BUTTON_COOLDOWNS.put(axisAsButton(axis, true), 5);
                }
            }

            if (currentMinusState != previousMinusState.isPressed()) {
                InputManager.STATES.put(axisAsButton(axis, false), currentMinusState ? ButtonState.PRESS : ButtonState.RELEASE);
                if (currentMinusState)
                    BUTTON_COOLDOWNS.put(axisAsButton(axis, false), 5);
            } else if (currentMinusState) {
                InputManager.STATES.put(axisAsButton(axis, false), ButtonState.REPEAT);
                if (BUTTON_COOLDOWNS.getOrDefault(axisAsButton(axis, false), 0) == 0) {
                    BUTTON_COOLDOWNS.put(axisAsButton(axis, false), 5);
                }
            }

            float axisValue = absValue < this.config.getDeadZone() ? 0.f : (float) (absValue - this.config.getDeadZone());
            axisValue /= (1.0 - this.config.getDeadZone());
            if (currentPlusState)
                InputManager.BUTTON_VALUES.put(axisAsButton(axis, true), axisValue);
            else
                InputManager.BUTTON_VALUES.put(axisAsButton(axis, true), 0.f);
            if (currentMinusState)
                InputManager.BUTTON_VALUES.put(axisAsButton(axis, false), axisValue);
            else
                InputManager.BUTTON_VALUES.put(axisAsButton(axis, false), 0.f);
        }

        double deadZone = this.config.getDeadZone();

        if (this.controlsInput != null && this.controlsInput.focusedBinding != null) {
            if (asButtonState != 0 && !this.controlsInput.currentButtons.contains(axisAsButton(axis, asButtonState == 1))) {

                this.controlsInput.currentButtons.add(axisAsButton(axis, asButtonState == 1));

                int[] buttons = new int[this.controlsInput.currentButtons.size()];
                for (int i = 0; i < this.controlsInput.currentButtons.size(); i++)
                    buttons[i] = this.controlsInput.currentButtons.get(i);
                this.controlsInput.focusedBinding.setButton(buttons);

                this.controlsInput.waiting = false;
            }
            return;
        } else if (client.currentScreen instanceof CreativeInventoryScreen) {
            if (axis == GLFW_GAMEPAD_AXIS_RIGHT_Y) {
                CreativeInventoryScreen screen = (CreativeInventoryScreen) client.currentScreen;
                CreativeInventoryScreenAccessor accessor = (CreativeInventoryScreenAccessor) screen;
                // @TODO allow rebinding to left stick
                if (accessor.lambdacontrols_hasScrollbar() && absValue >= deadZone) {
                    screen.mouseScrolled(0.0, 0.0, -value);
                }
                return;
            }
        } else if (client.currentScreen instanceof AdvancementsScreen) {
            if (axis == GLFW_GAMEPAD_AXIS_RIGHT_X || axis == GLFW_GAMEPAD_AXIS_RIGHT_Y) {
                AdvancementsScreen screen = (AdvancementsScreen) client.currentScreen;
                AdvancementsScreenAccessor accessor = (AdvancementsScreenAccessor) screen;
                if (absValue >= deadZone) {
                    AdvancementTab tab = accessor.getSelectedTab();
                    tab.move(axis == GLFW_GAMEPAD_AXIS_RIGHT_X ? -value * 5.0 : 0.0, axis == GLFW_GAMEPAD_AXIS_RIGHT_Y ? -value * 5.0 : 0.0);
                }
                return;
            }
        }

        if (client.currentScreen == null) {
            // Handles the look direction.
            absValue -= this.config.getDeadZone();
            this.handleLook(client, axis, (float) (absValue / (1.0 - this.config.getDeadZone())), state);
        } else {
            boolean allowMouseControl = true;

            if (this.actionGuiCooldown == 0 && this.config.isMovementAxis(axis) && isScreenInteractive(client.currentScreen)) {
                if (this.config.isForwardButton(axis, false, asButtonState)) {
                    allowMouseControl = this.changeFocus(client.currentScreen, NavigationDirection.UP);
                } else if (this.config.isBackButton(axis, false, asButtonState)) {
                    allowMouseControl = this.changeFocus(client.currentScreen, NavigationDirection.DOWN);
                } else if (this.config.isLeftButton(axis, false, asButtonState)) {
                    allowMouseControl = this.handleLeftRight(client.currentScreen, false);
                } else if (this.config.isRightButton(axis, false, asButtonState)) {
                    allowMouseControl = this.handleLeftRight(client.currentScreen, true);
                }
            }

            float movementX = 0.0F;
            float movementY = 0.0F;

            if (this.config.isBackButton(axis, false, (value > 0 ? 1 : 2))) {
                movementY = absValue;
            } else if (this.config.isForwardButton(axis, false, (value > 0 ? 1 : 2))) {
                movementY = -absValue;
            } else if (this.config.isLeftButton(axis, false, (value > 0 ? 1 : 2))) {
                movementX = -absValue;
            } else if (this.config.isRightButton(axis, false, (value > 0 ? 1 : 2))) {
                movementX = absValue;
            }

            if (client.currentScreen != null && allowMouseControl) {
                boolean moving = Math.abs(movementY) >= deadZone || Math.abs(movementX) >= deadZone;
                if (moving) {
                /*
                    Updates the target mouse position when the initial movement stick movement is detected.
                    It prevents the cursor to jump to the old target mouse position if the user moves the cursor with the mouse.
                 */
                    if (Math.abs(prevXAxis) < deadZone && Math.abs(prevYAxis) < deadZone) {
                        INPUT_MANAGER.resetMouseTarget(client);
                    }

                    if (Math.abs(movementX) >= deadZone)
                        this.mouseSpeedX = movementX;
                    else
                        this.mouseSpeedX = 0.F;

                    if (Math.abs(movementY) >= deadZone)
                        this.mouseSpeedY = movementY;
                    else
                        this.mouseSpeedY = 0.F;
                } else {
                    this.mouseSpeedX = 0.F;
                    this.mouseSpeedY = 0.F;
                }

                if (Math.abs(this.mouseSpeedX) >= .05F || Math.abs(this.mouseSpeedY) >= .05F) {
                    InputManager.queueMoveMousePosition(this.mouseSpeedX * this.config.getMouseSpeed(), this.mouseSpeedY * this.config.getMouseSpeed());
                }

                this.moveMouseToClosestSlot(client, client.currentScreen);
            }

            this.prevXAxis = movementX;
            this.prevYAxis = movementY;
        }
    }

    private boolean handleAButton(@NotNull Screen screen, @NotNull Element focused) {
        if (focused instanceof AbstractPressableButtonWidget) {
            AbstractPressableButtonWidget widget = (AbstractPressableButtonWidget) focused;
            widget.playDownSound(MinecraftClient.getInstance().getSoundManager());
            widget.onPress();
            return true;
        } else if (focused instanceof AbstractSprucePressableButtonWidget) {
            AbstractSprucePressableButtonWidget widget = (AbstractSprucePressableButtonWidget) focused;
            widget.playDownSound();
            widget.onPress();
            return true;
        } else if (focused instanceof SpruceLabelWidget) {
            ((SpruceLabelWidget) focused).onPress();
            return true;
        } else if (focused instanceof WorldListWidget) {
            WorldListWidget list = (WorldListWidget) focused;
            list.getSelectedAsOptional().ifPresent(WorldListWidget.Entry::play);
            return true;
        } else if (focused instanceof MultiplayerServerListWidget) {
            MultiplayerServerListWidget list = (MultiplayerServerListWidget) focused;
            MultiplayerServerListWidget.Entry entry = list.getSelected();
            if (entry instanceof MultiplayerServerListWidget.LanServerEntry || entry instanceof MultiplayerServerListWidget.ServerEntry) {
                ((MultiplayerScreen) screen).select(entry);
                ((MultiplayerScreen) screen).connect();
            }
        } else if (focused instanceof SpruceParentWidget) {
            Element childFocused = ((SpruceParentWidget<?>) focused).getFocused();
            if (childFocused != null)
                return this.handleAButton(screen, childFocused);
        } else if (focused instanceof ParentElement) {
            Element childFocused = ((ParentElement) focused).getFocused();
            if (childFocused != null)
                return this.handleAButton(screen, childFocused);
        }
        return false;
    }

    /**
     * Handles the left and right buttons.
     *
     * @param screen The current screen.
     * @param right True if the right button is pressed, else false.
     */
    private boolean handleLeftRight(@NotNull Screen screen, boolean right) {
        if (screen instanceof SpruceScreen) {
            ((SpruceScreen) screen).onNavigation(right ? NavigationDirection.RIGHT : NavigationDirection.LEFT, false);
            this.actionGuiCooldown = 5;
            return false;
        }
        Element focused = screen.getFocused();
        if (focused != null)
            if (this.handleRightLeftElement(focused, right))
                return this.changeFocus(screen, right ? NavigationDirection.RIGHT : NavigationDirection.LEFT);
        return true;
    }

    private boolean handleRightLeftElement(@NotNull Element element, boolean right) {
        if (element instanceof SpruceElement) {
            if (((SpruceElement) element).requiresCursor())
                return true;
            return !((SpruceElement) element).onNavigation(right ? NavigationDirection.RIGHT : NavigationDirection.LEFT, false);
        }
        if (element instanceof SliderWidget) {
            SliderWidget slider = (SliderWidget) element;
            slider.keyPressed(right ? 262 : 263, 0, 0);
            this.actionGuiCooldown = 2; // Prevent to press too quickly the focused element, so we have to skip 5 ticks.
            return false;
        } else if (element instanceof AlwaysSelectedEntryListWidget) {
            ((EntryListWidgetAccessor) element).lambdacontrols_moveSelection(right ? EntryListWidget.MoveDirection.UP : EntryListWidget.MoveDirection.DOWN);
            return false;
        } else if (element instanceof ParentElement) {
            ParentElement entryList = (ParentElement) element;
            Element focused = entryList.getFocused();
            if (focused == null)
                return true;
            return this.handleRightLeftElement(focused, right);
        }
        return true;
    }

    /**
     * Handles the look direction input.
     *
     * @param client The client instance.
     * @param axis The axis to change.
     * @param value The value of the look.
     * @param state The state.
     */
    public void handleLook(@NotNull MinecraftClient client, int axis, float value, int state) {
        // Handles the look direction.
        if (client.player != null) {
            double powValue = Math.pow(value, 2.0);
            if (axis == GLFW_GAMEPAD_AXIS_RIGHT_Y) {
                if (state == 2) {
                    this.targetPitch = -this.config.getRightYAxisSign() * (this.config.getRotationSpeed() * powValue) * 0.11D;
                } else if (state == 1) {
                    this.targetPitch = this.config.getRightYAxisSign() * (this.config.getRotationSpeed() * powValue) * 0.11D;
                }
            }
            if (axis == GLFW_GAMEPAD_AXIS_RIGHT_X) {
                if (state == 2) {
                    this.targetYaw = -this.config.getRightXAxisSign() * (this.config.getRotationSpeed() * powValue) * 0.11D;
                } else if (state == 1) {
                    this.targetYaw = this.config.getRightXAxisSign() * (this.config.getRotationSpeed() * powValue) * 0.11D;
                }
            }
        }
    }

    private boolean changeFocus(@NotNull Screen screen, NavigationDirection direction) {
        if (screen instanceof SpruceScreen) {
            if (((SpruceScreen) screen).onNavigation(direction, false)) {
                this.actionGuiCooldown = 5;
            }
            return false;
        }
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
    }

    public static boolean isScreenInteractive(@NotNull Screen screen) {
        return !(screen instanceof AdvancementsScreen || screen instanceof HandledScreen || screen instanceof PackScreen
                || (screen instanceof SpruceScreen && ((SpruceScreen) screen).requiresCursor())
                || LambdaControlsCompat.requireMouseOnScreen(screen));
    }

    // Inspired from https://github.com/MrCrayfish/Controllable/blob/1.14.X/src/main/java/com/mrcrayfish/controllable/client/ControllerInput.java#L686.
    private void moveMouseToClosestSlot(@NotNull MinecraftClient client, @Nullable Screen screen) {
        // Makes the mouse attracted to slots. This helps with selecting items when using a controller.
        if (screen instanceof HandledScreen) {
            HandledScreen inventoryScreen = (HandledScreen) screen;
            HandledScreenAccessor accessor = (HandledScreenAccessor) inventoryScreen;
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
                Slot slot = closestSlot.get().key;
                if (slot.hasStack() || !client.player.inventory.getMainHandStack().isEmpty()) {
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
