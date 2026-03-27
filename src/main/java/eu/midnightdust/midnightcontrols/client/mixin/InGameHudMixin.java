package eu.midnightdust.midnightcontrols.client.mixin;

import eu.midnightdust.midnightcontrols.client.gui.MidnightControlsHud;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public class InGameHudMixin {

    @Inject(method = "<init>", at = @At("TAIL"))
    private static void midnightcontrols$initHud(Minecraft client, CallbackInfo ci) {
        MidnightControlsHud.getInstance().init();
    }

    //~ if >= 26.1 'render' -> 'extract'
    //? fabric {
    @Inject(method = "extractHotbarAndDecorations", at = @At("HEAD"))
    //?} else if neoforge {
    /*@Inject(method = "renderCrosshair", at = @At("HEAD"))
    *///?}
    public void midnightcontrols$renderHud(GuiGraphicsExtractor context, DeltaTracker tickCounter, CallbackInfo ci) {
        MidnightControlsHud.getInstance().extractRenderState(context, tickCounter);
    }

    @Inject(method = "tick()V", at = @At("HEAD"))
    public void midnightcontrols$tickHud(CallbackInfo ci) {
        MidnightControlsHud.getInstance().tick();
    }
}
