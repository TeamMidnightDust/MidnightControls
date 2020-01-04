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
import me.lambdaurora.lambdacontrols.client.LambdaControlsConfig;
import net.minecraft.client.MinecraftClient;
import org.aperlambda.lambdacommon.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Represents an input manager for controllers.
 *
 * @author LambdAurora
 * @version 1.1.0
 * @since 1.1.0
 */
public class InputManager
{
    private static final List<ButtonBinding>       BINDINGS   = new ArrayList<>();
    private static final List<ButtonCategory>      CATEGORIES = new ArrayList<>();
    public static final  Map<Integer, ButtonState> STATES     = new HashMap<>();

    /**
     * Returns whether the specified binding is registered or not.
     *
     * @param binding The binding to check.
     * @return True if the binding is registered, else false.
     */
    public static boolean has_binding(@NotNull ButtonBinding binding)
    {
        return BINDINGS.contains(binding);
    }

    /**
     * Returns whether the specified binding is registered or not.
     *
     * @param name The name of the binding to check.
     * @return True if the binding is registered, else false.
     */
    public static boolean has_binding(@NotNull String name)
    {
        return BINDINGS.parallelStream().map(ButtonBinding::get_name).anyMatch(binding -> binding.equalsIgnoreCase(name));
    }

    /**
     * Returns whether the specified binding is registered or not.
     *
     * @param identifier The identifier of the binding to check.
     * @return True if the binding is registered, else false.
     */
    public static boolean has_binding(@NotNull Identifier identifier)
    {
        return has_binding(identifier.get_namespace() + "." + identifier.get_name());
    }

    /**
     * Registers a button binding.
     *
     * @param binding The binding to register.
     * @return The registered binding.
     */
    public static @NotNull ButtonBinding register_binding(@NotNull ButtonBinding binding)
    {
        if (has_binding(binding))
            throw new IllegalStateException("Cannot register twice a button binding in the registry.");
        BINDINGS.add(binding);
        return binding;
    }

    public static @NotNull ButtonBinding register_binding(@NotNull Identifier binding_id, int[] default_button, @NotNull List<PressAction> actions, boolean has_cooldown)
    {
        return register_binding(new ButtonBinding(binding_id.get_namespace() + "." + binding_id.get_name(), default_button, actions, has_cooldown));
    }

    public static @NotNull ButtonBinding register_binding(@NotNull Identifier binding_id, int[] default_button, boolean has_cooldown)
    {
        return register_binding(binding_id, default_button, Collections.emptyList(), has_cooldown);
    }

    public static @NotNull ButtonBinding register_binding(@NotNull net.minecraft.util.Identifier binding_id, int[] default_button, @NotNull List<PressAction> actions, boolean has_cooldown)
    {
        return register_binding(new Identifier(binding_id.getNamespace(), binding_id.getPath()), default_button, actions, has_cooldown);
    }

    public static @NotNull ButtonBinding register_binding(@NotNull net.minecraft.util.Identifier binding_id, int[] default_button, boolean has_cooldown)
    {
        return register_binding(binding_id, default_button, Collections.emptyList(), has_cooldown);
    }

    /**
     * Sorts bindings to get bindings with the higher button counts first.
     */
    public static void sort_bindings()
    {
        synchronized (BINDINGS) {
            List<ButtonBinding> sorted_bindings = BINDINGS.stream().sorted(Collections.reverseOrder(Comparator.comparingInt(binding -> binding.get_button().length)))
                    .collect(Collectors.toList());
            BINDINGS.clear();
            BINDINGS.addAll(sorted_bindings);
        }
    }

    /**
     * Registers a category of button bindings.
     *
     * @param category The category to register.
     * @return The registered category.
     */
    public static ButtonCategory register_category(@NotNull ButtonCategory category)
    {
        CATEGORIES.add(category);
        return category;
    }

    public static ButtonCategory register_category(@NotNull Identifier identifier, int priority)
    {
        return register_category(new ButtonCategory(identifier, priority));
    }

    public static ButtonCategory register_category(@NotNull Identifier identifier)
    {
        return register_category(new ButtonCategory(identifier));
    }

