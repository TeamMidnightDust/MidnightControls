/*
 * Copyright © 2019 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of LambdaControls.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package me.lambdaurora.lambdacontrols;

import me.lambdaurora.lambdacontrols.gui.LabelWidget;
import me.lambdaurora.lambdacontrols.gui.LambdaControlsControlsScreen;
import me.lambdaurora.lambdacontrols.mixin.EntryListWidgetAccessor;
import me.lambdaurora.lambdacontrols.util.AbstractContainerScreenAccessor;
import me.lambdaurora.lambdacontrols.util.CreativeInventoryScreenAccessor;
import me.lambdaurora.lambdacontrols.util.KeyBindingAccessor;
import me.lambdaurora.lambdacontrols.util.MouseAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.ParentElement;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.advancement.AdvancementsScreen;
import net.minecraft.client.gui.screen.ingame.AbstractContainerScreen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerServerListWidget;
import net.minecraft.client.gui.screen.world.WorldListWidget;
import net.minecraft.client.gui.widget.AbstractPressableButtonWidget;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.container.Slot;
import net.minecraft.container.SlotActionType;
import net.minecraft.item.ItemGroup;
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

import static me.lambdaurora.lambdacontrols.ButtonBinding.*;
import static org.lwjgl.glfw.GLFW.GLFW_GAMEPAD_AXIS_RIGHT_X;
import static org.lwjgl.glfw.GLFW.GLFW_GAMEPAD_AXIS_RIGHT_Y;

/**
 * Represents the controller input handler.
 */
public class ControllerInput
{
    private static final Map<Integer, Boolean> BUTTON_STATES       = new HashMap<>();
    private static final Map<Integer, Integer> BUTTON_COOLDOWNS    = new HashMap<>();
    private static final Map<Integer, Boolean> AXIS_STATES         = new HashMap<>();
    private static final Map<Integer, Integer> AXIS_COOLDOWNS      = new HashMap<>();
    private final        LambdaControlsConfig  config;
    private              int                   action_gui_cooldown = 0;
    private              int                   ignore_next_a       = 0;
    private              int                   last_sneak          = 0;
    private              double                prev_target_yaw     = 0.0;
    private              double                prev_target_pitch   = 0.0;
    private              double                target_yaw          = 0.0;
    private              double                target_pitch        = 0.0;
    private              float                 prev_x_axis         = 0.F;
    private              float                 prev_y_axis         = 0.F;
    private              int                   prev_target_mouse_x = 0;
    private              int                   prev_target_mouse_y = 0;
    private              int                   target_mouse_x      = 0;
    private              int                   target_mouse_y      = 0;
    private              float                 mouse_speed_x       = 0.F;
    private              float                 mouse_speed_y       = 0.F;

    public ControllerInput(@NotNull LambdaControls mod)
    {
        this.config = mod.config;
    }

    public void on_tick(@NotNull MinecraftClient client)
    {
        this.prev_target_yaw = this.target_yaw;
        this.prev_target_pitch = this.target_pitch;

        // Handles the key bindings.
        if (LambdaControls.BINDING_LOOK_UP.isPressed()) {
            this.handle_look(client, GLFW_GAMEPAD_AXIS_RIGHT_Y, 0.8F, 2);
        } else if (LambdaControls.BINDING_LOOK_DOWN.isPressed()) {
            this.handle_look(client, GLFW_GAMEPAD_AXIS_RIGHT_Y, 0.8F, 1);
        }
        if (LambdaControls.BINDING_LOOK_LEFT.isPressed()) {
            this.handle_look(client, GLFW_GAMEPAD_AXIS_RIGHT_X, 0.8F, 2);
        } else if (LambdaControls.BINDING_LOOK_RIGHT.isPressed()) {
            this.handle_look(client, GLFW_GAMEPAD_AXIS_RIGHT_X, 0.8F, 1);
        }
    }

