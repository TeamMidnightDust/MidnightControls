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
import eu.midnightdust.midnightcontrols.client.touch.gui.TouchscreenOverlay;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static eu.midnightdust.midnightcontrols.client.MidnightControlsClient.client;
import static eu.midnightdust.midnightcontrols.client.MidnightControlsClient.reacharound;

//? if < 26.2 {
/*import net.minecraft.client.gui.screens.Screen;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
*///?}

@Mixin(Minecraft.class)
public abstract class MinecraftClientMixin {
    @Shadow @Nullable public HitResult hitResult;

    @Shadow @Nullable public LocalPlayer player;

    @Shadow @Nullable public MultiPlayerGameMode gameMode;

    @Shadow @Final public GameRenderer gameRenderer;

    @Shadow private int rightClickDelay;

    @Shadow public int missTime;

    @Shadow protected abstract void handleKeybinds();

    @Unique private BlockPos midnightcontrols$lastTargetPos;
    @Unique private Vec3 midnightcontrols$lastPos;
    @Unique private Direction midnightcontrols$lastTargetSide;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onInit(CallbackInfo ci) {
        MidnightControlsClient.onMcInit((Minecraft) (Object) this);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void onStartTick(CallbackInfo ci) {
        if (this.player == null)
            return;

        if (!MidnightControlsFeature.FAST_BLOCK_PLACING.isAvailable())
            return;
        if (this.midnightcontrols$lastPos == null)
            this.midnightcontrols$lastPos = this.player.position();

        int cooldown = this.rightClickDelay;
        BlockHitResult hitResult;
        if (this.hitResult != null && this.hitResult.getType() == HitResult.Type.BLOCK && this.player.getAbilities().flying) {
            hitResult = (BlockHitResult) this.hitResult;
            var targetPos = hitResult.getBlockPos();
            var side = hitResult.getDirection();

            boolean sidewaysBlockPlacing = this.midnightcontrols$lastTargetPos == null || !targetPos.equals(this.midnightcontrols$lastTargetPos.relative(this.midnightcontrols$lastTargetSide));
            boolean backwardsBlockPlacing = this.player.input.getMoveVector().y < 0.0f && (this.midnightcontrols$lastTargetPos == null || targetPos.equals(this.midnightcontrols$lastTargetPos.relative(this.midnightcontrols$lastTargetSide)));

            if (cooldown > 1
                    && !targetPos.equals(this.midnightcontrols$lastTargetPos)
                    && (sidewaysBlockPlacing || backwardsBlockPlacing)) {
                this.rightClickDelay = 1;
            }

            this.midnightcontrols$lastTargetPos = targetPos.immutable();
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
        this.midnightcontrols$lastPos = this.player.position();
    }

    //? if < 26.2 {
    /*@WrapMethod(method = "setScreen")
    private void setScreen(Screen screen, Operation<Void> original) {
        MidnightControlsClient.onScreenOpen(screen, original);
    }
    *///?}


    @Inject(method = "startUseItem()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/HitResult;getType()Lnet/minecraft/world/phys/HitResult$Type;"), cancellable = true)
    private void onItemUse(CallbackInfo ci, @Local InteractionHand hand, @Local ItemStack stackInHand) {
        if (player != null && !stackInHand.isEmpty() && this.player.getViewXRot(0.f) > 35.0F && reacharound.isReacharoundAvailable()) {
            if (this.hitResult != null && this.hitResult.getType() == HitResult.Type.MISS && this.player.onGround()) {
                if (!stackInHand.isEmpty() && stackInHand.getItem() instanceof BlockItem) {
                    var hitResult = reacharound.getLastReacharoundResult();

                    if (hitResult == null || this.gameMode == null)
                        return;

                    hitResult = reacharound.withSideForReacharound(hitResult, stackInHand);

                    int previousStackCount = stackInHand.getCount();
                    var result = this.gameMode.useItemOn(this.player, hand, hitResult);
                    if (result.consumesAction()) {
                        //if (result.shouldSwingHand()) {
                            this.player.swing(hand);
                            if (!stackInHand.isEmpty() && (stackInHand.getCount() != previousStackCount || this.player.hasInfiniteMaterials())) {
                                this.gameRenderer.itemInHandRenderer.itemUsed(hand);
                            }
                        //}

                        ci.cancel();
                    }

                    if (result == InteractionResult.FAIL) {
                        ci.cancel();
                    }
                }
            }
        }
    }

    //~ if >= 26.2 'Lnet/minecraft/client/gui/components/DebugScreenOverlay;showDebugScreen()Z' -> 'Lnet/minecraft/client/gui/Gui;tick()V'
    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;tick()V"))
    private void midnightcontrols$handleKeybindsWithTouchOverlay(CallbackInfo ci, @Local ProfilerFiller profiler) {
        //~ if >= 26.2 'client.screen' -> 'client.gui.screen()'
        if (client.gui.screen() instanceof TouchscreenOverlay) {
            profiler.popPush("Keybindings");
            this.handleKeybinds();
            if (this.missTime > 0) {
                --this.missTime;
            }
        }
    }

    // Needed, as it will cause item actions not to work in touchscreen mode otherwise with the above method
    @Inject(method = "handleKeybinds", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;isUsingItem()Z"), cancellable = true)
    private void midnightcontrols$dontHandleItemAndBlockInteractions(CallbackInfo ci) {
        //~ if >= 26.2 'client.screen' -> 'client.gui.screen()'
        if (client.gui.screen() instanceof TouchscreenOverlay) ci.cancel();
    }
}
