/*
 * Copyright © 2020 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of LambdaControls.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package me.lambdaurora.lambdacontrols.client.util;

import net.minecraft.client.util.InputUtil;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a Minecraft keybinding with extra access.
 */
public interface KeyBindingAccessor
{
    @NotNull InputUtil.KeyCode get_key_code();

    boolean lambdacontrols_press();

    boolean lambdacontrols_unpress();

    default boolean handle_press_state(boolean pressed)
    {
        if (pressed)
            return this.lambdacontrols_press();
        else
            return this.lambdacontrols_unpress();
    }
}
