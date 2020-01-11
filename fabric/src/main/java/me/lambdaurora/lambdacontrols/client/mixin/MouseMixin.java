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
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Adds extra access to the mouse.
 */
@Mixin(Mouse.class)
public abstract class MouseMixin implements MouseAccessor
{
    @Shadow
    protected abstract void onCursorPos(long window, double x, double y);

    @Shadow
    protected abstract void onMouseButton(long window, int button, int action, int mods);

    @Inject(method = "lockCursor", at = @At("HEAD"), cancellable = true)
    private void on_mouse_locked(CallbackInfo ci)
    {
        if (LambdaControlsClient.get().config.get_controls_mode() == ControlsMode.TOUCHSCREEN)
            ci.cancel();
    }

    @Override
    public void lambdacontrols_on_cursor_pos(long window, double x, double y)
    {
        this.onCursorPos(window, x, y);
    }
}
