/*
 * Copyright © 2020 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of LambdaControls.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package dev.lambdaurora.lambdacontrols.client.util;

/**
 * Represents a Minecraft keybinding with extra access.
 */
public interface KeyBindingAccessor
{
    boolean lambdacontrols_press();

    boolean lambdacontrols_unpress();

    default boolean lambdacontrols_handlePressState(boolean pressed)
    {
        if (pressed)
            return this.lambdacontrols_press();
        else
            return this.lambdacontrols_unpress();
    }
}
