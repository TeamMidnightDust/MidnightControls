/*
 * Copyright © 2020 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of LambdaControls.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package me.lambdaurora.lambdacontrols.client.ring;

import net.minecraft.client.resource.language.I18n;
import org.aperlambda.lambdacommon.utils.Nameable;
import org.jetbrains.annotations.NotNull;

/**
 * Represents the mode of a ring button.
 *
 * @author LambdAurora
 * @version 1.4.0
 * @since 1.4.0
 */
public enum RingButtonMode implements Nameable
{
    PRESS("press"),
    HOLD("hold"),
    TOGGLE("toggle");

    private String name;

    RingButtonMode(@NotNull String name)
    {
        this.name = name;
    }

    /**
     * Returns the next ring button mode available.
     *
     * @return The next ring button mode.
     */
    public RingButtonMode next()
    {
        RingButtonMode[] v = values();
        if (v.length == this.ordinal() + 1)
            return v[0];
        return v[this.ordinal() + 1];
    }

    /**
     * Gets the translated name of this ring button mode.
     *
     * @return The translated name of this ring button mode.
     */
    public String getTranslatedName()
    {
        return I18n.translate("lambdacontrols.ring.button_mode." + this.getName());
    }

    @Override
    public @NotNull String getName()
    {
        return this.name;
    }
}
