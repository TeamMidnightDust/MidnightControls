package eu.midnightdust.midnightcontrols.client.mixin;

import net.minecraft.block.entity.SignText;
import net.minecraft.client.gui.screen.ingame.AbstractSignEditScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AbstractSignEditScreen.class)
public interface AbstractSignEditScreenAccessor {
    @Accessor("text")
    SignText midnightcontrols$getText();

    @Accessor("text")
    void midnightcontrols$setText(SignText text);
}
