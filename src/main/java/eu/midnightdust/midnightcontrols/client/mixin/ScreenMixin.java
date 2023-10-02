package eu.midnightdust.midnightcontrols.client.mixin;

import dev.lambdaurora.spruceui.Position;
import eu.midnightdust.midnightcontrols.ControlsMode;
import eu.midnightdust.midnightcontrols.client.ButtonState;
import eu.midnightdust.midnightcontrols.client.MidnightControlsConfig;
import eu.midnightdust.midnightcontrols.client.controller.ButtonBinding;
import eu.midnightdust.midnightcontrols.client.controller.InputHandlers;
import eu.midnightdust.midnightcontrols.client.touch.gui.SilentTexturedButtonWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static eu.midnightdust.midnightcontrols.client.gui.TouchscreenOverlay.WIDGETS_LOCATION;

@Mixin(Screen.class)
public abstract class ScreenMixin {
    @Shadow protected abstract <T extends Element & Drawable & Selectable> T addDrawableChild(T drawableElement);

    @Shadow public int width;

    @Inject(method = "init(Lnet/minecraft/client/MinecraftClient;II)V", at = @At("TAIL"))
    public void midnightcontrols$addCloseButton(MinecraftClient client, int width, int height, CallbackInfo ci) {
        if (MidnightControlsConfig.controlsMode == ControlsMode.TOUCHSCREEN && (MidnightControlsConfig.closeButtonScreens.stream().anyMatch(s -> this.getClass().getName().startsWith(s) || ((Object)this) instanceof HandledScreen<?>))) {
            this.addDrawableChild(new SilentTexturedButtonWidget(Position.of(this.width - 30, 10), 20, 20, Text.empty(), btn ->
                    InputHandlers.handleExit().press(client, ButtonBinding.BACK, 0f, ButtonState.PRESS), 20, 160, 20, WIDGETS_LOCATION));
        }
    }
}
