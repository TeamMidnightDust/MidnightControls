/*
 * Copyright © 2021 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of midnightcontrols.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package eu.midnightdust.midnightcontrols.client.controller;

import eu.midnightdust.midnightcontrols.client.enums.ButtonState;
import eu.midnightdust.midnightcontrols.client.MidnightControlsConfig;
import eu.midnightdust.midnightcontrols.client.mixin.InputAccessor;
import eu.midnightdust.midnightcontrols.client.util.MathUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Input;
import net.minecraft.world.phys.Vec2;
import org.jetbrains.annotations.NotNull;

/**
 * Represents the movement handler.
 *
 * @author LambdAurora
 * @version 1.6.0
 * @since 1.4.0
 */
public final class MovementHandler implements PressAction {
    public static final MovementHandler HANDLER = new MovementHandler();
    private boolean shouldOverrideMovement = false;
    private boolean pressingForward = false;
    private boolean pressingBack = false;
    private boolean pressingLeft = false;
    private boolean pressingRight = false;
    private float slowdownFactor = 1.f;
    private float movementForward = 0.f;
    private float movementSideways = 0.f;
    private final MathUtil.PolarUtil polarUtil = new MathUtil.PolarUtil();

    private MovementHandler() {
    }

    /**
     * Applies movement input of this handler to the player's input.
     *
     * @param player The client player.
     */
    public void applyMovement(@NotNull LocalPlayer player) {
        if (!this.shouldOverrideMovement)
            return;
        player.input.keyPresses = new Input(this.pressingForward, this.pressingBack, this.pressingLeft, this.pressingRight,
                player.input.keyPresses.jump(), player.input.keyPresses.shift(), player.input.keyPresses.sprint());

        polarUtil.calculate(this.movementSideways, this.movementForward, this.slowdownFactor);
        Vec2 inputVector = new Vec2(polarUtil.polarX, polarUtil.polarY);
        ((InputAccessor)player.input).setMoveVector(inputVector);

        this.shouldOverrideMovement = false;
    }

    @Override
    public boolean press(@NotNull Minecraft client, @NotNull ButtonBinding button, float value, @NotNull ButtonState action) {
        if (client.screen != null || client.player == null)
            return this.shouldOverrideMovement = false;

        int direction = 0;
        if (button == ButtonBinding.FORWARD || button == ButtonBinding.LEFT)
            direction = 1;
        else if (button == ButtonBinding.BACK || button == ButtonBinding.RIGHT)
            direction = -1;

        if (action.isUnpressed())
            direction = 0;

        this.shouldOverrideMovement = direction != 0;

        if (!MidnightControlsConfig.analogMovement) {
            value = 1.f;
        }

        this.slowdownFactor = client.player.isMovingSlowly() ? (Mth.clamp(
            0.3F + (float) client.player.getAttributeValue(Attributes.SNEAKING_SPEED),
            0.0F,
            1.0F
        )) : 1.f;

        if (button == ButtonBinding.FORWARD || button == ButtonBinding.BACK) {
            // Handle forward movement.
            this.pressingForward = direction > 0;
            this.pressingBack = direction < 0;
            this.movementForward = direction * value;
        } else {
            // Handle sideways movement.
            this.pressingLeft = direction > 0;
            this.pressingRight = direction < 0;
            this.movementSideways = direction * value;
        }

        return this.shouldOverrideMovement;
    }
}
