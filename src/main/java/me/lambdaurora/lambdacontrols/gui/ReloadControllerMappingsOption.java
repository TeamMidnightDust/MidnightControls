/*
 * Copyright © 2019 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of LambdaControls.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package me.lambdaurora.lambdacontrols.gui;

import me.lambdaurora.lambdacontrols.Controller;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.options.Option;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.text.TranslatableText;
import org.aperlambda.lambdacommon.utils.Nameable;
import org.jetbrains.annotations.NotNull;

/**
 * Represents the option to reload the controller mappings.
 */
public class ReloadControllerMappingsOption extends Option implements Nameable
{
    private static final String KEY = "lambdacontrols.menu.reload_controller_mappings";

    public ReloadControllerMappingsOption()
    {
        super(KEY);
    }

    @Override
    public AbstractButtonWidget createButton(GameOptions options, int x, int y, int width)
    {
        return new ButtonWidget(x, y, width, 20, this.get_name(), btn -> {
            Controller.update_mappings();
            MinecraftClient.getInstance().getToastManager().add(new SystemToast(SystemToast.Type.TUTORIAL_HINT, new TranslatableText("lambdacontrols.controller.mappings.updated"), null));
        });
    }

    @Override
    public @NotNull String get_name()
    {
        return I18n.translate(KEY);
    }
}
