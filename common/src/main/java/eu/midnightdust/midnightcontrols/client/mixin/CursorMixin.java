package eu.midnightdust.midnightcontrols.client.mixin;

import eu.midnightdust.midnightcontrols.client.gui.cursor.CursorRenderer;
import net.minecraft.client.gui.cursor.Cursor;
import net.minecraft.client.util.Window;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Cursor.class)
public abstract class CursorMixin {
    @Inject(method = "applyTo", at = @At("TAIL"))
    public void midnightcontrols$applyCursorStyle(Window window, CallbackInfo ci) {
        CursorRenderer.currentCursorStyle = ((Cursor) (Object) this);
    }
}
