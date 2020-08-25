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
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import org.aperlambda.lambdacommon.Identifier;
import org.aperlambda.lambdacommon.utils.Nameable;
import org.aperlambda.lambdacommon.utils.function.PairPredicate;
import org.aperlambda.lambdacommon.utils.function.Predicates;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

import static me.lambdaurora.lambdacontrols.client.controller.InputManager.registerDefaultCategory;
import static org.lwjgl.glfw.GLFW.*;

/**
 * Represents a button binding.
 *
 * @author LambdAurora
 * @version 1.5.0
 * @since 1.0.0
 */
public class ButtonBinding implements Nameable
{
    public static final ButtonCategory MOVEMENT_CATEGORY;
    public static final ButtonCategory GAMEPLAY_CATEGORY;
    public static final ButtonCategory INVENTORY_CATEGORY;
    public static final ButtonCategory MULTIPLAYER_CATEGORY;
    public static final ButtonCategory MISC_CATEGORY;

    public static final ButtonBinding ATTACK             = new Builder("attack").buttons(axisAsButton(GLFW_GAMEPAD_AXIS_RIGHT_TRIGGER, true)).onlyInGame().register();
    public static final ButtonBinding BACK               = new Builder("back").buttons(axisAsButton(GLFW_GAMEPAD_AXIS_LEFT_Y, false))
            .action(MovementHandler.HANDLER).onlyInGame().register();
    public static final ButtonBinding CHAT               = new Builder("chat").buttons(GLFW_GAMEPAD_BUTTON_DPAD_RIGHT).onlyInGame().cooldown().register();
    public static final ButtonBinding DROP_ITEM          = new Builder("drop_item").buttons(GLFW_GAMEPAD_BUTTON_B).onlyInGame().cooldown().register();
    public static final ButtonBinding FORWARD            = new Builder("forward").buttons(axisAsButton(GLFW_GAMEPAD_AXIS_LEFT_Y, true))
            .action(MovementHandler.HANDLER).onlyInGame().register();
    public static final ButtonBinding HOTBAR_LEFT        = new Builder("hotbar_left").buttons(GLFW_GAMEPAD_BUTTON_LEFT_BUMPER)
            .action(InputHandlers.handleHotbar(false)).onlyInGame().cooldown().register();
    public static final ButtonBinding HOTBAR_RIGHT       = new Builder("hotbar_right").buttons(GLFW_GAMEPAD_BUTTON_RIGHT_BUMPER)
            .action(InputHandlers.handleHotbar(true)).onlyInGame().cooldown().register();
    public static final ButtonBinding INVENTORY          = new Builder("inventory").buttons(GLFW_GAMEPAD_BUTTON_Y).onlyInGame().cooldown().register();
    public static final ButtonBinding JUMP               = new Builder("jump").buttons(GLFW_GAMEPAD_BUTTON_A).onlyInGame().register();
    public static final ButtonBinding LEFT               = new Builder("left").buttons(axisAsButton(GLFW_GAMEPAD_AXIS_LEFT_X, false))
            .action(MovementHandler.HANDLER).onlyInGame().register();
    public static final ButtonBinding PAUSE_GAME         = new Builder("pause_game").buttons(GLFW_GAMEPAD_BUTTON_START).action(InputHandlers::handlePauseGame).cooldown().register();
    public static final ButtonBinding PICK_BLOCK         = new Builder("pick_block").buttons(GLFW_GAMEPAD_BUTTON_DPAD_LEFT).onlyInGame().cooldown().register();
    public static final ButtonBinding PLAYER_LIST        = new Builder("player_list").buttons(GLFW_GAMEPAD_BUTTON_BACK).onlyInGame().register();
    public static final ButtonBinding RIGHT              = new Builder("right").buttons(axisAsButton(GLFW_GAMEPAD_AXIS_LEFT_X, true))
            .action(MovementHandler.HANDLER).onlyInGame().register();
    public static final ButtonBinding SCREENSHOT         = new Builder("screenshot").buttons(GLFW_GAMEPAD_BUTTON_DPAD_UP, GLFW_GAMEPAD_BUTTON_A)
            .action(InputHandlers::handleScreenshot).cooldown().register();
    public static final ButtonBinding SLOT_DOWN          = new Builder("slot_down").buttons(GLFW_GAMEPAD_BUTTON_DPAD_DOWN)
            .action(InputHandlers.handleInventorySlotPad(1)).onlyInInventory().cooldown().register();
    public static final ButtonBinding SLOT_LEFT          = new Builder("slot_left").buttons(GLFW_GAMEPAD_BUTTON_DPAD_LEFT)
            .action(InputHandlers.handleInventorySlotPad(3)).onlyInInventory().cooldown().register();
    public static final ButtonBinding SLOT_RIGHT         = new Builder("slot_right").buttons(GLFW_GAMEPAD_BUTTON_DPAD_RIGHT)
            .action(InputHandlers.handleInventorySlotPad(2)).onlyInInventory().cooldown().register();
    public static final ButtonBinding SLOT_UP            = new Builder("slot_up").buttons(GLFW_GAMEPAD_BUTTON_DPAD_UP)
            .action(InputHandlers.handleInventorySlotPad(0)).onlyInInventory().cooldown().register();
    public static final ButtonBinding SMOOTH_CAMERA      = new Builder("toggle_smooth_camera").onlyInGame().cooldown().register();
    public static final ButtonBinding SNEAK              = new Builder("sneak").buttons(GLFW_GAMEPAD_BUTTON_RIGHT_THUMB)
            .actions(InputHandlers::handleToggleSneak).onlyInGame().cooldown().register();
    public static final ButtonBinding SPRINT             = new Builder("sprint").buttons(GLFW_GAMEPAD_BUTTON_LEFT_THUMB).onlyInGame().register();
    public static final ButtonBinding SWAP_HANDS         = new Builder("swap_hands").buttons(GLFW_GAMEPAD_BUTTON_X).onlyInGame().cooldown().register();
    public static final ButtonBinding TAB_LEFT           = new Builder("tab_back").buttons(GLFW_GAMEPAD_BUTTON_LEFT_BUMPER)
            .action(InputHandlers.handleHotbar(false)).filter(Predicates.or(InputHandlers::inInventory, InputHandlers::inAdvancements)).cooldown().register();
    public static final ButtonBinding TAB_RIGHT          = new Builder("tab_next").buttons(GLFW_GAMEPAD_BUTTON_RIGHT_BUMPER)
            .action(InputHandlers.handleHotbar(true)).filter(Predicates.or(InputHandlers::inInventory, InputHandlers::inAdvancements)).cooldown().register();
    public static final ButtonBinding TOGGLE_PERSPECTIVE = new Builder("toggle_perspective").buttons(GLFW_GAMEPAD_BUTTON_DPAD_UP, GLFW_GAMEPAD_BUTTON_Y).cooldown().register();
    public static final ButtonBinding USE                = new Builder("use").buttons(axisAsButton(GLFW_GAMEPAD_AXIS_LEFT_TRIGGER, true)).register();

