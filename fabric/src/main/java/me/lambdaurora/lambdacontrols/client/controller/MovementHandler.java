/*
 * Copyright © 2020 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of LambdaControls.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package me.lambdaurora.lambdacontrols.client.controller;

import me.lambdaurora.lambdacontrols.client.ButtonState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import org.jetbrains.annotations.NotNull;

/**
 * Represents the movement handler.
 *
 * @author LambdAurora
 * @version 1.4.0
 * @since 1.4.0
 */
public final class MovementHandler implements PressAction
{
    public static final MovementHandler HANDLER                = new MovementHandler();
    private             boolean         shouldOverrideMovement = false;
    private             boolean         pressingForward        = false;
    private             boolean         pressingBack           = false;
    private             boolean         pressingLeft           = false;
    private             boolean         pressingRight          = false;
    private             float           movementForward        = 0.f;
    private             float           movementSideways       = 0.f;

    private MovementHandler()
    {
    }

    /**
     * Applies movement input of this handler to the player's input.
     *
     * @param player The client player.
     */
    public void applyMovement(@NotNull ClientPlayerEntity player)
    {
        if (!this.shouldOverrideMovement)
            return;
        player.input.pressingForward = this.pressingForward;
        player.input.pressingBack = this.pressingBack;
        player.input.pressingLeft = this.pressingLeft;
        player.input.pressingRight = this.pressingRight;
        player.input.movementForward = this.movementForward;
        player.input.movementSideways = this.movementSideways;
        this.shouldOverrideMovement = false;
    }

    @Override
    public boolean press(@NotNull MinecraftClient client, @NotNull ButtonBinding button, float value, @NotNull ButtonState action)
    {
        if (client.currentScreen != null || client.player == null)
            return this.shouldOverrideMovement = false;

        int direction = 0;
        if (button == ButtonBinding.FORWARD || button == ButtonBinding.LEFT)
            direction = 1;
        else if (button == ButtonBinding.BACK || button == ButtonBinding.RIGHT)
            direction = -1;

        if (direction == 0)
            return false;

        this.shouldOverrideMovement = true;

        value = (float) Math.pow(value, 2);

        if (button == ButtonBinding.FORWARD || button == ButtonBinding.BACK) {
            // Handle forward movement.
            this.pressingForward = direction > 0;
            this.pressingBack = direction < 0;
            this.movementForward = direction * value;

            // Slowing down if sneaking.
            if (client.player.input.sneaking)
                this.movementForward *= 0.3D;
        } else {
            // Handle sideways movement.
            this.pressingLeft = direction > 0;
            this.pressingRight = direction < 0;
            this.movementSideways = direction * value;

            // Slowing down if sneaking.
            if (client.player.input.sneaking)
                this.movementSideways *= 0.3D;
        }

        return true;
    }
}
