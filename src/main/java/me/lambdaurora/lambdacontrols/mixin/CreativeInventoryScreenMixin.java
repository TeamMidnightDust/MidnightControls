/*
 * Copyright © 2019 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of LambdaControls.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package me.lambdaurora.lambdacontrols.mixin;

import me.lambdaurora.lambdacontrols.util.CreativeInventoryScreenAccessor;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.item.ItemGroup;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(CreativeInventoryScreen.class)
public abstract class CreativeInventoryScreenMixin implements CreativeInventoryScreenAccessor
{
    @Shadow
    protected abstract void setSelectedTab(ItemGroup itemGroup);

    @Accessor("selectedTab")
    public abstract int get_selected_tab();

    @Override
    public void set_selected_tab(@NotNull ItemGroup group)
    {
        this.setSelectedTab(group);
    }
}