    private   int[]                                         button;
    private   int[]                                         defaultButton;
    private   String                                        key;
    private   KeyBinding                                    mcKeyBinding = null;
    protected PairPredicate<MinecraftClient, ButtonBinding> filter;
    private   List<PressAction>                             actions      = new ArrayList<>(Collections.singletonList(PressAction.DEFAULT_ACTION));
    private   boolean                                       hasCooldown;
    private   int                                           cooldown     = 0;
    boolean pressed = false;

    public ButtonBinding(@NotNull String key, int[] defaultButton, @NotNull List<PressAction> actions, PairPredicate<MinecraftClient, ButtonBinding> filter, boolean hasCooldown)
    {
        this.setButton(this.defaultButton = defaultButton);
        this.key = key;
        this.filter = filter;
        this.actions.addAll(actions);
        this.hasCooldown = hasCooldown;
    }

    public ButtonBinding(@NotNull String key, int[] defaultButton, boolean hasCooldown)
    {
        this(key, defaultButton, Collections.emptyList(), Predicates.pairAlwaysTrue(), hasCooldown);
    }

    /**
     * Returns the button bound.
     *
     * @return The bound button.
     */
    public int[] getButton()
    {
        return this.button;
    }

    /**
     * Sets the bound button.
     *
     * @param button The bound button.
     */
    public void setButton(int[] button)
    {
        this.button = button;

        if (InputManager.hasBinding(this))
            InputManager.sortBindings();
    }

