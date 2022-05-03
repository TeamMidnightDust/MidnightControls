/*
 * Copyright © 2021 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of midnightcontrols.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package eu.midnightdust.midnightcontrols.client.compat;

import eu.midnightdust.midnightcontrols.client.MidnightControlsClient;
import eu.midnightdust.midnightcontrols.client.controller.ButtonBinding;
import io.github.kosmx.emotes.arch.gui.screen.ingame.FastChosseScreen;
import io.github.kosmx.emotes.main.network.ClientEmotePlay;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a compatibility handler for Emotecraft.
 *
 * @author Motschen
 * @version 1.4.3
 * @since 1.0.0
 */
public class EmotecraftCompat implements CompatHandler {
    @Override
    public void handle(@NotNull MidnightControlsClient mod) {
        new ButtonBinding.Builder("key.emotecraft.fastchoose")
                .buttons(16)
                .onlyInGame()
                .cooldown(true)
                .category(ButtonBinding.MISC_CATEGORY)
                .action((client, button, value, action) -> {
                    client.setScreen(new FastChosseScreen(null));
                    return true;
                })
                .register();
        new ButtonBinding.Builder("key.emotecraft.stop")
                .buttons(17)
                .onlyInGame()
                .cooldown(true)
                .category(ButtonBinding.MISC_CATEGORY)
                .action((client, button, value, action) -> {
                    ClientEmotePlay.clientStopLocalEmote();
                    return true;
                })
                .register();
    }
}
