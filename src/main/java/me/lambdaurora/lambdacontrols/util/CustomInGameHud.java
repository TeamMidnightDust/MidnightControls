/*
 * Copyright © 2019 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of LambdaControls.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package me.lambdaurora.lambdacontrols.util;

import me.lambdaurora.lambdacontrols.gui.LambdaControlsHud;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a custom ingame hud with an accessor to an added hud.
 */
public interface CustomInGameHud
{
    @NotNull LambdaControlsHud get_lambdacontrols_hud();
}
