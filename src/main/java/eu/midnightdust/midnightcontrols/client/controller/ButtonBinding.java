/*
 * Copyright © 2021 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of midnightcontrols.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package eu.midnightdust.midnightcontrols.client.controller;

import eu.midnightdust.midnightcontrols.client.ButtonState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import org.aperlambda.lambdacommon.utils.function.PairPredicate;
import org.aperlambda.lambdacommon.utils.function.Predicates;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

import static org.lwjgl.glfw.GLFW.*;

/**
 * Represents a button binding.
 *
 * @author LambdAurora
 * @version 1.7.0
 * @since 1.0.0
 */
public class ButtonBinding {
    public static final ButtonCategory MOVEMENT_CATEGORY;
    public static final ButtonCategory GAMEPLAY_CATEGORY;
    public static final ButtonCategory INVENTORY_CATEGORY;
    public static final ButtonCategory MULTIPLAYER_CATEGORY;
    public static final ButtonCategory MISC_CATEGORY;

    public static final ButtonBinding ATTACK = new Builder("attack").buttons(axisAsButton(GLFW_GAMEPAD_AXIS_RIGHT_TRIGGER, true)).onlyInGame().register();
    public static final ButtonBinding BACK = new Builder("back").buttons(axisAsButton(GLFW_GAMEPAD_AXIS_LEFT_Y, false))
            .action(MovementHandler.HANDLER).onlyInGame().register();
    public static final ButtonBinding CHAT = new Builder("chat").buttons(GLFW_GAMEPAD_BUTTON_DPAD_RIGHT).onlyInGame().cooldown().register();
    public static final ButtonBinding DROP_ITEM = new Builder("drop_item").buttons(GLFW_GAMEPAD_BUTTON_B).onlyInGame().cooldown().register();
    public static final ButtonBinding FORWARD = new Builder("forward").buttons(axisAsButton(GLFW_GAMEPAD_AXIS_LEFT_Y, true))
            .action(MovementHandler.HANDLER).onlyInGame().register();
    public static final ButtonBinding HOTBAR_LEFT = new Builder("hotbar_left").buttons(GLFW_GAMEPAD_BUTTON_LEFT_BUMPER)
            .action(InputHandlers.handleHotbar(false)).onlyInGame().cooldown().register();
    public static final ButtonBinding HOTBAR_RIGHT = new Builder("hotbar_right").buttons(GLFW_GAMEPAD_BUTTON_RIGHT_BUMPER)
            .action(InputHandlers.handleHotbar(true)).onlyInGame().cooldown().register();
    public static final ButtonBinding INVENTORY = new Builder("inventory").buttons(GLFW_GAMEPAD_BUTTON_Y).onlyInGame().cooldown().register();
    public static final ButtonBinding JUMP = new Builder("jump").buttons(GLFW_GAMEPAD_BUTTON_A).onlyInGame().register();
    public static final ButtonBinding LEFT = new Builder("left").buttons(axisAsButton(GLFW_GAMEPAD_AXIS_LEFT_X, false))
            .action(MovementHandler.HANDLER).onlyInGame().register();
    public static final ButtonBinding PAUSE_GAME = new Builder("pause_game").buttons(GLFW_GAMEPAD_BUTTON_START).action(InputHandlers::handlePauseGame).cooldown().register();
    public static final ButtonBinding PICK_BLOCK = new Builder("pick_block").buttons(GLFW_GAMEPAD_BUTTON_DPAD_LEFT).onlyInGame().cooldown().register();
    public static final ButtonBinding PLAYER_LIST = new Builder("player_list").buttons(GLFW_GAMEPAD_BUTTON_BACK).onlyInGame().register();
    public static final ButtonBinding RIGHT = new Builder("right").buttons(axisAsButton(GLFW_GAMEPAD_AXIS_LEFT_X, true))
            .action(MovementHandler.HANDLER).onlyInGame().register();
    public static final ButtonBinding SCREENSHOT = new Builder("screenshot").buttons(GLFW_GAMEPAD_BUTTON_DPAD_UP, GLFW_GAMEPAD_BUTTON_A)
            .action(InputHandlers::handleScreenshot).cooldown().register();

