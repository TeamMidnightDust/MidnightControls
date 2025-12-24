package eu.midnightdust.midnightcontrols.client.mixin;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.platform.cursor.CursorType;
import eu.midnightdust.midnightcontrols.client.gui.cursor.CursorRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CursorType.class)
public abstract class CursorMixin {
    @Inject(method = "select", at = @At("TAIL"))
    public void midnightcontrols$applyCursorStyle(Window window, CallbackInfo ci) {
        CursorRenderer.currentCursorStyle = ((CursorType) (Object) this);
    }
}
