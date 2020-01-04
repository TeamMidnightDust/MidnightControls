/*
 * Copyright © 2020 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of LambdaControls.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package me.lambdaurora.lambdacontrols.client;

import io.github.prospector.modmenu.api.ModMenuApi;
import me.lambdaurora.lambdacontrols.LambdaControlsConstants;
import me.lambdaurora.lambdacontrols.client.gui.LambdaControlsSettingsScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;

import java.util.function.Function;

/**
 * Represents the API implementation of ModMenu for LambdaControls.
 *
 * @author LambdAurora
 * @version 1.1.0
 * @since 1.1.0
 */
public class LambdaControlsModMenu implements ModMenuApi
{
    @Override
    public String getModId()
    {
        return LambdaControlsConstants.NAMESPACE;
    }

    @Override
    public Function<Screen, ? extends Screen> getConfigScreenFactory()
    {
        return screen -> new LambdaControlsSettingsScreen(screen, MinecraftClient.getInstance().options, false);
    }
}
