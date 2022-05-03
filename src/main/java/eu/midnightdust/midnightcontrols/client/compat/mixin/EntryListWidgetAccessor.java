/*
 * Copyright © 2021 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of midnightcontrols.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package eu.midnightdust.midnightcontrols.client.compat.mixin;

import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.widget.EntryListWidget;
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
public interface EntryListWidgetAccessor {
    @Accessor(value = "children")
    List<Element> getEntries();
}
