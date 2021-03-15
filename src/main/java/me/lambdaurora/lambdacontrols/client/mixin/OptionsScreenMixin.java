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
import me.lambdaurora.lambdacontrols.client.gui.LambdaControlsSettingsScreen;
import me.lambdaurora.lambdacontrols.client.gui.widget.ControllerControlsWidget;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.options.OptionsScreen;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Injects the new controls settings button.
 */
@Mixin(OptionsScreen.class)
public class OptionsScreenMixin extends Screen
{
    protected OptionsScreenMixin(Text title)
    {
        super(title);
    }

    @Redirect(method = "init", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/options/OptionsScreen;addButton(Lnet/minecraft/client/gui/widget/AbstractButtonWidget;)Lnet/minecraft/client/gui/widget/AbstractButtonWidget;", ordinal = 7))
    private AbstractButtonWidget lambdacontrols_onInit(OptionsScreen screen, AbstractButtonWidget btn)
    {
        if (LambdaControlsClient.get().config.getControlsMode() == ControlsMode.CONTROLLER) {
            return this.addButton(new ButtonWidget(btn.x, btn.y, btn.getWidth(), ((AbstractButtonWidgetAccessor) btn).getHeight(), btn.getMessage(),
                    b -> this.client.openScreen(new LambdaControlsSettingsScreen(this, false))));
        } else {
            return this.addButton(btn);
        }
    }
}
