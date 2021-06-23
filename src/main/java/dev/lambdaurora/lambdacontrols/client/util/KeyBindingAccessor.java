/*
 * Copyright © 2021 LambdAurora <aurora42lambda@gmail.com>
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
public interface KeyBindingAccessor {
    boolean lambdacontrols$press();

    boolean lambdacontrols$unpress();

    default boolean lambdacontrols$handlePressState(boolean pressed) {
        if (pressed)
            return this.lambdacontrols$press();
        else
            return this.lambdacontrols$unpress();
    }
}
