package eu.midnightdust.midnightcontrols.client.mixin;

import eu.midnightdust.midnightcontrols.client.gui.MidnightControlsHud;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
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

    @Inject(method = "renderHotbarAndDecorations", at = @At("HEAD"))
    public void midnightcontrols$renderHud(GuiGraphics context, DeltaTracker tickCounter, CallbackInfo ci) {
        MidnightControlsHud.getInstance().render(context, tickCounter);
    }

    @Inject(method = "tick()V", at = @At("HEAD"))
    public void midnightcontrols$tickHud(CallbackInfo ci) {
        MidnightControlsHud.getInstance().tick();
    }
}
