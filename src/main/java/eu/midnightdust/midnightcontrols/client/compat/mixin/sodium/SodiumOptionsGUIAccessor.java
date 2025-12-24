package eu.midnightdust.midnightcontrols.client.compat.mixin.sodium;

import net.caffeinemc.mods.sodium.client.gui.VideoSettingsScreen;
import net.caffeinemc.mods.sodium.client.config.structure.OptionPage;
import net.caffeinemc.mods.sodium.client.gui.options.control.ControlElement;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(value = VideoSettingsScreen.class, remap = false)
public interface SodiumOptionsGUIAccessor {
//    @Accessor
//    List<ControlElement<?>> getControls();
//    @Accessor
//    List<OptionPage> getPages();
//    @Accessor
//    OptionPage getCurrentPage();
}
