/*
 * Copyright © 2021 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of LambdaControls.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package dev.lambdaurora.lambdacontrols.client.controller;

import dev.lambdaurora.lambdacontrols.client.ButtonState;
import dev.lambdaurora.lambdacontrols.client.util.KeyBindingAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.StickyKeyBinding;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a press action callback.
 *
 * @author LambdAurora
 * @version 1.4.3
 * @since 1.0.0
 */
@FunctionalInterface
public interface PressAction {
    PressAction DEFAULT_ACTION = (client, button, value, action) -> {
        if (action == ButtonState.REPEAT || client.currentScreen != null)
            return false;
        button.asKeyBinding().ifPresent(binding -> {
            if (binding instanceof StickyKeyBinding)
                binding.setPressed(button.pressed);
            else
                ((KeyBindingAccessor) binding).lambdacontrols_handlePressState(button.isButtonDown());
        });
        return true;
    };

    /**
     * Handles when there is a press action.
     *
     * @param client The client instance.
     * @param action The action done.
     */
    boolean press(@NotNull MinecraftClient client, @NotNull ButtonBinding button, float value, @NotNull ButtonState action);
}
