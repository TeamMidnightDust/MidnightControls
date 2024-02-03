package eu.midnightdust.midnightcontrols.client.mixin;

import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Mouse.class)
public interface MouseAccessor {
    @Accessor
    void setLeftButtonClicked(boolean value);

    @Invoker("onCursorPos")
    void midnightcontrols$onCursorPos(long window, double x, double y);
}