    public static final ButtonBinding DEBUG_SCREEN = new Builder("debug_screen").buttons(GLFW_GAMEPAD_BUTTON_DPAD_UP, GLFW_GAMEPAD_BUTTON_B)
            .action((client,binding,value,action) -> {if (action == ButtonState.PRESS) client.options.debugEnabled = !client.options.debugEnabled; return true;}).cooldown().register();
    public static final ButtonBinding SLOT_DOWN = new Builder("slot_down").buttons(GLFW_GAMEPAD_BUTTON_DPAD_DOWN)
            .action(InputHandlers.handleInventorySlotPad(1)).onlyInInventory().cooldown().register();
    public static final ButtonBinding SLOT_LEFT = new Builder("slot_left").buttons(GLFW_GAMEPAD_BUTTON_DPAD_LEFT)
            .action(InputHandlers.handleInventorySlotPad(3)).onlyInInventory().cooldown().register();
    public static final ButtonBinding SLOT_RIGHT = new Builder("slot_right").buttons(GLFW_GAMEPAD_BUTTON_DPAD_RIGHT)
            .action(InputHandlers.handleInventorySlotPad(2)).onlyInInventory().cooldown().register();
    public static final ButtonBinding SLOT_UP = new Builder("slot_up").buttons(GLFW_GAMEPAD_BUTTON_DPAD_UP)
            .action(InputHandlers.handleInventorySlotPad(0)).onlyInInventory().cooldown().register();
    public static final ButtonBinding SMOOTH_CAMERA = new Builder("toggle_smooth_camera").onlyInGame().cooldown().register();
    public static final ButtonBinding SNEAK = new Builder("sneak").buttons(GLFW_GAMEPAD_BUTTON_RIGHT_THUMB)
            .actions(InputHandlers::handleToggleSneak).onlyInGame().cooldown().register();
    public static final ButtonBinding SPRINT = new Builder("sprint").buttons(GLFW_GAMEPAD_BUTTON_LEFT_THUMB).onlyInGame().register();
    public static final ButtonBinding SWAP_HANDS = new Builder("swap_hands").buttons(GLFW_GAMEPAD_BUTTON_X).onlyInGame().cooldown().register();
    public static final ButtonBinding TAB_LEFT = new Builder("tab_back").buttons(GLFW_GAMEPAD_BUTTON_LEFT_BUMPER)
            .action(InputHandlers.handleHotbar(false)).filter(Predicates.or(InputHandlers::inInventory, InputHandlers::inAdvancements).or((client, binding) -> client.currentScreen != null && client.currentScreen.getClass().toString().contains("sodium"))).cooldown().register();
    public static final ButtonBinding TAB_RIGHT = new Builder("tab_next").buttons(GLFW_GAMEPAD_BUTTON_RIGHT_BUMPER)
            .action(InputHandlers.handleHotbar(true)).filter(Predicates.or(InputHandlers::inInventory, InputHandlers::inAdvancements).or((client, binding) -> client.currentScreen != null && client.currentScreen.getClass().toString().contains("sodium"))).cooldown().register();
    public static final ButtonBinding PAGE_LEFT = new Builder("page_back").buttons(axisAsButton(GLFW_GAMEPAD_AXIS_LEFT_TRIGGER, true))
            .action(InputHandlers.handlePage(false)).filter(InputHandlers::inInventory).cooldown().register();
    public static final ButtonBinding PAGE_RIGHT = new Builder("page_next").buttons(axisAsButton(GLFW_GAMEPAD_AXIS_RIGHT_TRIGGER, true))
            .action(InputHandlers.handlePage(true)).filter(InputHandlers::inInventory).cooldown().register();
    public static final ButtonBinding TAKE = new Builder("take").buttons(GLFW_GAMEPAD_BUTTON_X)
            .action(InputHandlers.handleActions()).filter(InputHandlers::inInventory).cooldown().register();
    public static final ButtonBinding TAKE_ALL = new Builder("take_all").buttons(GLFW_GAMEPAD_BUTTON_A)
            .action(InputHandlers.handleActions()).filter(InputHandlers::inInventory).cooldown().register();
    public static final ButtonBinding QUICK_MOVE = new Builder("quick_move").buttons(GLFW_GAMEPAD_BUTTON_Y)
            .action(InputHandlers.handleActions()).filter(InputHandlers::inInventory).cooldown().register();
    public static final ButtonBinding TOGGLE_PERSPECTIVE = new Builder("toggle_perspective").filter(InputHandlers::inGame).buttons(GLFW_GAMEPAD_BUTTON_DPAD_UP, GLFW_GAMEPAD_BUTTON_Y).cooldown().register();
    public static final ButtonBinding USE = new Builder("use").buttons(axisAsButton(GLFW_GAMEPAD_AXIS_LEFT_TRIGGER, true)).register();

