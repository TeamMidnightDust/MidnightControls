/*
 * Copyright © 2020 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of LambdaControls.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package dev.lambdaurora.lambdacontrols.client;

/**
 * Represents a button state.
 *
 * @author LambdAurora
 * @version 1.1.0
 * @since 1.1.0
 */
public enum ButtonState
{
    NONE(0),
    PRESS(1),
    RELEASE(2),
    REPEAT(3);

    public final int id;

    ButtonState(int id)
    {
        this.id = id;
    }

    /**
     * Returns whether this state is a pressed state.
     *
     * @return True if this state is a pressed state, else false.
     */
    public boolean isPressed()
    {
        return this == PRESS || this == REPEAT;
    }

    /**
     * Returns whether this state is an unpressed state.
     *
     * @return True if this state is an unpressed state, else false.
     */
    public boolean isUnpressed()
    {
        return this == RELEASE || this == NONE;
    }
}
