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
import me.lambdaurora.lambdacontrols.client.util.MouseAccessor;
import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Adds extra access to the mouse.
 */
@Mixin(Mouse.class)
public abstract class MouseMixin implements MouseAccessor
{
    @Invoker("onCursorPos")
    public abstract void lambdacontrols_onCursorPos(long window, double x, double y);

    @Inject(method = "lockCursor", at = @At("HEAD"), cancellable = true)
    private void onMouseLocked(CallbackInfo ci)
    {
        if (LambdaControlsClient.get().config.getControlsMode() == ControlsMode.TOUCHSCREEN)
            ci.cancel();
    }
}
