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
import org.aperlambda.lambdacommon.Identifier;
import org.aperlambda.lambdacommon.utils.Identifiable;
import org.aperlambda.lambdacommon.utils.Nameable;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * Represents a button binding.
 *
 * @author LambdAurora
 */
public class ButtonBinding implements Nameable
{
    private static final List<ButtonBinding> BINDINGS           = new ArrayList<>();
    private static final List<Category>      CATEGORIES         = new ArrayList<>();
    public static final  Category            MOVEMENT_CATEGORY;
    public static final  Category            GAMEPLAY_CATEGORY;
    public static final  Category            INVENTORY_CATEGORY;
    public static final  Category            MULTIPLAYER_CATEGORY;
    public static final  Category            MISC_CATEGORY;
    public static final  ButtonBinding       ATTACK             = new ButtonBinding("attack", axis_as_button(GLFW.GLFW_GAMEPAD_AXIS_RIGHT_TRIGGER, true));
    public static final  ButtonBinding       BACK               = new ButtonBinding("back", axis_as_button(GLFW.GLFW_GAMEPAD_AXIS_LEFT_Y, false));
    public static final  ButtonBinding       CHAT               = new ButtonBinding("chat", GLFW.GLFW_GAMEPAD_BUTTON_DPAD_RIGHT);
    public static final  ButtonBinding       DROP_ITEM          = new ButtonBinding("drop_item", GLFW.GLFW_GAMEPAD_BUTTON_B);
    public static final  ButtonBinding       FORWARD            = new ButtonBinding("forward", axis_as_button(GLFW.GLFW_GAMEPAD_AXIS_LEFT_Y, true));
    public static final  ButtonBinding       INVENTORY          = new ButtonBinding("inventory", GLFW.GLFW_GAMEPAD_BUTTON_Y);
    public static final  ButtonBinding       JUMP               = new ButtonBinding("jump", GLFW.GLFW_GAMEPAD_BUTTON_A);
    public static final  ButtonBinding       LEFT               = new ButtonBinding("left", axis_as_button(GLFW.GLFW_GAMEPAD_AXIS_LEFT_X, false));
    public static final  ButtonBinding       PAUSE_GAME         = new ButtonBinding("pause_game", GLFW.GLFW_GAMEPAD_BUTTON_START);
    public static final  ButtonBinding       PICK_BLOCK         = new ButtonBinding("pick_block", GLFW.GLFW_GAMEPAD_BUTTON_DPAD_LEFT);
    public static final  ButtonBinding       PLAYER_LIST        = new ButtonBinding("player_list", GLFW.GLFW_GAMEPAD_BUTTON_BACK);
    public static final  ButtonBinding       RIGHT              = new ButtonBinding("right", axis_as_button(GLFW.GLFW_GAMEPAD_AXIS_LEFT_X, true));
    public static final  ButtonBinding       SCREENSHOT         = new ButtonBinding("screenshot", GLFW.GLFW_GAMEPAD_BUTTON_DPAD_DOWN,
            Collections.singletonList((client, action) -> {
                ScreenshotUtils.saveScreenshot(client.runDirectory, client.getWindow().getFramebufferWidth(), client.getWindow().getFramebufferHeight(), client.getFramebuffer(),
                        text -> client.execute(() -> client.inGameHud.getChatHud().addMessage(text)));
                return true;
            }));
    public static final  ButtonBinding       SMOOTH_CAMERA      = new ButtonBinding("toggle_smooth_camera", -1);
    public static final  ButtonBinding       SNEAK              = new ButtonBinding("sneak", GLFW.GLFW_GAMEPAD_BUTTON_RIGHT_THUMB);
    public static final  ButtonBinding       SPRINT             = new ButtonBinding("sprint", GLFW.GLFW_GAMEPAD_BUTTON_LEFT_THUMB);
    public static final  ButtonBinding       SWAP_HANDS         = new ButtonBinding("swap_hands", GLFW.GLFW_GAMEPAD_BUTTON_X);
    public static final  ButtonBinding       TOGGLE_PERSPECTIVE = new ButtonBinding("toggle_perspective", GLFW.GLFW_GAMEPAD_BUTTON_DPAD_UP);
    public static final  ButtonBinding       USE                = new ButtonBinding("use", axis_as_button(GLFW.GLFW_GAMEPAD_AXIS_LEFT_TRIGGER, true));

    private int               button;
    private int               default_button;
    private String            key;
    private KeyBinding        minecraft_key_binding = null;
    private List<PressAction> actions               = new ArrayList<>(Collections.singletonList((client, action) -> {
        this.as_key_binding().ifPresent(key_binding -> ((KeyBindingAccessor) key_binding).handle_press_state(this.is_button_down()));
        return true;
    }));
    private boolean           pressed               = false;

