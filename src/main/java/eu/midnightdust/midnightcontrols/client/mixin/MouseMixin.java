/*
 * Copyright © 2021 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of midnightcontrols.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package eu.midnightdust.midnightcontrols.client.mixin;

import eu.midnightdust.midnightcontrols.ControlsMode;
import eu.midnightdust.midnightcontrols.client.MidnightControlsClient;
import eu.midnightdust.midnightcontrols.client.MidnightControlsConfig;
import eu.midnightdust.midnightcontrols.client.util.MouseAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import net.minecraft.client.gui.screen.Screen;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Adds extra access to the mouse.
 */
@Mixin(Mouse.class)
public abstract class MouseMixin implements MouseAccessor {
    @Shadow
    @Final
    private MinecraftClient client;

    @Accessor
    public abstract void setLeftButtonClicked(boolean value);

    @Invoker("onCursorPos")
    public abstract void midnightcontrols$onCursorPos(long window, double x, double y);

    @Inject(method = "onMouseButton", at = @At(value = "TAIL"))
    private void onMouseBackButton(long window, int button, int action, int mods, CallbackInfo ci) {
        if (action == 1 && button == GLFW.GLFW_MOUSE_BUTTON_4 && MinecraftClient.getInstance().currentScreen != null) {
            if (MidnightControlsClient.get().input.tryGoBack(MinecraftClient.getInstance().currentScreen)) {
                action = 0;
            }
        }
    }

    @Inject(method = "isCursorLocked", at = @At("HEAD"), cancellable = true)
    private void isCursorLocked(CallbackInfoReturnable<Boolean> ci) {
        if (this.client.currentScreen == null) {
            if (MidnightControlsConfig.controlsMode == ControlsMode.CONTROLLER && MidnightControlsConfig.virtualMouse) {
                ci.setReturnValue(true);
                ci.cancel();
            }
        }
    }

    @Inject(method = "lockCursor", at = @At("HEAD"), cancellable = true)
    private void onCursorLocked(CallbackInfo ci) {
        if (/*config.getControlsMode() == ControlsMode.TOUCHSCREEN
                ||*/ (MidnightControlsConfig.controlsMode == ControlsMode.CONTROLLER && MidnightControlsConfig.virtualMouse))
            ci.cancel();
    }
}
