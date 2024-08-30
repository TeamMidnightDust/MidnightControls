package eu.midnightdust.midnightcontrols.client.compat;

import eu.midnightdust.midnightcontrols.client.compat.mixin.sodium.SodiumOptionsGUIAccessor;
import net.caffeinemc.mods.sodium.client.gui.SodiumOptionsGUI;
import net.minecraft.client.gui.screen.Screen;

public class SodiumCompat implements CompatHandler {
    @Override
    public boolean handleTabs(Screen screen, boolean direction) {
        if (screen instanceof SodiumOptionsGUI optionsGUI) {
            SodiumOptionsGUIAccessor accessor = (SodiumOptionsGUIAccessor) optionsGUI;
            final int max = accessor.getPages().size()-1;
            int i = accessor.getPages().indexOf(accessor.getCurrentPage());
            i = (direction ? ((max > i) ? ++i : 0) : (i > 0 ? --i : max));
            optionsGUI.setPage(accessor.getPages().get(i));
            return true;
        }
        return false;
    }
}
