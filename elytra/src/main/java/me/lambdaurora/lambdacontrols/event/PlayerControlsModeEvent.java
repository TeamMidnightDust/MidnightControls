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
import me.lambdaurora.lambdacontrols.LambdaControlsConstants;
import org.aperlambda.lambdacommon.Identifier;
import org.jetbrains.annotations.NotNull;
import org.mcelytra.core.entity.EntityPlayer;
import org.mcelytra.core.event.HandlerList;
import org.mcelytra.core.event.player.PlayerEvent;

/**
 * Represents an event which is fired when a player change their controls mode.
 *
 * @author LambdAurora
 * @version 1.1.0
 * @since 1.1.0
 */
public class PlayerControlsModeEvent extends PlayerEvent
{
    private static final HandlerList  HANDLERS = new HandlerList();
    private final        ControlsMode controls_mode;

    public PlayerControlsModeEvent(@NotNull EntityPlayer player, @NotNull ControlsMode controls_mode)
    {
        super(new Identifier(LambdaControlsConstants.NAMESPACE, "player_controls_mode"), player, true);
        this.controls_mode = controls_mode;
    }

    /**
     * Returns the controls mode of the player.
     *
     * @return The player's controls mode.
     */
    public ControlsMode get_controls_mode()
    {
        return this.controls_mode;
    }

    @Override
    public @NotNull HandlerList get_handlers()
    {
        return HANDLERS;
    }
}
