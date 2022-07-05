/*
 * Copyright © 2021 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of midnightcontrols.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package eu.midnightdust.midnightcontrols.client.mixin;

import eu.midnightdust.midnightcontrols.ControlsMode;
import eu.midnightdust.midnightcontrols.client.MidnightControlsConfig;
import eu.midnightdust.midnightcontrols.client.MidnightInput;
import eu.midnightdust.midnightcontrols.client.compat.MidnightControlsCompat;
import eu.midnightdust.midnightcontrols.client.controller.ButtonBinding;
import eu.midnightdust.midnightcontrols.client.gui.MidnightControlsRenderer;
import eu.midnightdust.midnightcontrols.client.util.HandledScreenAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
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
    @Unique private static float scale = 1f;

    @Accessor("x")
    public abstract int getX();

    @Accessor("y")
    public abstract int getY();

    @Invoker("getSlotAt")
    public abstract Slot midnightcontrols$getSlotAt(double posX, double posY);

    @Invoker("isClickOutsideBounds")
    public abstract boolean midnightcontrols$isClickOutsideBounds(double mouseX, double mouseY, int x, int y, int button);


    @Invoker("onMouseClick")
    public abstract void midnightcontrols$onMouseClick(@Nullable Slot slot, int slotId, int clickData, SlotActionType actionType);

    @Inject(method = "render", at = @At("RETURN"))
    public void onRender(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (MidnightControlsConfig.controlsMode == ControlsMode.CONTROLLER) {
            var client = MinecraftClient.getInstance();
            if (client.options.guiScale >= 4) {
                scale = 0.75f;
            } else scale = 1f;
            if (scale != 1f) matrices.scale(scale,scale,scale);
            int x = 2, y = (int) (client.getWindow().getScaledHeight() * (1 / scale) - 2 - MidnightControlsRenderer.ICON_SIZE);
            if (MidnightControlsCompat.isEMIPresent()) {
                x += 40 * (1 / scale);
            }
            x = MidnightControlsRenderer.drawButtonTip(matrices, x, y, new int[]{GLFW.GLFW_GAMEPAD_BUTTON_A}, "midnightcontrols.action.pickup_all", true, client) + 2;
            x = MidnightControlsRenderer.drawButtonTip(matrices, x, y, new int[]{GLFW.GLFW_GAMEPAD_BUTTON_B}, "midnightcontrols.action.exit", true, client) + 2;
            if (MidnightControlsCompat.isReiPresent()) {
                x = 2;
                y -= 24;
            }
            if (MidnightControlsCompat.isEMIPresent()) {
                x = (int) (client.getWindow().getScaledWidth() * (1 / scale) - 55 - client.textRenderer.getWidth(new TranslatableText("midnightcontrols.action.pickup"))
                        * (1 / scale) - client.textRenderer.getWidth(new TranslatableText("midnightcontrols.action.quick_move"))
                        - MidnightControlsRenderer.getBindingIconWidth(ButtonBinding.TAKE) - MidnightControlsRenderer.getBindingIconWidth(ButtonBinding.QUICK_MOVE));
            }
            x = MidnightControlsRenderer.drawButtonTip(matrices, x, y, new int[]{GLFW.GLFW_GAMEPAD_BUTTON_X}, "midnightcontrols.action.pickup", true, client);
            MidnightControlsRenderer.drawButtonTip(matrices, x, y, new int[]{GLFW.GLFW_GAMEPAD_BUTTON_Y}, "midnightcontrols.action.quick_move", true, client);
            if (scale != 1f) matrices.scale(1,1,1);
        }
    }
}
