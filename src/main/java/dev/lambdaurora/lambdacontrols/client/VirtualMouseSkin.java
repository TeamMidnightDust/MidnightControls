/*
 * Copyright © 2021 LambdAurora <aurora42lambda@gmail.com>
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
 * Represents the virtual mouse skins.
 *
 * @version 1.4.0
 * @since 1.2.0
 */
public enum VirtualMouseSkin implements Nameable {
    DEFAULT_LIGHT("default_light"),
    DEFAULT_DARK("default_dark"),
    SECOND_LIGHT("second_light"),
    SECOND_DARK("second_dark");

    private final String name;
    private final Text text;

    VirtualMouseSkin(String name) {
        this.name = name;
        this.text = new TranslatableText(this.getTranslationKey());
    }

    /**
     * Returns the next virtual mouse skin available.
     *
     * @return The next available virtual mouse skin.
     */
    public @NotNull VirtualMouseSkin next() {
        VirtualMouseSkin[] v = values();
        if (v.length == this.ordinal() + 1)
            return v[0];
        return v[this.ordinal() + 1];
    }

    /**
     * Returns the translation key of this virtual mouse skin.
     *
     * @return The virtual mouse skin's translation key.
     */
    public @NotNull String getTranslationKey() {
        return "lambdacontrols.virtual_mouse.skin." + this.getName();
    }

    /**
     * Gets the translated text of this virtual mouse skin.
     *
     * @return The translated text of this virtual mouse skin.
     */
    public @NotNull Text getTranslatedText() {
        return this.text;
    }

    @Override
    public @NotNull String getName() {
        return this.name;
    }

    /**
     * Gets the virtual mouse skin from its identifier.
     *
     * @param id The identifier of the virtual mouse skin.
     * @return The virtual mouse skin if found, else empty.
     */
    public static @NotNull Optional<VirtualMouseSkin> byId(@NotNull String id) {
        return Arrays.stream(values()).filter(mode -> mode.getName().equalsIgnoreCase(id)).findFirst();
    }
}
