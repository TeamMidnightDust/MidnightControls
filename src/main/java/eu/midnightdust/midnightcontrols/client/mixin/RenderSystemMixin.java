package eu.midnightdust.midnightcontrols.client.mixin;

//? if sdl {
import com.mojang.blaze3d.systems.RenderSystem;
import eu.midnightdust.core.MidnightLib;
import eu.midnightdust.midnightcontrols.MidnightControls;
import net.minecraft.util.TimeSource;
import org.lwjgl.sdl.SDLInit;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RenderSystem.class)
public class RenderSystemMixin {
    @Inject(method = "initBackendSystem", at = @At("TAIL"))
    private static void midnightcontrols$initSdlSubsystems(CallbackInfoReturnable<TimeSource.NanoTimeSource> cir) {
        if (SDLInit.SDL_InitSubSystem(SDLInit.SDL_INIT_JOYSTICK | SDLInit.SDL_INIT_GAMEPAD)) {
            MidnightControls.log("Successfully initialized SDL subsystems for Joystick and Gamepad support.");
        }
        else {
            MidnightControls.warn("Could not initialize SDL subsystems for Joystick and Gamepad support.\nController support will not work!");
        }
        if (SDLInit.SDL_InitSubSystem(SDLInit.SDL_INIT_SENSOR)) {
            MidnightControls.log("Successfully initialized SDL subsystem for multitouch support.");
        }
        else {
            MidnightControls.warn("Could not initialize SDL subsystem for multitouch support.\nTouch controls will only register one finger!");
        }
    }
}
//?} else {
/*import org.spongepowered.asm.mixin.Mixin;
import eu.midnightdust.core.MidnightLib;
@Mixin(MidnightLib.class)
public class RenderSystemMixin {}
*///?}