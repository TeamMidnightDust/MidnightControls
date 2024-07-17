/*
 * Copyright © 2021 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of midnightcontrols.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package eu.midnightdust.midnightcontrols.client.mixin;

import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.client.gui.screen.recipebook.RecipeGroupButtonWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;

@Mixin(RecipeBookWidget.class)
public interface RecipeBookWidgetAccessor {
    @Accessor("tabButtons")
    List<RecipeGroupButtonWidget> getTabButtons();

    @Accessor("currentTab")
    RecipeGroupButtonWidget getCurrentTab();

    @Accessor("currentTab")
    void setCurrentTab(RecipeGroupButtonWidget currentTab);

    @Invoker("refreshResults")
    void midnightcontrols$refreshResults(boolean resetCurrentPage);
}
