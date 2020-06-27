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
 * Represents the virtual mouse skins.
 *
 * @version 1.2.0
 * @since 1.2.0
 */
public enum VirtualMouseSkin implements Nameable
{
    DEFAULT_LIGHT("default_light"),
    DEFAULT_DARK("default_dark"),
    SECOND_LIGHT("second_light"),
    SECOND_DARK("second_dark");

    private String name;

    VirtualMouseSkin(String name) {
        this.name = name;
    }

    /**
     * Returns the next virtual mouse skin available.
     *
     * @return The next available virtual mouse skin.
     */
    public VirtualMouseSkin next()
    {
        VirtualMouseSkin[] v = values();
        if (v.length == this.ordinal() + 1)
            return v[0];
        return v[this.ordinal() + 1];
    }

    /**
     * Gets the translated name of this controller type.
     *
     * @return The translated name of this controller type.
     */
    public String getTranslatedName()
    {
        return I18n.translate("lambdacontrols.virtual_mouse.skin." + this.getName());
    }

    @Override
    public @NotNull String getName()
    {
        return this.name;
    }

    /**
     * Gets the controller type from its identifier.
     *
     * @param id The identifier of the controller type.
     * @return The controller type if found, else empty.
     */
    public static Optional<VirtualMouseSkin> byId(@NotNull String id)
    {
        return Arrays.stream(values()).filter(mode -> mode.getName().equalsIgnoreCase(id)).findFirst();
    }
}
