package eu.midnightdust.midnightcontrols.client.mixin;

import net.minecraft.client.util.InputUtil;
import org.spongepowered.asm.mixin.Mixin;
import eu.midnightdust.midnightcontrols.client.MidnightControlsConfig;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(InputUtil.class)
public abstract class InputUtilMixin {

    /**
     * @author kabliz
     * @reason This method is static, and there is a terrible UX issue if raw input is turned on at the same time as
     * eye tracking. Raw input only tracks literal mice and not other devices, leading to the game appearing to be
     * unresponsive and the player not understanding why. This overwrite preserves the user's mouse preferences,
     * while not interfering with eye tracking, and the two modes can be switched between during a play session.
     */
    @Inject(method = "isRawMouseMotionSupported", at = @At("HEAD"), cancellable = true)
    private static void setRawMouseMotionSupported(CallbackInfoReturnable<Boolean> cir) {
        if (MidnightControlsConfig.eyeTrackerAsMouse) cir.setReturnValue(false);
    }
}
