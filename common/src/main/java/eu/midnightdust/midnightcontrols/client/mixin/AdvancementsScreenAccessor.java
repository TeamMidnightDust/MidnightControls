/*
 * Copyright © 2021 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of midnightcontrols.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package eu.midnightdust.midnightcontrols.client.mixin;

import net.minecraft.advancement.Advancement;
import net.minecraft.client.gui.screen.advancement.AdvancementTab;
import net.minecraft.client.gui.screen.advancement.AdvancementsScreen;
import net.minecraft.client.network.ClientAdvancementManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

/**
 * Represents an accessor of {@link AdvancementsScreen}.
 */
@Mixin(AdvancementsScreen.class)
public interface AdvancementsScreenAccessor {
    @Accessor("advancementHandler")
    ClientAdvancementManager getAdvancementManager();

    @Accessor("tabs")
    Map<Advancement, AdvancementTab> getTabs();

    @Accessor("selectedTab")
    AdvancementTab getSelectedTab();
}
