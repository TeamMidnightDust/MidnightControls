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
import eu.midnightdust.midnightcontrols.client.gui.MidnightControlsRenderer;
import eu.midnightdust.midnightcontrols.client.touch.TouchUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderTickCounter;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
    @Shadow
    @Final
    MinecraftClient client;

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Mouse;getX()D", shift = At.Shift.BEFORE))
    private void onRender(RenderTickCounter tickCounter, boolean tick, CallbackInfo ci) {
        if (this.client.currentScreen != null && MidnightControlsConfig.controlsMode == ControlsMode.CONTROLLER)
            MidnightControlsClient.get().input.onPreRenderScreen(this.client, this.client.currentScreen);
    }
    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;draw()V", shift = At.Shift.BEFORE))
    private void renderVirtualCursor(RenderTickCounter tickCounter, boolean tick, CallbackInfo ci, @Local DrawContext drawContext) {
        MidnightControlsRenderer.renderVirtualCursor(drawContext,  client);
        drawContext.draw();
    }
    @Inject(at = @At(value = "FIELD", target = "Lnet/minecraft/client/render/GameRenderer;renderHand:Z"), method = "renderWorld")
    private void captureProjAndModMatrix(RenderTickCounter tickCounter, CallbackInfo ci, @Local(ordinal = 1) Matrix4f matrices) {
        TouchUtils.lastProjMat.set(RenderSystem.getProjectionMatrix());
        TouchUtils.lastModMat.set(RenderSystem.getModelViewMatrix());
        TouchUtils.lastWorldSpaceMatrix.set(matrices);
    }

}
