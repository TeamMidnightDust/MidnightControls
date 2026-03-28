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
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.ShapeRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.util.ARGB;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//? if >= 26.1 {
import net.minecraft.client.renderer.state.level.LevelRenderState;
//?} else {
/*import net.minecraft.client.renderer.state.LevelRenderState;
*///?}

import java.awt.*;

import static eu.midnightdust.midnightcontrols.client.MidnightControlsClient.reacharound;

import com.mojang.blaze3d.vertex.PoseStack;

/**
 * Represents a mixin to WorldRenderer.
 * <p>
 * Handles the rendering of the block outline of the reach-around features.
 */
@Mixin(LevelRenderer.class)
public abstract class WorldRendererMixin {
    @Shadow
    @Final
    private Minecraft minecraft;

    @Shadow
    private ClientLevel level;

    @Inject(
            method = "renderBlockOutline",
            at = @At("HEAD"),
            cancellable = true
    )
    private void onOutlineRender(MultiBufferSource.BufferSource immediate, PoseStack matrices, boolean renderBlockOutline, LevelRenderState renderStates, CallbackInfo ci) {
        if (((MidnightControlsConfig.controlsMode == ControlsMode.CONTROLLER && MidnightControlsConfig.touchInControllerMode) || MidnightControlsConfig.controlsMode == ControlsMode.TOUCHSCREEN)
                && MidnightControlsConfig.touchMode == TouchMode.FINGER_POS) {
            this.midnightcontrols$renderFingerOutline(immediate, matrices, minecraft.gameRenderer.getMainCamera());
            ci.cancel();
        }
        this.midnightcontrols$renderReacharoundOutline(immediate, matrices, minecraft.gameRenderer.getMainCamera());
    }
    @Unique
    private void midnightcontrols$renderFingerOutline(MultiBufferSource.BufferSource immediate, PoseStack matrices, Camera camera) {
        if (TouchInput.firstHitResult == null || TouchInput.firstHitResult.getType() != HitResult.Type.BLOCK)
            return;
        BlockHitResult result = (BlockHitResult) TouchInput.firstHitResult;
        var blockPos = result.getBlockPos();
        if (this.level.getWorldBorder().isWithinBounds(blockPos) && this.minecraft.player != null) {
            var outlineShape = this.level.getBlockState(blockPos).getShape(this.level, blockPos, CollisionContext.of(camera.entity()));
            Color rgb = MidnightColorUtil.hex2Rgb(MidnightControlsConfig.touchOutlineColorHex);
            if (MidnightControlsConfig.touchOutlineColorHex.isEmpty()) rgb = RainbowColor.radialRainbow(1,1);
            var pos = camera.position();
            matrices.pushPose();
            var vertexConsumer = immediate.getBuffer(RenderTypes.lines());
            ShapeRenderer.renderShape(matrices, vertexConsumer, outlineShape, blockPos.getX() - pos.x(), blockPos.getY() - pos.y(), blockPos.getZ() - pos.z(),
                    ARGB.color(MidnightControlsConfig.touchOutlineColorAlpha, rgb.getRGB()), 4);
            matrices.popPose();
        }
    }
    @Unique
    private void midnightcontrols$renderReacharoundOutline(MultiBufferSource.BufferSource immediate, PoseStack matrices, Camera camera) {
        if (this.minecraft.hitResult == null || this.minecraft.hitResult.getType() != HitResult.Type.MISS || !MidnightControlsConfig.shouldRenderReacharoundOutline)
            return;
        var result = reacharound.getLastReacharoundResult();
        if (result == null)
            return;
        var blockPos = result.getBlockPos();
        if (this.level.getWorldBorder().isWithinBounds(blockPos) && this.minecraft.player != null) {
            var stack = this.minecraft.player.getItemInHand(InteractionHand.MAIN_HAND);
            if (stack == null || !(stack.getItem() instanceof BlockItem))
                return;

            var block = ((BlockItem) stack.getItem()).getBlock();
            result = reacharound.withSideForReacharound(result, block);
            var context = new BlockPlaceContext(new UseOnContext(this.minecraft.player, InteractionHand.MAIN_HAND, result));

            var placementState = block.getStateForPlacement(context);
            if (placementState == null)
                return;
            var pos = camera.position();

            var outlineShape = placementState.getShape(this.level, blockPos, CollisionContext.of(camera.entity()));
            Color rgb = MidnightColorUtil.hex2Rgb(MidnightControlsConfig.reacharoundOutlineColorHex);
            if (MidnightControlsConfig.reacharoundOutlineColorHex.isEmpty()) rgb = RainbowColor.radialRainbow(1,1);
            matrices.pushPose();
            var vertexConsumer = immediate.getBuffer(RenderTypes.lines());
            ShapeRenderer.renderShape(matrices, vertexConsumer, outlineShape, blockPos.getX() - pos.x(), blockPos.getY() - pos.y(), blockPos.getZ() - pos.z(),
                    ARGB.color(MidnightControlsConfig.touchOutlineColorAlpha, rgb.getRGB()), 4);
            matrices.popPose();
        }
    }
}
