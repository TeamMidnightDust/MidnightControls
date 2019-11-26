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
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin
{
    @Inject(method = "init", at = @At("RETURN"))
    private void on_init(CallbackInfo ci)
    {
        LambdaControls.get().on_mc_init((MinecraftClient) (Object) this);
    }

    @Inject(method = "handleInputEvents", at = @At("HEAD"))
    private void on_handle_input_events(CallbackInfo ci)
    {
        if (LambdaControls.get().config.get_controls_mode() == ControlsMode.CONTROLLER)
            LambdaControls.get().on_tick((MinecraftClient) (Object) this);
    }
}
