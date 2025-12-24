package eu.midnightdust.midnightcontrols.client.mixin;

import net.minecraft.client.MouseHandler;
import net.minecraft.client.input.MouseButtonInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(MouseHandler.class)
public interface MouseAccessor {
    @Invoker("onMove")
    void midnightcontrols$onCursorPos(long window, double x, double y);
    @Invoker("onButton")
    void midnightcontrols$onMouseButton(long window, MouseButtonInfo input, int action);
}
