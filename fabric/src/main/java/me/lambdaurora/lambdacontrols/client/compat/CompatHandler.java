/*
 * Copyright © 2020 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of LambdaControls.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package me.lambdaurora.lambdacontrols.client.compat;

import me.lambdaurora.lambdacontrols.client.LambdaControlsClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.hit.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a compatibility handler for a mod.
 *
 * @author LambdAurora
 * @version 1.3.2
 * @since 1.1.0
 */
public interface CompatHandler
{
    /**
     * Handles compatibility of a mod.
     *
     * @param mod This mod instance.
     */
    void handle(@NotNull LambdaControlsClient mod);

    /**
     * Returns whether the mouse is required on the specified screen.
     *
     * @param screen The screen.
     * @return True if the mouse is required on the specified screen, else false.
     */
    default boolean requireMouseOnScreen(Screen screen)
    {
        return false;
    }

    /**
     * Returns whether the current slot is a creative slot or not.
     *
     * @param screen The screen.
     * @param slot   The slot to check.
     * @return True if the slot is a creative slot, else false.
     */
    default boolean isCreativeSlot(@NotNull HandledScreen screen, @NotNull Slot slot)
    {
        return false;
    }

    /**
     * Returns a custom translation key to make custom attack action strings on the HUD.
     *
     * @param client      The client instance.
     * @param placeResult The last place block result.
     * @return Null if untouched, else a translation key.
     */
    default String getAttackActionAt(@NotNull MinecraftClient client, @Nullable BlockHitResult placeResult)
    {
        return null;
    }

    /**
     * Returns a custom translation key to make custom use action strings on the HUD.
     *
     * @param client      The client instance.
     * @param placeResult The last place block result.
     * @return Null if untouched, else a translation key.
     */
    default String getUseActionAt(@NotNull MinecraftClient client, @Nullable BlockHitResult placeResult)
    {
        return null;
    }

    /**
     * Handles the menu back button.
     *
     * @param client The client instance.
     * @param screen The screen.
     * @return True if the handle was fired and succeed, else false.
     */
    default boolean handleMenuBack(@NotNull MinecraftClient client, @NotNull Screen screen)
    {
        return false;
    }
}
