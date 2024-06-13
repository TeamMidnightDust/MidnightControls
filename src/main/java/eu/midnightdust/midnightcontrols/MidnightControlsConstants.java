/*
 * Copyright © 2021 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of midnightcontrols.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package eu.midnightdust.midnightcontrols;


import net.minecraft.util.Identifier;

/**
 * Represents the constants used by MidnightControls.
 *
 * @author LambdAurora
 * @version 1.1.0
 * @since 1.1.0
 */
public class MidnightControlsConstants {
    public static final String NAMESPACE = "midnightcontrols";
    public static final Identifier CONTROLS_MODE_CHANNEL = Identifier.of(NAMESPACE, "controls_mode");
    public static final Identifier FEATURE_CHANNEL = Identifier.of(NAMESPACE, "feature");
    public static final Identifier HELLO_CHANNEL = Identifier.of(NAMESPACE, "hello");
}
