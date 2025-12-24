package eu.midnightdust.midnightcontrols.client.util.platform.fabric;


import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.world.item.CreativeModeTab;

import java.util.List;

/**
 * Implementation of fabric methods for
 * @see eu.midnightdust.midnightcontrols.client.util.platform.ItemGroupUtil
 */
public class ItemGroupUtilImpl {
    public static List<CreativeModeTab> getVisibleGroups(CreativeModeInventoryScreen screen) {
        return (screen.getItemGroupsOnPage(screen.getCurrentPage()));
    }
}