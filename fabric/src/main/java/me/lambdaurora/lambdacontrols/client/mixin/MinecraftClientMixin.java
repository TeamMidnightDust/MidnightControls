/*
 * Copyright © 2020 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of LambdaControls.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package me.lambdaurora.lambdacontrols.client.mixin;

import me.lambdaurora.lambdacontrols.LambdaControlsFeature;
import me.lambdaurora.lambdacontrols.client.LambdaControlsClient;
import net.minecraft.block.BlockState;
import net.minecraft.block.FluidBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin
{
    @Shadow
    @Nullable
    public HitResult crosshairTarget;

    @Shadow
    @Nullable
    public ClientPlayerEntity player;

    @Shadow
    @Nullable
    public ClientPlayerInteractionManager interactionManager;

    @Shadow
    @Nullable
    public ClientWorld world;

    @Shadow
    @Final
    public GameRenderer gameRenderer;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void lambdacontrols_onInit(CallbackInfo ci)
    {
        LambdaControlsClient.get().onMcInit((MinecraftClient) (Object) this);
    }

    @Inject(method = "render", at = @At("HEAD"))
    private void lambdacontrols_onRender(boolean fullRender, CallbackInfo ci)
    {
        LambdaControlsClient.get().onRender((MinecraftClient) (Object) (this));
    }

    @Inject(method = "disconnect(Lnet/minecraft/client/gui/screen/Screen;)V", at = @At("RETURN"))
    private void lambdacontrols_onLeave(@Nullable Screen screen, CallbackInfo ci)
    {
        LambdaControlsClient.get().onLeave();
    }

    @Inject(method = "doItemUse()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/hit/HitResult;getType()Lnet/minecraft/util/hit/HitResult$Type;"), locals = LocalCapture.CAPTURE_FAILEXCEPTION, cancellable = true)
    private void lambdacontrols_onItemUse(CallbackInfo ci, Hand[] hands, int handCount, int handIndex, Hand hand, ItemStack stackInHand)
    {
        if (!stackInHand.isEmpty() && this.player.pitch > 35.0F && LambdaControlsFeature.FRONT_BLOCK_PLACING.isAvailable()) {
            if (this.crosshairTarget != null && this.crosshairTarget.getType() == HitResult.Type.MISS && this.player.onGround) {
                if (!stackInHand.isEmpty() && stackInHand.getItem() instanceof BlockItem) {
                    BlockPos playerPos = this.player.getBlockPos().down();
                    BlockPos targetPos = new BlockPos(this.crosshairTarget.getPos()).subtract(playerPos);
                    BlockPos vector = new BlockPos(MathHelper.clamp(targetPos.getX(), -1, 1), 0, MathHelper.clamp(targetPos.getZ(), -1, 1));
                    BlockPos blockPos = playerPos.add(vector);

                    Direction direction = player.getHorizontalFacing();

                    BlockState adjacentBlockState = this.world.getBlockState(blockPos.offset(direction.getOpposite()));
                    if (adjacentBlockState.isAir() || adjacentBlockState.getBlock() instanceof FluidBlock || (vector.getX() == 0 && vector.getZ() == 0)) {
                        return;
                    }

                    BlockHitResult hitResult = new BlockHitResult(this.crosshairTarget.getPos(), direction.getOpposite(), blockPos, false);

                    int previousStackCount = stackInHand.getCount();
                    ActionResult result = this.interactionManager.interactBlock(this.player, this.world, hand, hitResult);
                    if (result.isAccepted()) {
                        if (result.shouldSwingHand()) {
                            this.player.swingHand(hand);
                            if (!stackInHand.isEmpty() && (stackInHand.getCount() != previousStackCount || this.interactionManager.hasCreativeInventory())) {
                                this.gameRenderer.firstPersonRenderer.resetEquipProgress(hand);
                            }
                        }

                        ci.cancel();
                    }

                    if (result == ActionResult.FAIL) {
                        ci.cancel();
                    }
                }
            }
        }
    }
}
