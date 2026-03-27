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
import com.mojang.blaze3d.vertex.PoseStack;
import eu.midnightdust.midnightcontrols.ControlsMode;
import eu.midnightdust.midnightcontrols.client.MidnightControlsClient;
import eu.midnightdust.midnightcontrols.client.MidnightControlsConfig;
import eu.midnightdust.midnightcontrols.client.gui.cursor.VirtualCursorRenderer;
import eu.midnightdust.midnightcontrols.client.gui.cursor.WaylandCursorRenderer;
import eu.midnightdust.midnightcontrols.client.touch.TouchUtils;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.state.GameRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
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

    //~ if >= 26.1 'render' -> 'extractGui'
    @Inject(method = "extractGui", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MouseHandler;getScaledXPos(Lcom/mojang/blaze3d/platform/Window;)D", shift = At.Shift.BEFORE))
    private void midnightcontrols$onRender(DeltaTracker tickCounter, boolean tick, /*? if >= 26.1 {*/final boolean resourcesLoaded, /*?}*/ CallbackInfo ci) {
        if (this.minecraft.screen != null && MidnightControlsConfig.controlsMode == ControlsMode.CONTROLLER)
            MidnightControlsClient.input.onPreRenderScreen(this.minecraft.screen);
    }
    //? fabric {
    //~ if >= 26.1 'Lnet/minecraft/client/gui/screens/Screen;renderWithTooltipAndSubtitles(Lnet/minecraft/client/gui/GuiGraphicsExtractor;IIF)V' -> 'Lnet/minecraft/client/gui/screens/Screen;extractRenderStateWithTooltipAndSubtitles(Lnet/minecraft/client/gui/GuiGraphicsExtractor;IIF)V'
    //~ if >= 26.1 'render' -> 'extractGui'
    @Inject(method = "extractGui", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;extractRenderStateWithTooltipAndSubtitles(Lnet/minecraft/client/gui/GuiGraphicsExtractor;IIF)V", shift = At.Shift.AFTER))
    //?} else if neoforge {
    /*//~ if >= 26.1 'render' -> 'extractGui'
    @Inject(method = "extractGui", at = @At(value = "INVOKE", target = "Lnet/neoforged/neoforge/client/ClientHooks;drawScreen(Lnet/minecraft/client/gui/screens/Screen;Lnet/minecraft/client/gui/GuiGraphicsExtractor;IIF)V", shift = At.Shift.AFTER))
    *//*?}*/
    private void midnightcontrols$renderVirtualCursor(DeltaTracker tickCounter, boolean tick, /*? if >= 26.1 {*/final boolean resourcesLoaded, /*?}*/ CallbackInfo ci, @Local GuiGraphicsExtractor drawContext) {
        VirtualCursorRenderer.getInstance().renderCursor(drawContext,  minecraft);
        if (MidnightControlsClient.isWayland) WaylandCursorRenderer.getInstance().renderCursor(drawContext, minecraft);
    }
    //~ if >= 26.1 'Lnet/minecraft/client/renderer/GameRenderer;renderItemInHand(FZLorg/joml/Matrix4f;)V' -> 'Lnet/minecraft/client/renderer/GameRenderer;renderItemInHand(Lnet/minecraft/client/renderer/state/level/CameraRenderState;FLorg/joml/Matrix4fc;)V'
    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GameRenderer;renderItemInHand(Lnet/minecraft/client/renderer/state/level/CameraRenderState;FLorg/joml/Matrix4fc;)V"), method = "renderLevel")
    private void midnigtcontrols$captureMatrices(DeltaTracker tickCounter, CallbackInfo ci, @Local(ordinal = 0) Matrix4f projectionMatrix, /*? if >= 26.1 {*/@Local CameraRenderState camState/*?} else {*/ /*@Local(ordinal = 1) Matrix4f worldSpaceMatrix*//*?}*/) {
        TouchUtils.lastProjMat.set(projectionMatrix);
        TouchUtils.lastModMat.set(RenderSystem.getModelViewMatrix());
        TouchUtils.lastWorldSpaceMatrix.set(/*? if >= 26.1 {*/ camState.viewRotationMatrix /*?} else {*/ /*worldSpaceMatrix*/ /*?}*/);
    }
}
