/*
 * Copyright © 2020 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of LambdaControls.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package dev.lambdaurora.lambdacontrols.client;

import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import org.aperlambda.lambdacommon.utils.Nameable;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Optional;

/**
 * Represents the hud side which is the side where the movements buttons are.
 *
 * @author LambdAurora
 * @version 1.4.0
 * @since 1.0.0
 */
public enum HudSide implements Nameable
{
    LEFT,
    RIGHT;

    private final Text text;

    HudSide()
    {
        this.text = new TranslatableText(this.getTranslationKey());
    }

    /**
     * Returns the next side available.
     *
     * @return The next available side.
     */
    public @NotNull HudSide next()
    {
        HudSide[] v = values();
        if (v.length == this.ordinal() + 1)
            return v[0];
        return v[this.ordinal() + 1];
    }

    /**
     * Returns the translation key of this hud side.
     *
     * @return The translation key of this hude side.
     */
    public @NotNull String getTranslationKey()
    {
        return "lambdacontrols.hud_side." + this.getName();
    }

    /**
     * Gets the translated text of this hud side.
     *
     * @return The translated text of this hud side.
     */
    public @NotNull Text getTranslatedText()
    {
        return this.text;
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
    public static @NotNull Optional<HudSide> byId(@NotNull String id)
    {
        return Arrays.stream(values()).filter(mode -> mode.getName().equalsIgnoreCase(id)).findFirst();
    }
}
