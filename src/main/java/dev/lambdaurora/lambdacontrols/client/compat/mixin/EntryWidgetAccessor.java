/*
 * Copyright © 2020 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of LambdaControls.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package dev.lambdaurora.lambdacontrols.client.compat.mixin;

import me.shedaniel.rei.api.EntryStack;
import me.shedaniel.rei.gui.widget.EntryWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/**
 * Represents an accessor to REI's EntryWidget.
 *
 * @author LambdAurora
 * @version 1.5.0
 * @since 1.5.0
 */
@Mixin(value = EntryWidget.class, remap = false)
public interface EntryWidgetAccessor
{
    @Invoker("getCurrentEntry")
    EntryStack lambdacontrols_getCurrentEntry();
}
