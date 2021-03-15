/*
 * Copyright © 2020 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of LambdaControls.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package me.lambdaurora.lambdacontrols;

import org.aperlambda.lambdacommon.Identifier;

/**
 * Represents the constants used by LambdaControls.
 *
 * @author LambdAurora
 * @version 1.1.0
 * @since 1.1.0
 */
public class LambdaControlsConstants
{
    public static final String     NAMESPACE             = "lambdacontrols";
    public static final Identifier CONTROLS_MODE_CHANNEL = new Identifier(NAMESPACE, "controls_mode");
    public static final Identifier FEATURE_CHANNEL       = new Identifier(NAMESPACE, "feature");
    public static final Identifier HELLO_CHANNEL         = new Identifier(NAMESPACE, "hello");
}
