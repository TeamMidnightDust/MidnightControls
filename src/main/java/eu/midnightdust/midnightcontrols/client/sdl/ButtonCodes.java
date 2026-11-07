package eu.midnightdust.midnightcontrols.client.sdl;

import com.mojang.blaze3d.platform.InputConstants;

// Valid button codes for SDL and GLFW
public abstract class ButtonCodes {
    // Keyboard buttons
    public static final int
            KEYBOARD_0 = 39,
            KEYBOARD_1 = 30,
            KEYBOARD_2 = 31,
            KEYBOARD_3 = 32,
            KEYBOARD_4 = 33,
            KEYBOARD_5 = 34,
            KEYBOARD_6 = 35,
            KEYBOARD_7 = 36,
            KEYBOARD_8 = 37,
            KEYBOARD_9 = 38,
            KEYBOARD_A = 4,
            KEYBOARD_B = 5,
            KEYBOARD_C = 6,
            KEYBOARD_D = 7,
            KEYBOARD_E = 8,
            KEYBOARD_F = 9,
            KEYBOARD_G = 10,
            KEYBOARD_H = 11,
            KEYBOARD_I = 12,
            KEYBOARD_J = 13,
            KEYBOARD_K = 14,
            KEYBOARD_L = 15,
            KEYBOARD_M = 16,
            KEYBOARD_N = 17,
            KEYBOARD_O = 18,
            KEYBOARD_P = 19,
            KEYBOARD_Q = 20,
            KEYBOARD_R = 21,
            KEYBOARD_S = 22,
            KEYBOARD_T = 23,
            KEYBOARD_U = 24,
            KEYBOARD_V = 25,
            KEYBOARD_W = 26,
            KEYBOARD_X = 27,
            KEYBOARD_Y = 28,
            KEYBOARD_Z = 29,
            KEYBOARD_F1 = 58,
            KEYBOARD_F2 = 59,
            KEYBOARD_F3 = 60,
            KEYBOARD_F4 = 61,
            KEYBOARD_F5 = 62,
            KEYBOARD_F6 = 63,
            KEYBOARD_F7 = 64,
            KEYBOARD_F8 = 65,
            KEYBOARD_F9 = 66,
            KEYBOARD_F10 = 67,
            KEYBOARD_F11 = 68,
            KEYBOARD_F12 = 69,
            KEYBOARD_F13 = 104,
            KEYBOARD_F14 = 105,
            KEYBOARD_F15 = 106,
            KEYBOARD_F16 = 107,
            KEYBOARD_F17 = 108,
            KEYBOARD_F18 = 109,
            KEYBOARD_F19 = 110,
            KEYBOARD_F20 = 111,
            KEYBOARD_F21 = 112,
            KEYBOARD_F22 = 113,
            KEYBOARD_F23 = 114,
            KEYBOARD_F24 = 115,
            KEYBOARD_NUMLOCK = 83,
            KEYBOARD_NUMPAD0 = 98,
            KEYBOARD_NUMPAD1 = 89,
            KEYBOARD_NUMPAD2 = 90,
            KEYBOARD_NUMPAD3 = 91,
            KEYBOARD_NUMPAD4 = 92,
            KEYBOARD_NUMPAD5 = 93,
            KEYBOARD_NUMPAD6 = 94,
            KEYBOARD_NUMPAD7 = 95,
            KEYBOARD_NUMPAD8 = 96,
            KEYBOARD_NUMPAD9 = 97,
            KEYBOARD_NUMPADCOMMA = 220,
            KEYBOARD_NUMPADENTER = 88,
            KEYBOARD_NUMPADEQUALS = 103,
            KEYBOARD_DOWN = 81,
            KEYBOARD_LEFT = 80,
            KEYBOARD_RIGHT = 79,
            KEYBOARD_UP = 82,
            KEYBOARD_ADD = 87,
            KEYBOARD_APOSTROPHE = 52,
            KEYBOARD_BACKSLASH = 49,
            KEYBOARD_COMMA = 54,
            KEYBOARD_EQUALS = 46,
            KEYBOARD_GRAVE = 53,
            KEYBOARD_LBRACKET = 47,
            KEYBOARD_MINUS = 45,
            KEYBOARD_MULTIPLY = 85,
            KEYBOARD_PERIOD = 55,
            KEYBOARD_RBRACKET = 48,
            KEYBOARD_SEMICOLON = 51,
            KEYBOARD_SLASH = 56,
            KEYBOARD_SPACE = 44,
            KEYBOARD_TAB = 43,
            KEYBOARD_LALT = 226,
            KEYBOARD_LCONTROL = 224,
            KEYBOARD_LSHIFT = 225,
            KEYBOARD_LGUI = 227,
            KEYBOARD_RALT = 230,
            KEYBOARD_RCONTROL = 228,
            KEYBOARD_RSHIFT = 229,
            KEYBOARD_RGUI = 231,
            KEYBOARD_RETURN = 40,
            KEYBOARD_ESCAPE = 41,
            KEYBOARD_BACKSPACE = 42,
            KEYBOARD_DELETE = 76,
            KEYBOARD_END = 77,
            KEYBOARD_HOME = 74,
            KEYBOARD_INSERT = 73,
            KEYBOARD_PAGEDOWN = 78,
            KEYBOARD_PAGEUP = 75,
            KEYBOARD_CAPSLOCK = 57,
            KEYBOARD_PAUSE = 72,
            KEYBOARD_SCROLLLOCK = 71,
            KEYBOARD_PRINTSCREEN = 70;

