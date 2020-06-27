/*
 * Copyright © 2020 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of LambdaControls.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package me.lambdaurora.lambdacontrols.client.compat.mixin;

import me.shedaniel.rei.api.widgets.Button;
import me.shedaniel.rei.gui.RecipeViewingScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * Represents an accessor to REI's RecipeViewingScreen.
 *
 * @author LambdAurora
 * @version 1.3.0
 * @since 1.2.0
 */
@Mixin(RecipeViewingScreen.class)
public interface RecipeViewingScreenAccessor
{
    @Accessor("categoryBack")
    Button getCategoryBack();

    @Accessor("categoryNext")
    Button getCategoryNext();
}
