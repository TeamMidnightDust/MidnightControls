/*
 * Copyright © 2019 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of LambdaControls.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package me.lambdaurora.lambdacontrols.mixin;

import net.minecraft.client.gui.ParentElement;
import net.minecraft.client.gui.screen.Screen;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Screen.class)
public abstract class ScreenMixin implements ParentElement
{
    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    private void on_key_pressed(int key_code, int scan_code, int modifiers, CallbackInfoReturnable<Boolean> ci)
    {
        if (key_code == GLFW.GLFW_KEY_UP || key_code == GLFW.GLFW_KEY_LEFT) {
            this.changeFocus(false);
            ci.setReturnValue(true);
            ci.cancel();
        } else if (key_code == GLFW.GLFW_KEY_DOWN || key_code == GLFW.GLFW_KEY_RIGHT) {
            this.changeFocus(true);
            ci.setReturnValue(true);
            ci.cancel();
        }
    }
}