    private int[] button;
    private final int[] defaultButton;
    private final String key;
    private final Text text;
    private KeyBinding mcKeyBinding = null;
    protected PairPredicate<MinecraftClient, ButtonBinding> filter;
    private final List<PressAction> actions = new ArrayList<>(Collections.singletonList(PressAction.DEFAULT_ACTION));
    private boolean hasCooldown;
    private int cooldown = 0;
    boolean pressed = false;

    public ButtonBinding(String key, int[] defaultButton, List<PressAction> actions, PairPredicate<MinecraftClient, ButtonBinding> filter, boolean hasCooldown) {
        this.setButton(this.defaultButton = defaultButton);
        this.key = key;
        this.text = new TranslatableText(this.key);
        this.filter = filter;
        this.actions.addAll(actions);
        this.hasCooldown = hasCooldown;
    }

    public ButtonBinding(String key, int[] defaultButton, boolean hasCooldown) {
        this(key, defaultButton, Collections.emptyList(), Predicates.pairAlwaysTrue(), hasCooldown);
    }

    /**
     * Returns the button bound.
     *
     * @return the bound button
     */
    public int[] getButton() {
        return this.button;
    }

    /**
     * Sets the bound button.
     *
     * @param button the bound button
     */
    public void setButton(int[] button) {
        this.button = button;

        if (InputManager.hasBinding(this))
            InputManager.sortBindings();
    }

    /**
     * Returns whether the bound button is the specified button or not.
     *
     * @param button the button to check
     * @return true if the bound button is the specified button, else false
     */
    public boolean isButton(int[] button) {
        return InputManager.areButtonsEquivalent(button, this.button);
    }

    /**
     * Returns whether this button is down or not.
     *
     * @return true if the button is down, else false
     */
    public boolean isButtonDown() {
        return this.pressed;
    }

    /**
     * Returns whether this button binding is bound or not.
     *
     * @return true if this button binding is bound, else false
     */
    public boolean isNotBound() {
        return this.button.length == 0 || this.button[0] == -1;
    }

    /**
     * Gets the default button assigned to this binding.
     *
     * @return the default button
     */
    public int[] getDefaultButton() {
        return this.defaultButton;
    }

    /**
     * Returns whether the assigned button is the default button.
     *
     * @return true if the assigned button is the default button, else false
     */
    public boolean isDefault() {
        return this.button.length == this.defaultButton.length && InputManager.areButtonsEquivalent(this.button, this.defaultButton);
    }

    /**
     * Returns the button code.
     *
     * @return the button code
     */
    public String getButtonCode() {
        return Arrays.stream(this.button)
                .mapToObj(btn -> Integer.valueOf(btn).toString())
                .collect(Collectors.joining("+"));
    }

    /**
     * Sets the key binding to emulate with this button binding.
     *
     * @param keyBinding the optional key binding
     */
    public void setKeyBinding(@Nullable KeyBinding keyBinding) {
        this.mcKeyBinding = keyBinding;
    }

    /**
     * Returns whether the button binding is available in the current context.
     *
     * @param client the client instance
     * @return true if the button binding is available, else false
     */
    public boolean isAvailable(@NotNull MinecraftClient client) {
        return this.filter.test(client, this);
    }

    /**
     * Updates the button binding cooldown.
     */
    public void update() {
        if (this.hasCooldown && this.cooldown > 0)
            this.cooldown--;
    }

    /**
     * Handles the button binding.
     *
     * @param client the client instance
     * @param state the state
     */
    public void handle(@NotNull MinecraftClient client, float value, @NotNull ButtonState state) {
        if (state == ButtonState.REPEAT && this.hasCooldown && this.cooldown != 0)
            return;
        if (this.hasCooldown && state.isPressed()) {
            this.cooldown = 5;
        }
        for (int i = this.actions.size() - 1; i >= 0; i--) {
            if (this.actions.get(i).press(client, this, value, state))
                break;
        }
    }

    public @NotNull String getName() {
        return this.key;
    }

