/*
 * Copyright © 2021 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of midnightcontrols.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package eu.midnightdust.midnightcontrols.client.mixin;

import eu.midnightdust.midnightcontrols.MidnightControlsFeature;
import eu.midnightdust.midnightcontrols.client.MidnightControlsClient;
import eu.midnightdust.midnightcontrols.client.MidnightControlsConfig;
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
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import static eu.midnightdust.midnightcontrols.client.MidnightControlsClient.reacharound;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {
    @Shadow @Nullable public HitResult crosshairTarget;

    @Shadow @Nullable public ClientPlayerEntity player;

    @Shadow @Nullable public ClientPlayerInteractionManager interactionManager;

    @Shadow @Final public GameRenderer gameRenderer;

    @Shadow private int itemUseCooldown;

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
            this.midnightcontrols$lastPos = this.player.getPos();

        int cooldown = this.itemUseCooldown;
        BlockHitResult hitResult;
        if (this.crosshairTarget != null && this.crosshairTarget.getType() == HitResult.Type.BLOCK && this.player.getAbilities().flying) {
            hitResult = (BlockHitResult) this.crosshairTarget;
            var targetPos = hitResult.getBlockPos();
            var side = hitResult.getSide();

            boolean sidewaysBlockPlacing = this.midnightcontrols$lastTargetPos == null || !targetPos.equals(this.midnightcontrols$lastTargetPos.offset(this.midnightcontrols$lastTargetSide));
            boolean backwardsBlockPlacing = this.player.input.movementForward < 0.0f && (this.midnightcontrols$lastTargetPos == null || targetPos.equals(this.midnightcontrols$lastTargetPos.offset(this.midnightcontrols$lastTargetSide)));

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
        this.midnightcontrols$lastPos = this.player.getPos();
    }

    @Inject(at = @At("TAIL"), method = "setScreen")
    private void setScreen(Screen screen, CallbackInfo info) {
        if (MidnightControlsConfig.hideNormalMouse){
            if (screen != null) GLFW.glfwSetInputMode(MinecraftClient.getInstance().getWindow().getHandle(), GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_HIDDEN);
            else GLFW.glfwSetInputMode(MinecraftClient.getInstance().getWindow().getHandle(), GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_DISABLED);
        }
    }

    @Inject(method = "doItemUse()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/hit/HitResult;getType()Lnet/minecraft/util/hit/HitResult$Type;"), locals = LocalCapture.CAPTURE_FAILEXCEPTION, cancellable = true)
    private void onItemUse(CallbackInfo ci, Hand[] hands, int handCount, int handIndex, Hand hand, ItemStack stackInHand) {
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
    // This is always supposed to be located at before the line 'this.profiler.swap("Keybindings");'
//    @Redirect(method = "tick", at = @At(value = "FIELD",target = "Lnet/minecraft/client/MinecraftClient;currentScreen:Lnet/minecraft/client/gui/screen/Screen;", ordinal = 6))
//    private Screen midnightcontrols$ignoreTouchOverlay(MinecraftClient instance) {
//        if (instance.currentScreen instanceof TouchscreenOverlay) return null;
//        return instance.currentScreen;
//    }
}
