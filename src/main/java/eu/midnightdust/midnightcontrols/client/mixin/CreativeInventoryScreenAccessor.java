/*
 * Copyright © 2021 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of midnightcontrols.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package eu.midnightdust.midnightcontrols.client.mixin;

import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

/**
 * Represents an accessor to CreativeInventoryScreen.
 */
@Mixin(CreativeModeInventoryScreen.class)
public interface CreativeInventoryScreenAccessor {
    /**
     * Gets the selected tab.
     *
     * @return the selected tab index
     */
    @Accessor("selectedTab")
    static CreativeModeTab getSelectedTab() {
        return CreativeModeTabs.getDefaultTab();
    }

    /**
     * Sets the selected tab.
     *
     * @param group the tab's item group
     */
    @Invoker("selectTab")
    void midnightcontrols$setSelectedTab(@NotNull CreativeModeTab group);

    /**
     * Returns whether the slot belongs to the creative inventory or not.
     *
     * @param slot the slot to check
     * @return true if the slot is from the creative inventory, else false
     */
    @Invoker("isCreativeSlot")
    boolean midnightcontrols$isCreativeInventorySlot(@Nullable Slot slot);

    /**
     * Returns whether the current tab has a scrollbar or not.
     *
     * @return true if the current tab has a scrollbar, else false
     */
    @Invoker("canScroll")
    boolean midnightcontrols$hasScrollbar();

    /**
     * Triggers searching the creative inventory from the current value of the internal {@link net.minecraft.client.gui.components.EditBox}
     */
    @Invoker("refreshSearchResults")
    void midnightcontrols$search();
}