    /**
     * Returns the translation key of this button binding.
     *
     * @return the translation key
     */
    public @NotNull String getTranslationKey() {
        return I18n.hasTranslation("midnightcontrols.action." + this.getName()) ? "midnightcontrols.action." + this.getName() : this.getName();
    }

    public @NotNull Text getText() {
        return this.text;
    }

    /**
     * Returns the key binding equivalent of this button binding.
     *
     * @return the key binding equivalent
     */
    public @NotNull Optional<KeyBinding> asKeyBinding() {
        return Optional.ofNullable(this.mcKeyBinding);
    }

    @Override
    public String toString() {
        return "ButtonBinding{id=\"" + this.key + "\","
                + "hasCooldown=" + this.hasCooldown
                + "}";
    }

    /**
     * Returns the specified axis as a button.
     *
     * @param axis the axis
     * @param positive true if the axis part is positive, else false
     * @return the axis as a button
     */
    public static int axisAsButton(int axis, boolean positive) {
        return positive ? 100 + axis : 200 + axis;
    }

    /**
     * Returns whether the specified button is an axis or not.
     *
     * @param button the button
     * @return true if the button is an axis, else false
     */
    public static boolean isAxis(int button) {
        button %= 500;
        return button >= 100;
    }

    /**
     * Returns the second Joycon's specified button code.
     *
     * @param button the raw button code
     * @return the second Joycon's button code
     */
    public static int controller2Button(int button) {
        return 500 + button;
    }

    public static void init(@NotNull GameOptions options) {
        ATTACK.mcKeyBinding = options.attackKey;
        BACK.mcKeyBinding = options.backKey;
        CHAT.mcKeyBinding = options.chatKey;
        DROP_ITEM.mcKeyBinding = options.dropKey;
        FORWARD.mcKeyBinding = options.forwardKey;
        INVENTORY.mcKeyBinding = options.inventoryKey;
        JUMP.mcKeyBinding = options.jumpKey;
        LEFT.mcKeyBinding = options.leftKey;
        PICK_BLOCK.mcKeyBinding = options.pickItemKey;
        PLAYER_LIST.mcKeyBinding = options.playerListKey;
        RIGHT.mcKeyBinding = options.rightKey;
        SCREENSHOT.mcKeyBinding = options.screenshotKey;
        SMOOTH_CAMERA.mcKeyBinding = options.smoothCameraKey;
        SNEAK.mcKeyBinding = options.sneakKey;
        SPRINT.mcKeyBinding = options.sprintKey;
        SWAP_HANDS.mcKeyBinding = options.swapHandsKey;
        TOGGLE_PERSPECTIVE.mcKeyBinding = options.togglePerspectiveKey;
        USE.mcKeyBinding = options.useKey;
    }

