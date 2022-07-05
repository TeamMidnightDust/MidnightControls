/*
 * Copyright © 2021 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of midnightcontrols.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package eu.midnightdust.midnightcontrols.client.ring;

import net.minecraft.text.Text;
import org.aperlambda.lambdacommon.utils.Nameable;
import org.jetbrains.annotations.NotNull;

/**
 * Represents the mode of a ring button.
 *
 * @author LambdAurora
 * @version 1.4.0
 * @since 1.4.0
 */
public enum RingButtonMode implements Nameable {
    PRESS("press"),
    HOLD("hold"),
    TOGGLE("toggle");

    private final String name;
    private final Text text;

    RingButtonMode(@NotNull String name) {
        this.name = name;
        this.text = Text.translatable(this.getTranslationKey());
    }

    /**
     * Returns the next ring button mode available.
     *
     * @return the next ring button mode
     */
    public @NotNull RingButtonMode next() {
        var v = values();
        if (v.length == this.ordinal() + 1)
            return v[0];
        return v[this.ordinal() + 1];
    }

    /**
     * Returns the translation key of this ring button mode.
     *
     * @return the translation key of this ring button mode
     */
    public @NotNull String getTranslationKey() {
        return "midnightcontrols.ring.button_mode." + this.getName();
    }

    /**
     * Gets the translated name of this ring button mode.
     *
     * @return the translated name of this ring button mode
     */
    public @NotNull Text getTranslatedText() {
        return this.text;
    }

    @Override
    public @NotNull String getName() {
        return this.name;
    }
}
