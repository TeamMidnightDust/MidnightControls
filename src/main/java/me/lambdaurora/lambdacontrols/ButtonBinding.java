/*
 * Copyright © 2019 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of LambdaControls.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package me.lambdaurora.lambdacontrols;

import me.lambdaurora.lambdacontrols.util.KeyBindingAccessor;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.options.KeyBinding;
import org.aperlambda.lambdacommon.utils.Nameable;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Represents a button binding.
 *
 * @author LambdAurora
 */
public class ButtonBinding implements Nameable
{
    private static final List<ButtonBinding> BINDINGS   = new ArrayList<>();
    public static final  ButtonBinding       ATTACK     = new ButtonBinding(axis_as_button(GLFW.GLFW_GAMEPAD_AXIS_RIGHT_TRIGGER, true), "attack");
    public static final  ButtonBinding       BACK       = new ButtonBinding(axis_as_button(GLFW.GLFW_GAMEPAD_AXIS_LEFT_Y, false), "back");
    public static final  ButtonBinding       DROP_ITEM  = new ButtonBinding(GLFW.GLFW_GAMEPAD_BUTTON_B, "drop_item");
    public static final  ButtonBinding       FORWARD    = new ButtonBinding(axis_as_button(GLFW.GLFW_GAMEPAD_AXIS_LEFT_Y, true), "forward");
    public static final  ButtonBinding       INVENTORY  = new ButtonBinding(GLFW.GLFW_GAMEPAD_BUTTON_Y, "inventory");
    public static final  ButtonBinding       JUMP       = new ButtonBinding(GLFW.GLFW_GAMEPAD_BUTTON_A, "jump");
    public static final  ButtonBinding       PAUSE_GAME = new ButtonBinding(GLFW.GLFW_GAMEPAD_BUTTON_START, "pause_game");
    public static final  ButtonBinding       SNEAK      = new ButtonBinding(GLFW.GLFW_GAMEPAD_BUTTON_RIGHT_THUMB, "sneak");
    public static final  ButtonBinding       SPRINT     = new ButtonBinding(GLFW.GLFW_GAMEPAD_BUTTON_LEFT_THUMB, "sprint");
    public static final  ButtonBinding       SWAP_HANDS = new ButtonBinding(GLFW.GLFW_GAMEPAD_BUTTON_X, "swap_hands");

    private int        button;
    private String     key;
    private KeyBinding minecraft_key_binding = null;
    private boolean    pressed               = false;

    public ButtonBinding(int button, @NotNull String key)
    {
        this.button = button;
        this.key = key;
        BINDINGS.add(this);
    }

    /**
     * Returns the button bound.
     *
     * @return The bound button.
     */
    public int get_button()
    {
        return this.button;
    }

    public void set_button(int button)
    {
        this.button = button;
    }

    /**
     * Returns whether the bound button is the specified button or not.
     *
     * @param button The button to check.
     * @return True if the bound button is the specified button, else false.
     */
    public boolean is_button(int button)
    {
        return this.button == button;
    }

    /**
     * Returns whether this button is down or not.
     *
     * @return True if the button is down, else false.
     */
    public boolean is_button_down()
    {
        return this.pressed;
    }

    @Override
    public @NotNull String get_name()
    {
        return this.key;
    }

    /**
     * Returns the translation key of this button binding.
     *
     * @return The translation key.
     */
    public @NotNull String get_translation_key()
    {
        return "lambdacontrols.action." + this.get_name();
    }

    /**
     * Returns the key binding equivalent of this button binding.
     *
     * @return The key binding equivalent.
     */
    public @NotNull Optional<KeyBinding> as_key_binding()
    {
        return Optional.ofNullable(this.minecraft_key_binding);
    }

    /**
     * Returns the specified axis as a button.
     *
     * @param axis     The axis.
     * @param positive True if the axis part is positive, else false.
     * @return The axis as a button.
     */
    public static int axis_as_button(int axis, boolean positive)
    {
        return positive ? 100 + axis : 200 + axis;
    }

    /**
     * Returns the second Joycon's specified button code.
     *
     * @param button The raw button code.
     * @return The second Joycon's button code.
     */
    public static int joycon2_button(int button)
    {
        return 300 + button;
    }

    public static void init(@NotNull GameOptions options)
    {
        BACK.minecraft_key_binding = options.keyBack;
        DROP_ITEM.minecraft_key_binding = options.keyDrop;
        FORWARD.minecraft_key_binding = options.keyForward;
        INVENTORY.minecraft_key_binding = options.keyInventory;
        JUMP.minecraft_key_binding = options.keyJump;
        SPRINT.minecraft_key_binding = options.keySprint;
        SWAP_HANDS.minecraft_key_binding = options.keySwapHands;
    }

    public static void set_button_state(int button, boolean state)
    {
        BINDINGS.parallelStream().filter(binding -> Objects.equals(binding.button, button))
                .forEach(binding -> binding.pressed = state);
    }

    public static void handle_button(int button, boolean state)
    {
        BINDINGS.parallelStream().filter(binding -> binding.button == button)
                .map(ButtonBinding::as_key_binding)
                .forEach(binding -> binding.ifPresent(key_binding -> ((KeyBindingAccessor) key_binding).handle_press_state(state)));
    }
}
