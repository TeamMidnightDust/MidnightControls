/*
 * Copyright © 2019 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of LambdaControls.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package me.lambdaurora.lambdacontrols.util;

import net.minecraft.container.Slot;
import net.minecraft.item.ItemGroup;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * Represents an accessor to AbstractContainerScreen.
 */
public interface AbstractContainerScreenAccessor
{
    /**
     * Gets the left coordinate of the GUI.
     *
     * @return The left coordinate of the GUI.
     */
    int get_left();

    /**
     * Gets the top coordinate of the GUI.
     *
     * @return The top coordinate of the GUI.
     */
    int get_top();

    /**
     * Gets the slot at position.
     *
     * @param pos_x The X position to check.
     * @param pos_y The Y position to check.
     * @return The slot at the specified position.
     */
    Slot get_slot_at(double pos_x, double pos_y);
}
