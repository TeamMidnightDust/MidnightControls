package eu.midnightdust.midnightcontrols.client.compat;

import eu.midnightdust.midnightcontrols.MidnightControls;
import eu.midnightdust.midnightcontrols.client.MidnightControlsConfig;
import eu.midnightdust.midnightcontrols.client.compat.mixin.SodiumOptionsGUIAccessor;
import me.jellysquid.mods.sodium.client.gui.SodiumOptionsGUI;
import net.minecraft.client.gui.screen.Screen;

public class SodiumCompat {
    public static void handleTabs(Screen screen, boolean direction) {
        if (screen instanceof SodiumOptionsGUI optionsGUI) {
            SodiumOptionsGUIAccessor accessor = (SodiumOptionsGUIAccessor) optionsGUI;
            final int max = accessor.getPages().size()-1;
            int i = accessor.getPages().indexOf(accessor.getCurrentPage());
            i = (direction ? ((max > i) ? ++i : 0) : (i > 0 ? --i : max));
            if (MidnightControlsConfig.debug) MidnightControls.get().log(""+i);
            optionsGUI.setPage(accessor.getPages().get(i));
        }
    }
}
