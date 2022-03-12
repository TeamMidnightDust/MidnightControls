/*
 * Copyright © 2021 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of midnightcontrols.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package eu.midnightdust.midnightcontrols.client.util;

/**
 * Represents a Minecraft keybinding with extra access.
 */
public interface KeyBindingAccessor {
    boolean midnightcontrols$press();

    boolean midnightcontrols$unpress();

    default boolean midnightcontrols$handlePressState(boolean pressed) {
        if (pressed)
            return this.midnightcontrols$press();
        else
            return this.midnightcontrols$unpress();
    }
}
