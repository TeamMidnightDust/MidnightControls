/*
 * Copyright © 2020 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of LambdaControls.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package dev.lambdaurora.lambdacontrols.client.compat.mixin;

import me.shedaniel.rei.gui.widget.EntryListEntryWidget;
import me.shedaniel.rei.gui.widget.EntryListWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

/**
 * Represents an accessor to REI's EntryListWidget.
 *
 * @author LambdAurora
 * @version 1.5.0
 * @since 1.5.0
 */
@Mixin(value = EntryListWidget.class, remap = false)
public interface EntryListWidgetAccessor
{
    @Accessor(value = "entries")
    List<EntryListEntryWidget> getEntries();
}
