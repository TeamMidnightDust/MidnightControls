package eu.midnightdust.midnightcontrols.client.compat;

import com.kqp.inventorytabs.tabs.TabManager;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;

public class InventoryTabsCompat implements CompatHandler {
    protected static boolean isPresent;

    @Override
    public boolean handleTabs(Screen screen, boolean next) {
        if (screen instanceof HandledScreen<?> && !(screen instanceof CreativeInventoryScreen)) {
            TabManager tabManager = TabManager.getInstance();
            int tabIndex = tabManager.tabs.indexOf(tabManager.currentTab);
            if (next) {
                if (tabIndex < tabManager.tabs.size() - 1) tabManager.onTabClick(tabManager.tabs.get(tabIndex + 1));
                else tabManager.onTabClick(tabManager.tabs.getFirst());
            } else {
                if (tabIndex > 0) tabManager.onTabClick(tabManager.tabs.get(tabIndex - 1));
                else tabManager.onTabClick(tabManager.tabs.getLast());
            }
            return true;
        }
        return false;
    }
    @Override
    public boolean handlePages(Screen screen, boolean next) {
        if (screen instanceof HandledScreen<?> && !(screen instanceof CreativeInventoryScreen)) {
            TabManager tabManager = TabManager.getInstance();
            if (next) {
                if (tabManager.canGoForwardAPage()) {
                    tabManager.setCurrentPage(tabManager.currentPage + 1);
                    return true;
                }
            } else {
                if (tabManager.canGoBackAPage()) {
                    tabManager.setCurrentPage(tabManager.currentPage - 1);
                    return true;
                }
            }
        }
        return false;
    }
    /**
     * Returns whether InventoryTabs is present.
     *
     * @return true if InventoryTabs is present, else false
     */
    public static boolean isPresent() {
        return isPresent;
    }
}
