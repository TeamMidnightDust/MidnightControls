package eu.midnightdust.midnightcontrols.client.util.platform;

import dev.architectury.injectables.annotations.ExpectPlatform;
import eu.midnightdust.midnightcontrols.client.mixin.CreativeInventoryScreenAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.widget.PressableWidget;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ItemGroupUtil {
    @ExpectPlatform
    public static List<ItemGroup> getVisibleGroups(CreativeInventoryScreen screen) {
        throw new AssertionError();
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

    public static @NotNull ItemGroup cycleTab(boolean next, MinecraftClient client) {
        ItemGroup currentTab = CreativeInventoryScreenAccessor.getSelectedTab();
        int currentColumn = currentTab.getColumn();
        ItemGroup.Row currentRow = currentTab.getRow();
        ItemGroup newTab = null;
        List<ItemGroup> visibleTabs = ItemGroupUtil.getVisibleGroups((CreativeInventoryScreen) client.currentScreen);
        for (ItemGroup tab : visibleTabs) {
            if (tab.getRow().equals(currentRow) && ((newTab == null && ((next && tab.getColumn() > currentColumn) ||
                    (!next && tab.getColumn() < currentColumn))) || (newTab != null && ((next && tab.getColumn() > currentColumn && tab.getColumn() < newTab.getColumn()) ||
                    (!next && tab.getColumn() < currentColumn && tab.getColumn() > newTab.getColumn())))))
                newTab = tab;
        }
        if (newTab == null)
            for (ItemGroup tab : visibleTabs) {
                if ((tab.getRow().compareTo(currentRow)) != 0 && ((next && newTab == null || next && newTab.getColumn() > tab.getColumn()) || (!next && newTab == null) || (!next && newTab.getColumn() < tab.getColumn())))
                    newTab = tab;
            }
        if (newTab == null) {
            for (ItemGroup tab : visibleTabs) {
                if ((next && tab.getRow() == ItemGroup.Row.TOP && tab.getColumn() == 0) ||
                        !next && tab.getRow() == ItemGroup.Row.BOTTOM && (newTab == null || tab.getColumn() > newTab.getColumn()))
                    newTab = tab;
            }
        }
        if (newTab == null || newTab.equals(currentTab)) newTab = ItemGroups.getDefaultTab();
        return newTab;
    }
}