    // Keycodes
    public static final int
            KEYCODE_UP = 1073741906,
            KEYCODE_DOWN = 1073741905,
            KEYCODE_FORWARD = 1073741906,
            KEYCODE_BACKWARD = 1073741905,
            KEYCODE_LEFT = 1073741904,
            KEYCODE_RIGHT = 1073741903;

    // Mouse buttons
    public static final int
            MOUSE_BUTTON_LEFT             = 1,
            MOUSE_BUTTON_MIDDLE           = 2,
            MOUSE_BUTTON_RIGHT            = 3,
            MOUSE_BUTTON_BACK             = 4,
            MOUSE_BUTTON_FORWARD          = 5;

    // Gamepad buttons
    public static final int
            GAMEPAD_BUTTON_INVALID = -1,
            GAMEPAD_BUTTON_SOUTH = 0,           /*< Bottom face button (e.g. Xbox A button) */
            GAMEPAD_BUTTON_EAST = 1,            /*< Right face button (e.g. Xbox B button) */
            GAMEPAD_BUTTON_WEST = 2,            /*< Left face button (e.g. Xbox X button) */
            GAMEPAD_BUTTON_NORTH = 3,           /*< Top face button (e.g. Xbox Y button) */
            GAMEPAD_BUTTON_BACK = 4,
            GAMEPAD_BUTTON_GUIDE = 5,
            GAMEPAD_BUTTON_START = 6,
            GAMEPAD_BUTTON_LEFT_STICK = 7,
            GAMEPAD_BUTTON_RIGHT_STICK = 8,
            GAMEPAD_BUTTON_LEFT_SHOULDER = 9,
            GAMEPAD_BUTTON_RIGHT_SHOULDER = 10,
            GAMEPAD_BUTTON_DPAD_UP = 11,
            GAMEPAD_BUTTON_DPAD_DOWN = 12,
            GAMEPAD_BUTTON_DPAD_LEFT = 13,
            GAMEPAD_BUTTON_DPAD_RIGHT = 14,
            GAMEPAD_BUTTON_MISC1 = 15,           /*< Additional button (e.g. Xbox Series X share button, PS5 microphone button, Nintendo Switch Pro capture button, Steam Controller QAM button, Amazon Luna microphone button, Google Stadia capture button) */
            GAMEPAD_BUTTON_RIGHT_PADDLE1 = 16,   /*< Upper or primary paddle, under your right hand (e.g. Xbox Elite paddle P1, DualSense Edge RB button, Right Joy-Con SR button, Steam Controller R4 button) */
            GAMEPAD_BUTTON_LEFT_PADDLE1 = 17,    /*< Upper or primary paddle, under your left hand (e.g. Xbox Elite paddle P3, DualSense Edge LB button, Left Joy-Con SL button, Steam Controller L4 button) */
            GAMEPAD_BUTTON_RIGHT_PADDLE2 = 18,   /**< Lower or secondary paddle, under your right hand (e.g. Xbox Elite paddle P2, DualSense Edge right Fn button, Right Joy-Con SL button, Steam Controller R5 button) */
            GAMEPAD_BUTTON_LEFT_PADDLE2 = 19,    /*< Lower or secondary paddle, under your left hand (e.g. Xbox Elite paddle P4, DualSense Edge left Fn button, Left Joy-Con SR button, Steam Controller L5 button) */
            GAMEPAD_BUTTON_TOUCHPAD = 20,        /*< PS4/PS5 touchpad button */
            GAMEPAD_BUTTON_MISC2 = 21,           /*< Additional button */
            GAMEPAD_BUTTON_MISC3 = 22,           /*< Additional button (e.g. Nintendo GameCube left trigger click) */
            GAMEPAD_BUTTON_MISC4 = 23,           /*< Additional button (e.g. Nintendo GameCube right trigger click) */
            GAMEPAD_BUTTON_MISC5 = 24,           /*< Additional button */
            GAMEPAD_BUTTON_MISC6 = 25;           /*< Additional button */
}
