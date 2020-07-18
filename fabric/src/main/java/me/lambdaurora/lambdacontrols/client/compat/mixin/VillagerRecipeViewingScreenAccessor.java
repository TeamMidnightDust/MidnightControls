/*
 * Copyright © 2020 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of LambdaControls.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package me.lambdaurora.lambdacontrols.client.compat.mixin;

import me.shedaniel.rei.api.RecipeCategory;
import me.shedaniel.rei.gui.VillagerRecipeViewingScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;

/**
 * Represents an accessor to REI's VillagerRecipeViewingScreen.
 *
 * @author LambdAurora
 * @version 1.2.0
 * @since 1.2.0
 */
@Mixin(value = VillagerRecipeViewingScreen.class, remap = false)
public interface VillagerRecipeViewingScreenAccessor
{
    @Accessor("categories")
    List<RecipeCategory<?>> getCategories();

    @Accessor("selectedCategoryIndex")
    int getSelectedCategoryIndex();

    @Accessor("selectedCategoryIndex")
    void setSelectedCategoryIndex(int selectedCategoryIndex);

    @Invoker("init")
    void lambdacontrols_init();
}
