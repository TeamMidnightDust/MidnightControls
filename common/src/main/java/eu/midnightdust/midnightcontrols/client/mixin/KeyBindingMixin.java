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
import net.minecraft.client.KeyMapping;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(KeyMapping.class)
public abstract class KeyBindingMixin implements KeyBindingAccessor {
    @Shadow
    private int clickCount;

    @Shadow
    private boolean isDown;

    @Override
    public boolean midnightcontrols$press() {
        boolean oldPressed = this.isDown;
        if (!this.isDown)
            this.isDown = true;
        ++this.clickCount;
        return !oldPressed;
    }

    @Override
    public boolean midnightcontrols$unpress() {
        if (this.isDown) {
            this.isDown = false;
            return true;
        }
        return false;
    }
}