    /**
     * This method is called every Minecraft tick.
     *
     * @param client The client instance.
     */
    public void on_controller_tick(@NotNull MinecraftClient client)
    {
        BUTTON_COOLDOWNS.entrySet().stream().filter(entry -> entry.getValue() > 0).forEach(entry -> BUTTON_COOLDOWNS.put(entry.getKey(), entry.getValue() - 1));
        AXIS_COOLDOWNS.entrySet().stream().filter(entry -> entry.getValue() > 0).forEach(entry -> AXIS_COOLDOWNS.put(entry.getKey(), entry.getValue() - 1));
        // Decreases the last_sneak counter which allows to double press to sneak continuously.
        if (this.last_sneak > 0)
            --this.last_sneak;
        // Decreases the cooldown for GUI actions.
        if (this.action_gui_cooldown > 0)
            --this.action_gui_cooldown;
        this.prev_target_mouse_x = this.target_mouse_x;
        this.prev_target_mouse_y = this.target_mouse_y;

        Controller controller = this.config.get_controller();
        if (controller.is_connected()) {
            GLFWGamepadState state = controller.get_state();
            this.fetch_button_input(client, state);
            this.fetch_axe_input(client, state);
        }

        if (this.ignore_next_a > 0)
            this.ignore_next_a--;
    }

    public void on_pre_render_screen(@NotNull MinecraftClient client, @NotNull Screen screen)
    {
        if (!is_screen_interactive(screen)) {
            if (this.prev_target_mouse_x != this.target_mouse_x || this.prev_target_mouse_y != this.target_mouse_y) {
                double mouse_x = this.prev_target_mouse_x + (this.target_mouse_x - this.prev_target_mouse_x) * client.getTickDelta() + 0.5;
                double mouse_y = this.prev_target_mouse_y + (this.target_mouse_y - this.prev_target_mouse_y) * client.getTickDelta() + 0.5;
                GLFW.glfwSetCursorPos(client.getWindow().getHandle(), mouse_x, mouse_y);
                ((MouseAccessor) client.mouse).on_cursor_pos(client.getWindow().getHandle(), mouse_x, mouse_y);
            }
        }
    }

    public void on_render(@NotNull MinecraftClient client)
    {
        if (client.currentScreen == null && (this.prev_target_yaw != this.target_yaw || this.prev_target_pitch != this.target_pitch)) {
            float rotation_yaw = (float) (client.player.prevYaw + (this.target_yaw - client.player.prevYaw) * client.getTickDelta());
            float rotation_pitch = (float) (client.player.prevPitch + (this.target_pitch - client.player.prevPitch) * client.getTickDelta());
            client.player.yaw = rotation_yaw;
            client.player.pitch = MathHelper.clamp(rotation_pitch, -90.F, 90.F);
            if (client.player.isRiding()) {
                client.player.getVehicle().copyPositionAndRotation(client.player);
            }
        }
    }

    public void on_screen_open(@NotNull MinecraftClient client, int window_width, int window_height)
    {
        if (client.currentScreen == null) {
            this.mouse_speed_x = this.mouse_speed_y = 0.0F;
            this.target_mouse_x = this.prev_target_mouse_x = (int) (window_width / 2.F);
            this.target_mouse_y = this.prev_target_mouse_y = (int) (window_height / 2.F);
        }
    }

    private void fetch_button_input(@NotNull MinecraftClient client, @NotNull GLFWGamepadState gamepad_state)
    {
        ByteBuffer buffer = gamepad_state.buttons();
        for (int btn = 0; btn < buffer.limit(); btn++) {
            boolean btn_state = buffer.get() == (byte) 1;
            boolean previous_state = BUTTON_STATES.getOrDefault(btn, false);

            ButtonBinding.set_button_state(btn, btn_state);

            if (btn_state != previous_state) {
                this.handle_button(client, btn, btn_state ? 0 : 1, btn_state);
                if (btn_state)
                    BUTTON_COOLDOWNS.put(btn, 5);
            } else if (btn_state) {
                if (BUTTON_COOLDOWNS.getOrDefault(btn, 0) == 0) {
                    BUTTON_COOLDOWNS.put(btn, 5);
                    this.handle_button(client, btn, 2, true);
                }
            }

            BUTTON_STATES.put(btn, btn_state);
        }
    }

    private void fetch_axe_input(@NotNull MinecraftClient client, @NotNull GLFWGamepadState gamepad_state)
    {
        FloatBuffer buffer = gamepad_state.axes();
        for (int i = 0; i < buffer.limit(); i++) {
            float value = buffer.get();
            float abs_value = Math.abs(value);

            if (i == GLFW.GLFW_GAMEPAD_AXIS_LEFT_Y)
                value *= -1.0F;

            ButtonBinding.set_button_state(axis_as_button(i, true), value > 0.5F);
            ButtonBinding.set_button_state(axis_as_button(i, false), value < -0.5F);

            int state = value > this.config.get_dead_zone() ? 1 : (value < -this.config.get_dead_zone() ? 2 : 0);
            this.handle_axe(client, i, value, abs_value, state);
        }
    }

