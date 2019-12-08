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
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.ScreenshotUtils;
import org.aperlambda.lambdacommon.utils.Nameable;
import org.aperlambda.lambdacommon.utils.Pair;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

import java.util.*;
import java.util.stream.Stream;

/**
 * Represents a button binding.
 *
 * @author LambdAurora
 */
public class ButtonBinding implements Nameable
{
    private static final List<ButtonBinding>                             BINDINGS             = new ArrayList<>();
    private static final Map<Pair<String, Integer>, List<ButtonBinding>> CATEGORIES           = new HashMap<>();
    public static final  String                                          MOVEMENT_CATEGORY    = "key.categories.movement";
    public static final  String                                          GAMEPLAY_CATEGORY    = "key.categories.gameplay";
    public static final  String                                          INVENTORY_CATEGORY   = "key.categories.inventory";
    public static final  String                                          MULTIPLAYER_CATEGORY = "key.categories.multiplayer";
    public static final  String                                          MISC_CATEGORY        = "key.categories.misc";
    public static final  ButtonBinding                                   ATTACK               = new ButtonBinding(axis_as_button(GLFW.GLFW_GAMEPAD_AXIS_RIGHT_TRIGGER, true), "attack");
    public static final  ButtonBinding                                   BACK                 = new ButtonBinding(axis_as_button(GLFW.GLFW_GAMEPAD_AXIS_LEFT_Y, false), "back");
    public static final  ButtonBinding                                   CHAT                 = new ButtonBinding(GLFW.GLFW_GAMEPAD_BUTTON_DPAD_RIGHT, "chat");
    public static final  ButtonBinding                                   DROP_ITEM            = new ButtonBinding(GLFW.GLFW_GAMEPAD_BUTTON_B, "drop_item");
    public static final  ButtonBinding                                   FORWARD              = new ButtonBinding(axis_as_button(GLFW.GLFW_GAMEPAD_AXIS_LEFT_Y, true), "forward");
    public static final  ButtonBinding                                   INVENTORY            = new ButtonBinding(GLFW.GLFW_GAMEPAD_BUTTON_Y, "inventory");
    public static final  ButtonBinding                                   JUMP                 = new ButtonBinding(GLFW.GLFW_GAMEPAD_BUTTON_A, "jump");
    public static final  ButtonBinding                                   LEFT                 = new ButtonBinding(axis_as_button(GLFW.GLFW_GAMEPAD_AXIS_LEFT_X, false), "left");
    public static final  ButtonBinding                                   PAUSE_GAME           = new ButtonBinding(GLFW.GLFW_GAMEPAD_BUTTON_START, "pause_game");
    public static final  ButtonBinding                                   PICK_BLOCK           = new ButtonBinding(GLFW.GLFW_GAMEPAD_BUTTON_DPAD_LEFT, "pick_block");
    public static final  ButtonBinding                                   PLAYER_LIST          = new ButtonBinding(GLFW.GLFW_GAMEPAD_BUTTON_BACK, "player_list");
    public static final  ButtonBinding                                   RIGHT                = new ButtonBinding(axis_as_button(GLFW.GLFW_GAMEPAD_AXIS_LEFT_X, true), "right");
    public static final  ButtonBinding                                   SCREENSHOT           = new ButtonBinding(GLFW.GLFW_GAMEPAD_BUTTON_DPAD_DOWN, "screenshot",
            Collections.singletonList((client, action) -> {
                ScreenshotUtils.method_1659(client.runDirectory, client.window.getFramebufferWidth(), client.window.getFramebufferHeight(), client.getFramebuffer(),
                        text -> client.execute(() -> client.inGameHud.getChatHud().addMessage(text)));
                return true;
            }));
    public static final  ButtonBinding                                   SMOOTH_CAMERA        = new ButtonBinding(-1, "toggle_smooth_camera");
    public static final  ButtonBinding                                   SNEAK                = new ButtonBinding(GLFW.GLFW_GAMEPAD_BUTTON_RIGHT_THUMB, "sneak");
    public static final  ButtonBinding                                   SPRINT               = new ButtonBinding(GLFW.GLFW_GAMEPAD_BUTTON_LEFT_THUMB, "sprint");
    public static final  ButtonBinding                                   SWAP_HANDS           = new ButtonBinding(GLFW.GLFW_GAMEPAD_BUTTON_X, "swap_hands");
    public static final  ButtonBinding                                   TOGGLE_PERSPECTIVE   = new ButtonBinding(GLFW.GLFW_GAMEPAD_BUTTON_DPAD_UP, "toggle_perspective");
    public static final  ButtonBinding                                   USE                  = new ButtonBinding(axis_as_button(GLFW.GLFW_GAMEPAD_AXIS_LEFT_TRIGGER, true), "use");

