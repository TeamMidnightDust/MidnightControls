/*
 * Copyright © 2020 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of LambdaControls.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package me.lambdaurora.lambdacontrols.client.ring;

import net.minecraft.text.TranslatableText;
import org.aperlambda.lambdacommon.utils.Nameable;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a ring action.
 *
 * @author LambdAurora
 * @version 1.4.0
 * @since 1.4.0
 */
public abstract class RingAction implements Nameable
{
    protected boolean activated = false;

    /**
     * Gets the translated name of the ring action.
     *
     * @return The translated name.
     */
    public TranslatableText getTranslatedName()
    {
        return new TranslatableText(this.getName());
    }

    /**
     * Returns whether the action is activated or not.
     *
     * @return True if the action is activated, else false.
     */
    public boolean isActivated()
    {
        return this.activated;
    }

    public void activate(@NotNull RingButtonMode mode)
    {
        this.activated = !this.activated;

        this.onAction(mode);
    }

    public abstract void onAction(@NotNull RingButtonMode mode);
}
