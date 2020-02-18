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
import net.minecraft.client.gui.screen.Screen;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a compatibility handler for a mod.
 *
 * @author LambdAurora
 * @version 1.2.0
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
     * @return True if the mouse is requried on the specified screen, else false.
     */
    default boolean requireMouseOnScreen(Screen screen)
    {
        return false;
    }
}
