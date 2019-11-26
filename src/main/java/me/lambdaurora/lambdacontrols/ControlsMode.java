/*
 * Copyright © 2019 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of LambdaControls.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package me.lambdaurora.lambdacontrols;

import net.minecraft.client.resource.language.I18n;
import org.aperlambda.lambdacommon.utils.Nameable;
import org.jetbrains.annotations.NotNull;

/**
 * Represents the controls mode.
 */
public enum ControlsMode implements Nameable
{
    ;

    /**
     * Returns the next controls mode available.
     *
     * @return The next available controls mode.
     */
    public ControlsMode next()
    {
        ControlsMode[] v = values();
        if (v.length == this.ordinal() + 1)
            return v[0];
        return v[this.ordinal() + 1];
    }

    /**
     * Gets the translated name of this controls mode.
     *
     * @return The translated name of this controls mode.
     */
    public String get_translated_name()
    {
        return I18n.translate("lambdacontrols.controls_mode." + this.get_name());
    }

    @Override
    public @NotNull String get_name()
    {
        return this.name().toLowerCase();
    }
}
