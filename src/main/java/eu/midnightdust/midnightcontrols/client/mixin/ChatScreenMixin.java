package eu.midnightdust.midnightcontrols.client.mixin;

import eu.midnightdust.midnightcontrols.client.MidnightControlsConfig;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatScreen.class)
public abstract class ChatScreenMixin extends Screen {
    @Shadow protected EditBox input;

    protected ChatScreenMixin(Component title) {
        super(title);
    }

    @Inject(at = @At("TAIL"), method = "init")
    private void midnightcontrols$moveInputField(CallbackInfo ci) {
        if (MidnightControlsConfig.moveChat) input.setY(4);
    }
    //~ if >= 26.1 'render' -> 'extractRenderState' {
    @Inject(method = "extractRenderState", at = @At("HEAD"))
    private void midnightcontrols$moveInputFieldBackground(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (MidnightControlsConfig.moveChat) context.pose().translate(0f, -this.height + 16);
    }
    @Inject(method = "extractRenderState", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;fill(IIIII)V", shift = At.Shift.AFTER))
    private void midnightcontrols$dontMoveOtherStuff(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (MidnightControlsConfig.moveChat) context.pose().translate(0f, this.height - 16);
    }
    //~}
}
