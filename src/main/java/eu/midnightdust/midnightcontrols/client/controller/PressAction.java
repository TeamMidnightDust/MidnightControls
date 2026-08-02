/*
 * Copyright © 2021 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of midnightcontrols.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package eu.midnightdust.midnightcontrols.client.controller;

import eu.midnightdust.midnightcontrols.client.enums.ButtonState;
import eu.midnightdust.midnightcontrols.client.util.KeyBindingAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.ToggleKeyMapping;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a press action callback.
 *
 * @author LambdAurora
 * @version 1.7.0
 * @since 1.0.0
 */
@FunctionalInterface
public interface PressAction {
    PressAction DEFAULT_ACTION = (client, button, value, action) -> {
        //~ if >= 26.2 'client.screen' -> 'client.gui.screen()'
        if (action == ButtonState.REPEAT || client.gui.screen() != null)
            return false;
        button.asKeyBinding().ifPresent(binding -> {
            if (binding instanceof ToggleKeyMapping && binding != client.options.keyAttack) // TODO: Properly fix sticky keys so the attack key doesn't need to be a hardcoded exception
                binding.setDown(button.isPressed());
            else
                ((KeyBindingAccessor) binding).midnightcontrols$handlePressState(button.isPressed());
        });
        return true;
    };

    /**
     * Handles when there is a press action.
     *
     * @param client the client instance
     * @param action the action done
     */
    boolean press(@NotNull Minecraft client, @NotNull ButtonBinding button, float value, @NotNull ButtonState action);
}
