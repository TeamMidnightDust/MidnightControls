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
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.input.Input;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.MovementType;
import net.minecraft.network.encryption.PlayerPublicKey;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Injects the anti fly drifting feature.
 */
@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin extends AbstractClientPlayerEntity {
    private boolean midnightcontrols$driftingPrevented = false;

    public ClientPlayerEntityMixin(ClientWorld world, GameProfile profile) {
        super(world, profile);
    }

    @Shadow
    protected abstract boolean hasMovementInput();

    @Shadow
    @Final
    protected MinecraftClient client;

    @Shadow
    public Input input;

    @Shadow
    protected abstract boolean isCamera();

    @Shadow protected int ticksLeftToDoubleTapSprint;


    @Inject(method = "move(Lnet/minecraft/entity/MovementType;Lnet/minecraft/util/math/Vec3d;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/AbstractClientPlayerEntity;move(Lnet/minecraft/entity/MovementType;Lnet/minecraft/util/math/Vec3d;)V"))
    public void onMove(MovementType type, Vec3d movement, CallbackInfo ci) {
        if (!MidnightControlsConfig.doubleTapToSprint) ticksLeftToDoubleTapSprint = 0;
        if (!MidnightControls.isExtrasLoaded) return;
        if (type == MovementType.SELF) {
            if (this.getAbilities().flying && (!MidnightControlsConfig.flyDrifting || !MidnightControlsConfig.verticalFlyDrifting)) {
                if (!this.hasMovementInput()) {
                    if (!this.midnightcontrols$driftingPrevented) {
                        if (!MidnightControlsConfig.flyDrifting)
                            this.setVelocity(this.getVelocity().multiply(0, 1.0, 0));
                    }
                    this.midnightcontrols$driftingPrevented = true;
                } else
                    this.midnightcontrols$driftingPrevented = false;
            }
        }
    }

    @Inject(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/input/Input;tick(ZF)V", shift = At.Shift.AFTER))
    public void onInputUpdate(CallbackInfo ci) {
        MovementHandler.HANDLER.applyMovement((ClientPlayerEntity) (Object) this);
    }

    @Inject(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isCamera()Z"))
    public void onTickMovement(CallbackInfo ci) {
        if (this.getAbilities().flying && this.isCamera()) {
            if (MidnightControlsConfig.verticalFlyDrifting || !MidnightControls.isExtrasLoaded)
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
