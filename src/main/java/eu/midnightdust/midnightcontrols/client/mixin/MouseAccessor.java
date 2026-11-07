package eu.midnightdust.midnightcontrols.client.mixin;

import net.minecraft.client.MouseHandler;
import net.minecraft.client.input.MouseButtonInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(MouseHandler.class)
public interface MouseAccessor {
    @Invoker("onMove")
    //? if >= 26.3 {
    void midnightcontrols$onCursorPos(final long handle, final double xpos, final double ypos, final double xrel, final double yrel);
    //?} else {
    //void midnightcontrols$onCursorPos(long window, double x, double y);
    //?}
    @Invoker("onButton")
    void midnightcontrols$onMouseButton(long window, MouseButtonInfo input, int action);
}
