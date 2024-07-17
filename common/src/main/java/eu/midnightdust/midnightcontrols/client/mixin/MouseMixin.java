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
import eu.midnightdust.midnightcontrols.client.gui.TouchscreenOverlay;
import eu.midnightdust.midnightcontrols.client.touch.TouchInput;
import eu.midnightdust.midnightcontrols.client.touch.TouchUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import net.minecraft.client.util.GlfwUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ThrowablePotionItem;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.Smoother;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import eu.midnightdust.midnightcontrols.client.mouse.EyeTrackerHandler;

import static eu.midnightdust.midnightcontrols.client.MidnightControlsConfig.doMixedInput;
import static org.lwjgl.glfw.GLFW.*;

/**
 * Adds extra access to the mouse.
 */
@Mixin(Mouse.class)
public abstract class MouseMixin implements MouseAccessor {
    @Shadow @Final private MinecraftClient client;

    @Shadow private double y;

    @Shadow private double cursorDeltaX;

    @Shadow private double cursorDeltaY;

    @Shadow private double x;

    @Shadow private boolean cursorLocked;

    @Shadow private boolean hasResolutionChanged;

    @Shadow private double glfwTime;

    @Shadow @Final private Smoother cursorXSmoother;

    @Shadow @Final private Smoother cursorYSmoother;

    @Shadow private boolean leftButtonClicked;

    @Inject(method = "onMouseButton", at = @At(value = "HEAD"), cancellable = true)
    private void midnightcontrols$onMouseButton(long window, int button, int action, int mods, CallbackInfo ci) {
        if (window != this.client.getWindow().getHandle()) return;
        if (action == 1 && button == GLFW.GLFW_MOUSE_BUTTON_4 && client.currentScreen != null) {
            MidnightControlsClient.input.tryGoBack(client.currentScreen);
        }
        else if ((client.currentScreen == null && doMixedInput() || client.currentScreen instanceof TouchscreenOverlay) && client.player != null && button == GLFW_MOUSE_BUTTON_1) {
            double mouseX = x / client.getWindow().getScaleFactor();
            double mouseY = y / client.getWindow().getScaleFactor();
            int centerX = client.getWindow().getScaledWidth() / 2;
            if (action == 1 && mouseY >= (double) (client.getWindow().getScaledHeight() - 22) && mouseX >= (double) (centerX - 90) && mouseX <= (double) (centerX + 90)) {
                for (int slot = 0; slot < 9; ++slot) {
                    int slotX = centerX - 90 + slot * 20 + 2;
                    if (mouseX >= (double) slotX && mouseX <= (double) (slotX + 20)) {
                        client.player.getInventory().selectedSlot = slot;
                        ci.cancel();
                        return;
                    }
                }
            }
            if (action == 1) {
                TouchInput.clickStartTime = System.currentTimeMillis();
                boolean bl = false;
                if (client.currentScreen instanceof TouchscreenOverlay overlay) bl = overlay.mouseClicked(mouseX, mouseY, button);
                if (!bl) TouchInput.firstHitResult = TouchUtils.getTargetedObject(mouseX, mouseY);
                if (client.currentScreen == null) ci.cancel();
            }
            else if (TouchInput.mouseReleased(mouseX, mouseY, button)) ci.cancel();
        }
    }

    @Inject(method = "isCursorLocked", at = @At("HEAD"), cancellable = true)
    private void midnightcontrols$isCursorLocked(CallbackInfoReturnable<Boolean> ci) {
        if (this.client.currentScreen == null) {
            if (MidnightControlsConfig.controlsMode == ControlsMode.CONTROLLER && MidnightControlsConfig.virtualMouse) {
                ci.setReturnValue(true);
                ci.cancel();
            }
        }
    }

    @Inject(method = "lockCursor", at = @At("HEAD"), cancellable = true)
    private void midnightcontrols$onCursorLocked(CallbackInfo ci) {
        if ((MidnightControlsConfig.controlsMode == ControlsMode.CONTROLLER && MidnightControlsConfig.virtualMouse) ||
                MidnightControlsConfig.controlsMode == ControlsMode.TOUCHSCREEN || doMixedInput())
            ci.cancel();
    }

    @Inject(method = "updateMouse", at = @At("HEAD"), cancellable = true)
    private void midnightcontrols$updateMouse(CallbackInfo ci) {
        if (MidnightControlsConfig.eyeTrackerAsMouse && cursorLocked && client.isWindowFocused()) {
            // Eye Tracking is only for the camera controlling cursor, we need the normal cursor everywhere else.
            if (!client.options.smoothCameraEnabled) {
                cursorXSmoother.clear();
                cursorYSmoother.clear();
            }
            EyeTrackerHandler.updateMouseWithEyeTracking(x + cursorDeltaX, y + cursorDeltaY, client,
                    glfwTime, leftButtonClicked, midnightcontrols$isUsingLongRangedTool(), cursorXSmoother, cursorYSmoother);
            glfwTime = GlfwUtil.getTime();
            cursorDeltaX = 0.0;
            cursorDeltaY = 0.0;
            ci.cancel();
        }
        if (doMixedInput() && client.isWindowFocused()) {
            ci.cancel();
        }
    }

    @Unique
    private boolean midnightcontrols$isUsingLongRangedTool() {
        if (client.player == null) return false;
        ItemStack stack = client.player.getActiveItem();
        return (leftButtonClicked && (stack.getUseAction() == UseAction.BOW || stack.getUseAction() == UseAction.CROSSBOW ||
                        stack.getUseAction() == UseAction.SPEAR || stack.getItem() instanceof ThrowablePotionItem));
    }

    @Inject(method = "lockCursor", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/InputUtil;setCursorParameters(JIDD)V",shift = At.Shift.BEFORE), cancellable = true)
    private void midnightcontrols$lockCursor(CallbackInfo ci) {
        if ((doMixedInput() || MidnightControlsConfig.eyeTrackerAsMouse)) {
            //In eye tracking mode, we cannot have the cursor locked to the center.
            GLFW.glfwSetInputMode(client.getWindow().getHandle(), GLFW_CURSOR, GLFW_CURSOR_HIDDEN);
            client.setScreen(null);
            hasResolutionChanged = true;
            ci.cancel();
        }
    }

}
