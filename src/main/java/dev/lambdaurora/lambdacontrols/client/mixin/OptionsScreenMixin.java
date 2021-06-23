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
import dev.lambdaurora.lambdacontrols.client.gui.LambdaControlsSettingsScreen;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Injects the new controls settings button.
 */
@Mixin(OptionsScreen.class)
public class OptionsScreenMixin extends Screen {
    protected OptionsScreenMixin(Text title) {
        super(title);
    }

    @SuppressWarnings("unchecked")
    @Redirect(
            method = "init",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screen/option/OptionsScreen;addDrawableChild(Lnet/minecraft/client/gui/Element;)Lnet/minecraft/client/gui/Element;",
                    ordinal = 7
            )
    )
    private <T extends Element & Drawable & Selectable> T lambdacontrols$onInit(OptionsScreen screen, T element) {
        if (LambdaControlsClient.get().config.getControlsMode() == ControlsMode.CONTROLLER && element instanceof ButtonWidget btn) {
            return (T) this.addDrawableChild(new ButtonWidget(btn.x, btn.y, btn.getWidth(), ((ClickableWidgetAccessor) btn).getHeight(),
                    btn.getMessage(),
                    b -> this.client.openScreen(new LambdaControlsSettingsScreen(this, false))));
        } else {
            return this.addDrawableChild(element);
        }
    }
}