    private void handle_button(@NotNull MinecraftClient client, int button, int action, boolean state)
    {
        if (client.currentScreen instanceof LambdaControlsControlsScreen && action == 0) {
            LambdaControlsControlsScreen controls_screen = (LambdaControlsControlsScreen) client.currentScreen;
            if (controls_screen.focused_binding != null) {
                this.config.set_button_binding(controls_screen.focused_binding, button);
                controls_screen.focused_binding = null;
                return;
            }
        }

        if (action == 0 || action == 2) {
            // Handles RB and LB buttons.
            if (button == GLFW.GLFW_GAMEPAD_BUTTON_LEFT_BUMPER || button == GLFW.GLFW_GAMEPAD_BUTTON_RIGHT_BUMPER) {
                this.handle_rb_lb(client, button == GLFW.GLFW_GAMEPAD_BUTTON_RIGHT_BUMPER);
                return;
            }

            if (client.currentScreen != null && is_screen_interactive(client.currentScreen)
                    && (button == GLFW.GLFW_GAMEPAD_BUTTON_DPAD_UP || button == GLFW.GLFW_GAMEPAD_BUTTON_DPAD_DOWN
                    || button == GLFW.GLFW_GAMEPAD_BUTTON_DPAD_LEFT || button == GLFW.GLFW_GAMEPAD_BUTTON_DPAD_RIGHT)) {
                if (this.action_gui_cooldown == 0) {
                    if (button == GLFW.GLFW_GAMEPAD_BUTTON_DPAD_UP) {
                        this.change_focus(client.currentScreen, false);
                    } else if (button == GLFW.GLFW_GAMEPAD_BUTTON_DPAD_DOWN) {
                        this.change_focus(client.currentScreen, true);
                    } else if (button == GLFW.GLFW_GAMEPAD_BUTTON_DPAD_LEFT) {
                        this.handle_left_right(client.currentScreen, false);
                    } else {
                        this.handle_left_right(client.currentScreen, true);
                    }
                }
                return;
            }
        }

        if (action == 0) {
            // Handles when the player presses the Start button.
            if (PAUSE_GAME.is_button(button)) {
                // If in game, then pause the game.
                if (client.currentScreen == null)
                    client.openPauseMenu(false);
                else if (client.currentScreen instanceof AbstractContainerScreen) // If the current screen is a container then close it.
                    client.player.closeContainer();
                else// Else just close the current screen.
                    client.currentScreen.onClose();
                return;
            }

            if (button == GLFW.GLFW_GAMEPAD_BUTTON_A && client.currentScreen != null) {
                if (this.action_gui_cooldown == 0) {
                    Element focused = client.currentScreen.getFocused();
                    if (focused != null && is_screen_interactive(client.currentScreen)) {
                        if (this.handle_a_button(client.currentScreen, focused)) {
                            this.action_gui_cooldown = 5; // Prevent to press too quickly the focused element, so we have to skip 5 ticks.
                            return;
                        }
                    }
                }
            }

            if (client.currentScreen instanceof AbstractContainerScreen && client.interactionManager != null && client.player != null) {
                double pos_x = client.mouse.getX() * (double) client.getWindow().getScaledWidth() / (double) client.getWindow().getWidth();
                double pos_y = client.mouse.getY() * (double) client.getWindow().getScaledHeight() / (double) client.getWindow().getHeight();
                Slot slot = ((AbstractContainerScreenAccessor) client.currentScreen).get_slot_at(pos_x, pos_y);
                if (button == GLFW.GLFW_GAMEPAD_BUTTON_A && slot != null) {
                    client.interactionManager.clickSlot(((AbstractContainerScreen) client.currentScreen).getContainer().syncId, slot.id, GLFW.GLFW_MOUSE_BUTTON_1, SlotActionType.PICKUP, client.player);
                    this.action_gui_cooldown = 5;
                    return;
                } else if (button == GLFW.GLFW_GAMEPAD_BUTTON_B) {
                    client.player.closeContainer();
                    return;
                } else if (button == GLFW.GLFW_GAMEPAD_BUTTON_X && slot != null) {
                    client.interactionManager.clickSlot(((AbstractContainerScreen) client.currentScreen).getContainer().syncId, slot.id, GLFW.GLFW_MOUSE_BUTTON_2, SlotActionType.PICKUP, client.player);
                    return;
                } else if (button == GLFW.GLFW_GAMEPAD_BUTTON_Y && slot != null) {
                    client.interactionManager.clickSlot(((AbstractContainerScreen) client.currentScreen).getContainer().syncId, slot.id, GLFW.GLFW_MOUSE_BUTTON_1, SlotActionType.QUICK_MOVE, client.player);
                    return;
                }
            } else if (button == GLFW.GLFW_GAMEPAD_BUTTON_B) {
                if (client.currentScreen != null) {
                    client.currentScreen.onClose();
                    return;
                }
            }

            // Handles sneak button.
            if (SNEAK.is_button(button) && client.player != null) {
                this.toggle_sneaking(client);
            }
        }

        if (button == GLFW.GLFW_GAMEPAD_BUTTON_A && client.currentScreen != null && !is_screen_interactive(client.currentScreen) && this.action_gui_cooldown == 0 && this.ignore_next_a == 0) {
            double mouse_x = client.mouse.getX() * (double) client.getWindow().getScaledWidth() / (double) client.getWindow().getWidth();
            double mouse_y = client.mouse.getY() * (double) client.getWindow().getScaledHeight() / (double) client.getWindow().getHeight();
            if (action == 0) {
                client.currentScreen.mouseClicked(mouse_x, mouse_y, GLFW.GLFW_MOUSE_BUTTON_1);
            } else if (action == 1) {
                client.currentScreen.mouseReleased(mouse_x, mouse_y, GLFW.GLFW_MOUSE_BUTTON_1);
            }
            this.action_gui_cooldown = 5;
            return;
        }

        if (client.currentScreen == null && action != 2) {
            ButtonBinding.handle_button(client, button, action);
        }
    }

