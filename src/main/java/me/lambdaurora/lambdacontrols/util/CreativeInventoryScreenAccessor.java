/*
 * Copyright © 2020 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of LambdaControls.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package me.lambdaurora.lambdacontrols.util;

import net.minecraft.item.ItemGroup;
import org.jetbrains.annotations.NotNull;

/**
 * Represents an accessor to CreativeInventoryScreen.
 */
public interface CreativeInventoryScreenAccessor
{
    /**
     * Gets the selected tab.
     *
     * @return The selected tab index.
     */
    int get_selected_tab();

    /**
     * Sets the selected tab.
     *
     * @param group The tab's item group.
     */
    void set_selected_tab(@NotNull ItemGroup group);
}
