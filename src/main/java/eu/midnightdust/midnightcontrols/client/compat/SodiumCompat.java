package eu.midnightdust.midnightcontrols.client.compat;

import eu.midnightdust.midnightcontrols.MidnightControls;
import eu.midnightdust.midnightcontrols.client.MidnightControlsClient;
import eu.midnightdust.midnightcontrols.client.MidnightControlsConfig;
import eu.midnightdust.midnightcontrols.client.compat.mixin.SodiumOptionsGUIAccessor;
import eu.midnightdust.midnightcontrols.client.controller.InputManager;
import me.jellysquid.mods.sodium.client.gui.SodiumOptionsGUI;
import me.jellysquid.mods.sodium.client.gui.options.control.ControlElement;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;

public class SodiumCompat {
    private static final MinecraftClient client = MinecraftClient.getInstance();
    public static void handleInput(Screen screen, boolean direction) {
        if (screen instanceof SodiumOptionsGUI optionsGUI) {
            SodiumOptionsGUIAccessor accessor = (SodiumOptionsGUIAccessor) optionsGUI;
            final int max = accessor.getControls().size()-1;

            var option = accessor.getControls().stream().filter(ControlElement::isHovered).findFirst().orElse(accessor.getControls().get(0));
            int i = accessor.getControls().indexOf(option);
            i = (direction ? ((max > i) ? ++i : 0) : (i > 0 ? --i : max));

            var dimensions = accessor.getControls().get(i).getDimensions();
            InputManager.INPUT_MANAGER.targetMouseX = (int) (client.getWindow().getScaleFactor() * dimensions.getCenterX());
            InputManager.INPUT_MANAGER.targetMouseY = (int) (client.getWindow().getScaleFactor() * dimensions.getCenterY());
            MidnightControlsClient.get().input.actionGuiCooldown = 5;
            if (MidnightControlsConfig.debug) MidnightControls.get().log(i+" "+accessor.getControls().size()+" | " + dimensions.getCenterX() + " " + dimensions.getCenterY());
        }
    }
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