    private void handle_axe(@NotNull MinecraftClient client, int axis, float value, float abs_value, int state)
    {
        int as_button_state = value > 0.5F ? 1 : (value < -0.5F ? 2 : 0);

        if (client.currentScreen instanceof LambdaControlsControlsScreen && as_button_state != 0
                && !(as_button_state == 2 && (axis == GLFW.GLFW_GAMEPAD_AXIS_LEFT_TRIGGER || axis == GLFW.GLFW_GAMEPAD_AXIS_RIGHT_TRIGGER))) {
            LambdaControlsControlsScreen controls_screen = (LambdaControlsControlsScreen) client.currentScreen;
            if (controls_screen.focused_binding != null) {
                this.config.set_button_binding(controls_screen.focused_binding, axis_as_button(axis, as_button_state == 1));
                controls_screen.focused_binding = null;
                return;
            }
        }

        double dead_zone = this.config.get_dead_zone();
        if (client.currentScreen == null) {
            {
                int axis_minus = axis + 100;
                boolean current_plus_state = as_button_state == 1;
                boolean current_minus_state = as_button_state == 2;
                boolean previous_plus_state = AXIS_STATES.getOrDefault(axis, false);
                boolean previous_minus_state = AXIS_STATES.getOrDefault(axis_minus, false);

                if (current_plus_state != previous_plus_state) {
                    ButtonBinding.handle_button(client, ButtonBinding.axis_as_button(axis, true), 0);
                    if (current_plus_state)
                        AXIS_COOLDOWNS.put(axis, 5);
                } else if (current_plus_state) {
                    if (AXIS_COOLDOWNS.getOrDefault(axis, 0) == 0) {
                        AXIS_COOLDOWNS.put(axis, 5);
                    }
                }

                if (current_minus_state != previous_minus_state) {
                    ButtonBinding.handle_button(client, ButtonBinding.axis_as_button(axis, false), 0);
                    if (current_minus_state)
                        AXIS_COOLDOWNS.put(axis_minus, 5);
                } else if (current_minus_state) {
                    if (AXIS_COOLDOWNS.getOrDefault(axis_minus, 0) == 0) {
                        AXIS_COOLDOWNS.put(axis_minus, 5);
                    }
                }

                AXIS_STATES.put(axis, current_plus_state);
                AXIS_STATES.put(axis_minus, current_minus_state);
            }

            // Handles the look direction.
            this.handle_look(client, axis, abs_value, state);
        } else {
            boolean allow_mouse_control = true;

            if (this.action_gui_cooldown == 0 && this.config.is_movement_axis(axis) && is_screen_interactive(client.currentScreen)) {
                if (this.config.is_forward_button(axis, false, as_button_state)) {
                    allow_mouse_control = this.change_focus(client.currentScreen, false);
                } else if (this.config.is_back_button(axis, false, as_button_state)) {
                    allow_mouse_control = this.change_focus(client.currentScreen, true);
                } else if (this.config.is_left_button(axis, false, as_button_state)) {
                    allow_mouse_control = this.handle_left_right(client.currentScreen, false);
                } else if (this.config.is_right_button(axis, false, as_button_state)) {
                    allow_mouse_control = this.handle_left_right(client.currentScreen, true);
                }
            }

            float movement_x = 0.0F;
            float movement_y = 0.0F;

            if (this.config.is_back_button(axis, false, (value > 0 ? 1 : 2))) {
                movement_y = abs_value;
            } else if (this.config.is_forward_button(axis, false, (value > 0 ? 1 : 2))) {
                movement_y = -abs_value;
            } else if (this.config.is_left_button(axis, false, (value > 0 ? 1 : 2))) {
                movement_x = -abs_value;
            } else if (this.config.is_right_button(axis, false, (value > 0 ? 1 : 2))) {
                movement_x = abs_value;
            }

            if (client.currentScreen != null && allow_mouse_control) {
                boolean moving = Math.abs(movement_y) >= dead_zone || Math.abs(movement_x) >= dead_zone;
                if (moving) {
                /*
                    Updates the target mouse position when the initial movement stick movement is detected.
                    It prevents the cursor to jump to the old target mouse position if the user moves the cursor with the mouse.
                 */
                    if (Math.abs(prev_x_axis) < dead_zone && Math.abs(prev_y_axis) < dead_zone) {
                        double mouse_x = client.mouse.getX();
                        double mouse_y = client.mouse.getY();
                        prev_target_mouse_x = target_mouse_x = (int) mouse_x;
                        prev_target_mouse_y = target_mouse_y = (int) mouse_y;
                    }

                    if (Math.abs(movement_x) >= dead_zone)
                        this.mouse_speed_x = movement_x;
                    else
                        this.mouse_speed_x = 0.F;

                    if (Math.abs(movement_y) >= dead_zone)
                        this.mouse_speed_y = movement_y;
                    else
                        this.mouse_speed_y = 0.F;
                } else {
                    this.mouse_speed_x = 0.F;
                    this.mouse_speed_y = 0.F;
                }

                if (Math.abs(this.mouse_speed_x) > .05F || Math.abs(this.mouse_speed_y) > .05F) {
                    this.target_mouse_x += this.mouse_speed_x * this.config.get_mouse_speed();
                    this.target_mouse_x = MathHelper.clamp(this.target_mouse_x, 0, client.getWindow().getWidth());
                    this.target_mouse_y += this.mouse_speed_y * this.config.get_mouse_speed();
                    this.target_mouse_y = MathHelper.clamp(this.target_mouse_y, 0, client.getWindow().getHeight());
                }

                this.move_mouse_to_closest_slot(client, client.currentScreen);
            }

            this.prev_x_axis = movement_x;
            this.prev_y_axis = movement_y;
        }
    }

