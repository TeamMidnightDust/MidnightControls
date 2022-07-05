package eu.midnightdust.midnightcontrols.client.compat.mixin;

import me.jellysquid.mods.sodium.client.gui.SodiumOptionsGUI;
import me.jellysquid.mods.sodium.client.gui.options.OptionPage;
import me.jellysquid.mods.sodium.client.gui.options.control.ControlElement;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(value = SodiumOptionsGUI.class, remap = false)
public interface SodiumOptionsGUIAccessor {
    @Accessor
    List<ControlElement<?>> getControls();
    @Accessor
    List<OptionPage> getPages();
    @Accessor
    OptionPage getCurrentPage();
}
