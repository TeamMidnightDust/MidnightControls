package eu.midnightdust.midnightcontrols.client.mixin;

import net.minecraft.client.gui.screen.ingame.BookEditScreen;
import net.minecraft.client.gui.widget.EditBoxWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BookEditScreen.class)
public interface BookEditScreenAccessor {
    @Accessor("editBox")
    EditBoxWidget midnightcontrols$getEditBox();
}
