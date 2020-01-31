/*
 * Copyright © 2020 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of LambdaControls.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package me.lambdaurora.lambdacontrols.client;

import net.minecraft.client.resource.language.I18n;
import org.aperlambda.lambdacommon.utils.Nameable;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Optional;

/**
 * Represents the hud side which is the side where the movements buttons are.
 *
 * @author LambdAurora
 * @version 1.0.0
 * @since 1.0.0
 */
public enum HudSide implements Nameable
{
    LEFT,
    RIGHT;

    /**
     * Returns the next side available.
     *
     * @return The next available side.
     */
    public HudSide next()
    {
        HudSide[] v = values();
        if (v.length == this.ordinal() + 1)
            return v[0];
        return v[this.ordinal() + 1];
    }

    /**
     * Gets the translated name of this hud side.
     *
     * @return The translated name of this hud side.
     */
    public String getTranslatedName()
    {
        return I18n.translate("lambdacontrols.hud_side." + this.getName());
    }

    @Override
    public @NotNull String getName()
    {
        return this.name().toLowerCase();
    }

    /**
     * Gets the hud side from its identifier.
     *
     * @param id The identifier of the hud side.
     * @return The hud side if found, else empty.
     */
    public static Optional<HudSide> byId(@NotNull String id)
    {
        return Arrays.stream(values()).filter(mode -> mode.getName().equalsIgnoreCase(id)).findFirst();
    }
}
