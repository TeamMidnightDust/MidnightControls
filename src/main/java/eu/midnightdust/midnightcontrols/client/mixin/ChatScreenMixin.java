package eu.midnightdust.midnightcontrols.client.mixin;

import eu.midnightdust.midnightcontrols.client.MidnightControlsConfig;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatScreen.class)
public abstract class ChatScreenMixin extends Screen {
    @Shadow protected TextFieldWidget chatField;

    protected ChatScreenMixin(Text title) {
        super(title);
    }

    @Inject(at = @At("TAIL"), method = "init")
    private void midnightcontrols$moveInputField(CallbackInfo ci) {
        if (MidnightControlsConfig.moveChat) chatField.setY(4);
    }
    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/widget/TextFieldWidget;setTextFieldFocused(Z)V", shift = At.Shift.AFTER))
    private void midnightcontrols$moveInputFieldBackground(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (MidnightControlsConfig.moveChat) matrices.translate(0f, -this.height + 16, 0f);
    }
    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/widget/TextFieldWidget;render(Lnet/minecraft/client/util/math/MatrixStack;IIF)V", shift = At.Shift.BEFORE))
    private void midnightcontrols$dontMoveOtherStuff(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (MidnightControlsConfig.moveChat) matrices.translate(0f, this.height - 16, 0f);
    }
}
