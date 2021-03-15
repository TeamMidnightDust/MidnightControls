/*
 * Copyright © 2020 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of LambdaControls.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package me.lambdaurora.lambdacontrols.client.mixin;

import me.lambdaurora.lambdacontrols.client.util.KeyBindingAccessor;
import net.minecraft.client.options.KeyBinding;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(KeyBinding.class)
public class KeyBindingMixin implements KeyBindingAccessor
{
    @Shadow
    private int timesPressed;

    @Shadow
    private boolean pressed;

    @Override
    public boolean lambdacontrols_press()
    {
        boolean oldPressed = this.pressed;
        if (!this.pressed)
            this.pressed = true;
        ++this.timesPressed;
        return oldPressed != this.pressed;
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