    /**
     * Returns whether the bound button is the specified button or not.
     *
     * @param button The button to check.
     * @return True if the bound button is the specified button, else false.
     */
    public boolean isButton(int[] button)
    {
        return InputManager.areButtonsEquivalent(button, this.button);
    }

    /**
     * Returns whether this button is down or not.
     *
     * @return True if the button is down, else false.
     */
    public boolean isButtonDown()
    {
        return this.pressed;
    }

    /**
     * Returns whether this button binding is bound or not.
     *
     * @return True if this button binding is bound, else false.
     */
    public boolean isNotBound()
    {
        return this.button.length == 0 || this.button[0] == -1;
    }

    /**
     * Gets the default button assigned to this binding.
     *
     * @return The default button.
     */
    public int[] getDefaultButton()
    {
        return this.defaultButton;
    }

    /**
     * Returns whether the assigned button is the default button.
     *
     * @return True if the assigned button is the default button, else false.
     */
    public boolean isDefault()
    {
        return this.button.length == this.defaultButton.length && InputManager.areButtonsEquivalent(this.button, this.defaultButton);
    }

    /**
     * Returns the button code.
     *
     * @return The button code.
     */
    public @NotNull
    String getButtonCode()
    {
        return Arrays.stream(this.button)
                .mapToObj(btn -> Integer.valueOf(btn).toString())
                .collect(Collectors.joining("+"));
    }

    /**
     * Sets the key binding to emulate with this button binding.
     *
     * @param keyBinding The optional key binding.
     */
    public void setKeyBinding(@Nullable KeyBinding keyBinding)
    {
        this.mcKeyBinding = keyBinding;
    }

    /**
     * Returns whether the button binding is available in the current context.
     *
     * @param client The client instance.
     * @return True if the button binding is available, else false.
     */
    public boolean isAvailable(@NotNull MinecraftClient client)
    {
        return this.filter.test(client, this);
    }

    /**
     * Updates the button binding cooldown.
     */
    public void update()
    {
        if (this.hasCooldown && this.cooldown > 0)
            this.cooldown--;
    }

