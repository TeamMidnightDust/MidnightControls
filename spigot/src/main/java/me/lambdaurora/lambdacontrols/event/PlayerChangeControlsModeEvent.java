/*
 * Copyright © 2020 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of LambdaControls.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package me.lambdaurora.lambdacontrols.event;

import me.lambdaurora.lambdacontrols.ControlsMode;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Represents an event which is fired when a player change their controls mode.
 *
 * @author LambdAurora
 * @version 1.1.0
 * @since 1.1.0
 */
public class PlayerChangeControlsModeEvent extends PlayerEvent
{
    private static final HandlerList  HANDLERS = new HandlerList();
    private final        ControlsMode controlsMode;

    public PlayerChangeControlsModeEvent(@NotNull Player who, @NotNull ControlsMode controlsMode)
    {
        super(who);
        this.controlsMode = controlsMode;
    }

    /**
     * Returns the controls mode of the player.
     *
     * @return The player's controls mode.
     */
    public ControlsMode getControlsMode()
    {
        return this.controlsMode;
    }

    @Override
    public String toString()
    {
        return "PlayerChangeControlsModeEvent{" +
                "player=" + this.player +
                ", controls_mode=" + this.controlsMode +
                '}';
    }

    @Override
    public @NotNull HandlerList getHandlers()
    {
        return HANDLERS;
    }

    public static @NotNull HandlerList getHandlerList()
    {
        return HANDLERS;
    }
}