    /**
     * Handles the press on RB on LB.
     *
     * @param client The client's instance.
     * @param right  True if RB is pressed, else false.
     */
    private void handle_rb_lb(@NotNull MinecraftClient client, boolean right)
    {
        // When ingame
        if (client.currentScreen == null) {
            if (right)
                client.player.inventory.selectedSlot = client.player.inventory.selectedSlot == 8 ? 0 : client.player.inventory.selectedSlot + 1;
            else
                client.player.inventory.selectedSlot = client.player.inventory.selectedSlot == 0 ? 8 : client.player.inventory.selectedSlot - 1;
        } else if (client.currentScreen instanceof CreativeInventoryScreen) {
            CreativeInventoryScreenAccessor creative_inventory = (CreativeInventoryScreenAccessor) client.currentScreen;
            int current_selected_tab = creative_inventory.get_selected_tab();
            int next_tab = current_selected_tab + (right ? 1 : -1);
            if (next_tab < 0)
                next_tab = ItemGroup.GROUPS.length - 1;
            else if (next_tab >= ItemGroup.GROUPS.length)
                next_tab = 0;
            creative_inventory.set_selected_tab(ItemGroup.GROUPS[next_tab]);
        }
    }

    private boolean handle_a_button(@NotNull Screen screen, @NotNull Element focused)
    {
        if (focused instanceof AbstractPressableButtonWidget) {
            AbstractPressableButtonWidget button_widget = (AbstractPressableButtonWidget) focused;
            button_widget.playDownSound(MinecraftClient.getInstance().getSoundManager());
            button_widget.onPress();
            return true;
        } else if (focused instanceof LabelWidget) {
            ((LabelWidget) focused).on_press();
            return true;
        } else if (focused instanceof WorldListWidget) {
            WorldListWidget list = (WorldListWidget) focused;
            list.method_20159().ifPresent(WorldListWidget.Entry::play);
            return true;
        } else if (focused instanceof MultiplayerServerListWidget) {
            MultiplayerServerListWidget list = (MultiplayerServerListWidget) focused;
            MultiplayerServerListWidget.Entry entry = list.getSelected();
            if (entry instanceof MultiplayerServerListWidget.LanServerEntry || entry instanceof MultiplayerServerListWidget.ServerEntry) {
                ((MultiplayerScreen) screen).select(entry);
                ((MultiplayerScreen) screen).connect();
            }
        } else if (focused instanceof ParentElement) {
            Element child_focused = ((ParentElement) focused).getFocused();
            if (child_focused != null)
                return this.handle_a_button(screen, child_focused);
        }
        return false;
    }

