/*
 * Copyright © 2021 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of midnightcontrols.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package eu.midnightdust.midnightcontrols.client.enums;

import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Optional;

/**
 * Represents the virtual mouse skins.
 *
 * @version 1.7.0
 * @since 1.2.0
 */
public enum VirtualMouseSkin {
    DEFAULT_LIGHT("default_light"),
    DEFAULT_DARK("default_dark"),
    SECOND_LIGHT("second_light"),
    SECOND_DARK("second_dark");

    private final String name;
    private final Text text;

    VirtualMouseSkin(String name) {
        this.name = name;
        this.text = Text.translatable(this.getTranslationKey());
    }

    /**
     * Returns the next virtual mouse skin available.
     *
     * @return the next available virtual mouse skin
     */
    public @NotNull VirtualMouseSkin next() {
        var v = values();
        if (v.length == this.ordinal() + 1)
            return v[0];
        return v[this.ordinal() + 1];
    }

    /**
     * Returns the translation key of this virtual mouse skin.
     *
     * @return the virtual mouse skin's translation key
     */
    public @NotNull String getTranslationKey() {
        return "midnightcontrols.virtual_mouse.skin." + this.getName();
    }

    /**
     * Gets the translated text of this virtual mouse skin.
     *
     * @return the translated text of this virtual mouse skin
     */
    public @NotNull Text getTranslatedText() {
        return this.text;
    }

    public @NotNull String getName() {
        return this.name;
    }

    /**
     * Gets the virtual mouse skin from its identifier.
     *
     * @param id the identifier of the virtual mouse skin
     * @return the virtual mouse skin if found, else empty
     */
    @Deprecated
    public static @NotNull Optional<VirtualMouseSkin> byId(@NotNull String id) {
        return Arrays.stream(values()).filter(mode -> mode.getName().equalsIgnoreCase(id)).findFirst();
    }
    public String getSpritePath() {
        return switch (this) {
            case DEFAULT_LIGHT -> "cursor/light/default";
            case DEFAULT_DARK -> "cursor/dark/default";
            case SECOND_LIGHT -> "cursor/light/secondary";
            case SECOND_DARK -> "cursor/dark/secondary";
        };
    }
}
