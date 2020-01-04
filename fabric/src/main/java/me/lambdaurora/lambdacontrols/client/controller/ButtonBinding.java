/*
 * Copyright © 2020 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of LambdaControls.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package me.lambdaurora.lambdacontrols.client.controller;

import me.lambdaurora.lambdacontrols.client.ButtonState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.resource.language.I18n;
import org.aperlambda.lambdacommon.utils.Nameable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.*;
import java.util.stream.Collectors;

import static me.lambdaurora.lambdacontrols.client.controller.InputManager.register_binding;
import static me.lambdaurora.lambdacontrols.client.controller.InputManager.register_default_category;

/**
 * Represents a button binding.
 *
 * @author LambdAurora
 * @version 1.1.0
 * @since 1.0.0
 */
public class ButtonBinding implements Nameable
{
    public static final ButtonCategory MOVEMENT_CATEGORY;
    public static final ButtonCategory GAMEPLAY_CATEGORY;
    public static final ButtonCategory INVENTORY_CATEGORY;
    public static final ButtonCategory MULTIPLAYER_CATEGORY;
    public static final ButtonCategory MISC_CATEGORY;

    public static final ButtonBinding ATTACK             = register_binding(new ButtonBinding("attack", new int[]{axis_as_button(GLFW.GLFW_GAMEPAD_AXIS_RIGHT_TRIGGER, true)}, false));
    public static final ButtonBinding BACK               = register_binding(new ButtonBinding("back", new int[]{axis_as_button(GLFW.GLFW_GAMEPAD_AXIS_LEFT_Y, false)}, false));
    public static final ButtonBinding CHAT               = register_binding(new ButtonBinding("chat", new int[]{GLFW.GLFW_GAMEPAD_BUTTON_DPAD_RIGHT}, true));
    public static final ButtonBinding DROP_ITEM          = register_binding(new ButtonBinding("drop_item", new int[]{GLFW.GLFW_GAMEPAD_BUTTON_B}, true));
    public static final ButtonBinding FORWARD            = register_binding(new ButtonBinding("forward", new int[]{axis_as_button(GLFW.GLFW_GAMEPAD_AXIS_LEFT_Y, true)}, false));
    public static final ButtonBinding HOTBAR_LEFT        = register_binding(new ButtonBinding("hotbar_left", new int[]{GLFW.GLFW_GAMEPAD_BUTTON_LEFT_BUMPER},
            Collections.singletonList(InputHandlers.handle_hotbar(false)), true));
    public static final ButtonBinding HOTBAR_RIGHT       = register_binding(new ButtonBinding("hotbar_right", new int[]{GLFW.GLFW_GAMEPAD_BUTTON_RIGHT_BUMPER},
            Collections.singletonList(InputHandlers.handle_hotbar(true)), true));
    public static final ButtonBinding INVENTORY          = register_binding(new ButtonBinding("inventory", new int[]{GLFW.GLFW_GAMEPAD_BUTTON_Y}, true));
    public static final ButtonBinding JUMP               = register_binding(new ButtonBinding("jump", new int[]{GLFW.GLFW_GAMEPAD_BUTTON_A}, false));
    public static final ButtonBinding LEFT               = register_binding(new ButtonBinding("left", new int[]{axis_as_button(GLFW.GLFW_GAMEPAD_AXIS_LEFT_X, false)}, false));
    public static final ButtonBinding PAUSE_GAME         = register_binding(new ButtonBinding("pause_game", new int[]{GLFW.GLFW_GAMEPAD_BUTTON_START},
            Collections.singletonList(InputHandlers::handle_pause_game), true));
    public static final ButtonBinding PICK_BLOCK         = register_binding(new ButtonBinding("pick_block", new int[]{GLFW.GLFW_GAMEPAD_BUTTON_DPAD_LEFT}, true));
    public static final ButtonBinding PLAYER_LIST        = register_binding(new ButtonBinding("player_list", new int[]{GLFW.GLFW_GAMEPAD_BUTTON_BACK}, false));
    public static final ButtonBinding RIGHT              = register_binding(new ButtonBinding("right", new int[]{axis_as_button(GLFW.GLFW_GAMEPAD_AXIS_LEFT_X, true)}, false));
    public static final ButtonBinding SCREENSHOT         = register_binding(new ButtonBinding("screenshot", new int[]{GLFW.GLFW_GAMEPAD_BUTTON_DPAD_UP, GLFW.GLFW_GAMEPAD_BUTTON_A},
            Collections.singletonList(InputHandlers::handle_screenshot), true));
    public static final ButtonBinding SMOOTH_CAMERA      = register_binding(new ButtonBinding("toggle_smooth_camera", new int[]{-1}, true));
    public static final ButtonBinding SNEAK              = register_binding(new ButtonBinding("sneak", new int[]{GLFW.GLFW_GAMEPAD_BUTTON_RIGHT_THUMB},
            Arrays.asList(PressAction.DEFAULT_ACTION, InputHandlers::handle_toggle_sneak), true));
    public static final ButtonBinding SPRINT             = register_binding(new ButtonBinding("sprint", new int[]{GLFW.GLFW_GAMEPAD_BUTTON_LEFT_THUMB}, false));
    public static final ButtonBinding SWAP_HANDS         = register_binding(new ButtonBinding("swap_hands", new int[]{GLFW.GLFW_GAMEPAD_BUTTON_X}, true));
    public static final ButtonBinding TOGGLE_PERSPECTIVE = register_binding(new ButtonBinding("toggle_perspective", new int[]{GLFW.GLFW_GAMEPAD_BUTTON_DPAD_UP, GLFW.GLFW_GAMEPAD_BUTTON_Y}, true));
    public static final ButtonBinding USE                = register_binding(new ButtonBinding("use", new int[]{axis_as_button(GLFW.GLFW_GAMEPAD_AXIS_LEFT_TRIGGER, true)}, false));

