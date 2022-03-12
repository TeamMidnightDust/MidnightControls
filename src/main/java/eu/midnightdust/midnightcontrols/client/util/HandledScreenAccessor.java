/*
 * Copyright © 2021 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of midnightcontrols.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package eu.midnightdust.midnightcontrols.client.util;

import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import org.jetbrains.annotations.Nullable;

/**
 * Represents an accessor to AbstractContainerScreen.
 */
public interface HandledScreenAccessor {
    /**
     * Gets the left coordinate of the GUI.
     *
     * @return the left coordinate of the GUI
     */
    int getX();

    /**
     * Gets the top coordinate of the GUI.
     *
     * @return the top coordinate of the GUI
     */
    int getY();

    /**
     * Gets the slot at position.
     *
     * @param posX the X position to check
     * @param posY the Y position to check
     * @return the slot at the specified position
     */
    Slot midnightcontrols$getSlotAt(double posX, double posY);

    boolean midnightcontrols$isClickOutsideBounds(double mouseX, double mouseY, int x, int y, int button);

    /**
     * Handles a mouse click on the specified slot.
     *
     * @param slot the slot instance
     * @param slotId the slot id
     * @param clickData the click data
     * @param actionType the action type
     */
    void midnightcontrols$onMouseClick(@Nullable Slot slot, int slotId, int clickData, SlotActionType actionType);
}
