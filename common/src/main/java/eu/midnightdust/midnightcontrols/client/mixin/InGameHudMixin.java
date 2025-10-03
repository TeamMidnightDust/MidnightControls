package eu.midnightdust.midnightcontrols.client.mixin;

import eu.midnightdust.midnightcontrols.client.gui.MidnightControlsHud;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class InGameHudMixin {

    @Inject(method = "<init>", at = @At("TAIL"))
    private static void midnightcontrols$initHud(MinecraftClient client, CallbackInfo ci) {
        MidnightControlsHud.getInstance().init();
    }

    @Inject(method = "renderMainHud", at = @At("HEAD"))
    public void midnightcontrols$renderHud(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        MidnightControlsHud.getInstance().render(context, tickCounter);
    }

    @Inject(method = "tick()V", at = @At("HEAD"))
    public void midnightcontrols$tickHud(CallbackInfo ci) {
        MidnightControlsHud.getInstance().tick();
    }
}
