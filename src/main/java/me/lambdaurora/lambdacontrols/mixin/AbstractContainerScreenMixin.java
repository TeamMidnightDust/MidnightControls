/*
 * Copyright © 2019 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of LambdaControls.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package me.lambdaurora.lambdacontrols.mixin;

import me.lambdaurora.lambdacontrols.ControlsMode;
import me.lambdaurora.lambdacontrols.LambdaControls;
import me.lambdaurora.lambdacontrols.util.AbstractContainerScreenAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.AbstractContainerScreen;
import net.minecraft.container.Slot;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Represents the mixin for the class AbstractContainerScreen.
 */
@Mixin(AbstractContainerScreen.class)
public abstract class AbstractContainerScreenMixin implements AbstractContainerScreenAccessor
{
    @Shadow
    protected int left;

    @Shadow
    protected int top;

    @Shadow
    protected abstract Slot getSlotAt(double xPosition, double yPosition);

    @Override
    public int get_left()
    {
        return this.left;
    }

    @Override
    public int get_top()
    {
        return this.top;
    }

    @Override
    public Slot get_slot_at(double pos_x, double pos_y)
    {
        return this.getSlotAt(pos_x, pos_y);
    }

    @Inject(method = "render", at = @At("RETURN"))
    public void render(int mouseX, int mouseY, float delta, CallbackInfo ci)
    {
        if (LambdaControls.get().config.get_controls_mode() == ControlsMode.CONTROLLER) {
            MinecraftClient client = MinecraftClient.getInstance();
            int x = 10, y = client.window.getScaledHeight() - 10 - 15;

            x += LambdaControls.draw_button_tip(x, y, GLFW.GLFW_GAMEPAD_BUTTON_A, "lambdacontrols.action.pickup_all", true, client) + 10;
            x += LambdaControls.draw_button_tip(x, y, GLFW.GLFW_GAMEPAD_BUTTON_B, "lambdacontrols.action.exit", true, client) + 10;
            x += LambdaControls.draw_button_tip(x, y, GLFW.GLFW_GAMEPAD_BUTTON_X, "lambdacontrols.action.pickup", true, client) + 10;
            LambdaControls.draw_button_tip(x, y, GLFW.GLFW_GAMEPAD_BUTTON_Y, "lambdacontrols.action.quick_move", true, client);
        }
    }
}
