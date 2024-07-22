/*
 * Copyright © 2021 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of midnightcontrols.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package eu.midnightdust.midnightcontrols.client.mixin;

import eu.midnightdust.lib.util.PlatformFunctions;
import eu.midnightdust.midnightcontrols.ControlsMode;
import eu.midnightdust.midnightcontrols.client.MidnightControlsConfig;
import eu.midnightdust.midnightcontrols.client.compat.EMICompat;
import eu.midnightdust.midnightcontrols.client.compat.MidnightControlsCompat;
import eu.midnightdust.midnightcontrols.client.controller.ButtonBinding;
import eu.midnightdust.midnightcontrols.client.gui.MidnightControlsRenderer;
import eu.midnightdust.midnightcontrols.client.util.HandledScreenAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
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
    public abstract Slot midnightcontrols$getSlotAt(double posX, double posY);

    @Invoker("isClickOutsideBounds")
    public abstract boolean midnightcontrols$isClickOutsideBounds(double mouseX, double mouseY, int x, int y, int button);


    @Invoker("onMouseClick")
    public abstract void midnightcontrols$onMouseClick(@Nullable Slot slot, int slotId, int clickData, SlotActionType actionType);

    @Inject(method = "render", at = @At("RETURN"))
    public void onRender(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (MidnightControlsConfig.controlsMode == ControlsMode.CONTROLLER && MidnightControlsConfig.hudEnable) {
            var client = MinecraftClient.getInstance();
            int x = 2, y = client.getWindow().getScaledHeight() - 2 - MidnightControlsRenderer.ICON_SIZE;
            if (EMICompat.isPresent() && EMICompat.isEMIEnabled()) {
                x += 42;
            }
            if (!ButtonBinding.TAKE_ALL.isNotBound()) x = MidnightControlsRenderer.drawButtonTip(context, x, y, ButtonBinding.TAKE_ALL,true, client) + 2;
            if (!ButtonBinding.EXIT.isNotBound()) x = MidnightControlsRenderer.drawButtonTip(context, x, y, ButtonBinding.EXIT, true, client) + 2;
            if (PlatformFunctions.isModLoaded("roughlyenoughitems")) {
                x = 2;
                y -= 24;
            }
            if (EMICompat.isPresent() && EMICompat.isEMIEnabled() && EMICompat.isSearchBarCentered()) {
                x = client.getWindow().getScaledWidth() - 4 - client.textRenderer.getWidth(Text.translatable("midnightcontrols.action.pickup"))
                        - client.textRenderer.getWidth(Text.translatable("midnightcontrols.action.quick_move"))
                        - 2 * MidnightControlsRenderer.getBindingIconWidth(ButtonBinding.TAKE) - MidnightControlsRenderer.getBindingIconWidth(ButtonBinding.QUICK_MOVE);
                y += 2;
            }
            if (!ButtonBinding.TAKE.isNotBound()) x = MidnightControlsRenderer.drawButtonTip(context, x, y, ButtonBinding.TAKE, true, client);
            if (!ButtonBinding.QUICK_MOVE.isNotBound()) MidnightControlsRenderer.drawButtonTip(context, x, y, ButtonBinding.QUICK_MOVE, true, client);
        }
    }
}
