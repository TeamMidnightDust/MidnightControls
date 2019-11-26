/*
 * Copyright © 2019 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of LambdaControls.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package me.lambdaurora.lambdacontrols.mixin;

import me.lambdaurora.lambdacontrols.util.LambdaKeyBinding;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(KeyBinding.class)
public class KeyBindingMixin implements LambdaKeyBinding
{
    @Shadow
    private InputUtil.KeyCode keyCode;

    @Shadow
    private int timesPressed;

    @Shadow
    private boolean pressed;

    @Override
    public @NotNull InputUtil.KeyCode get_key_code()
    {
        return this.keyCode;
    }

    @Override
    public boolean lambdacontrols_press()
    {
        boolean old_pressed = this.pressed;
        if (!this.pressed)
            this.pressed = true;
        ++this.timesPressed;
        return old_pressed != this.pressed;
    }

    @Override
    public boolean lambdacontrols_unpress()
    {
        if (this.pressed) {
            this.pressed = false;
            return true;
        }
        return false;
    }
}
