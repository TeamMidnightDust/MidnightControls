/*
 * Copyright © 2019 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of LambdaControls.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package me.lambdaurora.lambdacontrols.gui;

import me.lambdaurora.lambdacontrols.LambdaControls;
import net.minecraft.client.MinecraftClient;
import org.jetbrains.annotations.NotNull;

/**
 * Represents the LambdaControls HUD.
 */
public class LambdaControlsHud
{
    private final MinecraftClient client;
    private final LambdaControls mod;

    public LambdaControlsHud(@NotNull MinecraftClient client, @NotNull LambdaControls mod)
    {
        this.client = client;
        this.mod = mod;
    }

    public void render() {

    }
}
