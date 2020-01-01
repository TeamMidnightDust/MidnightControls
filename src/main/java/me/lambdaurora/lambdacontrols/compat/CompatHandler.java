/*
 * Copyright © 2020 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of LambdaControls.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package me.lambdaurora.lambdacontrols.compat;

import me.lambdaurora.lambdacontrols.LambdaControls;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a compatibility handler for a mod.
 *
 * @author LambdAurora
 * @version 1.1.0
 * @since 1.1.0
 */
public interface CompatHandler
{
    /**
     * Handles compatibility of a mod.
     *
     * @param mod This mod instance.
     */
    void handle(@NotNull LambdaControls mod);
}
