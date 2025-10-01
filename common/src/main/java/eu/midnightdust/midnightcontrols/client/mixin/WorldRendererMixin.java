/*
 * Copyright © 2021 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of midnightcontrols.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package eu.midnightdust.midnightcontrols.client.mixin;

import eu.midnightdust.lib.util.MidnightColorUtil;
import eu.midnightdust.midnightcontrols.ControlsMode;
import eu.midnightdust.midnightcontrols.client.MidnightControlsConfig;
import eu.midnightdust.midnightcontrols.client.touch.TouchInput;
import eu.midnightdust.midnightcontrols.client.enums.TouchMode;
import eu.midnightdust.midnightcontrols.client.util.RainbowColor;
import net.minecraft.block.ShapeContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.render.state.WorldRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.ColorHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;

import static eu.midnightdust.midnightcontrols.client.MidnightControlsClient.reacharound;

/**
 * Represents a mixin to WorldRenderer.
 * <p>
 * Handles the rendering of the block outline of the reach-around features.
 */
@Mixin(WorldRenderer.class)
public abstract class WorldRendererMixin {
    @Shadow
    @Final
    private MinecraftClient client;

    @Shadow
    private ClientWorld world;

    @Inject(
            method = "renderTargetBlockOutline",
            at = @At("HEAD"),
            cancellable = true
    )
    private void onOutlineRender(VertexConsumerProvider.Immediate immediate, MatrixStack matrices, boolean renderBlockOutline, WorldRenderState renderStates, CallbackInfo ci) {
        if (((MidnightControlsConfig.controlsMode == ControlsMode.CONTROLLER && MidnightControlsConfig.touchInControllerMode) || MidnightControlsConfig.controlsMode == ControlsMode.TOUCHSCREEN)
                && MidnightControlsConfig.touchMode == TouchMode.FINGER_POS) {
            this.midnightcontrols$renderFingerOutline(immediate, matrices, client.gameRenderer.getCamera());
            ci.cancel();
        }
        this.midnightcontrols$renderReacharoundOutline(immediate, matrices, client.gameRenderer.getCamera());
    }
    @Unique
    private void midnightcontrols$renderFingerOutline(VertexConsumerProvider.Immediate immediate, MatrixStack matrices, Camera camera) {
        if (TouchInput.firstHitResult == null || TouchInput.firstHitResult.getType() != HitResult.Type.BLOCK)
            return;
        BlockHitResult result = (BlockHitResult) TouchInput.firstHitResult;
        var blockPos = result.getBlockPos();
        if (this.world.getWorldBorder().contains(blockPos) && this.client.player != null) {
            var outlineShape = this.world.getBlockState(blockPos).getOutlineShape(this.client.world, blockPos, ShapeContext.of(camera.getFocusedEntity()));
            Color rgb = MidnightColorUtil.hex2Rgb(MidnightControlsConfig.touchOutlineColorHex);
            if (MidnightControlsConfig.touchOutlineColorHex.isEmpty()) rgb = RainbowColor.radialRainbow(1,1);
            var pos = camera.getPos();
            matrices.push();
            var vertexConsumer = immediate.getBuffer(RenderLayer.getLines());
            VertexRendering.drawOutline(matrices, vertexConsumer, outlineShape, blockPos.getX() - pos.getX(), blockPos.getY() - pos.getY(), blockPos.getZ() - pos.getZ(),
                    ColorHelper.withAlpha(MidnightControlsConfig.touchOutlineColorAlpha, rgb.getRGB()));
            matrices.pop();
        }
    }
    @Unique
    private void midnightcontrols$renderReacharoundOutline(VertexConsumerProvider.Immediate immediate, MatrixStack matrices, Camera camera) {
        if (this.client.crosshairTarget == null || this.client.crosshairTarget.getType() != HitResult.Type.MISS || !MidnightControlsConfig.shouldRenderReacharoundOutline)
            return;
        var result = reacharound.getLastReacharoundResult();
        if (result == null)
            return;
        var blockPos = result.getBlockPos();
        if (this.world.getWorldBorder().contains(blockPos) && this.client.player != null) {
            var stack = this.client.player.getStackInHand(Hand.MAIN_HAND);
            if (stack == null || !(stack.getItem() instanceof BlockItem))
                return;

            var block = ((BlockItem) stack.getItem()).getBlock();
            result = reacharound.withSideForReacharound(result, block);
            var context = new ItemPlacementContext(new ItemUsageContext(this.client.player, Hand.MAIN_HAND, result));

            var placementState = block.getPlacementState(context);
            if (placementState == null)
                return;
            var pos = camera.getPos();

            var outlineShape = placementState.getOutlineShape(this.client.world, blockPos, ShapeContext.of(camera.getFocusedEntity()));
            Color rgb = MidnightColorUtil.hex2Rgb(MidnightControlsConfig.reacharoundOutlineColorHex);
            if (MidnightControlsConfig.reacharoundOutlineColorHex.isEmpty()) rgb = RainbowColor.radialRainbow(1,1);
            matrices.push();
            var vertexConsumer = immediate.getBuffer(RenderLayer.getLines());
            VertexRendering.drawOutline(matrices, vertexConsumer, outlineShape, blockPos.getX() - pos.getX(), blockPos.getY() - pos.getY(), blockPos.getZ() - pos.getZ(),
                    ColorHelper.withAlpha(MidnightControlsConfig.touchOutlineColorAlpha, rgb.getRGB()));
            matrices.pop();
        }
    }
}
