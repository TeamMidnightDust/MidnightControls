/*
 * Copyright © 2020 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of LambdaControls.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package me.lambdaurora.lambdacontrols.client.mixin;

import me.lambdaurora.lambdacontrols.LambdaControls;
import me.lambdaurora.lambdacontrols.client.LambdaControlsClient;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin
{
    @Inject(method = "onGameJoin", at = @At(value = "TAIL"))
    private void onGameJoin(GameJoinS2CPacket packet, CallbackInfo ci)
    {
        ClientSidePacketRegistry.INSTANCE.sendToServer(LambdaControls.HELLO_CHANNEL, LambdaControls.get().makeHello(LambdaControlsClient.get().config.getControlsMode()));
        ClientSidePacketRegistry.INSTANCE.sendToServer(LambdaControls.CONTROLS_MODE_CHANNEL,
                LambdaControls.get().makeControlsModeBuffer(LambdaControlsClient.get().config.getControlsMode()));
    }
}