    protected ButtonBinding(@NotNull String key, int default_button, @NotNull List<PressAction> actions)
    {
        this.default_button = this.button = default_button;
        this.key = key;
        this.actions.addAll(actions);
        BINDINGS.add(this);
    }

    protected ButtonBinding(@NotNull String key, int default_button)
    {
        this(key, default_button, Collections.emptyList());
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

    public static @NotNull Stream<Category> stream_categories()
    {
        return CATEGORIES.stream();
    }

    static {
        MOVEMENT_CATEGORY = register_default_category("key.categories.movement", category -> category.register_all_bindings(
                FORWARD,
                BACK,
                LEFT,
                RIGHT,
                JUMP,
                SNEAK,
                SPRINT));
        GAMEPLAY_CATEGORY = register_default_category("key.categories.gameplay", category -> category.register_all_bindings(
                ATTACK,
                PICK_BLOCK,
                USE
        ));
        INVENTORY_CATEGORY = register_default_category("key.categories.inventory", category -> category.register_all_bindings(
                DROP_ITEM,
                INVENTORY,
                SWAP_HANDS
        ));
        MULTIPLAYER_CATEGORY = register_default_category("key.categories.multiplayer",
                category -> category.register_all_bindings(CHAT, PLAYER_LIST));
        MISC_CATEGORY = register_default_category("key.categories.misc", category -> category.register_all_bindings(
                SCREENSHOT,
                //SMOOTH_CAMERA,
                TOGGLE_PERSPECTIVE
        ));
    }

    public static ButtonBinding register(@NotNull Identifier binding_id, int default_button, @NotNull List<PressAction> actions)
    {
        return new ButtonBinding(binding_id.get_namespace() + "." + binding_id.get_name(), default_button, actions);
    }

    public static ButtonBinding register(@NotNull Identifier binding_id, int default_button)
    {
        return register(binding_id, default_button, Collections.emptyList());
    }

    public static ButtonBinding register(@NotNull net.minecraft.util.Identifier binding_id, int default_button, @NotNull List<PressAction> actions)
    {
        return register(new Identifier(binding_id.getNamespace(), binding_id.getPath()), default_button, actions);
    }

    public static ButtonBinding register(@NotNull net.minecraft.util.Identifier binding_id, int default_button)
    {
        return register(binding_id, default_button, Collections.emptyList());
    }

    /**
     * Registers a category of button bindings.
     *
     * @param category The category to register.
     * @return The registered category.
     */
    public static Category register_category(@NotNull Category category)
    {
        CATEGORIES.add(category);
        return category;
    }

    public static Category register_category(@NotNull Identifier identifier, int priority)
    {
        return register_category(new Category(identifier, priority));
    }

    public static Category register_category(@NotNull Identifier identifier)
    {
        return register_category(new Category(identifier));
    }

    private static Category register_default_category(@NotNull String key, @NotNull Consumer<Category> key_adder)
    {
        Category category = register_category(new Identifier("minecraft", key), CATEGORIES.size());
        key_adder.accept(category);
        return category;
    }

    public static class Category implements Identifiable
    {
        private final List<ButtonBinding> bindings = new ArrayList<>();
        private final Identifier          id;
        private       int                 priority;

        public Category(@NotNull Identifier id, int priority)
        {
            this.id = id;
            this.priority = priority;
        }

        public Category(@NotNull Identifier id)
        {
            this(id, 100);
        }

        public void register_binding(@NotNull ButtonBinding binding)
        {
            if (this.bindings.contains(binding))
                throw new IllegalStateException("Cannot register twice a button binding in the same category.");
            this.bindings.add(binding);
        }

        public void register_all_bindings(@NotNull ButtonBinding... bindings)
        {
            this.register_all_bindings(Arrays.asList(bindings));
        }

        public void register_all_bindings(@NotNull List<ButtonBinding> bindings)
        {
            bindings.forEach(this::register_binding);
        }

        /**
         * Gets the bindings assigned to this category.
         *
         * @return The bindings assigned to this category.
         */
        public @NotNull List<ButtonBinding> get_bindings()
        {
            return Collections.unmodifiableList(this.bindings);
        }

        /**
         * Gets the translated name of this category.
         * <p>
         * The translation key should be `modid.identifier_name`.
         *
         * @return The translated name.
         */
        public @NotNull String get_translated_name()
        {
            System.out.println(id.toString());
            if (this.id.get_namespace().equals("minecraft"))
                return I18n.translate(this.id.get_name());
            else
                return I18n.translate(this.id.get_namespace() + "." + this.id.get_name());
        }

        /**
         * Gets the priority display of this category.
         * It will defines in which order the categories will display on the controls screen.
         *
         * @return The priority of this category.
         */
        public int get_priority()
        {
            return this.priority;
        }

        @Override
        public @NotNull Identifier get_identifier()
        {
            return this.id;
        }
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
