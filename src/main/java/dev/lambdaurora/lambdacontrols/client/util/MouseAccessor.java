/*
 * Copyright © 2021 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of LambdaControls.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package dev.lambdaurora.lambdacontrols.client.util;

/**
 * Represents mouse's extra access.
 */
public interface MouseAccessor {
    void lambdacontrols$onCursorPos(long window, double x, double y);
}
