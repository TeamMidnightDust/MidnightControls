package eu.midnightdust.midnightcontrols.client.mixin;

import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Mouse.class)
public interface MouseAccessor {
    @Invoker("onCursorPos")
    void midnightcontrols$onCursorPos(long window, double x, double y);
    @Accessor
    void setLeftButtonClicked(boolean value);
    @Invoker("onMouseButton")
    void midnightcontrols$onMouseButton(long window, int button, int action, int mods);
}
