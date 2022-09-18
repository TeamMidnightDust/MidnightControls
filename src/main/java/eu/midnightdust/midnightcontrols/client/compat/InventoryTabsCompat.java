package eu.midnightdust.midnightcontrols.client.compat;

import com.kqp.inventorytabs.tabs.TabManager;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;

public class InventoryTabsCompat {

    public static void handleInventoryTabs(Screen screen, boolean next) {
        if (screen instanceof HandledScreen<?> && !(screen instanceof CreativeInventoryScreen)) {
            TabManager tabManager = TabManager.getInstance();
            int tabIndex = tabManager.tabs.indexOf(tabManager.currentTab);
            if (next) {
                if (tabIndex < tabManager.tabs.size() - 1) tabManager.onTabClick(tabManager.tabs.get(tabIndex + 1));
                else tabManager.onTabClick(tabManager.tabs.get(0));
            } else {
                if (tabIndex > 0) tabManager.onTabClick(tabManager.tabs.get(tabIndex - 1));
                else tabManager.onTabClick(tabManager.tabs.get(tabManager.tabs.size() - 1));
            }
        }
    }
    public static void handleInventoryPage(Screen screen, boolean next) {
        if (screen instanceof HandledScreen<?> && !(screen instanceof CreativeInventoryScreen)) {
            TabManager tabManager = TabManager.getInstance();
            if (next) {
                if (tabManager.canGoForwardAPage()) tabManager.setCurrentPage(tabManager.currentPage + 1);
            } else {
                if (tabManager.canGoBackAPage()) tabManager.setCurrentPage(tabManager.currentPage - 1);
            }
        }
    }
}
