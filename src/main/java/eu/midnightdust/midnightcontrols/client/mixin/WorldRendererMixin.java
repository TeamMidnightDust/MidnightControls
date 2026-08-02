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
import net.minecraft.client.renderer.LevelRenderer;
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
//? if >= 26.2 {
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
//?} else {
/*import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.ShapeRenderer;
*///?}
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
    //? if >= 26.2 {
    @Shadow
    @Final
    private GameRenderer gameRenderer;
    //?} else {
    /*@Shadow
    @Final
    private Minecraft minecraft;
    *///?}

    @Inject(
            //~ if >= 26.2 'renderBlockOutline' -> 'submitBlockOutline'
            method = "submitBlockOutline",
            at = @At("HEAD"),
            cancellable = true
    )
    //? if >= 26.2 {
    private void onOutlineRender(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, LevelRenderState levelRenderState, CallbackInfo ci) {
    //? } else {
    /*private void onOutlineRender(MultiBufferSource.BufferSource bufferSource, PoseStack poseStack, boolean onlyTranslucentBlocks, LevelRenderState levelRenderState, CallbackInfo ci) {
    *///?}
        //~ if >= 26.2 'bufferSource' -> 'submitNodeCollector' {
        //~ if >= 26.2 'minecraft.gameRenderer.getMainCamera()' -> 'gameRenderer.mainCamera()' {
        if (((MidnightControlsConfig.controlsMode == ControlsMode.CONTROLLER && MidnightControlsConfig.touchInControllerMode) || MidnightControlsConfig.controlsMode == ControlsMode.TOUCHSCREEN)
                && MidnightControlsConfig.touchMode == TouchMode.FINGER_POS) {

            this.midnightcontrols$renderFingerOutline(submitNodeCollector, poseStack, gameRenderer.mainCamera());
            ci.cancel();
        }
        this.midnightcontrols$renderReacharoundOutline(submitNodeCollector, poseStack, gameRenderer.mainCamera());
        //~}
        //~}
    }

    //~ if >= 26.2 'MultiBufferSource.BufferSource bufferSource' -> 'SubmitNodeCollector submitNodeCollector' {
    @Unique
    private void midnightcontrols$renderFingerOutline(SubmitNodeCollector submitNodeCollector, PoseStack poseStack, Camera camera) {
        if (TouchInput.firstHitResult == null || TouchInput.firstHitResult.getType() != HitResult.Type.BLOCK)
            return;
        BlockHitResult result = (BlockHitResult) TouchInput.firstHitResult;
        var blockPos = result.getBlockPos();
        if (camera.entity() != null && Minecraft.getInstance().level != null && Minecraft.getInstance().player != null
                && Minecraft.getInstance().level.getWorldBorder().isWithinBounds(blockPos)) {
            var outlineShape = Minecraft.getInstance().level.getBlockState(blockPos).getShape(Minecraft.getInstance().level, blockPos, CollisionContext.of(camera.entity()));
            Color rgb = MidnightColorUtil.hex2Rgb(MidnightControlsConfig.touchOutlineColorHex);
            if (MidnightControlsConfig.touchOutlineColorHex.isEmpty()) rgb = RainbowColor.radialRainbow(1,1);
            var camPos = camera.position();
            poseStack.pushPose();
            //? if >= 26.2 {
            poseStack.translate((double)blockPos.getX() - camPos.x, (double)blockPos.getY() - camPos.y, (double)blockPos.getZ() - camPos.z);
            submitNodeCollector.submitShapeOutline(poseStack, outlineShape, RenderTypes.lines(), ARGB.color(MidnightControlsConfig.touchOutlineColorAlpha, rgb.getRGB()), this.gameRenderer.gameRenderState().windowRenderState.appropriateLineWidth, false);
            //?} else {
            /*var vertexConsumer = bufferSource.getBuffer(RenderTypes.lines());
            ShapeRenderer.renderShape(poseStack, vertexConsumer, outlineShape, blockPos.getX() - camPos.x(), blockPos.getY() - camPos.y(), blockPos.getZ() - camPos.z(),
                    ARGB.color(MidnightControlsConfig.touchOutlineColorAlpha, rgb.getRGB()), 4);
            *///?}
            poseStack.popPose();
        }
    }

    @Unique
    private void midnightcontrols$renderReacharoundOutline(SubmitNodeCollector submitNodeCollector, PoseStack poseStack, Camera camera) {
        if (Minecraft.getInstance().hitResult == null || Minecraft.getInstance().hitResult.getType() != HitResult.Type.MISS || !MidnightControlsConfig.shouldRenderReacharoundOutline)
            return;
        var result = reacharound.getLastReacharoundResult();
        if (result == null)
            return;
        var blockPos = result.getBlockPos();
        if (camera.entity() != null && Minecraft.getInstance().level != null && Minecraft.getInstance().player != null
                && Minecraft.getInstance().level.getWorldBorder().isWithinBounds(blockPos)) {
            var stack = Minecraft.getInstance().player.getItemInHand(InteractionHand.MAIN_HAND);
            if (stack == null || !(stack.getItem() instanceof BlockItem))
                return;

            var block = ((BlockItem) stack.getItem()).getBlock();
            result = reacharound.withSideForReacharound(result, block);
            var context = new BlockPlaceContext(new UseOnContext(Minecraft.getInstance().player, InteractionHand.MAIN_HAND, result));

            var placementState = block.getStateForPlacement(context);
            if (placementState == null)
                return;
            var camPos = camera.position();

            var outlineShape = placementState.getShape(Minecraft.getInstance().level, blockPos, CollisionContext.of(camera.entity()));
            Color rgb = MidnightColorUtil.hex2Rgb(MidnightControlsConfig.reacharoundOutlineColorHex);
            if (MidnightControlsConfig.reacharoundOutlineColorHex.isEmpty()) rgb = RainbowColor.radialRainbow(1,1);

            poseStack.pushPose();
            //? if >= 26.2 {
            poseStack.translate((double)blockPos.getX() - camPos.x, (double)blockPos.getY() - camPos.y, (double)blockPos.getZ() - camPos.z);
            submitNodeCollector.submitShapeOutline(poseStack, outlineShape, RenderTypes.lines(), ARGB.color(MidnightControlsConfig.reacharoundOutlineColorAlpha, rgb.getRGB()), this.gameRenderer.gameRenderState().windowRenderState.appropriateLineWidth, false);
            //?} else {
            /*var vertexConsumer = bufferSource.getBuffer(RenderTypes.lines());
            ShapeRenderer.renderShape(poseStack, vertexConsumer, outlineShape, blockPos.getX() - camPos.x(), blockPos.getY() - camPos.y(), blockPos.getZ() - camPos.z(),
                    ARGB.color(MidnightControlsConfig.reacharoundOutlineColorAlpha, rgb.getRGB()), 4);
            *///?}
            poseStack.popPose();
        }
    }
    //~}
}
