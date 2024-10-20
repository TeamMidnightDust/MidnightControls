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
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.ControlsOptionsScreen;
import net.minecraft.client.gui.screen.option.GameOptionsScreen;
import net.minecraft.client.gui.widget.OptionListWidget;
import net.minecraft.client.gui.widget.TextIconButtonWidget;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static eu.midnightdust.midnightcontrols.MidnightControls.id;

/**
 * Injects the new controls settings button.
 */
@Mixin(GameOptionsScreen.class)
public abstract class GameOptionsScreenMixin extends Screen {
    @Shadow @Nullable protected OptionListWidget body;
    @Unique TextIconButtonWidget midnightcontrols$button = TextIconButtonWidget.builder(Text.translatable("midnightcontrols.menu.title.controller"), (button -> this.client.setScreen(new MidnightControlsSettingsScreen(this, false))), true)
            .dimension(20,20).texture(id("icon/controller"), 20, 20).build();

    protected GameOptionsScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At("TAIL"))
    public void midnightcontrols$addMCButton(CallbackInfo ci) {
        if (this.getClass().toString().equals(ControlsOptionsScreen.class.toString())) {
            this.midnightcontrols$setButtonPos();
            this.addDrawableChild(midnightcontrols$button);
        }
    }

    @Inject(method = "initTabNavigation", at = @At("TAIL"))
    public void midnightcontrols$onResize(CallbackInfo ci) {
        this.midnightcontrols$setButtonPos();
    }
    @Unique
    public void midnightcontrols$setButtonPos() {
        if (body != null) {
            midnightcontrols$button.setPosition(body.getWidth() / 2 + 158, body.getY() + 4);
        }
    }
}
