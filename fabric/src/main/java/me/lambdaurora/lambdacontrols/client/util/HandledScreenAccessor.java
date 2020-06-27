/*
 * Copyright © 2020 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of LambdaControls.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package me.lambdaurora.lambdacontrols.client.util;

import net.minecraft.screen.slot.Slot;

/**
 * Represents an accessor to AbstractContainerScreen.
 */
public interface HandledScreenAccessor
{
    /**
     * Gets the left coordinate of the GUI.
     *
     * @return The left coordinate of the GUI.
     */
    int getX();

    /**
     * Gets the top coordinate of the GUI.
     *
     * @return The top coordinate of the GUI.
     */
    int getY();

    /**
     * Gets the slot at position.
     *
     * @param pos_x The X position to check.
     * @param pos_y The Y position to check.
     * @return The slot at the specified position.
     */
    Slot lambdacontrols_getSlotAt(double pos_x, double pos_y);
}
