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
import me.lambdaurora.lambdacontrols.client.util.KeyBindingAccessor;
import net.minecraft.client.MinecraftClient;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a press action callback.
 *
 * @author LambdAurora
 * @version 1.1.0
 * @since 1.0.0
 */
@FunctionalInterface
public interface PressAction
{
    PressAction DEFAULT_ACTION = (client, button, action) -> {
        if (action == ButtonState.REPEAT || client.currentScreen != null)
            return false;
        button.as_key_binding().ifPresent(key_binding -> ((KeyBindingAccessor) key_binding).handle_press_state(button.is_button_down()));
        return true;
    };

    /**
     * Handles when there is a press action.
     *
     * @param client The client instance.
     * @param action The action done.
     */
    boolean press(@NotNull MinecraftClient client, @NotNull ButtonBinding button, @NotNull ButtonState action);
}
