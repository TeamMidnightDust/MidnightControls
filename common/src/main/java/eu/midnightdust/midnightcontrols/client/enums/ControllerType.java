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
 * Represents a controller type.
 *
 * @author LambdAurora
 * @version 1.4.3
 * @since 1.0.0
 */
public enum ControllerType {
    DEFAULT(0),
    DUALSHOCK(1),
    DUALSENSE(2),
    SWITCH(3),
    XBOX_360(4),
    XBOX(5),
    STEAM_DECK(6),
    STEAM_CONTROLLER(7),
    OUYA(8),
    NUMBERED(9);

    private final int id;
    private final Text text;

    ControllerType(int id) {
        this.id = id;
        this.text = Text.translatable("midnightcontrols.controller_type." + this.getName());
    }

    ControllerType(int id, @NotNull Text text) {
        this.id = id;
        this.text = text;
    }

    /**
     * Returns the controller type's identifier.
     *
     * @return the controller type's identifier
     */
    public int getId() {
        return this.id;
    }

    /**
     * Returns the next controller type available.
     *
     * @return the next available controller type
     */
    public @NotNull ControllerType next() {
        var v = values();
        if (v.length == this.ordinal() + 1)
            return v[0];
        return v[this.ordinal() + 1];
    }

    /**
     * Gets the translated text of this controller type.
     *
     * @return the translated text of this controller type
     */
    public @NotNull Text getTranslatedText() {
        return this.text;
    }

    public @NotNull String getName() {
        return this.name().toLowerCase();
    }

    /**
     * Gets the controller type from its identifier.
     *
     * @param id the identifier of the controller type
     * @return the controller type if found, else empty
     */
    public static @NotNull Optional<ControllerType> byId(@NotNull String id) {
        return Arrays.stream(values()).filter(mode -> mode.getName().equalsIgnoreCase(id)).findFirst();
    }
}
