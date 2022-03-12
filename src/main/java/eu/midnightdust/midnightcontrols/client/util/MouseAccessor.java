/*
 * Copyright © 2021 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of midnightcontrols.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package eu.midnightdust.midnightcontrols.client.util;

/**
 * Represents mouse's extra access.
 */
public interface MouseAccessor {
    void midnightcontrols$onCursorPos(long window, double x, double y);
}
