package eu.midnightdust.midnightcontrols.client.mixin;

import net.minecraft.client.Mouse;
import net.minecraft.client.input.MouseInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Mouse.class)
public interface MouseAccessor {
    @Invoker("onCursorPos")
    void midnightcontrols$onCursorPos(long window, double x, double y);
    @Invoker("onMouseButton")
    void midnightcontrols$onMouseButton(long window, MouseInput input, int action);
}