    protected static ButtonCategory register_default_category(@NotNull String key, @NotNull Consumer<ButtonCategory> key_adder)
    {
        ButtonCategory category = register_category(new Identifier("minecraft", key), CATEGORIES.size());
        key_adder.accept(category);
        return category;
    }

    /**
     * Loads the button bindings from configuration.
     *
     * @param config The configuration instance.
     */
    public static void load_button_bindings(@NotNull LambdaControlsConfig config)
    {
        List<ButtonBinding> load_queue = new ArrayList<>(BINDINGS);
        load_queue.forEach(config::load_button_binding);
    }

    /**
     * Returns the binding state.
     *
     * @param binding The binding.
     * @return The current state of the binding.
     */
    public static @NotNull ButtonState get_binding_state(@NotNull ButtonBinding binding)
    {
        ButtonState state = ButtonState.REPEAT;
        for (int btn : binding.get_button()) {
            ButtonState btn_state = InputManager.STATES.getOrDefault(btn, ButtonState.NONE);
            if (btn_state == ButtonState.PRESS)
                state = ButtonState.PRESS;
            else if (btn_state == ButtonState.RELEASE) {
                state = ButtonState.RELEASE;
                break;
            } else if (btn_state == ButtonState.NONE) {
                state = ButtonState.NONE;
                break;
            }
        }
        return state;
    }

    /**
     * Returns whether the button has duplicated bindings.
     *
     * @param button The button to check.
     * @return True if the button has duplicated bindings, else false.
     */
    public static boolean has_duplicated_bindings(int[] button)
    {
        return BINDINGS.parallelStream().filter(binding -> are_buttons_equivalent(binding.get_button(), button)).count() > 1;
    }

    /**
     * Returns whether the specified buttons are equivalent or not.
     *
     * @param buttons1 First set of buttons.
     * @param buttons2 Second set of buttons.
     * @return True if the two sets of buttons are equivalent, else false.
     */
    public static boolean are_buttons_equivalent(int[] buttons1, int[] buttons2)
    {
        if (buttons1.length != buttons2.length)
            return false;
        int count = 0;
        for (int btn : buttons1) {
            for (int btn2 : buttons2) {
                if (btn == btn2) {
                    count++;
                    break;
                }
            }
        }
        return count == buttons1.length;
    }

    /**
     * Returns whether the button set contains the specified button or not.
     *
     * @param buttons The button set.
     * @param button  The button to check.
     * @return True if the button set contains the specified button, else false.
     */
    public static boolean contains_button(int[] buttons, int button)
    {
        return Arrays.stream(buttons).anyMatch(btn -> btn == button);
    }

    /**
     * Updates the button states.
     */
    public static void update_states()
    {
        STATES.forEach((btn, state) -> {
            if (state == ButtonState.PRESS)
                STATES.put(btn, ButtonState.REPEAT);
            else if (state == ButtonState.RELEASE)
                STATES.put(btn, ButtonState.NONE);
        });
    }

    public static void update_bindings(@NotNull MinecraftClient client)
    {
        List<Integer> skip_buttons = new ArrayList<>();
        Map<ButtonBinding, ButtonState> states = new HashMap<>();
        for (ButtonBinding binding : BINDINGS) {
            ButtonState binding_state = get_binding_state(binding);
            if (skip_buttons.stream().anyMatch(btn -> contains_button(binding.get_button(), btn))) {
                if (binding.pressed)
                    binding_state = ButtonState.RELEASE;
                else
                    binding_state = ButtonState.NONE;
            }
            binding.pressed = binding_state.is_pressed();
            binding.update();
            if (binding.pressed)
                Arrays.stream(binding.get_button()).forEach(skip_buttons::add);
            states.put(binding, binding_state);
        }

        states.forEach((binding, state) -> {
            if (state != ButtonState.NONE)
                binding.handle(client, state);
        });
    }

    public static @NotNull Stream<ButtonBinding> stream_bindings()
    {
        return BINDINGS.stream();
    }

    public static @NotNull Stream<ButtonCategory> stream_categories()
    {
        return CATEGORIES.stream();
    }
}