    /**
     * Handles the button binding.
     *
     * @param client The client instance.
     * @param state  The state.
     */
    public void handle(@NotNull MinecraftClient client, float value, @NotNull ButtonState state)
    {
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

    @Override
    public @NotNull String getName()
    {
        return this.key;
    }

    /**
     * Returns the translation key of this button binding.
     *
     * @return The translation key.
     */
    public @NotNull String getTranslationKey()
    {
        return "lambdacontrols.action." + this.getName();
    }

    /**
     * Returns the key binding equivalent of this button binding.
     *
     * @return The key binding equivalent.
     */
    public @NotNull Optional<KeyBinding> asKeyBinding()
    {
        return Optional.ofNullable(this.mcKeyBinding);
    }

    @Override
    public String toString()
    {
        return "ButtonBinding{id=\"" + this.key + "\","
                + "hasCooldown=" + this.hasCooldown
                + "}";
    }

    /**
     * Returns the specified axis as a button.
     *
     * @param axis     The axis.
     * @param positive True if the axis part is positive, else false.
     * @return The axis as a button.
     */
    public static int axisAsButton(int axis, boolean positive)
    {
        return positive ? 100 + axis : 200 + axis;
    }

    /**
     * Returns whether the specified button is an axis or not.
     *
     * @param button The button.
     * @return True if the button is an axis, else false.
     */
    public static boolean isAxis(int button)
    {
        button %= 500;
        return button >= 100;
    }

    /**
     * Returns the second Joycon's specified button code.
     *
     * @param button The raw button code.
     * @return The second Joycon's button code.
     */
    public static int controller2Button(int button)
    {
        return 500 + button;
    }

    public static void init(@NotNull GameOptions options)
    {
        ATTACK.mcKeyBinding = options.keyAttack;
        BACK.mcKeyBinding = options.keyBack;
        CHAT.mcKeyBinding = options.keyChat;
        DROP_ITEM.mcKeyBinding = options.keyDrop;
        FORWARD.mcKeyBinding = options.keyForward;
        INVENTORY.mcKeyBinding = options.keyInventory;
        JUMP.mcKeyBinding = options.keyJump;
        LEFT.mcKeyBinding = options.keyLeft;
        PICK_BLOCK.mcKeyBinding = options.keyPickItem;
        PLAYER_LIST.mcKeyBinding = options.keyPlayerList;
        RIGHT.mcKeyBinding = options.keyRight;
        SCREENSHOT.mcKeyBinding = options.keyScreenshot;
        SMOOTH_CAMERA.mcKeyBinding = options.keySmoothCamera;
        SNEAK.mcKeyBinding = options.keySneak;
        SPRINT.mcKeyBinding = options.keySprint;
        SWAP_HANDS.mcKeyBinding = options.keySwapHands;
        TOGGLE_PERSPECTIVE.mcKeyBinding = options.keyTogglePerspective;
        USE.mcKeyBinding = options.keyUse;
    }

    /**
     * Returns the localized name of the specified button.
     *
     * @param button The button.
     * @return The localized name of the button.
     */
    public static @NotNull Text getLocalizedButtonName(int button)
    {
        switch (button % 500) {
            case -1:
                return new TranslatableText("key.keyboard.unknown");
            case GLFW_GAMEPAD_BUTTON_A:
                return new TranslatableText("lambdacontrols.button.a");
            case GLFW_GAMEPAD_BUTTON_B:
                return new TranslatableText("lambdacontrols.button.b");
            case GLFW_GAMEPAD_BUTTON_X:
                return new TranslatableText("lambdacontrols.button.x");
            case GLFW_GAMEPAD_BUTTON_Y:
                return new TranslatableText("lambdacontrols.button.y");
            case GLFW_GAMEPAD_BUTTON_LEFT_BUMPER:
                return new TranslatableText("lambdacontrols.button.left_bumper");
            case GLFW_GAMEPAD_BUTTON_RIGHT_BUMPER:
                return new TranslatableText("lambdacontrols.button.right_bumper");
            case GLFW_GAMEPAD_BUTTON_BACK:
                return new TranslatableText("lambdacontrols.button.back");
            case GLFW_GAMEPAD_BUTTON_START:
                return new TranslatableText("lambdacontrols.button.start");
            case GLFW_GAMEPAD_BUTTON_GUIDE:
                return new TranslatableText("lambdacontrols.button.guide");
            case GLFW_GAMEPAD_BUTTON_LEFT_THUMB:
                return new TranslatableText("lambdacontrols.button.left_thumb");
            case GLFW_GAMEPAD_BUTTON_RIGHT_THUMB:
                return new TranslatableText("lambdacontrols.button.right_thumb");
            case GLFW_GAMEPAD_BUTTON_DPAD_UP:
                return new TranslatableText("lambdacontrols.button.dpad_up");
            case GLFW_GAMEPAD_BUTTON_DPAD_RIGHT:
                return new TranslatableText("lambdacontrols.button.dpad_right");
            case GLFW_GAMEPAD_BUTTON_DPAD_DOWN:
                return new TranslatableText("lambdacontrols.button.dpad_down");
            case GLFW_GAMEPAD_BUTTON_DPAD_LEFT:
                return new TranslatableText("lambdacontrols.button.dpad_left");
            case 100:
                return new TranslatableText("lambdacontrols.axis.left_x+");
            case 101:
                return new TranslatableText("lambdacontrols.axis.left_y+");
            case 102:
                return new TranslatableText("lambdacontrols.axis.right_x+");
            case 103:
                return new TranslatableText("lambdacontrols.axis.right_y+");
            case 104:
                return new TranslatableText("lambdacontrols.axis.left_trigger");
            case 105:
                return new TranslatableText("lambdacontrols.axis.right_trigger");
            case 200:
                return new TranslatableText("lambdacontrols.axis.left_x-");
            case 201:
                return new TranslatableText("lambdacontrols.axis.left_y-");
            case 202:
                return new TranslatableText("lambdacontrols.axis.right_x-");
            case 203:
                return new TranslatableText("lambdacontrols.axis.right_y-");
            default:
                return new TranslatableText("lambdacontrols.button.unknown", button);
        }
    }

    static {
        MOVEMENT_CATEGORY = registerDefaultCategory("key.categories.movement", category -> category.registerAllBindings(
                ButtonBinding.FORWARD,
                ButtonBinding.BACK,
                ButtonBinding.LEFT,
                ButtonBinding.RIGHT,
                ButtonBinding.JUMP,
                ButtonBinding.SNEAK,
                ButtonBinding.SPRINT));
        GAMEPLAY_CATEGORY = registerDefaultCategory("key.categories.gameplay", category -> category.registerAllBindings(
                ButtonBinding.ATTACK,
                ButtonBinding.PICK_BLOCK,
                ButtonBinding.USE
        ));
        INVENTORY_CATEGORY = registerDefaultCategory("key.categories.inventory", category -> category.registerAllBindings(
                ButtonBinding.DROP_ITEM,
                ButtonBinding.HOTBAR_LEFT,
                ButtonBinding.HOTBAR_RIGHT,
                ButtonBinding.INVENTORY,
                ButtonBinding.SWAP_HANDS
        ));
        MULTIPLAYER_CATEGORY = registerDefaultCategory("key.categories.multiplayer",
                category -> category.registerAllBindings(ButtonBinding.CHAT, ButtonBinding.PLAYER_LIST));
        MISC_CATEGORY = registerDefaultCategory("key.categories.misc", category -> category.registerAllBindings(
                ButtonBinding.SCREENSHOT,
                //SMOOTH_CAMERA,
                ButtonBinding.TOGGLE_PERSPECTIVE
        ));
    }

    /**
     * Returns a builder instance.
     *
     * @param identifier The identifier of the button binding.
     * @return The builder instance
     * @since 1.5.0
     */
    public static Builder builder(@NotNull Identifier identifier)
    {
        return new Builder(identifier);
    }

    /**
     * Returns a builder instance.
     *
     * @param identifier The identifier of the button binding.
     * @return The builder instance.
     * @since 1.5.0
     */
    public static Builder builder(@NotNull net.minecraft.util.Identifier identifier)
    {
        return new Builder(identifier);

    }

    /**
     * Represents a quick {@link ButtonBinding} builder.
     *
     * @author LambdAurora
     * @version 1.5.0
     * @since 1.1.0
     */
    public static class Builder
    {
        private final String                                        key;
        private       int[]                                         buttons   = new int[0];
        private       List<PressAction>                             actions   = new ArrayList<>();
        private       PairPredicate<MinecraftClient, ButtonBinding> filter    = Predicates.pairAlwaysTrue();
        private       boolean                                       cooldown  = false;
        private       ButtonCategory                                category  = null;
        private       KeyBinding                                    mcBinding = null;

        /**
         * This constructor shouldn't be used for other mods.
         *
         * @param key The key with format {@code "<namespace>.<name>"}.
         */
        public Builder(@NotNull String key)
        {
            this.key = key;
            this.unbound();
        }

        public Builder(@NotNull Identifier identifier)
        {
            this(identifier.getNamespace() + "." + identifier.getName());
        }

        public Builder(@NotNull net.minecraft.util.Identifier identifier)
        {
            this(new Identifier(identifier.toString()));
        }

        /**
         * Defines the default buttons of the {@link ButtonBinding}.
         *
         * @param buttons The default buttons.
         * @return The builder instance.
         */
        public Builder buttons(int... buttons)
        {
            this.buttons = buttons;
            return this;
        }

        /**
         * Sets the {@link ButtonBinding} to unbound.
         *
         * @return The builder instance.
         */
        public Builder unbound()
        {
            return this.buttons(-1);
        }

        /**
         * Adds the actions to the {@link ButtonBinding}.
         *
         * @param actions The actions to add.
         * @return The builder instance.
         */
        public Builder actions(@NotNull PressAction... actions)
        {
            this.actions.addAll(Arrays.asList(actions));
            return this;
        }

        /**
         * Adds an action to the {@link ButtonBinding}.
         *
         * @param action The action to add.
         * @return The builder instance.
         */
        public Builder action(@NotNull PressAction action)
        {
            this.actions.add(action);
            return this;
        }

        /**
         * Sets a filter for the {@link ButtonBinding}.
         *
         * @param filter The filter.
         * @return The builder instance.
         */
        public Builder filter(@NotNull PairPredicate<MinecraftClient, ButtonBinding> filter)
        {
            this.filter = filter;
            return this;
        }

        /**
         * Sets the filter of {@link ButtonBinding} to only in game.
         *
         * @return The builder instance.
         * @see #filter(PairPredicate)
         * @see InputHandlers#inGame(MinecraftClient, ButtonBinding)
         */
        public Builder onlyInGame()
        {
            return this.filter(InputHandlers::inGame);
        }

        /**
         * Sets the filter of {@link ButtonBinding} to only in inventory.
         *
         * @return The builder instance.
         * @see #filter(PairPredicate)
         * @see InputHandlers#inInventory(MinecraftClient, ButtonBinding)
         */
        public Builder onlyInInventory()
        {
            return this.filter(InputHandlers::inInventory);
        }

        /**
         * Sets whether the {@link ButtonBinding} has a cooldown or not.
         *
         * @param cooldown True if the {@link ButtonBinding} has a cooldown, else false.
         * @return The builder instance.
         */
        public Builder cooldown(boolean cooldown)
        {
            this.cooldown = cooldown;
            return this;
        }

        /**
         * Puts a cooldown on the {@link ButtonBinding}.
         *
         * @return The builder instance.
         * @since 1.5.0
         */
        public Builder cooldown()
        {
            return this.cooldown(true);
        }

        /**
         * Sets the category of the {@link ButtonBinding}.
         *
         * @param category The category.
         * @return The builder instance.
         */
        public Builder category(@Nullable ButtonCategory category)
        {
            this.category = category;
            return this;
        }

        /**
         * Sets the keybinding linked to the {@link ButtonBinding}.
         *
         * @param binding The keybinding to link.
         * @return The builder instance.
         */
        public Builder linkKeybind(@Nullable KeyBinding binding)
        {
            this.mcBinding = binding;
            return this;
        }

        /**
         * Builds the {@link ButtonBinding}.
         *
         * @return The built {@link ButtonBinding}.
         */
        public ButtonBinding build()
        {
            ButtonBinding binding = new ButtonBinding(this.key, this.buttons, this.actions, this.filter, this.cooldown);
            if (this.category != null)
                this.category.registerBinding(binding);
            if (this.mcBinding != null)
                binding.setKeyBinding(this.mcBinding);
            return binding;
        }

        /**
         * Builds and registers the {@link ButtonBinding}.
         *
         * @return The built {@link ButtonBinding}.
         * @see #build()
         */
        public ButtonBinding register()
        {
            return InputManager.registerBinding(this.build());
        }
    }
}