    private int[]             button;
    private int[]             default_button;
    private String            key;
    private KeyBinding        minecraft_key_binding = null;
    private List<PressAction> actions               = new ArrayList<>(Collections.singletonList(PressAction.DEFAULT_ACTION));
    private boolean           has_cooldown;
    private int               cooldown              = 0;
    boolean pressed = false;

    public ButtonBinding(@NotNull String key, int[] default_button, @NotNull List<PressAction> actions, boolean has_cooldown)
    {
        this.set_button(this.default_button = default_button);
        this.key = key;
        this.actions.addAll(actions);
        this.has_cooldown = has_cooldown;
    }

    public ButtonBinding(@NotNull String key, int[] default_button, boolean has_cooldown)
    {
        this(key, default_button, Collections.emptyList(), has_cooldown);
    }

    /**
     * Returns the button bound.
     *
     * @return The bound button.
     */
    public int[] get_button()
    {
        return this.button;
    }

    /**
     * Sets the bound button.
     *
     * @param button The bound button.
     */
    public void set_button(int[] button)
    {
        this.button = button;

        if (InputManager.has_binding(this))
            InputManager.sort_bindings();
    }

    /**
     * Returns whether the bound button is the specified button or not.
     *
     * @param button The button to check.
     * @return True if the bound button is the specified button, else false.
     */
    public boolean is_button(int[] button)
    {
        return InputManager.are_buttons_equivalent(button, this.button);
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

    /**
     * Returns whether this button binding is bound or not.
     *
     * @return True if this button binding is bound, else false.
     */
    public boolean is_not_bound()
    {
        return this.button.length == 0 || this.button[0] == -1;
    }

    /**
     * Gets the default button assigned to this binding.
     *
     * @return The default button.
     */
    public int[] get_default_button()
    {
        return this.default_button;
    }

    /**
     * Returns whether the assigned button is the default button.
     *
     * @return True if the assigned button is the default button, else false.
     */
    public boolean is_default()
    {
        return this.button.length == this.default_button.length && InputManager.are_buttons_equivalent(this.button, this.default_button);
    }

    /**
     * Returns the button code.
     *
     * @return The button code.
     */
    public @NotNull String get_button_code()
    {
        return Arrays.stream(this.button)
                .mapToObj(btn -> Integer.valueOf(btn).toString())
                .collect(Collectors.joining("+"));
    }

    /**
     * Sets the key binding to emulate with this button binding.
     *
     * @param key_binding The optional key binding.
     */
    public void set_key_binding(@Nullable KeyBinding key_binding)
    {
        this.minecraft_key_binding = key_binding;
    }

    /**
     * Updates the button binding cooldown.
     */
    public void update()
    {
        if (this.has_cooldown && this.cooldown > 0)
            this.cooldown--;
    }

    /**
     * Handles the button binding.
     *
     * @param client The client instance.
     * @param state  The state.
     */
    public void handle(@NotNull MinecraftClient client, @NotNull ButtonState state)
    {
        if (state == ButtonState.REPEAT && this.has_cooldown && this.cooldown != 0)
            return;
        if (this.has_cooldown && state.is_pressed()) {
            this.cooldown = 5;

        }
        for (int i = this.actions.size() - 1; i >= 0; i--) {
            if (this.actions.get(i).press(client, this, state))
                break;
        }
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
    public static int controller2_button(int button)
    {
        return 500 + button;
    }

    public static void init(@NotNull GameOptions options)
    {
        ATTACK.minecraft_key_binding = options.keyAttack;
        BACK.minecraft_key_binding = options.keyBack;
        CHAT.minecraft_key_binding = options.keyChat;
        DROP_ITEM.minecraft_key_binding = options.keyDrop;
        FORWARD.minecraft_key_binding = options.keyForward;
        INVENTORY.minecraft_key_binding = options.keyInventory;
        JUMP.minecraft_key_binding = options.keyJump;
        LEFT.minecraft_key_binding = options.keyLeft;
        PICK_BLOCK.minecraft_key_binding = options.keyPickItem;
        PLAYER_LIST.minecraft_key_binding = options.keyPlayerList;
        RIGHT.minecraft_key_binding = options.keyRight;
        SCREENSHOT.minecraft_key_binding = options.keyScreenshot;
        SMOOTH_CAMERA.minecraft_key_binding = options.keySmoothCamera;
        SNEAK.minecraft_key_binding = options.keySneak;
        SPRINT.minecraft_key_binding = options.keySprint;
        SWAP_HANDS.minecraft_key_binding = options.keySwapHands;
        TOGGLE_PERSPECTIVE.minecraft_key_binding = options.keyTogglePerspective;
        USE.minecraft_key_binding = options.keyUse;
    }

    /**
     * Returns the localized name of the specified button.
     *
     * @param button The button.
     * @return The localized name of the button.
     */
    public static @NotNull String get_localized_button_name(int button)
    {
        switch (button % 500) {
            case -1:
                return I18n.translate("key.keyboard.unknown");
            case GLFW.GLFW_GAMEPAD_BUTTON_A:
                return I18n.translate("lambdacontrols.button.a");
            case GLFW.GLFW_GAMEPAD_BUTTON_B:
                return I18n.translate("lambdacontrols.button.b");
            case GLFW.GLFW_GAMEPAD_BUTTON_X:
                return I18n.translate("lambdacontrols.button.x");
            case GLFW.GLFW_GAMEPAD_BUTTON_Y:
                return I18n.translate("lambdacontrols.button.y");
            case GLFW.GLFW_GAMEPAD_BUTTON_LEFT_BUMPER:
                return I18n.translate("lambdacontrols.button.left_bumper");
            case GLFW.GLFW_GAMEPAD_BUTTON_RIGHT_BUMPER:
                return I18n.translate("lambdacontrols.button.right_bumper");
            case GLFW.GLFW_GAMEPAD_BUTTON_BACK:
                return I18n.translate("lambdacontrols.button.back");
            case GLFW.GLFW_GAMEPAD_BUTTON_START:
                return I18n.translate("lambdacontrols.button.start");
            case GLFW.GLFW_GAMEPAD_BUTTON_GUIDE:
                return I18n.translate("lambdacontrols.button.guide");
            case GLFW.GLFW_GAMEPAD_BUTTON_LEFT_THUMB:
                return I18n.translate("lambdacontrols.button.left_thumb");
            case GLFW.GLFW_GAMEPAD_BUTTON_RIGHT_THUMB:
                return I18n.translate("lambdacontrols.button.right_thumb");
            case GLFW.GLFW_GAMEPAD_BUTTON_DPAD_UP:
                return I18n.translate("lambdacontrols.button.dpad_up");
            case GLFW.GLFW_GAMEPAD_BUTTON_DPAD_RIGHT:
                return I18n.translate("lambdacontrols.button.dpad_right");
            case GLFW.GLFW_GAMEPAD_BUTTON_DPAD_DOWN:
                return I18n.translate("lambdacontrols.button.dpad_down");
            case GLFW.GLFW_GAMEPAD_BUTTON_DPAD_LEFT:
                return I18n.translate("lambdacontrols.button.dpad_left");
            case 100:
                return I18n.translate("lambdacontrols.axis.left_x+");
            case 101:
                return I18n.translate("lambdacontrols.axis.left_y+");
            case 102:
                return I18n.translate("lambdacontrols.axis.right_x+");
            case 103:
                return I18n.translate("lambdacontrols.axis.right_y+");
            case 104:
                return I18n.translate("lambdacontrols.axis.left_trigger");
            case 105:
                return I18n.translate("lambdacontrols.axis.right_trigger");
            case 200:
                return I18n.translate("lambdacontrols.axis.left_x-");
            case 201:
                return I18n.translate("lambdacontrols.axis.left_y-");
            case 202:
                return I18n.translate("lambdacontrols.axis.right_x-");
            case 203:
                return I18n.translate("lambdacontrols.axis.right_y-");
            default:
                return I18n.translate("lambdacontrols.button.unknown", button);
        }
    }

    static {
        MOVEMENT_CATEGORY = register_default_category("key.categories.movement", category -> category.register_all_bindings(
                ButtonBinding.FORWARD,
                ButtonBinding.BACK,
                ButtonBinding.LEFT,
                ButtonBinding.RIGHT,
                ButtonBinding.JUMP,
                ButtonBinding.SNEAK,
                ButtonBinding.SPRINT));
        GAMEPLAY_CATEGORY = register_default_category("key.categories.gameplay", category -> category.register_all_bindings(
                ButtonBinding.ATTACK,
                ButtonBinding.PICK_BLOCK,
                ButtonBinding.USE
        ));
        INVENTORY_CATEGORY = register_default_category("key.categories.inventory", category -> category.register_all_bindings(
                ButtonBinding.DROP_ITEM,
                ButtonBinding.HOTBAR_LEFT,
                ButtonBinding.HOTBAR_RIGHT,
                ButtonBinding.INVENTORY,
                ButtonBinding.SWAP_HANDS
        ));
        MULTIPLAYER_CATEGORY = register_default_category("key.categories.multiplayer",
                category -> category.register_all_bindings(ButtonBinding.CHAT, ButtonBinding.PLAYER_LIST));
        MISC_CATEGORY = register_default_category("key.categories.misc", category -> category.register_all_bindings(
                ButtonBinding.SCREENSHOT,
                //SMOOTH_CAMERA,
                ButtonBinding.TOGGLE_PERSPECTIVE
        ));
    }
}
