/*
 * Copyright © 2019 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of LambdaControls.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package me.lambdaurora.lambdacontrols.mixin;

import me.lambdaurora.lambdacontrols.ControlsMode;
import me.lambdaurora.lambdacontrols.LambdaControls;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mouse.class)
public class MouseMixin
{
    @Shadow
    @Final
    private MinecraftClient client;

    @Shadow
    private double x;

    @Shadow
    private double y;

    @Inject(method = "lockCursor", at = @At("HEAD"), cancellable = true)
    private void on_mouse_locked(CallbackInfo ci)
    {
        if (LambdaControls.get().config.get_controls_mode() == ControlsMode.TOUCHSCREEN)
            ci.cancel();
    }
}
