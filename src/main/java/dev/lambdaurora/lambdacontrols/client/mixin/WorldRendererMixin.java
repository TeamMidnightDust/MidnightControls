/*
 * Copyright © 2020 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of LambdaControls.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package dev.lambdaurora.lambdacontrols.client.mixin;

import dev.lambdaurora.lambdacontrols.client.LambdaControlsClient;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

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

    @Shadow
    @Final
    private BufferBuilderStorage bufferBuilders;

    @Shadow
    private static void drawShapeOutline(MatrixStack matrixStack, VertexConsumer vertexConsumer, VoxelShape voxelShape, double d, double e, double f, float g, float h, float i, float j) {
    }

    @Inject(
            method = "render",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/client/MinecraftClient;crosshairTarget:Lnet/minecraft/util/hit/HitResult;",
                    ordinal = 1,
                    shift = At.Shift.AFTER
            )
    )
    private void onOutlineRender(MatrixStack matrices, float tickDelta, long limitTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer,
                                 LightmapTextureManager lightmapTextureManager, Matrix4f matrix4f, CallbackInfo ci) {
        if (this.client.crosshairTarget == null || this.client.crosshairTarget.getType() != HitResult.Type.MISS || !LambdaControlsClient.get().config.shouldRenderReacharoundOutline())
            return;
        BlockHitResult result = LambdaControlsClient.get().reacharound.getLastReacharoundResult();
        if (result == null)
            return;
        BlockPos blockPos = result.getBlockPos();
        if (this.world.getWorldBorder().contains(blockPos)) {
            ItemStack stack = this.client.player.getStackInHand(Hand.MAIN_HAND);
            if (stack == null || !(stack.getItem() instanceof BlockItem))
                return;

            LambdaControlsClient mod = LambdaControlsClient.get();

            Block block = ((BlockItem) stack.getItem()).getBlock();
            result = mod.reacharound.withSideForReacharound(result, block);
            ItemPlacementContext context = new ItemPlacementContext(new ItemUsageContext(this.client.player, Hand.MAIN_HAND, result));

            BlockState placementState = block.getPlacementState(context);
            if (placementState == null)
                return;
            Vec3d pos = camera.getPos();

            VoxelShape outlineShape = placementState.getOutlineShape(this.client.world, blockPos, ShapeContext.of(camera.getFocusedEntity()));
            int[] color = mod.config.getReacharoundOutlineColor();

            VertexConsumer vertexConsumer = this.bufferBuilders.getEntityVertexConsumers().getBuffer(RenderLayer.getLines());
            drawShapeOutline(matrices, vertexConsumer, outlineShape,
                    (double) blockPos.getX() - pos.getX(), (double) blockPos.getY() - pos.getY(), (double) blockPos.getZ() - pos.getZ(),
                    color[0] / 255.f, color[1] / 255.f, color[2] / 255.f, color[3] / 255.f);
        }
    }
}
