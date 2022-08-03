package eu.midnightdust.midnightcontrols.client.mixin;

import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.Screen;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(Screen.class)
public interface ScreenAccessor {
    @Accessor
    List<Selectable> getSelectables();
    @Accessor @Nullable
    Selectable getSelected();
}
