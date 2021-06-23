/*
 * Copyright © 2021 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of LambdaControls.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package dev.lambdaurora.lambdacontrols.client.mixin;

import dev.lambdaurora.lambdacontrols.ControlsMode;
import dev.lambdaurora.lambdacontrols.client.LambdaControlsClient;
import dev.lambdaurora.lambdacontrols.client.compat.LambdaControlsCompat;
import dev.lambdaurora.lambdacontrols.client.gui.LambdaControlsRenderer;
import dev.lambdaurora.lambdacontrols.client.util.HandledScreenAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Represents the mixin for the class ContainerScreen.
 */
@Mixin(HandledScreen.class)
public abstract class HandledScreenMixin implements HandledScreenAccessor {
    @Accessor("x")
    public abstract int getX();

    @Accessor("y")
    public abstract int getY();

    @Invoker("getSlotAt")
    public abstract Slot lambdacontrols$getSlotAt(double posX, double posY);

    @Invoker("isClickOutsideBounds")
    public abstract boolean lambdacontrols$isClickOutsideBounds(double mouseX, double mouseY, int x, int y, int button);

    @Invoker("onMouseClick")
    public abstract void lambdacontrols$onMouseClick(@Nullable Slot slot, int slotId, int clickData, SlotActionType actionType);

    @Inject(method = "render", at = @At("RETURN"))
    public void onRender(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (LambdaControlsClient.get().config.getControlsMode() == ControlsMode.CONTROLLER) {
            var client = MinecraftClient.getInstance();
            int x = 2, y = client.getWindow().getScaledHeight() - 2 - LambdaControlsRenderer.ICON_SIZE;

            x = LambdaControlsRenderer.drawButtonTip(matrices, x, y, new int[]{GLFW.GLFW_GAMEPAD_BUTTON_A}, "lambdacontrols.action.pickup_all", true, client) + 2;
            x = LambdaControlsRenderer.drawButtonTip(matrices, x, y, new int[]{GLFW.GLFW_GAMEPAD_BUTTON_B}, "lambdacontrols.action.exit", true, client) + 2;
            if (LambdaControlsCompat.isReiPresent()) {
                x = 2;
                y -= 24;
            }
            x = LambdaControlsRenderer.drawButtonTip(matrices, x, y, new int[]{GLFW.GLFW_GAMEPAD_BUTTON_X}, "lambdacontrols.action.pickup", true, client) + 2;
            LambdaControlsRenderer.drawButtonTip(matrices, x, y, new int[]{GLFW.GLFW_GAMEPAD_BUTTON_Y}, "lambdacontrols.action.quick_move", true, client);
        }
    }
}