    /**
     * Handles the left and right buttons.
     *
     * @param screen The current screen.
     * @param right  True if the right button is pressed, else false.
     */
    private boolean handle_left_right(@NotNull Screen screen, boolean right)
    {
        Element focused = screen.getFocused();
        if (focused != null)
            return this.handle_right_left_element(focused, right);
        return true;
    }

    private boolean handle_right_left_element(@NotNull Element element, boolean right)
    {
        if (element instanceof SliderWidget) {
            SliderWidget slider = (SliderWidget) element;
            slider.keyPressed(right ? 262 : 263, 0, 0);
            this.action_gui_cooldown = 2; // Prevent to press too quickly the focused element, so we have to skip 5 ticks.
            return false;
        } else if (element instanceof AlwaysSelectedEntryListWidget) {
            ((EntryListWidgetAccessor) element).move_selection(right ? 1 : -1);
            return false;
        } else if (element instanceof ParentElement) {
            ParentElement entry_list = (ParentElement) element;
            Element focused = entry_list.getFocused();
            if (focused == null)
                return true;
            return this.handle_right_left_element(focused, right);
        }
        return true;
    }

    /**
     * Toggles whether the player is sneaking.
     *
     * @param client The client's instance.
     */
    private void toggle_sneaking(@NotNull MinecraftClient client)
    {
        ((KeyBindingAccessor) client.options.keySneak).handle_press_state(!client.options.keySneak.isPressed());
    }

    /**
     * Handles the look direction input.
     *
     * @param client The client isntance.
     * @param axis   The axis to change.
     * @param value  The value of the look.
     * @param state  The state.
     */
    private void handle_look(@NotNull MinecraftClient client, int axis, float value, int state)
    {
        // Handles the look direction.
        if (client.player != null) {
            double pow_value = Math.pow(value, 2.0);
            if (axis == GLFW_GAMEPAD_AXIS_RIGHT_Y) {
                if (state == 2) {
                    this.target_pitch = client.player.pitch - this.config.get_right_y_axis_sign() * (this.config.get_rotation_speed() * pow_value) * 0.33D;
                    this.target_pitch = MathHelper.clamp(this.target_pitch, -90.0D, 90.0D);
                } else if (state == 1) {
                    this.target_pitch = client.player.pitch + this.config.get_right_y_axis_sign() * (this.config.get_rotation_speed() * pow_value) * 0.33D;
                    this.target_pitch = MathHelper.clamp(this.target_pitch, -90.0D, 90.0D);
                }
            }
            if (axis == GLFW_GAMEPAD_AXIS_RIGHT_X) {
                if (state == 2) {
                    this.target_yaw = client.player.yaw - this.config.get_right_x_axis_sign() * (this.config.get_rotation_speed() * pow_value) * 0.33D;
                } else if (state == 1) {
                    this.target_yaw = client.player.yaw + this.config.get_right_x_axis_sign() * (this.config.get_rotation_speed() * pow_value) * 0.33D;
                }
            }
        }
    }

