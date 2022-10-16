/*
 * Copyright © 2022 Motschen <motschen@midnightdust.eu>
 *
 * This file is part of midnightcontrols.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package eu.midnightdust.midnightcontrols.client.compat;

import eu.midnightdust.midnightcontrols.client.MidnightControlsClient;
import me.juancarloscp52.bedrockify.client.BedrockifyClient;
import me.juancarloscp52.bedrockify.client.BedrockifyClientSettings;
import net.minecraft.client.gui.screen.Screen;
import org.aperlambda.lambdacommon.utils.LambdaReflection;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Represents HQM compatibility handler.
 *
 * @author Motschen
 * @version 1.7.0
 * @since 1.7.0
 */
public class BedrockifyCompat implements CompatHandler {

    @Override
    public void handle(@NotNull MidnightControlsClient mod) {
        BedrockifyClient.getInstance().settings.disableFlyingMomentum = false;
    }
}
