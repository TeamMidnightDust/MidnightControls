package eu.midnightdust.midnightcontrols.client.util.platform;

import eu.midnightdust.midnightcontrols.client.mixin.CreativeInventoryScreenAccessor;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;

import static eu.midnightdust.midnightcontrols.client.MidnightInput.ENTER_KEY_INPUT;

public class ItemGroupUtil {
    public static List<CreativeModeTab> getVisibleGroups(CreativeModeInventoryScreen screen) {
        return /*? fabric {*/ /*(screen.getItemGroupsOnPage(screen.getCurrentPage())) *//*?} else {*/ (screen.getCurrentPage().getVisibleTabs()) /*?}*/;
    }

    public static boolean cyclePage(boolean next, CreativeModeInventoryScreen screen) {
        try {
            return screen.children().stream().filter(element -> element instanceof AbstractButton)
                    .map(element -> (AbstractButton) element)
                    .filter(element -> element.getMessage() != null && element.getMessage().getContents() != null)
                    .anyMatch(element -> {
                        if (next && element.getMessage().getString().equals(">")) {
                            element.onPress(ENTER_KEY_INPUT);
                            return true;
                        } else if (element.getMessage().getString().equals("<")) {
                            element.onPress(ENTER_KEY_INPUT);
                            return true;
                        }
                        return false;
                    });
        } catch (Exception ignored) {}
        return false;
    }

    public static @NotNull CreativeModeTab cycleTab(boolean next, Minecraft client) {
        CreativeModeTab currentTab = CreativeInventoryScreenAccessor.getSelectedTab();
        int currentColumn = currentTab.column();
        CreativeModeTab.Row currentRow = currentTab.row();
        CreativeModeTab newTab = null;
        List<CreativeModeTab> visibleTabs = ItemGroupUtil.getVisibleGroups((CreativeModeInventoryScreen) client.screen);
        for (CreativeModeTab tab : visibleTabs) {
            if (tab.row().equals(currentRow) && ((newTab == null && ((next && tab.column() > currentColumn) ||
                    (!next && tab.column() < currentColumn))) || (newTab != null && ((next && tab.column() > currentColumn && tab.column() < newTab.column()) ||
                    (!next && tab.column() < currentColumn && tab.column() > newTab.column())))))
                newTab = tab;
        }
        if (newTab == null)
            for (CreativeModeTab tab : visibleTabs) {
                if ((tab.row().compareTo(currentRow)) != 0 && ((next && newTab == null || next && newTab.column() > tab.column()) || (!next && newTab == null) || (!next && newTab.column() < tab.column())))
                    newTab = tab;
            }
        if (newTab == null) {
            for (CreativeModeTab tab : visibleTabs) {
                if ((next && tab.row() == CreativeModeTab.Row.TOP && tab.column() == 0) ||
                        !next && tab.row() == CreativeModeTab.Row.BOTTOM && (newTab == null || tab.column() > newTab.column()))
                    newTab = tab;
            }
        }
        if (newTab == null || newTab.equals(currentTab)) newTab = CreativeModeTabs.getDefaultTab();
        return newTab;
    }
}