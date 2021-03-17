/*
 * Copyright © 2021 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of LambdaControls.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package dev.lambdaurora.lambdacontrols.event;

import dev.lambdaurora.lambdacontrols.ControlsMode;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.player.PlayerEntity;
import org.jetbrains.annotations.NotNull;

/**
 * Represents an event callback which is fired when a player changes the controls mode.
 *
 * @author LambdAurora
 * @version 1.1.0
 * @since 1.1.0
 */
@FunctionalInterface
public interface PlayerChangeControlsModeCallback {
    Event<PlayerChangeControlsModeCallback> EVENT = EventFactory.createArrayBacked(PlayerChangeControlsModeCallback.class, listeners -> (player, controlsMode) -> {
        for (PlayerChangeControlsModeCallback event : listeners) {
            event.apply(player, controlsMode);
        }
    });

    void apply(@NotNull PlayerEntity player, @NotNull ControlsMode controlsMode);
}
