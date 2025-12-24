package eu.midnightdust.midnightcontrols.client.mixin;

import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.input.KeyEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(KeyboardHandler.class)
public interface KeyboardAccessor {
    @Invoker("keyPress")
    void midnightcontrols$onKey(long window, int action, KeyEvent input);
}