    private boolean change_focus(@NotNull Screen screen, boolean down)
    {
        if (!screen.changeFocus(down)) {
            if (screen.changeFocus(down)) {
                this.action_gui_cooldown = 5;
                return false;
            }
            return true;
        } else {
            this.action_gui_cooldown = 5;
            return false;
        }
    }

    private static boolean is_screen_interactive(@NotNull Screen screen)
    {
        return !(screen instanceof AdvancementsScreen || screen instanceof AbstractContainerScreen);
    }

    // Inspired from https://github.com/MrCrayfish/Controllable/blob/1.14.X/src/main/java/com/mrcrayfish/controllable/client/ControllerInput.java#L686.
    private void move_mouse_to_closest_slot(@NotNull MinecraftClient client, @Nullable Screen screen)
    {
        // Makes the mouse attracted to slots. This helps with selecting items when using a controller.
        if (screen instanceof AbstractContainerScreen) {
            AbstractContainerScreen inventory_screen = (AbstractContainerScreen) screen;
            AbstractContainerScreenAccessor accessor = (AbstractContainerScreenAccessor) inventory_screen;
            int gui_left = accessor.get_x();
            int gui_top = accessor.get_y();
            int mouse_x = (int) (target_mouse_x * (double) client.getWindow().getScaledWidth() / (double) client.getWindow().getWidth());
            int mouse_y = (int) (target_mouse_y * (double) client.getWindow().getScaledHeight() / (double) client.getWindow().getHeight());

            // Finds the closest slot in the GUI within 14 pixels.
            Optional<Pair<Slot, Double>> closest_slot = inventory_screen.getContainer().slotList.parallelStream()
                    .map(slot -> {
                        int pos_x = gui_left + slot.xPosition + 8;
                        int pos_y = gui_top + slot.yPosition + 8;

                        // Distance between the slot and the cursor.
                        double distance = Math.sqrt(Math.pow(pos_x - mouse_x, 2) + Math.pow(pos_y - mouse_y, 2));
                        return Pair.of(slot, distance);
                    }).filter(entry -> entry.get_value() <= 14.0)
                    .min(Comparator.comparingDouble(Pair::get_value));

            if (closest_slot.isPresent()) {
                Slot slot = closest_slot.get().get_key();
                if (slot.hasStack() || !client.player.inventory.getMainHandStack().isEmpty()) {
                    int slot_center_x_scaled = gui_left + slot.xPosition + 8;
                    int slot_center_y_scaled = gui_top + slot.yPosition + 8;
                    int slot_center_x = (int) (slot_center_x_scaled / ((double) client.getWindow().getScaledWidth() / (double) client.getWindow().getWidth()));
                    int slot_center_y = (int) (slot_center_y_scaled / ((double) client.getWindow().getScaledHeight() / (double) client.getWindow().getHeight()));
                    double delta_x = slot_center_x - target_mouse_x;
                    double delta_y = slot_center_y - target_mouse_y;

                    if (mouse_x != slot_center_x_scaled || mouse_y != slot_center_y_scaled) {
                        this.target_mouse_x += delta_x * 0.75;
                        this.target_mouse_y += delta_y * 0.75;
                    } else {
                        this.mouse_speed_x *= 0.3F;
                        this.mouse_speed_y *= 0.3F;
                    }
                    this.mouse_speed_x *= .75F;
                    this.mouse_speed_y *= .75F;
                } else {
                    this.mouse_speed_x *= .1F;
                    this.mouse_speed_y *= .1F;
                }
            } else {
                this.mouse_speed_x *= .3F;
                this.mouse_speed_y *= .3F;
            }
        } else {
            this.mouse_speed_x = 0.F;
            this.mouse_speed_y = 0.F;
        }
    }
}
