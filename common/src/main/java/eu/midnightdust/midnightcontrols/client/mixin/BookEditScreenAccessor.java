package eu.midnightdust.midnightcontrols.client.mixin;

import net.minecraft.client.gui.screen.ingame.BookEditScreen;
import net.minecraft.client.util.SelectionManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(BookEditScreen.class)
public interface BookEditScreenAccessor {
    @Accessor("signing")
    boolean midnightcontrols$isSigning();

    @Accessor("title")
    String midnightcontrols$getTitle();

    @Accessor("title")
    void midnightcontrols$setTitle(String title);

    @Accessor("currentPageSelectionManager")
    SelectionManager midnightcontrols$getCurrentPageSelectionManager();

    @Invoker("getCurrentPageContent")
    String midnightcontrols$getCurrentPageContent();

    @Invoker("setPageContent")
    void midnightcontrols$setPageContent(String newContent);
}
