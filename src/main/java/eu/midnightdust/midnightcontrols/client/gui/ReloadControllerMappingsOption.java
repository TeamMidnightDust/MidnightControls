/*
 * Copyright © 2021 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of midnightcontrols.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package eu.midnightdust.midnightcontrols.client.gui;

import eu.midnightdust.midnightcontrols.client.controller.Controller;
import dev.lambdaurora.spruceui.option.SpruceSimpleActionOption;
import dev.lambdaurora.spruceui.widget.SpruceButtonWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

/**
 * Represents the option to reload the controller mappings.
 */
public class ReloadControllerMappingsOption {
    private static final String KEY = "midnightcontrols.menu.reload_controller_mappings";

    public static SpruceSimpleActionOption newOption(@Nullable Consumer<SpruceButtonWidget> before) {
        return SpruceSimpleActionOption.of(KEY, btn -> {
            var client = MinecraftClient.getInstance();
            if (before != null)
                before.accept(btn);
            Controller.updateMappings();
            if (client.currentScreen instanceof MidnightControlsSettingsScreen)
                client.currentScreen.init(client, client.getWindow().getScaledWidth(), client.getWindow().getScaledHeight());
            client.getToastManager().add(SystemToast.create(client, SystemToast.Type.TUTORIAL_HINT,
                    Text.translatable("midnightcontrols.controller.mappings.updated"), Text.empty()));
        }, Text.translatable("midnightcontrols.tooltip.reload_controller_mappings"));
    }
}
