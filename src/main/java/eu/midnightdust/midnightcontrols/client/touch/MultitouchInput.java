package eu.midnightdust.midnightcontrols.client.touch;

//? if sdl {
import eu.midnightdust.midnightcontrols.client.sdl.ButtonCodes;
import eu.midnightdust.midnightcontrols.client.util.storage.AxisStorage;
import org.lwjgl.sdl.SDLEvents;
import org.lwjgl.sdl.SDLTouch;
import org.lwjgl.sdl.SDL_TouchFingerEvent;

import static eu.midnightdust.midnightcontrols.client.MidnightControlsClient.input;
import static eu.midnightdust.midnightcontrols.client.sdl.AxisCodes.GAMEPAD_AXIS_RIGHT_X;
import static eu.midnightdust.midnightcontrols.client.sdl.AxisCodes.GAMEPAD_AXIS_RIGHT_Y;

public class MultitouchInput {
    public static void onFingerEvent(SDL_TouchFingerEvent tfinger, int type) {
        // Reject indirect (trackpad) input
        if (SDLTouch.SDL_GetTouchDeviceType(tfinger.touchID()) != SDLTouch.SDL_TOUCH_DEVICE_DIRECT) return;

        switch (type) {
            case SDLEvents.SDL_EVENT_FINGER_DOWN -> {
                TouchInput.forFinger(tfinger.fingerID()).mouseHeldDown(tfinger.x(), tfinger.y());
            }
            case SDLEvents.SDL_EVENT_FINGER_MOTION -> {
                input.handleTouchscreenLook(AxisStorage.of(GAMEPAD_AXIS_RIGHT_Y, tfinger.dy(), 0.25d));
                input.handleTouchscreenLook(AxisStorage.of(GAMEPAD_AXIS_RIGHT_X, tfinger.dx(), 0.25d));
            }
            case SDLEvents.SDL_EVENT_FINGER_UP | SDLEvents.SDL_EVENT_FINGER_CANCELED -> {
                TouchInput.forFinger(tfinger.fingerID()).mouseReleased(tfinger.x(), tfinger.y(), ButtonCodes.MOUSE_BUTTON_LEFT);
            }
        }
    }
}
//?}