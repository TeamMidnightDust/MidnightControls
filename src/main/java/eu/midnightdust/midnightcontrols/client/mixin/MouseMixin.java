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
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.GlfwUtil;
import net.minecraft.client.util.SmoothUtil;
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
import eu.midnightdust.midnightcontrols.client.mouse.EyeTrackerHandler;

import static org.lwjgl.glfw.GLFW.GLFW_CURSOR;
import static org.lwjgl.glfw.GLFW.GLFW_CURSOR_HIDDEN;

/**
 * Adds extra access to the mouse.
 */
@Mixin(Mouse.class)
public abstract class MouseMixin implements MouseAccessor {
    @Shadow
    @Final
    private MinecraftClient client;

    @Shadow
    private double y;

    @Shadow
    private double cursorDeltaX;

    @Shadow
    private double cursorDeltaY;

    @Shadow
    private double x;

    @Shadow
    private boolean cursorLocked;

    @Shadow
    private boolean hasResolutionChanged;

    @Shadow
    private double lastMouseUpdateTime;

    @Shadow
    @Final
    private SmoothUtil cursorXSmoother;

    @Shadow
    @Final
    private SmoothUtil cursorYSmoother;

    @Shadow private boolean leftButtonClicked;

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
                //ci.setReturnValue(true);
                ci.cancel();
            }
        }
    }

    @Inject(method = "lockCursor", at = @At("HEAD"), cancellable = true)
    private void onCursorLocked(CallbackInfo ci) {
        if ((MidnightControlsConfig.eyeTrackerAsMouse && client.isWindowFocused() && !this.cursorLocked)
                || MidnightControlsConfig.controlsMode == ControlsMode.TOUCHSCREEN
                || (MidnightControlsConfig.controlsMode == ControlsMode.CONTROLLER && MidnightControlsConfig.virtualMouse))
            ci.cancel();
    }

    @Inject(method = "updateMouse", at = @At("HEAD"), cancellable = true)
    private void updateMouse(CallbackInfo ci) {
        if (MidnightControlsConfig.eyeTrackerAsMouse && cursorLocked && client.isWindowFocused()) {
            //Eye Tracking is only for the camera controlling cursor, we need the normal cursor everywhere else.
            if (!client.options.smoothCameraEnabled) {
                cursorXSmoother.clear();
                cursorYSmoother.clear();
            }
            EyeTrackerHandler.updateMouseWithEyeTracking(x + cursorDeltaX, y + cursorDeltaY, client,
                    lastMouseUpdateTime, leftButtonClicked, cursorXSmoother, cursorYSmoother);
            lastMouseUpdateTime = GlfwUtil.getTime();
            cursorDeltaX = 0.0;
            cursorDeltaY = 0.0;
            ci.cancel();
        }
    }

    @Inject(method = "lockCursor", at = @At("HEAD"), cancellable = true)
    private void lockCursor(CallbackInfo ci) {
        if (MidnightControlsConfig.eyeTrackerAsMouse && client.isWindowFocused() && !this.cursorLocked) {
            if (!MinecraftClient.IS_SYSTEM_MAC) {
                KeyBinding.updatePressedStates();
            }
            //In eye tracking mode, we cannot have the cursor locked to the center.
            GLFW.glfwSetInputMode(client.getWindow().getHandle(), GLFW_CURSOR, GLFW_CURSOR_HIDDEN);
            cursorLocked = true; //The game uses this flag for other gameplay checks
            client.setScreen(null);
            hasResolutionChanged = true;
            ci.cancel();
        }
    }

}
