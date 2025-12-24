package eu.midnightdust.midnightcontrols.client.util.platform.neoforge;

import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.world.item.CreativeModeTab;

import java.util.List;

/**
 * Implementation of neoforge methods for
 * @see eu.midnightdust.midnightcontrols.client.util.platform.ItemGroupUtil
 */
public class ItemGroupUtilImpl {
    public static List<CreativeModeTab> getVisibleGroups(CreativeModeInventoryScreen screen) {
        return (screen.getCurrentPage().getVisibleTabs());
    }
}