    /**
     * Returns the localized name of the specified button.
     *
     * @param button the button
     * @return the localized name of the button
     */
    public static @NotNull Text getLocalizedButtonName(int button) {
        return switch (button % 500) {
            case -1 -> new TranslatableText("key.keyboard.unknown");
            case GLFW_GAMEPAD_BUTTON_A -> new TranslatableText("midnightcontrols.button.a");
            case GLFW_GAMEPAD_BUTTON_B -> new TranslatableText("midnightcontrols.button.b");
            case GLFW_GAMEPAD_BUTTON_X -> new TranslatableText("midnightcontrols.button.x");
            case GLFW_GAMEPAD_BUTTON_Y -> new TranslatableText("midnightcontrols.button.y");
            case GLFW_GAMEPAD_BUTTON_LEFT_BUMPER -> new TranslatableText("midnightcontrols.button.left_bumper");
            case GLFW_GAMEPAD_BUTTON_RIGHT_BUMPER -> new TranslatableText("midnightcontrols.button.right_bumper");
            case GLFW_GAMEPAD_BUTTON_BACK -> new TranslatableText("midnightcontrols.button.back");
            case GLFW_GAMEPAD_BUTTON_START -> new TranslatableText("midnightcontrols.button.start");
            case GLFW_GAMEPAD_BUTTON_GUIDE -> new TranslatableText("midnightcontrols.button.guide");
            case GLFW_GAMEPAD_BUTTON_LEFT_THUMB -> new TranslatableText("midnightcontrols.button.left_thumb");
            case GLFW_GAMEPAD_BUTTON_RIGHT_THUMB -> new TranslatableText("midnightcontrols.button.right_thumb");
            case GLFW_GAMEPAD_BUTTON_DPAD_UP -> new TranslatableText("midnightcontrols.button.dpad_up");
            case GLFW_GAMEPAD_BUTTON_DPAD_RIGHT -> new TranslatableText("midnightcontrols.button.dpad_right");
            case GLFW_GAMEPAD_BUTTON_DPAD_DOWN -> new TranslatableText("midnightcontrols.button.dpad_down");
            case GLFW_GAMEPAD_BUTTON_DPAD_LEFT -> new TranslatableText("midnightcontrols.button.dpad_left");
            case 100 -> new TranslatableText("midnightcontrols.axis.left_x+");
            case 101 -> new TranslatableText("midnightcontrols.axis.left_y+");
            case 102 -> new TranslatableText("midnightcontrols.axis.right_x+");
            case 103 -> new TranslatableText("midnightcontrols.axis.right_y+");
            case 104 -> new TranslatableText("midnightcontrols.axis.left_trigger");
            case 105 -> new TranslatableText("midnightcontrols.axis.right_trigger");
            case 200 -> new TranslatableText("midnightcontrols.axis.left_x-");
            case 201 -> new TranslatableText("midnightcontrols.axis.left_y-");
            case 202 -> new TranslatableText("midnightcontrols.axis.right_x-");
            case 203 -> new TranslatableText("midnightcontrols.axis.right_y-");
            case 15 -> new TranslatableText("midnightcontrols.button.l4");
            case 16 -> new TranslatableText("midnightcontrols.button.l5");
            case 17 -> new TranslatableText("midnightcontrols.button.r4");
            case 18 -> new TranslatableText("midnightcontrols.button.r5");
            default -> new TranslatableText("midnightcontrols.button.unknown", button);
        };
    }

    static {
        MOVEMENT_CATEGORY = InputManager.registerDefaultCategory("key.categories.movement", category -> category.registerAllBindings(
                ButtonBinding.FORWARD,
                ButtonBinding.BACK,
                ButtonBinding.LEFT,
                ButtonBinding.RIGHT,
                ButtonBinding.JUMP,
                ButtonBinding.SNEAK,
                ButtonBinding.SPRINT));
        GAMEPLAY_CATEGORY = InputManager.registerDefaultCategory("key.categories.gameplay", category -> category.registerAllBindings(
                ButtonBinding.ATTACK,
                ButtonBinding.PICK_BLOCK,
                ButtonBinding.USE
        ));
        INVENTORY_CATEGORY = InputManager.registerDefaultCategory("key.categories.inventory", category -> category.registerAllBindings(
                ButtonBinding.DROP_ITEM,
                ButtonBinding.HOTBAR_LEFT,
                ButtonBinding.HOTBAR_RIGHT,
                ButtonBinding.INVENTORY,
                ButtonBinding.SWAP_HANDS,
                ButtonBinding.TAB_LEFT,
                ButtonBinding.TAB_RIGHT,
                ButtonBinding.PAGE_LEFT,
                ButtonBinding.PAGE_RIGHT,
                ButtonBinding.TAKE,
                ButtonBinding.TAKE_ALL,
                ButtonBinding.QUICK_MOVE,
                ButtonBinding.SLOT_UP,
                ButtonBinding.SLOT_DOWN,
                ButtonBinding.SLOT_LEFT,
                ButtonBinding.SLOT_RIGHT
        ));
        MULTIPLAYER_CATEGORY = InputManager.registerDefaultCategory("key.categories.multiplayer",
                category -> category.registerAllBindings(ButtonBinding.CHAT, ButtonBinding.PLAYER_LIST));
        MISC_CATEGORY = InputManager.registerDefaultCategory("key.categories.misc", category -> category.registerAllBindings(
                ButtonBinding.SCREENSHOT,
                ButtonBinding.TOGGLE_PERSPECTIVE,
                ButtonBinding.PAUSE_GAME,
                //SMOOTH_CAMERA,
                ButtonBinding.DEBUG_SCREEN
        ));
    }

    /**
     * Returns a builder instance.
     *
     * @param identifier the identifier of the button binding
     * @return the builder instance
     * @since 1.5.0
     */
    public static Builder builder(@NotNull Identifier identifier) {
        return new Builder(identifier);

    }

