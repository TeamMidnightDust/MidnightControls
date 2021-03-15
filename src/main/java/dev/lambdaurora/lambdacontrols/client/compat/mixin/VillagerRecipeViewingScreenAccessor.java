/*
 * Copyright © 2020 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of LambdaControls.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package dev.lambdaurora.lambdacontrols.client.compat.mixin;

import me.shedaniel.clothconfig2.api.ScrollingContainer;
import me.shedaniel.rei.api.RecipeCategory;
import me.shedaniel.rei.api.RecipeDisplay;
import me.shedaniel.rei.gui.VillagerRecipeViewingScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;
import java.util.Map;

/**
 * Represents an accessor to REI's VillagerRecipeViewingScreen.
 *
 * @author LambdAurora
 * @version 1.5.0
 * @since 1.2.0
 */
@Mixin(VillagerRecipeViewingScreen.class)
public interface VillagerRecipeViewingScreenAccessor
{
    @Accessor(value = "categoryMap", remap = false)
    Map<RecipeCategory<?>, List<RecipeDisplay>> getCategoryMap();

    @Accessor(value = "categories", remap = false)
    List<RecipeCategory<?>> getCategories();

    @Accessor(value = "selectedCategoryIndex", remap = false)
    int getSelectedCategoryIndex();

    @Accessor(value = "selectedCategoryIndex", remap = false)
    void setSelectedCategoryIndex(int selectedCategoryIndex);

    @Accessor(value = "selectedRecipeIndex", remap = false)
    int getSelectedRecipeIndex();

    @Accessor(value = "selectedRecipeIndex", remap = false)
    void setSelectedRecipeIndex(int selectedRecipeIndex);

    @Accessor(value = "scrolling", remap = false)
    ScrollingContainer getScrolling();

    @Invoker("init")
    void lambdacontrols_init();
}
