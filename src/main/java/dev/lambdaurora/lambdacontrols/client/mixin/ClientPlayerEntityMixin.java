/*
 * Copyright © 2020 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of LambdaControls.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package dev.lambdaurora.lambdacontrols.client.mixin;

import com.mojang.authlib.GameProfile;
import dev.lambdaurora.lambdacontrols.client.LambdaControlsClient;
import dev.lambdaurora.lambdacontrols.client.controller.MovementHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.input.Input;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.MovementType;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Injects the anti fly drifting feature.
 */
@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin extends AbstractClientPlayerEntity {
    private boolean lambdacontrols$driftingPrevented = false;

    @Shadow
    protected abstract boolean hasMovementInput();

    @Shadow
    @Final
    protected MinecraftClient client;

    @Shadow
    public Input input;

    @Shadow
    protected abstract boolean isCamera();

    public ClientPlayerEntityMixin(ClientWorld world, GameProfile profile) {
        super(world, profile);
    }

    @Inject(method = "move(Lnet/minecraft/entity/MovementType;Lnet/minecraft/util/math/Vec3d;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/AbstractClientPlayerEntity;move(Lnet/minecraft/entity/MovementType;Lnet/minecraft/util/math/Vec3d;)V"))
    public void onMove(MovementType type, Vec3d movement, CallbackInfo ci) {
        LambdaControlsClient mod = LambdaControlsClient.get();
        if (type == MovementType.SELF) {
            if (this.abilities.flying && (!mod.config.hasFlyDrifting() || !mod.config.hasFlyVerticalDrifting())) {
                if (!this.hasMovementInput()) {
                    if (!this.lambdacontrols$driftingPrevented) {
                        if (!mod.config.hasFlyDrifting())
                            this.setVelocity(this.getVelocity().multiply(0, 1.0, 0));
                    }
                    this.lambdacontrols$driftingPrevented = true;
                } else
                    this.lambdacontrols$driftingPrevented = false;
            }
        }
    }

    @Inject(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/input/Input;tick(Z)V", shift = At.Shift.AFTER))
    public void onInputUpdate(CallbackInfo ci) {
        MovementHandler.HANDLER.applyMovement((ClientPlayerEntity) (Object) this);
    }

    @Inject(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isCamera()Z"))
    public void onTickMovement(CallbackInfo ci) {
        if (this.abilities.flying && this.isCamera()) {
            if (LambdaControlsClient.get().config.hasFlyVerticalDrifting())
                return;
            int moving = 0;
            if (this.input.sneaking) {
                --moving;
            }

            if (this.input.jumping) {
                ++moving;
            }

            if (moving == 0) {
                this.setVelocity(this.getVelocity().multiply(1.0, 0.0, 1.0));
            }
        }
    }
}
