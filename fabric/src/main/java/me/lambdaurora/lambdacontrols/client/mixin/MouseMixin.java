/*
 * Copyright © 2020 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of LambdaControls.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package me.lambdaurora.lambdacontrols.client.mixin;

import me.lambdaurora.lambdacontrols.ControlsMode;
import me.lambdaurora.lambdacontrols.client.LambdaControlsClient;
import me.lambdaurora.lambdacontrols.client.LambdaControlsConfig;
import me.lambdaurora.lambdacontrols.client.util.MouseAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Adds extra access to the mouse.
 */
@Mixin(Mouse.class)
public abstract class MouseMixin implements MouseAccessor
{
    @Shadow
    @Final
    private MinecraftClient client;

    @Invoker("onCursorPos")
    public abstract void lambdacontrols_onCursorPos(long window, double x, double y);

    @Inject(method = "isCursorLocked", at = @At("HEAD"), cancellable = true)
    private void isCursorLocked(CallbackInfoReturnable<Boolean> ci)
    {
        if (client.currentScreen == null) {
            LambdaControlsConfig config = LambdaControlsClient.get().config;
            if (config.getControlsMode() == ControlsMode.CONTROLLER && config.hasVirtualMouse()) {
                ci.setReturnValue(true);
                ci.cancel();
            }
        }
    }

    @Inject(method = "lockCursor", at = @At("HEAD"), cancellable = true)
    private void onCursorLocked(CallbackInfo ci)
    {
        LambdaControlsConfig config = LambdaControlsClient.get().config;
        if (config.getControlsMode() == ControlsMode.TOUCHSCREEN
                || (config.getControlsMode() == ControlsMode.CONTROLLER && config.hasVirtualMouse()))
            ci.cancel();
    }
}
