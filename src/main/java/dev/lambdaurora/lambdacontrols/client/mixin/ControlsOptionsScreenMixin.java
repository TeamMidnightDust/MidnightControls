/*
 * Copyright © 2020 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of LambdaControls.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package dev.lambdaurora.lambdacontrols.client.mixin;

import dev.lambdaurora.lambdacontrols.client.gui.LambdaControlsSettingsScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.options.ControlsOptionsScreen;
import net.minecraft.client.gui.screen.options.GameOptionsScreen;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.options.GameOptions;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Injects the new controls settings button.
 */
@Mixin(ControlsOptionsScreen.class)
public class ControlsOptionsScreenMixin extends GameOptionsScreen {
    public ControlsOptionsScreenMixin(Screen parent, GameOptions gameOptions, Text text) {
        super(parent, gameOptions, text);
    }

    @Redirect(method = "init", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/options/ControlsOptionsScreen;addButton(Lnet/minecraft/client/gui/widget/AbstractButtonWidget;)Lnet/minecraft/client/gui/widget/AbstractButtonWidget;", ordinal = 1))
    private AbstractButtonWidget onInit(ControlsOptionsScreen screen, AbstractButtonWidget btn) {
        /*if (this.parent instanceof ControllerControlsWidget)
            return this.addButton(btn);
        else*/
        return this.addButton(new ButtonWidget(btn.x, btn.y, btn.getWidth(), ((AbstractButtonWidgetAccessor) btn).getHeight(), new TranslatableText("menu.options"),
                b -> this.client.openScreen(new LambdaControlsSettingsScreen(this, true))));
    }
}
