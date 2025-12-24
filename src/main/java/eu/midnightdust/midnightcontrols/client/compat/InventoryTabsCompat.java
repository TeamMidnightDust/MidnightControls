package eu.midnightdust.midnightcontrols.client.compat;

import com.kqp.inventorytabs.tabs.TabManager;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;

public class InventoryTabsCompat implements CompatHandler {
    @Override
    public boolean handleTabs(Screen screen, boolean next) {
        if (screen instanceof AbstractContainerScreen<?> && !(screen instanceof CreativeModeInventoryScreen)) {
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
        if (screen instanceof AbstractContainerScreen<?> && !(screen instanceof CreativeModeInventoryScreen)) {
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
}