    /**
     * Represents a quick {@link ButtonBinding} builder.
     *
     * @author LambdAurora
     * @version 1.5.0
     * @since 1.1.0
     */
    public static class Builder {
        private final String key;
        private int[] buttons = new int[0];
        private final List<PressAction> actions = new ArrayList<>();
        private PairPredicate<MinecraftClient, ButtonBinding> filter = Predicates.pairAlwaysTrue();
        private boolean cooldown = false;
        private ButtonCategory category = null;
        private KeyBinding mcBinding = null;

        /**
         * This constructor shouldn't be used for other mods.
         *
         * @param key the key with format {@code "<namespace>.<name>"}
         */
        public Builder(@NotNull String key) {
            this.key = key;
            this.unbound();
        }

        public Builder(@NotNull Identifier identifier) {
            this(identifier.getNamespace() + "." + identifier.getPath());
        }

        /**
         * Defines the default buttons of the {@link ButtonBinding}.
         *
         * @param buttons the default buttons
         * @return the builder instance
         */
        public Builder buttons(int... buttons) {
            this.buttons = buttons;
            return this;
        }

        /**
         * Sets the {@link ButtonBinding} to unbound.
         *
         * @return the builder instance
         */
        public Builder unbound() {
            return this.buttons(-1);
        }

        /**
         * Adds the actions to the {@link ButtonBinding}.
         *
         * @param actions the actions to add
         * @return the builder instance
         */
        public Builder actions(@NotNull PressAction... actions) {
            this.actions.addAll(Arrays.asList(actions));
            return this;
        }

        /**
         * Adds an action to the {@link ButtonBinding}.
         *
         * @param action the action to add
         * @return the builder instance
         */
        public Builder action(@NotNull PressAction action) {
            this.actions.add(action);
            return this;
        }

        /**
         * Sets a filter for the {@link ButtonBinding}.
         *
         * @param filter the filter
         * @return the builder instance
         */
        public Builder filter(@NotNull PairPredicate<MinecraftClient, ButtonBinding> filter) {
            this.filter = filter;
            return this;
        }

        /**
         * Sets the filter of {@link ButtonBinding} to only in game.
         *
         * @return the builder instance
         * @see #filter(PairPredicate)
         * @see InputHandlers#inGame(MinecraftClient, ButtonBinding)
         */
        public Builder onlyInGame() {
            return this.filter(InputHandlers::inGame);
        }

        /**
         * Sets the filter of {@link ButtonBinding} to only in inventory.
         *
         * @return the builder instance
         * @see #filter(PairPredicate)
         * @see InputHandlers#inInventory(MinecraftClient, ButtonBinding)
         */
        public Builder onlyInInventory() {
            return this.filter(InputHandlers::inInventory);
        }

        /**
         * Sets whether the {@link ButtonBinding} has a cooldown or not.
         *
         * @param cooldown true if the {@link ButtonBinding} has a cooldown, else false
         * @return the builder instance
         */
        public Builder cooldown(boolean cooldown) {
            this.cooldown = cooldown;
            return this;
        }

        /**
         * Puts a cooldown on the {@link ButtonBinding}.
         *
         * @return the builder instance
         * @since 1.5.0
         */
        public Builder cooldown() {
            return this.cooldown(true);
        }

        /**
         * Sets the category of the {@link ButtonBinding}.
         *
         * @param category the category
         * @return the builder instance
         */
        public Builder category(@Nullable ButtonCategory category) {
            this.category = category;
            return this;
        }

        /**
         * Sets the keybinding linked to the {@link ButtonBinding}.
         *
         * @param binding the keybinding to link
         * @return the builder instance
         */
        public Builder linkKeybind(@Nullable KeyBinding binding) {
            this.mcBinding = binding;
            return this;
        }

        /**
         * Builds the {@link ButtonBinding}.
         *
         * @return the built {@link ButtonBinding}
         */
        public ButtonBinding build() {
            var binding = new ButtonBinding(this.key, this.buttons, this.actions, this.filter, this.cooldown);
            if (this.category != null)
                this.category.registerBinding(binding);
            if (this.mcBinding != null)
                binding.setKeyBinding(this.mcBinding);
            return binding;
        }

        /**
         * Builds and registers the {@link ButtonBinding}.
         *
         * @return the built {@link ButtonBinding}
         * @see #build()
         */
        public ButtonBinding register() {
            return InputManager.registerBinding(this.build());
        }
    }
}
