/*
 * Copyright © 2021 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of midnightcontrols.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package eu.midnightdust.midnightcontrols.client.compat;

import eu.midnightdust.midnightcontrols.client.MidnightControlsClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.hit.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a compatibility handler for a mod.
 *
 * @author LambdAurora
 * @version 1.7.0
 * @since 1.1.0
 */
public interface CompatHandler {
    /**
     * Handles compatibility of a mod.
     *
     * @param mod this mod instance
     */
    void handle(@NotNull MidnightControlsClient mod);

    /**
     * Returns whether the mouse is required on the specified screen.
     *
     * @param screen the screen
     * @return true if the mouse is required on the specified screen, else false
     */
    default boolean requireMouseOnScreen(Screen screen) {
        return false;
    }

    /**
     * Returns a slot at the specified location if possible.
     *
     * @param screen the screen
     * @param mouseX the mouse X-coordinate
     * @param mouseY the mouse Y-coordinate
     * @return a slot if present, else null
     * @since 1.5.0
     */
    default @Nullable CompatHandler.SlotPos getSlotAt(@NotNull Screen screen, int mouseX, int mouseY) {
        return null;
    }

    /**
     * Returns whether the current slot is a creative slot or not.
     *
     * @param screen the screen
     * @param slot the slot to check
     * @return true if the slot is a creative slot, else false
     */
    default boolean isCreativeSlot(@NotNull HandledScreen screen, @NotNull Slot slot) {
        return false;
    }

    /**
     * Returns a custom translation key to make custom attack action strings on the HUD.
     *
     * @param client the client instance
     * @param placeResult the last place block result
     * @return null if untouched, else a translation key
     */
    default String getAttackActionAt(@NotNull MinecraftClient client, @Nullable BlockHitResult placeResult) {
        return null;
    }

    /**
     * Returns a custom translation key to make custom use action strings on the HUD.
     *
     * @param client the client instance
     * @param placeResult the last place block result
     * @return null if untouched, else a translation key
     */
    default String getUseActionAt(@NotNull MinecraftClient client, @Nullable BlockHitResult placeResult) {
        return null;
    }

    /**
     * Handles the menu back button.
     *
     * @param client the client instance
     * @param screen the screen
     * @return true if the handle was fired and succeed, else false
     */
    default boolean handleMenuBack(@NotNull MinecraftClient client, @NotNull Screen screen) {
        return false;
    }

    record SlotPos(int x, int y) {
        public static final SlotPos INVALID_SLOT = new SlotPos(-1, -1);
    }
}
