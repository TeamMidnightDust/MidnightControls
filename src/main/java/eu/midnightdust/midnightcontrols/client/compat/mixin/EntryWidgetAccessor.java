/*
 * Copyright © 2021 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of midnightcontrols.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package eu.midnightdust.midnightcontrols.client.compat.mixin;

/**
 * Represents an accessor to REI's EntryWidget.
 *
 * @author LambdAurora
 * @version 1.5.0
 * @since 1.5.0
 */
//@Mixin(value = EntryWidget.class, remap = false)
public interface EntryWidgetAccessor {
    /*@Invoker("getCurrentEntry")
    EntryStack midnightcontrols_getCurrentEntry();*/
}
