/*
 * Copyright © 2021 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of midnightcontrols.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package eu.midnightdust.midnightcontrols.client;

import eu.midnightdust.midnightcontrols.MidnightControlsFeature;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static eu.midnightdust.midnightcontrols.client.MidnightControlsClient.client;

/**
 * Represents the reach-around API of midnightcontrols.
 *
 * @version 1.7.0
 * @since 1.3.2
 */
public class MidnightReacharound {
    private BlockHitResult lastReacharoundResult = null;
    private boolean lastReacharoundVertical = false;
    private boolean onSlab = false;

    public void tick() {
        this.lastReacharoundResult = this.tryVerticalReachAround();
        if (this.lastReacharoundResult == null) {
            this.lastReacharoundResult = this.tryHorizontalReachAround();
            this.lastReacharoundVertical = false;
        } else this.lastReacharoundVertical = true;
    }

    /**
     * Returns the last reach around result.
     *
     * @return the last reach around result
     */
    public @Nullable BlockHitResult getLastReacharoundResult() {
        return this.lastReacharoundResult;
    }

    /**
     * Returns whether the last reach around is vertical.
     *
     * @return {@code true} if the reach around is vertical
     */
    public boolean isLastReacharoundVertical() {
        return this.lastReacharoundVertical;
    }

    /**
     * Returns whether reacharound is available or not.
     *
     * @return {@code true} if reacharound is available, else {@code false}
     */
    public boolean isReacharoundAvailable() {
        return MidnightControlsFeature.HORIZONTAL_REACHAROUND.isAvailable() || MidnightControlsFeature.VERTICAL_REACHAROUND.isAvailable();
    }

    public static float getPlayerRange(@NotNull Minecraft client) {
        return client.player != null ? Double.valueOf(client.player.getAttributeValue(Attributes.BLOCK_INTERACTION_RANGE)).floatValue() : 0.f;
    }

    /**
     * Returns a nullable block hit result if vertical reach-around is possible.
     *
     * @return a block hit result if vertical reach-around is possible, else {@code null}
     */
    public @Nullable BlockHitResult tryVerticalReachAround() {
        if (!MidnightControlsFeature.VERTICAL_REACHAROUND.isAvailable())
            return null;
        if (client.player == null || client.level == null || client.hitResult == null || client.hitResult.getType() != HitResult.Type.MISS
                || !client.player.onGround() || client.player.getViewXRot(0.f) < 80.0F
                || client.player.isHandsBusy())
            return null;

        Vec3 pos = client.player.getEyePosition(1.0F);
        Vec3 rotationVec = client.player.getViewVector(1.0F);
        float range = getPlayerRange(client);
        var rayVec = pos.add(rotationVec.x * range, rotationVec.y * range, rotationVec.z * range).add(0, 0.75, 0);
        var result = client.level.clip(new ClipContext(pos, rayVec, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, client.player));

        if (result.getType() == HitResult.Type.BLOCK) {
            BlockPos blockPos = result.getBlockPos().below();
            BlockState state = client.level.getBlockState(blockPos);

            if (client.player.blockPosition().getY() - blockPos.getY() > 1 && (client.level.isEmptyBlock(blockPos) || state.canBeReplaced())) {
                return new BlockHitResult(result.getLocation(), Direction.DOWN, blockPos, false);
            }
        }

        return null;
    }

    /**
     * Returns a nullable block hit result if horizontal reach-around is possible.
     *
     * @return a block hit result if horizontal reach-around is possible
     */
    public @Nullable BlockHitResult tryHorizontalReachAround() {
        if (!MidnightControlsFeature.HORIZONTAL_REACHAROUND.isAvailable())
            return null;

        if (client.level != null && client.player != null && client.hitResult != null && client.hitResult.getType() == HitResult.Type.MISS
                && client.player.onGround() && client.player.getViewXRot(0.f) >= 35.f) {
            if (client.player.isHandsBusy())
                return null;
            // Temporary pos, do not use
            Vec3 playerPosi = client.player.position();

            // Imitates var playerPos = client.player.getBlockPos().down();
            Vec3 playerPos = new Vec3(playerPosi.x(), playerPosi.y() - 1.0, playerPosi.z());
            if (client.player.getY() - playerPos.y() - 1.0 >= 0.25) {
                // Imitates playerPos = playerPos.up();
                playerPos = playerPosi;
                this.onSlab = true;
            } else {
                this.onSlab = false;
            }
            var targetPos = new Vec3(client.hitResult.getLocation().x(), client.hitResult.getLocation().y(), client.hitResult.getLocation().z()).subtract(playerPos);
            var vector = new Vec3(Mth.clamp(targetPos.x(), -1, 1), 0, Mth.clamp(targetPos.z(), -1, 1));
            var blockPos = playerPos.add(vector);

            // Some functions still need BlockPos, so this is here to let that happen
            var blockyPos = BlockPos.containing(blockPos);

            var direction = client.player.getDirection();

            var state = client.level.getBlockState(blockyPos);
            if (!state.isAir())
                return null;
            var adjacentBlockState = client.level.getBlockState(blockyPos.relative(direction.getOpposite()));
            if (adjacentBlockState.isAir() || adjacentBlockState.getBlock() instanceof LiquidBlock || (vector.x() == 0 && vector.z() == 0)) {
                return null;
            }

            return new BlockHitResult(blockPos, direction, blockyPos, false);
        }
        return null;
    }

    public @NotNull BlockHitResult withSideForReacharound(@NotNull BlockHitResult result, @Nullable ItemStack stack) {
        if (stack == null || stack.isEmpty() || !(stack.getItem() instanceof BlockItem))
            return result;
        return withSideForReacharound(result, Block.byItem(stack.getItem()));
    }

    public @NotNull BlockHitResult withSideForReacharound(@NotNull BlockHitResult result, @NotNull Block block) {
        if (block instanceof SlabBlock) {
            if (this.onSlab) result = result.withDirection(Direction.UP);
            else result = result.withDirection(Direction.DOWN);
        }
        return result;
    }
}
