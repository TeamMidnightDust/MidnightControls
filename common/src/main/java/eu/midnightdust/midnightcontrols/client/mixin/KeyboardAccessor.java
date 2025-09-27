package eu.midnightdust.midnightcontrols.client.mixin;

import net.minecraft.client.Keyboard;
import net.minecraft.client.input.KeyInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Keyboard.class)
public interface KeyboardAccessor {
    @Invoker("onKey")
    void midnightcontrols$onKey(long window, int action, KeyInput input);
}
