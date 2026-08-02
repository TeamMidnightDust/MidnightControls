package eu.midnightdust.midnightcontrols.client.mixin;

//? if >= 26.2 {
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;

import com.llamalad7.mixinextras.sugar.Local;
import eu.midnightdust.midnightcontrols.ControlsMode;
import eu.midnightdust.midnightcontrols.client.MidnightControlsClient;
import eu.midnightdust.midnightcontrols.client.MidnightControlsConfig;
import eu.midnightdust.midnightcontrols.client.gui.cursor.VirtualCursorRenderer;
import eu.midnightdust.midnightcontrols.client.gui.cursor.WaylandCursorRenderer;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public abstract class GuiMixin {
    @Shadow
    @Final
    private Minecraft minecraft;

    @Shadow
    public abstract Screen screen();

    @WrapMethod(method = "setScreen")
    private void midnightcontrols$onSetScreen(Screen screen, Operation<Void> original) {
        MidnightControlsClient.onScreenOpen(screen, original);
    }

    @Inject(method = "extractRenderState", at = @At("HEAD"))
    private void midnightcontrols$onRender(DeltaTracker deltaTracker, final boolean shouldRenderLevel, final boolean resourcesLoaded, CallbackInfo ci) {
        if (this.screen() != null && MidnightControlsConfig.controlsMode == ControlsMode.CONTROLLER)
            MidnightControlsClient.input.onPreRenderScreen(this.screen());
    }

    @Inject(method = "extractRenderState", at = @At("TAIL"))
    private void midnightcontrols$renderVirtualCursor(DeltaTracker deltaTracker, final boolean shouldRenderLevel, final boolean resourcesLoaded, CallbackInfo ci, @Local(name = "graphics") GuiGraphicsExtractor graphics) {
        VirtualCursorRenderer.getInstance().renderCursor(graphics, minecraft);
        if (MidnightControlsClient.isWayland) WaylandCursorRenderer.getInstance().renderCursor(graphics, minecraft);
    }
}
//?} else {
/*import eu.midnightdust.core.MidnightLib;
import org.spongepowered.asm.mixin.Mixin;
@Mixin(MidnightLib.class)
public class GuiMixin {}
*///?}
