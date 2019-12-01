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
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

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

    @Inject(method = "init", at = @At("RETURN"))
    private void on_init(CallbackInfo ci)
    {
        this.buttons.stream().filter(button -> button.getMessage().equals(I18n.translate("options.controls")))
                .findFirst()
                .ifPresent(btn -> {
                    this.buttons.remove(btn);
                    this.children.remove(btn);
                    this.addButton(new ButtonWidget(btn.x, btn.y, btn.getWidth(), ((AbstractButtonWidgetAccessor) btn).get_height(), btn.getMessage(),
                            b -> this.minecraft.openScreen(new LambdaControlsSettingsScreen(this, this.settings))));
                });
    }
}
