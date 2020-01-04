/*
 * Copyright © 2020 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of LambdaControls.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package me.lambdaurora.lambdacontrols.client.util;

/**
 * Represents mouse's extra access.
 */
public interface MouseAccessor
{
    void on_mouse_button(long window, int button, int action, int mods);

    void on_cursor_pos(long window, double x, double y);
}
