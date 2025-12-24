/*
 * Copyright © 2021 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of midnightcontrols.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package eu.midnightdust.midnightcontrols.client.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.systems.RenderSystem;
import eu.midnightdust.midnightcontrols.ControlsMode;
import eu.midnightdust.midnightcontrols.client.MidnightControlsClient;
import eu.midnightdust.midnightcontrols.client.MidnightControlsConfig;
import eu.midnightdust.midnightcontrols.client.gui.cursor.VirtualCursorRenderer;
import eu.midnightdust.midnightcontrols.client.gui.cursor.WaylandCursorRenderer;
import eu.midnightdust.midnightcontrols.client.touch.TouchUtils;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
    @Shadow @Final private Minecraft minecraft;

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MouseHandler;getScaledXPos(Lcom/mojang/blaze3d/platform/Window;)D", shift = At.Shift.BEFORE))
    private void midnightcontrols$onRender(DeltaTracker tickCounter, boolean tick, CallbackInfo ci) {
        if (this.minecraft.screen != null && MidnightControlsConfig.controlsMode == ControlsMode.CONTROLLER)
            MidnightControlsClient.input.onPreRenderScreen(this.minecraft.screen);
    }
    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;renderWithTooltipAndSubtitles(Lnet/minecraft/client/gui/GuiGraphics;IIF)V", shift = At.Shift.AFTER))
    private void midnightcontrols$renderVirtualCursor(DeltaTracker tickCounter, boolean tick, CallbackInfo ci, @Local GuiGraphics drawContext) {
        VirtualCursorRenderer.getInstance().renderCursor(drawContext,  minecraft);
        if (MidnightControlsClient.isWayland) WaylandCursorRenderer.getInstance().renderCursor(drawContext, minecraft);
    }
    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GameRenderer;renderItemInHand(FZLorg/joml/Matrix4f;)V"), method = "renderLevel")
    private void midnigtcontrols$captureMatrices(DeltaTracker tickCounter, CallbackInfo ci, @Local(ordinal = 0) Matrix4f projectionMatrix, @Local(ordinal = 1) Matrix4f worldSpaceMatrix) {
        TouchUtils.lastProjMat.set(projectionMatrix);
        TouchUtils.lastModMat.set(RenderSystem.getModelViewMatrix());
        TouchUtils.lastWorldSpaceMatrix.set(worldSpaceMatrix);
    }
}
