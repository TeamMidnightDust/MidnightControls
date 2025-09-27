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
import eu.midnightdust.midnightcontrols.MidnightControlsFeature;
import eu.midnightdust.midnightcontrols.client.MidnightControlsClient;
import eu.midnightdust.midnightcontrols.client.MidnightControlsConfig;
import eu.midnightdust.midnightcontrols.client.touch.gui.TouchscreenOverlay;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.profiler.Profiler;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static eu.midnightdust.midnightcontrols.client.MidnightControlsClient.client;
import static eu.midnightdust.midnightcontrols.client.MidnightControlsClient.reacharound;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {
    @Shadow @Nullable public HitResult crosshairTarget;

    @Shadow @Nullable public ClientPlayerEntity player;

    @Shadow @Nullable public ClientPlayerInteractionManager interactionManager;

    @Shadow @Final public GameRenderer gameRenderer;

    @Shadow private int itemUseCooldown;

    @Shadow public abstract void setScreen(Screen screen);

    @Shadow public int attackCooldown;

    @Shadow protected abstract void handleInputEvents();

    @Unique private BlockPos midnightcontrols$lastTargetPos;
    @Unique private Vec3d midnightcontrols$lastPos;
    @Unique private Direction midnightcontrols$lastTargetSide;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onInit(CallbackInfo ci) {
        MidnightControlsClient.onMcInit((MinecraftClient) (Object) this);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void onStartTick(CallbackInfo ci) {
        if (this.player == null)
            return;

        if (!MidnightControlsFeature.FAST_BLOCK_PLACING.isAvailable())
            return;
        if (this.midnightcontrols$lastPos == null)
            this.midnightcontrols$lastPos = this.player.getEntityPos();

        int cooldown = this.itemUseCooldown;
        BlockHitResult hitResult;
        if (this.crosshairTarget != null && this.crosshairTarget.getType() == HitResult.Type.BLOCK && this.player.getAbilities().flying) {
            hitResult = (BlockHitResult) this.crosshairTarget;
            var targetPos = hitResult.getBlockPos();
            var side = hitResult.getSide();

            boolean sidewaysBlockPlacing = this.midnightcontrols$lastTargetPos == null || !targetPos.equals(this.midnightcontrols$lastTargetPos.offset(this.midnightcontrols$lastTargetSide));
            boolean backwardsBlockPlacing = this.player.input.getMovementInput().y < 0.0f && (this.midnightcontrols$lastTargetPos == null || targetPos.equals(this.midnightcontrols$lastTargetPos.offset(this.midnightcontrols$lastTargetSide)));

            if (cooldown > 1
                    && !targetPos.equals(this.midnightcontrols$lastTargetPos)
                    && (sidewaysBlockPlacing || backwardsBlockPlacing)) {
                this.itemUseCooldown = 1;
            }

            this.midnightcontrols$lastTargetPos = targetPos.toImmutable();
            this.midnightcontrols$lastTargetSide = side;
        }
        // Removed front placing sprinting as way too cheaty.
//        else if (this.player.isSprinting()) {
//            hitResult = MidnightControlsClient.get().reacharound.getLastReacharoundResult();
//            if (hitResult != null) {
//                if (cooldown > 0)
//                    this.itemUseCooldown = 0;
//            }
//        }
        this.midnightcontrols$lastPos = this.player.getEntityPos();
    }

    @Inject(at = @At("TAIL"), method = "setScreen")
    private void setScreen(Screen screen, CallbackInfo info) {
        if (MidnightControlsConfig.hideNormalMouse){
            if (screen != null && !(screen instanceof TouchscreenOverlay)) GLFW.glfwSetInputMode(MinecraftClient.getInstance().getWindow().getHandle(), GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_HIDDEN);
            else GLFW.glfwSetInputMode(MinecraftClient.getInstance().getWindow().getHandle(), GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_DISABLED);
        }
        MidnightControlsClient.onScreenOpen(screen);
    }

    @Inject(method = "doItemUse()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/hit/HitResult;getType()Lnet/minecraft/util/hit/HitResult$Type;"), cancellable = true)
    private void onItemUse(CallbackInfo ci, @Local Hand hand, @Local ItemStack stackInHand) {
        if (player != null && !stackInHand.isEmpty() && this.player.getPitch(0.f) > 35.0F && reacharound.isReacharoundAvailable()) {
            if (this.crosshairTarget != null && this.crosshairTarget.getType() == HitResult.Type.MISS && this.player.isOnGround()) {
                if (!stackInHand.isEmpty() && stackInHand.getItem() instanceof BlockItem) {
                    var hitResult = reacharound.getLastReacharoundResult();

                    if (hitResult == null || this.interactionManager == null)
                        return;

                    hitResult = reacharound.withSideForReacharound(hitResult, stackInHand);

                    int previousStackCount = stackInHand.getCount();
                    var result = this.interactionManager.interactBlock(this.player, hand, hitResult);
                    if (result.isAccepted()) {
                        //if (result.shouldSwingHand()) {
                            this.player.swingHand(hand);
                            if (!stackInHand.isEmpty() && (stackInHand.getCount() != previousStackCount || this.player.isInCreativeMode())) {
                                this.gameRenderer.firstPersonRenderer.resetEquipProgress(hand);
                            }
                        //}

                        ci.cancel();
                    }

                    if (result == ActionResult.FAIL) {
                        ci.cancel();
                    }
                }
            }
        }
    }
    // TODO: Replace this with MixinExtras' Expressions once that's officially released
    @Inject(method = "tick", at = @At(value = "INVOKE",target = "Lnet/minecraft/client/gui/hud/DebugHud;shouldShowDebugHud()Z"))
    private void midnightcontrols$handleKeybindsWithTouchOverlay(CallbackInfo ci, @Local Profiler profiler) {
        if (client.currentScreen instanceof TouchscreenOverlay) {
            profiler.swap("Keybindings");
            this.handleInputEvents();
            if (this.attackCooldown > 0) {
                --this.attackCooldown;
            }
        }
    }

    // Needed, as it will cause item actions not to work in touchscreen mode otherwise with the above method
    @Inject(method = "handleInputEvents", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isUsingItem()Z"), cancellable = true)
    private void midnightcontrols$dontHandleItemAndBlockInteractions(CallbackInfo ci) {
        if (client.currentScreen instanceof TouchscreenOverlay) ci.cancel();
    }
}
