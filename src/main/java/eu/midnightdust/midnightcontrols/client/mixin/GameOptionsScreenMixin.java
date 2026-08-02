/*
 * Copyright © 2021 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of midnightcontrols.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package eu.midnightdust.midnightcontrols.client.mixin;

import eu.midnightdust.midnightcontrols.client.MidnightControlsConfig;
import eu.midnightdust.midnightcontrols.client.gui.MidnightControlsSettingsScreen;
import net.minecraft.client.gui.components.OptionsList;
import net.minecraft.client.gui.components.SpriteIconButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
import net.minecraft.client.gui.screens.options.controls.ControlsScreen;
import net.minecraft.network.chat.Component;
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
@Mixin(OptionsSubScreen.class)
public abstract class GameOptionsScreenMixin extends Screen {
    @Shadow @Nullable protected OptionsList list;
    @Unique SpriteIconButton midnightcontrols$button = SpriteIconButton.builder(Component.translatable("midnightcontrols.menu.title.controller"),
                    //~ if >= 26.2 'minecraft.setScreen(' -> 'minecraft.gui.setScreen('
                    (button -> this.minecraft.gui.setScreen(MidnightControlsConfig.getScreen(this, "midnightcontrols"))), true)
            .size(20,20).sprite(id("icon/controller"), 20, 20).build();

    protected GameOptionsScreenMixin(Component title) {
        super(title);
    }

    @Inject(method = "addContents", at = @At("TAIL"))
    public void midnightcontrols$addMCButton(CallbackInfo ci) {
        if (this.getClass().toString().equals(ControlsScreen.class.toString())) {
            this.midnightcontrols$setButtonPos();
            this.addWidget(midnightcontrols$button);
        }
    }
    @Inject(method = "init", at = @At("TAIL"))
    public void midnightcontrols$drawMCButton(CallbackInfo ci) {
        if (this.getClass().toString().equals(ControlsScreen.class.toString())) {
            this.addRenderableWidget(midnightcontrols$button);
        }
    }

    @Inject(method = "repositionElements", at = @At("TAIL"))
    public void midnightcontrols$onResize(CallbackInfo ci) {
        this.midnightcontrols$setButtonPos();
    }
    @Unique
    public void midnightcontrols$setButtonPos() {
        if (list != null) {
            midnightcontrols$button.setPosition(list.getWidth() / 2 + 158, list.getY() + 4);
        }
    }
}
