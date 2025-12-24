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
import eu.midnightdust.midnightcontrols.client.touch.gui.TouchscreenOverlay;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.MouseButtonInfo;
import net.minecraft.util.SmoothDouble;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.ThrowablePotionItem;
import eu.midnightdust.midnightcontrols.client.touch.TouchInput;
import eu.midnightdust.midnightcontrols.client.touch.TouchUtils;
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

import com.mojang.blaze3d.Blaze3D;

/**
 * Adds extra access to the mouse.
 */
@Mixin(MouseHandler.class)
public abstract class MouseMixin implements MouseAccessor {
    @Shadow @Final private Minecraft minecraft;

    @Shadow private double ypos;

    @Shadow private double accumulatedDX;

    @Shadow private double accumulatedDY;

    @Shadow private double xpos;

    @Shadow private boolean mouseGrabbed;

    @Shadow private boolean ignoreFirstMove;

    @Shadow private double mousePressedTime;

    @Shadow @Final private SmoothDouble smoothTurnX;

    @Shadow @Final private SmoothDouble smoothTurnY;

    @Shadow private boolean isLeftPressed;

    @Inject(method = "onButton", at = @At(value = "HEAD"), cancellable = true)
    private void midnightcontrols$onMouseButton(long window, MouseButtonInfo input, int action, CallbackInfo ci) {
        if (window != this.minecraft.getWindow().handle()) return;
        if (action == 1 && input.button() == GLFW.GLFW_MOUSE_BUTTON_4 && minecraft.screen != null) {
            MidnightControlsClient.input.tryGoBack(minecraft.screen);
        }
        else if ((minecraft.screen == null && doMixedInput() || minecraft.screen instanceof TouchscreenOverlay) && minecraft.player != null && input.button() == GLFW_MOUSE_BUTTON_1) {
            double mouseX = xpos / minecraft.getWindow().getGuiScale();
            double mouseY = ypos / minecraft.getWindow().getGuiScale();
            int centerX = minecraft.getWindow().getGuiScaledWidth() / 2;
            if (action == 1 && mouseY >= (double) (minecraft.getWindow().getGuiScaledHeight() - 22) && mouseX >= (double) (centerX - 90) && mouseX <= (double) (centerX + 90)) {
                for (int slot = 0; slot < 9; ++slot) {
                    int slotX = centerX - 90 + slot * 20 + 2;
                    if (mouseX >= (double) slotX && mouseX <= (double) (slotX + 20)) {
                        minecraft.player.getInventory().setSelectedSlot(slot);
                        TouchInput.clickStartTime = -1;
                        ci.cancel();
                        return;
                    }
                }
            }
            if (action == 1) {
                TouchInput.clickStartTime = System.currentTimeMillis();
                boolean bl = false;
                if (minecraft.screen instanceof TouchscreenOverlay overlay) bl = overlay.mouseClicked(new MouseButtonEvent(mouseX, mouseY, input), false);
                if (!bl) TouchInput.firstHitResult = TouchUtils.getTargetedObject(mouseX, mouseY);
                if (minecraft.screen == null) ci.cancel();
            }
            else if (TouchInput.mouseReleased(mouseX, mouseY, input.button())) ci.cancel();
        }
    }

    @Inject(method = "isMouseGrabbed", at = @At("HEAD"), cancellable = true)
    private void midnightcontrols$isCursorLocked(CallbackInfoReturnable<Boolean> ci) {
        if (this.minecraft.screen == null) {
            if (MidnightControlsConfig.controlsMode == ControlsMode.CONTROLLER && MidnightControlsConfig.virtualMouse) {
                ci.setReturnValue(true);
                ci.cancel();
            }
        }
    }

    @Inject(method = "grabMouse", at = @At("HEAD"), cancellable = true)
    private void midnightcontrols$onCursorLocked(CallbackInfo ci) {
        if ((MidnightControlsConfig.controlsMode == ControlsMode.CONTROLLER && MidnightControlsConfig.virtualMouse) ||
                MidnightControlsConfig.controlsMode == ControlsMode.TOUCHSCREEN || doMixedInput())
            ci.cancel();
    }

    @Inject(method = "turnPlayer", at = @At("HEAD"), cancellable = true)
    private void midnightcontrols$updateMouse(CallbackInfo ci) {
        if (MidnightControlsConfig.eyeTrackerAsMouse && mouseGrabbed && minecraft.isWindowActive()) {
            // Eye Tracking is only for the camera controlling cursor, we need the normal cursor everywhere else.
            if (!minecraft.options.smoothCamera) {
                smoothTurnX.reset();
                smoothTurnY.reset();
            }
            EyeTrackerHandler.updateMouseWithEyeTracking(xpos + accumulatedDX, ypos + accumulatedDY, minecraft,
                    mousePressedTime, isLeftPressed, midnightcontrols$isUsingLongRangedTool(), smoothTurnX, smoothTurnY);
            mousePressedTime = Blaze3D.getTime();
            accumulatedDX = 0.0;
            accumulatedDY = 0.0;
            ci.cancel();
        }
        if (doMixedInput() && minecraft.isWindowActive()) {
            ci.cancel();
        }
    }

    @Unique
    private boolean midnightcontrols$isUsingLongRangedTool() {
        if (minecraft.player == null) return false;
        ItemStack stack = minecraft.player.getUseItem();
        return (isLeftPressed && (stack.getUseAnimation() == ItemUseAnimation.BOW || stack.getUseAnimation() == ItemUseAnimation.CROSSBOW ||
                        stack.getUseAnimation() == ItemUseAnimation.SPEAR || stack.getItem() instanceof ThrowablePotionItem));
    }

    @Inject(method = "grabMouse", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/platform/InputConstants;grabOrReleaseMouse(Lcom/mojang/blaze3d/platform/Window;IDD)V",shift = At.Shift.BEFORE), cancellable = true)
    private void midnightcontrols$lockCursor(CallbackInfo ci) {
        if ((doMixedInput() || MidnightControlsConfig.eyeTrackerAsMouse)) {
            //In eye tracking mode, we cannot have the cursor locked to the center.
            GLFW.glfwSetInputMode(minecraft.getWindow().handle(), GLFW_CURSOR, GLFW_CURSOR_HIDDEN);
            minecraft.setScreen(null);
            ignoreFirstMove = true;
            ci.cancel();
        }
    }

}
