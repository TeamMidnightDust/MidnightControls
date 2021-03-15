/*
 * Copyright © 2020 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of LambdaControls.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package dev.lambdaurora.lambdacontrols.client.util;

import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import org.jetbrains.annotations.Nullable;

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

    boolean lambdacontrols_isClickOutsideBounds(double mouseX, double mouseY, int x, int y, int button);

    /**
     * Handles a mouse click on the specified slot.
     *
     * @param slot       The slot instance.
     * @param slotId     The slot id.
     * @param clickData  The click data.
     * @param actionType The action type.
     */
    void lambdacontrols_onMouseClick(@Nullable Slot slot, int slotId, int clickData, SlotActionType actionType);
}
