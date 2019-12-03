/*
 * Copyright © 2019 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of LambdaControls.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package me.lambdaurora.lambdacontrols.mixin;

import me.lambdaurora.lambdacontrols.util.AbstractContainerScreenAccessor;
import net.minecraft.client.gui.screen.ingame.AbstractContainerScreen;
import net.minecraft.container.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/**
 * Represents the mixin for the class AbstractContainerScreen.
 */
@Mixin(AbstractContainerScreen.class)
public abstract class AbstractContainerScreenMixin implements AbstractContainerScreenAccessor
{
    @Shadow
    protected int left;

    @Shadow
    protected int top;

    @Shadow
    protected abstract Slot getSlotAt(double xPosition, double yPosition);

    @Override
    public int get_left()
    {
        return this.left;
    }

    @Override
    public int get_top()
    {
        return this.top;
    }

    @Override
    public Slot get_slot_at(double pos_x, double pos_y)
    {
        return this.getSlotAt(pos_x, pos_y);
    }
}