    private int               button;
    private int               default_button;
    private String            key;
    private KeyBinding        minecraft_key_binding = null;
    private List<PressAction> actions               = new ArrayList<>(Collections.singletonList((client, action) -> {
        this.as_key_binding().ifPresent(key_binding -> ((KeyBindingAccessor) key_binding).handle_press_state(this.is_button_down()));
        return true;
    }));
    private boolean           pressed               = false;

    public ButtonBinding(int button, @NotNull String key, @NotNull List<PressAction> actions)
    {
        this.default_button = this.button = button;
        this.key = key;
        this.actions.addAll(actions);
        BINDINGS.add(this);
    }

    public ButtonBinding(int button, @NotNull String key)
    {
        this(button, key, Collections.emptyList());
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

    /**
     * Sets the bound button.
     *
     * @param button The bound button.
     */
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

    /**
     * Returns whether this button binding is bound or not.
     *
     * @return True if this button binding is bound, else false.
     */
    public boolean is_not_bound()
    {
        return this.button == -1;
    }

    /**
     * Gets the default button assigned to this binding.
     *
     * @return The default button.
     */
    public int get_default_button()
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
        return this.button == this.default_button;
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

    public static void load_from_config(@NotNull LambdaControlsConfig config)
    {
        BINDINGS.forEach(config::load_button_binding);
    }

    public static void set_button_state(int button, boolean state)
    {
        BINDINGS.parallelStream().filter(binding -> Objects.equals(binding.button, button))
                .forEach(binding -> binding.pressed = state);
    }

    public static void handle_button(@NotNull MinecraftClient client, int button, int action)
    {
        BINDINGS.parallelStream().filter(binding -> binding.button == button)
                .forEach(binding -> {
                    for (int i = binding.actions.size() - 1; i >= 0; i--) {
                        if (binding.actions.get(i).press(client, action))
                            break;
                    }
                });
    }

    /**
     * Returns whether the button has duplicated bindings.
     *
     * @param button The button to check.
     * @return True if the button has duplicated bindings, else false.
     */
    public static boolean has_duplicates(int button)
    {
        return BINDINGS.parallelStream().filter(binding -> binding.button == button).count() > 1;
    }

    /**
     * Returns the localized name of the specified button.
     *
     * @param button The button.
     * @return The localized name of the button.
     */
    public static @NotNull String get_localized_button_name(int button)
    {
        switch (button) {
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

    public static @NotNull Stream<ButtonBinding> stream()
    {
        return BINDINGS.stream();
    }

    public static @NotNull Stream<Map.Entry<Pair<String, Integer>, List<ButtonBinding>>> stream_categories()
    {
        return CATEGORIES.entrySet().stream();
    }

    static {
        CATEGORIES.put(Pair.of(MOVEMENT_CATEGORY, 0), Arrays.asList(
                FORWARD,
                BACK,
                LEFT,
                RIGHT,
                JUMP,
                SNEAK,
                SPRINT
        ));
        CATEGORIES.put(Pair.of(GAMEPLAY_CATEGORY, 1), Arrays.asList(
                ATTACK,
                PICK_BLOCK,
                USE
        ));
        CATEGORIES.put(Pair.of(INVENTORY_CATEGORY, 2), Arrays.asList(
                DROP_ITEM,
                INVENTORY,
                SWAP_HANDS
        ));
        CATEGORIES.put(Pair.of(MULTIPLAYER_CATEGORY, 2), Arrays.asList(
                CHAT,
                PLAYER_LIST
        ));
        CATEGORIES.put(Pair.of(MISC_CATEGORY, 3), Arrays.asList(
                SCREENSHOT,
                //SMOOTH_CAMERA,
                TOGGLE_PERSPECTIVE
        ));
    }

    @FunctionalInterface
    public static interface PressAction
    {
        /**
         * Handles when there is a press action on the button.
         *
         * @param client The client instance.
         * @param action The action done.
         */
        boolean press(@NotNull MinecraftClient client, int action);
    }
}
