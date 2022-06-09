/*
 * Copyright © 2021 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of midnightcontrols.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package eu.midnightdust.midnightcontrols.client.mixin;

import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.SimpleOption;
import org.spongepowered.asm.mixin.Final;
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
public abstract class GameOptionsMixin {

    @Shadow public abstract SimpleOption<Boolean> getAutoJump();

    @Inject(method = "load", at = @At("HEAD"))
    public void onInit(CallbackInfo ci) {
        // Set default value of the Auto-Jump option to false.
        getAutoJump().setValue(false);
    }
}
