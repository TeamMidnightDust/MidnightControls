package eu.midnightdust.midnightcontrols.client.util.platform.neoforge;

import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.item.ItemGroup;

import java.util.List;

/**
 * Implementation of neoforge methods for
 * @see eu.midnightdust.midnightcontrols.client.util.platform.ItemGroupUtil
 */
public class ItemGroupUtilImpl {
    public static List<ItemGroup> getVisibleGroups(CreativeInventoryScreen screen) {
        return (screen.getCurrentPage().getVisibleTabs());
    }
}