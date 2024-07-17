/*
 * Copyright © 2022 Motschen <motschen@midnightdust.eu>
 *
 * This file is part of midnightcontrols.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package eu.midnightdust.midnightcontrols.client.compat;

import me.juancarloscp52.bedrockify.client.BedrockifyClient;

/**
 * Represents HQM compatibility handler.
 *
 * @author Motschen
 * @version 1.7.0
 * @since 1.7.0
 */
public class BedrockifyCompat implements CompatHandler {

    @Override
    public void handle() {
        BedrockifyClient.getInstance().settings.disableFlyingMomentum = false;
    }
}
