package eu.midnightdust.midnightcontrols.client.mixin;

import net.minecraft.client.gui.screen.ingame.BookSigningScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BookSigningScreen.class)
public interface BookSigningScreenAccessor {
    @Accessor("bookTitleTextField")
    TextFieldWidget midnightcontrols$getBookTitleTextField();
}
