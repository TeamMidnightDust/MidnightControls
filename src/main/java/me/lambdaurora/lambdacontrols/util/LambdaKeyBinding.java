/*
 * Copyright © 2019 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of LambdaControls.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package me.lambdaurora.lambdacontrols.util;

import net.minecraft.client.util.InputUtil;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a Minecraft keybinding with extra access.
 */
public interface LambdaKeyBinding
{
    @NotNull InputUtil.KeyCode get_key_code();

    void lambdacontrols_press();
}
