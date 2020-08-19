/*
 * Copyright © 2020 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of LambdaControls.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package me.lambdaurora.lambdacontrols.client.gui;

import me.lambdaurora.lambdacontrols.client.controller.Controller;
import me.lambdaurora.spruceui.option.SpruceSimpleActionOption;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

/**
 * Represents the option to reload the controller mappings.
 */
public class ReloadControllerMappingsOption extends SpruceSimpleActionOption
{
    private static final String KEY = "lambdacontrols.menu.reload_controller_mappings";

    public ReloadControllerMappingsOption()
    {
        this(null);
    }

    public ReloadControllerMappingsOption(@Nullable Consumer<AbstractButtonWidget> before)
    {
        super(KEY, btn -> {
            MinecraftClient client = MinecraftClient.getInstance();
            if (before != null)
                before.accept(btn);
            Controller.updateMappings();
            if (client.currentScreen instanceof LambdaControlsSettingsScreen)
                client.currentScreen.init(client, client.getWindow().getScaledWidth(), client.getWindow().getScaledHeight());
            client.getToastManager().add(SystemToast.create(client, SystemToast.Type.TUTORIAL_HINT,
                    new TranslatableText("lambdacontrols.controller.mappings.updated"), LiteralText.EMPTY));
        }, new TranslatableText("lambdacontrols.tooltip.reload_controller_mappings"));
    }
}
