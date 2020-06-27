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
import me.lambdaurora.lambdacontrols.client.LambdaInput;
import me.lambdaurora.lambdacontrols.client.gui.LambdaControlsRenderer;
import me.lambdaurora.lambdacontrols.client.util.FrontBlockPlaceResultAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin implements FrontBlockPlaceResultAccessor
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

    @Shadow
    private int itemUseCooldown;

    private BlockHitResult lambdacontrols_frontBlockPlaceResult = null;

    private BlockPos  lambdacontrols_lastTargetPos;
    private Vec3d     lambdacontrols_lastPos;
    private Direction lambdacontrols_lastTargetSide;

    @Override
    public @Nullable BlockHitResult lambdacontrols_getFrontBlockPlaceResult()
    {
        return this.lambdacontrols_frontBlockPlaceResult;
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onInit(CallbackInfo ci)
    {
        LambdaControlsClient.get().onMcInit((MinecraftClient) (Object) this);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void onStartTick(CallbackInfo ci)
    {
        if (this.player == null)
            return;
        this.lambdacontrols_frontBlockPlaceResult = LambdaInput.tryFrontPlace(((MinecraftClient) (Object) this));
        if (!LambdaControlsFeature.FAST_BLOCK_PLACING.isAvailable())
            return;
        if (this.lambdacontrols_lastPos == null)
            this.lambdacontrols_lastPos = this.player.getPos();

        int cooldown = this.itemUseCooldown;
        BlockHitResult hitResult;
        if (this.crosshairTarget != null && this.crosshairTarget.getType() == HitResult.Type.BLOCK && this.player.abilities.flying) {
            hitResult = (BlockHitResult) this.crosshairTarget;
            BlockPos targetPos = hitResult.getBlockPos();
            Direction side = hitResult.getSide();

            boolean sidewaysBlockPlacing = this.lambdacontrols_lastTargetPos == null || !targetPos.equals(this.lambdacontrols_lastTargetPos.offset(this.lambdacontrols_lastTargetSide));
            boolean backwardsBlockPlacing = this.player.input.movementForward < 0.0f && (this.lambdacontrols_lastTargetPos == null || targetPos.equals(this.lambdacontrols_lastTargetPos.offset(this.lambdacontrols_lastTargetSide)));

            if (cooldown > 1
                    && !targetPos.equals(this.lambdacontrols_lastTargetPos)
                    && (sidewaysBlockPlacing || backwardsBlockPlacing)) {
                this.itemUseCooldown = 1;
            }

            this.lambdacontrols_lastTargetPos = targetPos.toImmutable();
            this.lambdacontrols_lastTargetSide = side;
        }
        // Removed front placing sprinting as way too cheaty.
        /* else if (this.player.isSprinting()) {
            hitResult = this.lambdacontrols_frontBlockPlaceResult;
            if (hitResult != null) {
                if (cooldown > 0)
                    this.itemUseCooldown = 0;
            }
        } */
        this.lambdacontrols_lastPos = this.player.getPos();
    }

    @Inject(method = "render", at = @At("HEAD"))
    private void onRender(boolean fullRender, CallbackInfo ci)
    {
        LambdaControlsClient.get().onRender((MinecraftClient) (Object) (this));
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/GameRenderer;render(FJZ)V", shift = At.Shift.AFTER))
    private void renderVirtualCursor(boolean fullRender, CallbackInfo ci) {
        LambdaControlsRenderer.renderVirtualCursor(new MatrixStack(), (MinecraftClient) (Object) this);
    }

    @Inject(method = "disconnect(Lnet/minecraft/client/gui/screen/Screen;)V", at = @At("RETURN"))
    private void onLeave(@Nullable Screen screen, CallbackInfo ci)
    {
        LambdaControlsClient.get().onLeave();
    }

    @Inject(method = "doItemUse()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/hit/HitResult;getType()Lnet/minecraft/util/hit/HitResult$Type;"), locals = LocalCapture.CAPTURE_FAILEXCEPTION, cancellable = true)
    private void onItemUse(CallbackInfo ci, Hand[] hands, int handCount, int handIndex, Hand hand, ItemStack stackInHand)
    {
        if (!stackInHand.isEmpty() && this.player.pitch > 35.0F && LambdaControlsFeature.FRONT_BLOCK_PLACING.isAvailable()) {
            if (this.crosshairTarget != null && this.crosshairTarget.getType() == HitResult.Type.MISS && this.player.isOnGround()) {
                if (!stackInHand.isEmpty() && stackInHand.getItem() instanceof BlockItem) {
                    BlockHitResult hitResult = LambdaInput.tryFrontPlace(((MinecraftClient) (Object) this));

                    if (hitResult == null)
                        return;

                    hitResult = LambdaInput.withSideForFrontPlace(hitResult, stackInHand);

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
