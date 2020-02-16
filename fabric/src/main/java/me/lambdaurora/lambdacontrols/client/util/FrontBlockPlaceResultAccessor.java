/*
 * Copyright © 2020 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of LambdaControls.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package me.lambdaurora.lambdacontrols.client.util;

import net.minecraft.util.hit.BlockHitResult;
import org.jetbrains.annotations.Nullable;

/**
 * Represents an accessor of the BlockHitResult for the front block placing feature.
 * <p>
 * It is implemented by {@link net.minecraft.client.MinecraftClient}.
 *
 * @author LambdAurora
 * @version 1.2.0
 * @since 1.2.0
 */
public interface FrontBlockPlaceResultAccessor
{
    /**
     * Returns the {@link BlockHitResult} if a block can be placed with the front block placing feature.
     *
     * @return If possible a {@link BlockHitResult}, else a null value.
     */
    @Nullable BlockHitResult lambdacontrols_getFrontBlockPlaceResult();
}
