/*
 * Copyright © 2021 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of LambdaControls.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package dev.lambdaurora.lambdacontrols.client.mixin;

import dev.lambdaurora.lambdacontrols.client.gui.LambdaControlsSettingsScreen;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.ControlsOptionsScreen;
import net.minecraft.client.gui.screen.option.GameOptionsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.option.GameOptions;
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

    @SuppressWarnings("unchecked")
    @Redirect(
            method = "init",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screen/option/ControlsOptionsScreen;addDrawableChild(Lnet/minecraft/client/gui/Element;)Lnet/minecraft/client/gui/Element;",
                    ordinal = 1
            )
    )
    private <T extends Element & Drawable & Selectable, R extends Element & Drawable & Selectable> R onInit(ControlsOptionsScreen screen, T element) {
        /*if (this.parent instanceof ControllerControlsWidget)
            return this.addButton(btn);
        else*/
        if (element instanceof CyclingButtonWidget btn) {
            return (R) this.addDrawableChild(new ButtonWidget(btn.x, btn.y, btn.getWidth(), ((ClickableWidgetAccessor) btn).getHeight(),
                    new TranslatableText("menu.options"),
                    b -> this.client.openScreen(new LambdaControlsSettingsScreen(this, true))));
        } else {
            return (R) this.addDrawableChild(element);
        }
    }
}
