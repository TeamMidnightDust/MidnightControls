/*
 * Copyright © 2020 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of LambdaControls.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package me.lambdaurora.lambdacontrols.client.mixin;

import me.lambdaurora.lambdacontrols.ControlsMode;
import me.lambdaurora.lambdacontrols.client.LambdaControlsClient;
import me.lambdaurora.lambdacontrols.client.util.ContainerScreenAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.ContainerScreen;
import net.minecraft.container.Slot;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Represents the mixin for the class ContainerScreen.
 */
@Mixin(ContainerScreen.class)
public abstract class ContainerScreenMixin implements ContainerScreenAccessor
{
    protected int x;
    protected int y;

    @Override
    public int lambdacontrols_getX()
    {
        return this.x;
    }

    @Override
    public int lambdacontrols_getY()
    {
        return this.y;
    }

    @Invoker("getSlotAt")
    public abstract Slot lambdacontrols_getSlotAt(double posX, double posY);

    @Inject(method = "render", at = @At("RETURN"))
    public void render(int mouseX, int mouseY, float delta, CallbackInfo ci)
    {
        if (LambdaControlsClient.get().config.getControlsMode() == ControlsMode.CONTROLLER) {
            MinecraftClient client = MinecraftClient.getInstance();
            int x = 10, y = client.getWindow().getScaledHeight() - 10 - 15;

            x += LambdaControlsClient.drawButtonTip(x, y, new int[]{GLFW.GLFW_GAMEPAD_BUTTON_A}, "lambdacontrols.action.pickup_all", true, client) + 10;
            x += LambdaControlsClient.drawButtonTip(x, y, new int[]{GLFW.GLFW_GAMEPAD_BUTTON_B}, "lambdacontrols.action.exit", true, client) + 10;
            x += LambdaControlsClient.drawButtonTip(x, y, new int[]{GLFW.GLFW_GAMEPAD_BUTTON_X}, "lambdacontrols.action.pickup", true, client) + 10;
            LambdaControlsClient.drawButtonTip(x, y, new int[]{GLFW.GLFW_GAMEPAD_BUTTON_Y}, "lambdacontrols.action.quick_move", true, client);
        }
    }
}
