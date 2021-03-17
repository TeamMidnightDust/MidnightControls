/*
 * Copyright © 2021 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of LambdaControls.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package dev.lambdaurora.lambdacontrols.client.mixin;

import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.item.ItemGroup;
import net.minecraft.screen.slot.Slot;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

/**
 * Represents an accessor to CreativeInventoryScreen.
 */
@Mixin(CreativeInventoryScreen.class)
public interface CreativeInventoryScreenAccessor {
    /**
     * Gets the selected tab.
     *
     * @return The selected tab index.
     */
    @Accessor("selectedTab")
    int getSelectedTab();

    /**
     * Sets the selected tab.
     *
     * @param group The tab's item group.
     */
    @Invoker("setSelectedTab")
    void lambdacontrols$setSelectedTab(@NotNull ItemGroup group);

    /**
     * Returns whether the slot belongs to the creative inventory or not.
     *
     * @param slot The slot to check.
     * @return True if the slot is from the creative inventory, else false.
     */
    @Invoker("isCreativeInventorySlot")
    boolean lambdacontrols$isCreativeInventorySlot(@Nullable Slot slot);

    /**
     * Returns whether the current tab has a scrollbar or not.
     *
     * @return True if the current tab has a scrollbar, else false.
     */
    @Invoker("hasScrollbar")
    boolean lambdacontrols$hasScrollbar();
}
