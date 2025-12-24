/*
 * Copyright © 2021 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of midnightcontrols.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package eu.midnightdust.midnightcontrols.client.mixin;

import com.mojang.authlib.GameProfile;
import eu.midnightdust.midnightcontrols.MidnightControls;
import eu.midnightdust.midnightcontrols.client.MidnightControlsClient;
import eu.midnightdust.midnightcontrols.client.MidnightControlsConfig;
import eu.midnightdust.midnightcontrols.client.controller.MovementHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.ClientInput;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Injects the anti fly drifting feature.
 */
@Mixin(LocalPlayer.class)
public abstract class ClientPlayerEntityMixin extends AbstractClientPlayer {
    @Unique private boolean midnightcontrols$driftingPrevented = false;

    public ClientPlayerEntityMixin(ClientLevel world, GameProfile profile) {
        super(world, profile);
    }

    @Shadow
    protected abstract boolean isMoving();

    @Shadow
    @Final
    protected Minecraft minecraft;

    @Shadow
    public ClientInput input;

    @Shadow
    protected abstract boolean isControlledCamera();


    @Inject(method = "move(Lnet/minecraft/world/entity/MoverType;Lnet/minecraft/world/phys/Vec3;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/AbstractClientPlayer;move(Lnet/minecraft/world/entity/MoverType;Lnet/minecraft/world/phys/Vec3;)V"))
    public void onMove(MoverType type, Vec3 movement, CallbackInfo ci) {
        if (!MidnightControls.isExtrasLoaded) return;
        if (type == MoverType.SELF) {
            if (this.getAbilities().flying && (!MidnightControlsConfig.flyDrifting || !MidnightControlsConfig.verticalFlyDrifting)) {
                if (!this.isMoving()) {
                    if (!this.midnightcontrols$driftingPrevented) {
                        if (!MidnightControlsConfig.flyDrifting)
                            this.setDeltaMovement(this.getDeltaMovement().multiply(0, 1.0, 0));
                    }
                    this.midnightcontrols$driftingPrevented = true;
                } else
                    this.midnightcontrols$driftingPrevented = false;
            }
        }
    }

    @Inject(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/ClientInput;tick()V", shift = At.Shift.AFTER))
    public void onInputUpdate(CallbackInfo ci) {
        MovementHandler.HANDLER.applyMovement((LocalPlayer) (Object) this);
    }

    @Inject(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;isControlledCamera()Z"))
    public void onTickMovement(CallbackInfo ci) {
        if (this.getAbilities().flying && this.isControlledCamera()) {
            if (MidnightControlsConfig.verticalFlyDrifting || !MidnightControls.isExtrasLoaded)
                return;
            int moving = 0;
            if (this.input.keyPresses.shift()) {
                --moving;
            }

            if (this.input.keyPresses.jump()) {
                ++moving;
            }

            if (moving == 0) {
                this.setDeltaMovement(this.getDeltaMovement().multiply(1.0, 0.0, 1.0));
            }
        }
    }
}
