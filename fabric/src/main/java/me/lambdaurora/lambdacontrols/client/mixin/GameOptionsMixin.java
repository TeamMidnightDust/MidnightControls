/*
 * Copyright © 2020 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of LambdaControls.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package me.lambdaurora.lambdacontrols.client.mixin;

import net.minecraft.client.options.GameOptions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Represents a mixin to GameOptions.
 * <p>
 * Sets the default of the Auto-Jump option to false.
 */
@Mixin(GameOptions.class)
public class GameOptionsMixin
{
    @Shadow
    public boolean autoJump;

    @Inject(method = "load", at = @At("HEAD"))
    public void onInit(CallbackInfo ci)
    {
        // Set default value of the Auto-Jump option to false.
        this.autoJump = false;
    }
}
