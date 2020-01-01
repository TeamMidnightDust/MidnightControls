/*
 * Copyright © 2020 LambdAurora <aurora42lambda@gmail.com>
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

import java.util.Arrays;
import java.util.Optional;

/**
 * Represents the controls mode.
 *
 * @author LambdAurora
 * @version 1.0.0
 * @since 1.0.0
 */
public enum ControlsMode implements Nameable
{
    DEFAULT,
    CONTROLLER,
    TOUCHSCREEN;

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

    /**
     * Gets the controls mode from its identifier.
     *
     * @param id The identifier of the controls mode.
     * @return The controls mode if found, else empty.
     */
    public static Optional<ControlsMode> by_id(@NotNull String id)
    {
        return Arrays.stream(values()).filter(mode -> mode.get_name().equalsIgnoreCase(id)).findFirst();
    }
}
