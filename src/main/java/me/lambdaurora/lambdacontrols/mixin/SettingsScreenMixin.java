/*
 * Copyright © 2019 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of LambdaControls.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package me.lambdaurora.lambdacontrols.mixin;

import me.lambdaurora.lambdacontrols.gui.LambdaControlsSettingsScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.SettingsScreen;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.options.GameOptions;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Injects the new controls settings button.
 */
@Mixin(SettingsScreen.class)
public class SettingsScreenMixin extends Screen
{
    @Final
    @Shadow
    private GameOptions settings;

    protected SettingsScreenMixin(Text title)
    {
        super(title);
    }

    @Redirect(method = "init", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/SettingsScreen;addButton(Lnet/minecraft/client/gui/widget/AbstractButtonWidget;)Lnet/minecraft/client/gui/widget/AbstractButtonWidget;", ordinal = 7))
    private AbstractButtonWidget on_init(SettingsScreen screen, AbstractButtonWidget btn)
    {
        return this.addButton(new ButtonWidget(btn.x, btn.y, btn.getWidth(), ((AbstractButtonWidgetAccessor) btn).get_height(), btn.getMessage(),
                b -> this.minecraft.openScreen(new LambdaControlsSettingsScreen(this, this.settings))));
    }
}
