/*
 * Copyright © 2021 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of midnightcontrols.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package eu.midnightdust.midnightcontrols.client.mixin;

import eu.midnightdust.midnightcontrols.client.gui.MidnightControlsSettingsScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.ControlsOptionsScreen;
import net.minecraft.client.gui.screen.option.GameOptionsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Injects the new controls settings button.
 */
@Mixin(ControlsOptionsScreen.class)
public class ControlsOptionsScreenMixin extends GameOptionsScreen {
    public ControlsOptionsScreenMixin(Screen parent, GameOptions gameOptions, Text text) {
        super(parent, gameOptions, text);
    }
    @Inject(method = "init", at = @At(value = "INVOKE", ordinal = 4, shift = At.Shift.AFTER, target = "Lnet/minecraft/client/gui/screen/option/ControlsOptionsScreen;addDrawableChild(Lnet/minecraft/client/gui/Element;)Lnet/minecraft/client/gui/Element;"))
    private void addControllerButton(CallbackInfo ci) {
        int i = this.width / 2 - 155;
        int j = i + 160;
        int k = this.height / 6 - 12 + 48;;
        this.addDrawableChild(new ButtonWidget(j, k, 150, 20, new TranslatableText("midnightcontrols.menu.title.controller").append("..."), (button) -> {
            this.client.setScreen(new MidnightControlsSettingsScreen(this, false));
        }));
    }
}
