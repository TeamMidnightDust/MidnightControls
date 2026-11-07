package eu.midnightdust.midnightcontrols.client.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.platform.SDLEventHandler;
import eu.midnightdust.midnightcontrols.client.MidnightControlsClient;
import org.lwjgl.sdl.SDLEvents;
import org.lwjgl.sdl.SDL_Event;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SDLEventHandler.class)
public class SDLEventHandlerMixin {
    @Inject(method = "pollEvents", at = @At(value = "INVOKE", target = "Lorg/lwjgl/sdl/SDL_Event;type()I"))
    private void midnightcontrols$listenToControllerConnection(CallbackInfo ci, @Local(name = "event") SDL_Event event) {
        if (event.type() == SDLEvents.SDL_EVENT_JOYSTICK_ADDED || event.type() == SDLEvents.SDL_EVENT_JOYSTICK_REMOVED) {
            MidnightControlsClient.onControllerConnectionChanged(event.jdevice().which(), event.type());
        }
    }
}
