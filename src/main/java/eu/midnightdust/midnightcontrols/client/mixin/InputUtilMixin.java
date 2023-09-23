package eu.midnightdust.midnightcontrols.client.mixin;

import net.minecraft.client.util.InputUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Final;
import eu.midnightdust.midnightcontrols.client.MidnightControlsConfig;
import java.lang.invoke.MethodHandle;


@Mixin(InputUtil.class)
public abstract class InputUtilMixin {

    @Final
    @Shadow
    private static MethodHandle GLFW_RAW_MOUSE_MOTION_SUPPORTED_HANDLE;

    /**
     * @author kabliz
     * @reason This method is static, and there is a terrible UX issue if raw input is turned on at the same time as
     * eye tracking. Raw input only tracks literal mice and not other devices, leading to the game appearing to be
     * unresponsive and the player not understanding why. This overwrite preserves the user's mouse preferences,
     * while not interfering with eye tracking, and the two modes can be switched between during a play session.
     */
    @Overwrite
    public static boolean isRawMouseMotionSupported(){
        if(MidnightControlsConfig.eyeTrackerAsMouse){
             return false;
        } else { //Paste original implementation from InputUtil below.
            try {
                return GLFW_RAW_MOUSE_MOTION_SUPPORTED_HANDLE != null &&
                        (boolean) GLFW_RAW_MOUSE_MOTION_SUPPORTED_HANDLE.invokeExact();
            } catch (Throwable var1) {
                throw new RuntimeException(var1);
            }
        }
    }
}
