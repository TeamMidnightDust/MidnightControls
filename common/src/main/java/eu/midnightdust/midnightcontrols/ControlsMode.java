/*
 * Copyright © 2021 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of midnightcontrols.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package eu.midnightdust.midnightcontrols;

import net.minecraft.text.Text;
import net.minecraft.text.object.AtlasTextObjectContents;
import net.minecraft.text.object.TextObjectContents;
import net.minecraft.util.Atlases;
import net.minecraft.util.Identifier;
import net.minecraft.util.TranslatableOption;
import org.jetbrains.annotations.NotNull;
import org.thinkingstudio.obsidianui.util.Nameable;

import java.util.Arrays;
import java.util.Optional;

import static eu.midnightdust.midnightcontrols.MidnightControls.id;

/**
 * Represents the controls mode.
 *
 * @author LambdAurora
 * @version 1.7.0
 * @since 1.0.0
 */
public enum ControlsMode implements TranslatableOption {
    DEFAULT("icon/keyboard_mouse"),
    CONTROLLER("icon/controller"),
    TOUCHSCREEN("icon/touchscreen");
    final String emoji;

    ControlsMode(String emoji) {
        this.emoji = emoji;
    }

    /**
     * Returns the next controls mode available.
     *
     * @return the next available controls mode
     */
    public ControlsMode next() {
        var v = values();
        if (v.length == this.ordinal() + 1)
            return v[0];
        return v[this.ordinal() + 1];
    }

    @Override
    public int getId() {
        return this.ordinal();
    }

    @Override
    public Text getText() {
        return Text.object(new AtlasTextObjectContents(Atlases.GUI, id(emoji))).append(" ").append(Text.translatable(getTranslationKey()));
    }

    /**
     * Gets the translation key of this controls mode.
     *
     * @return the translated key of this controls mode
     * @since 1.1.0
     */
    public String getTranslationKey() {
        return "midnightcontrols.controls_mode." + this.getName();
    }


    public @NotNull String getName() {
        return this.name().toLowerCase();
    }

    /**
     * Gets the controls mode from its identifier.
     *
     * @param id the identifier of the controls mode
     * @return the controls mode if found, else empty
     */
    public static Optional<ControlsMode> byId(@NotNull String id) {
        return Arrays.stream(values()).filter(mode -> mode.getName().equalsIgnoreCase(id)).findFirst();
    }
}
