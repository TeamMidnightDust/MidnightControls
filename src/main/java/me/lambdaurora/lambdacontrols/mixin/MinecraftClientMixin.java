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
import me.lambdaurora.lambdacontrols.gui.TouchscreenOverlay;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.Window;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin
{
    @Shadow
    public Window window;

    @Shadow
    public boolean skipGameRender;

    @Shadow public Screen currentScreen;

    @Inject(method = "init", at = @At("RETURN"))
    private void on_init(CallbackInfo ci)
    {
        LambdaControls.get().on_mc_init((MinecraftClient) (Object) this);
    }

    @Inject(method = "handleInputEvents", at = @At("HEAD"))
    private void on_handle_input_events(CallbackInfo ci)
    {
        if (LambdaControls.get().config.get_controls_mode() == ControlsMode.CONTROLLER)
            LambdaControls.get().on_tick((MinecraftClient) (Object) this);
    }

    @Inject(method = "openScreen", at = @At("RETURN"))
    private void on_open_screen(@Nullable Screen screen, CallbackInfo ci)
    {
        LambdaControls mod = LambdaControls.get();
        if (screen == null && mod.config.get_controls_mode() == ControlsMode.TOUCHSCREEN) {
            screen = new TouchscreenOverlay(mod);
            screen.init(((MinecraftClient) (Object) this), this.window.getScaledWidth(), this.window.getScaledHeight());
            this.skipGameRender = false;
            this.currentScreen = screen;
        }
    }
}
