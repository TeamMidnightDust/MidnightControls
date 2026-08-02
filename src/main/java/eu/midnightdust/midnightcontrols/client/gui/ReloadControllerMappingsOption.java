/*
 * Copyright © 2021 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of midnightcontrols.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package eu.midnightdust.midnightcontrols.client.gui;

import dev.lambdaurora.spruceui.option.SpruceSimpleActionOption;
import dev.lambdaurora.spruceui.tooltip.TooltipData;
import dev.lambdaurora.spruceui.widget.SpruceButtonWidget;
import eu.midnightdust.midnightcontrols.client.controller.Controller;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.network.chat.Component;

/**
 * Represents the option to reload the controller mappings.
 */
public class ReloadControllerMappingsOption {
    private static final String KEY = "midnightcontrols.menu.reload_controller_mappings";

    public static SpruceSimpleActionOption newOption(@Nullable Consumer<SpruceButtonWidget> before) {
        return SpruceSimpleActionOption.of(KEY, btn -> {
            var client = Minecraft.getInstance();
            if (before != null)
                before.accept(btn);
            Controller.updateMappings();
            //~ if >= 26.2 'client.screen' -> 'client.gui.screen()' {
            if (client.gui.screen() instanceof MidnightControlsSettingsScreen)
                client.gui.screen().init(client.getWindow().getGuiScaledWidth(), client.getWindow().getGuiScaledHeight());
            //~}
            //~ if >= 26.2 'client.getToastManager()' -> 'client.gui.toastManager()'
            client.gui.toastManager().addToast(new SystemToast(SystemToast.SystemToastId.PERIODIC_NOTIFICATION,
                    Component.translatable("midnightcontrols.controller.mappings.updated"), Component.empty()));
        }, TooltipData.builder().text(Component.translatable("midnightcontrols.tooltip.reload_controller_mappings")).build());
    }
}
