/*
 * Copyright © 2021 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of midnightcontrols.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package eu.midnightdust.midnightcontrols.client.mixin;

import eu.midnightdust.midnightcontrols.client.util.KeyBindingAccessor;
import net.minecraft.client.option.KeyBinding;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(KeyBinding.class)
public class KeyBindingMixin implements KeyBindingAccessor {
    @Shadow
    private int timesPressed;

    @Shadow
    private boolean pressed;

    @Override
    public boolean midnightcontrols$press() {
        boolean oldPressed = this.pressed;
        if (!this.pressed)
            this.pressed = true;
        ++this.timesPressed;
        return !oldPressed;
    }

    @Override
    public boolean midnightcontrols$unpress() {
        if (this.pressed) {
            this.pressed = false;
            return true;
        }
        return false;
    }
}
