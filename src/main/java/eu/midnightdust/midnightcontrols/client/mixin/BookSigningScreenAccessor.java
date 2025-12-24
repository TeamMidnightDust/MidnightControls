package eu.midnightdust.midnightcontrols.client.mixin;

import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.BookSignScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BookSignScreen.class)
public interface BookSigningScreenAccessor {
    @Accessor("titleBox")
    EditBox midnightcontrols$getBookTitleTextField();
}
