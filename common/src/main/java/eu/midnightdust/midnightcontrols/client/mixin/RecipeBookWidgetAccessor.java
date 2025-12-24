/*
 * Copyright © 2021 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of midnightcontrols.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package eu.midnightdust.midnightcontrols.client.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeBookTabButton;

@Mixin(RecipeBookComponent.class)
public interface RecipeBookWidgetAccessor {
    @Accessor("tabButtons")
    List<RecipeBookTabButton> getTabButtons();

    @Accessor("selectedTab")
    RecipeBookTabButton getCurrentTab();

    @Accessor("selectedTab")
    void setCurrentTab(RecipeBookTabButton currentTab);
}
