package eu.midnightdust.midnightcontrols.client.util.platform.fabric;

import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.widget.PressableWidget;
import net.minecraft.item.ItemGroup;

import java.util.List;

/**
 * Implementation of fabric methods for
 * @see eu.midnightdust.midnightcontrols.client.util.platform.ItemGroupUtil
 */
public class ItemGroupUtilImpl {
    public static List<ItemGroup> getVisibleGroups(CreativeInventoryScreen screen) {
        return (screen.getItemGroupsOnPage(screen.getCurrentPage()));
    }
    public static boolean cyclePage(boolean next, CreativeInventoryScreen screen) {
        try {
            return screen.children().stream().filter(element -> element instanceof PressableWidget)
                    .map(element -> (PressableWidget) element)
                    .filter(element -> element.getMessage() != null && element.getMessage().getContent() != null)
                    .anyMatch(element -> {
                        if (next && element.getMessage().getString().equals(">")) {
                            element.onPress();
                            return true;
                        } else if (element.getMessage().getString().equals("<")) {
                            element.onPress();
                            return true;
                        }
                        return false;
                    });
        } catch (Exception ignored) {}
        return false;
    }
}