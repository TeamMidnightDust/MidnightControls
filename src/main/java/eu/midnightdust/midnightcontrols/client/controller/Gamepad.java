/*
 * Copyright © 2021 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of midnightcontrols.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package eu.midnightdust.midnightcontrols.client.controller;

import eu.midnightdust.lib.util.PlatformFunctions;
import eu.midnightdust.midnightcontrols.MidnightControls;
import eu.midnightdust.midnightcontrols.client.MidnightControlsClient;
import eu.midnightdust.midnightcontrols.client.MidnightControlsConfig;
import eu.midnightdust.midnightcontrols.client.compat.EmotecraftCompat;
import eu.midnightdust.midnightcontrols.client.enums.ButtonState;
import eu.midnightdust.midnightcontrols.client.gui.RingScreen;
import eu.midnightdust.midnightcontrols.client.sdl.ButtonCodes;
import eu.midnightdust.midnightcontrols.client.util.MathUtil;
import eu.midnightdust.midnightcontrols.client.util.storage.AxisStorage;
import eu.midnightdust.midnightcontrols.client.util.storage.ButtonStorage;

import java.io.*;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

import static eu.midnightdust.midnightcontrols.client.MidnightControlsClient.client;
import static eu.midnightdust.midnightcontrols.client.MidnightControlsClient.input;
import static eu.midnightdust.midnightcontrols.client.MidnightInput.BUTTON_COOLDOWNS;
import static eu.midnightdust.midnightcontrols.client.sdl.AxisCodes.*;
import static eu.midnightdust.midnightcontrols.client.sdl.AxisCodes.GAMEPAD_AXIS_RIGHT_TRIGGER;
import static org.lwjgl.BufferUtils.createByteBuffer;

//? sdl {
import org.lwjgl.sdl.SDLGamepad;
import org.lwjgl.sdl.SDLJoystick;
//?} glfw {
//import org.lwjgl.glfw.GLFW;
//import org.lwjgl.glfw.GLFWGamepadState;
//?}

/**
 * Represents a controller.
 *
 * @author LambdAurora
 * @version 1.7.0
 * @since 1.0.0
 */
public final class Gamepad extends Joystick {
    //? if sdl {
    private final long instancePointer;

    public Gamepad(int id, long instancePointer) {
        super(id, instancePointer);
        this.instancePointer = SDLGamepad.SDL_OpenGamepad(id);
    }
    //?} else if glfw {
//    private final GLFWGamepadState gamepadState;
//    public Gamepad(int id) {
//        super(id);
//        gamepadState = GLFWGamepadState.create();
//        if (this.isGamepad())
//            GLFW.glfwGetGamepadState(getId(), gamepadState);
//    }
    //?}

    public void fetchAll(boolean leftJoycon) {
        this.fetchButtonInput(leftJoycon);
        this.fetchJoystickInput(leftJoycon, false);
        this.fetchTriggerInput(leftJoycon);
    }

    private void fetchButtonInput(boolean leftJoycon) {
        //? if sdl {
        for (int i = ButtonCodes.GAMEPAD_BUTTON_SOUTH; i < ButtonCodes.GAMEPAD_BUTTON_MISC6; i++) {
            if (!SDLGamepad.SDL_GamepadHasButton(instancePointer, i)) continue;
            boolean pressed = SDLGamepad.SDL_GetGamepadButton(instancePointer, i);
        //?} else if glfw {
//        var buffer = gamepadState.buttons();
//        for (int i = 0; i < buffer.limit(); i++) {
//            boolean pressed = buffer.get() == (byte) 1;
        //?}
            int btn = leftJoycon ? ButtonBinding.controller2Button(i) : i;
            var state = ButtonState.NONE;
            var previousState = InputManager.STATES.getOrDefault(btn, ButtonState.NONE);

            if (pressed != previousState.isPressed()) {
                state = pressed ? ButtonState.PRESS : ButtonState.RELEASE;
                input.handleButton(ButtonStorage.of(btn, state));
                if (pressed)
                    BUTTON_COOLDOWNS.put(btn, 5);
            } else if (pressed) {
                state = ButtonState.REPEAT;
                if (BUTTON_COOLDOWNS.getOrDefault(btn, 0) == 0) {
                    BUTTON_COOLDOWNS.put(btn, 5);
                    input.handleButton(ButtonStorage.of(btn, state));
                }
            }

            InputManager.STATES.put(btn, state);
        }
    }

    static final MathUtil.PolarUtil polarUtil = new MathUtil.PolarUtil();

    public void fetchJoystickInput(boolean leftJoycon, boolean cameraTick) {
        //? if sdl {
        Function<Integer, Float> getAxisValue = (axis) -> SDLGamepad.SDL_GetGamepadAxis(instancePointer, axis) / 32767f;
        //?} else if glfw {
//        var buffer = gamepadState.axes();
//        Function<Integer, Float> getAxisValue = (axis) -> buffer.get(axis);
        //?}

        polarUtil.calculate(getAxisValue.apply(GAMEPAD_AXIS_LEFT_X), getAxisValue.apply(GAMEPAD_AXIS_LEFT_Y), 1, MidnightControlsConfig.leftDeadZone);
        float leftX = polarUtil.polarX;
        float leftY = polarUtil.polarY;
        polarUtil.calculate(getAxisValue.apply(GAMEPAD_AXIS_RIGHT_X), getAxisValue.apply(GAMEPAD_AXIS_RIGHT_Y), 1, MidnightControlsConfig.rightDeadZone);
        float rightX = polarUtil.polarX;
        float rightY = polarUtil.polarY;

        boolean isRadialMenu = client.gui.screen() instanceof RingScreen || (PlatformFunctions.isModLoaded("emotecraft") && EmotecraftCompat.isEmotecraftScreen(client.gui.screen()));

        if (!isRadialMenu) {
            for (int i = cameraTick ? GAMEPAD_AXIS_RIGHT_X : 0; i < (cameraTick ? GAMEPAD_AXIS_LEFT_TRIGGER : GAMEPAD_AXIS_RIGHT_X); i++) {
                int axis = leftJoycon ? ButtonBinding.controller2Button(i) : i;
                float value = getAxisValue.apply(i);

                switch (i) {
                    case GAMEPAD_AXIS_LEFT_X -> {
                        if (MidnightControlsConfig.isAnalogMovementAllowed()) value = leftX;
                    }
                    case GAMEPAD_AXIS_LEFT_Y -> {
                        if (MidnightControlsConfig.isAnalogMovementAllowed()) value = leftY;
                    }
                    case GAMEPAD_AXIS_RIGHT_X -> value = rightX;
                    case GAMEPAD_AXIS_RIGHT_Y -> value = rightY;
                }

                if (i == GAMEPAD_AXIS_LEFT_Y)
                    value *= -1.0F;

                input.handleJoystickAxis(AxisStorage.of(axis, value));
            }
        } else {
            boolean leftStickActive = leftX != 0 || leftY != 0;
            input.handleRadialMenu(leftStickActive ? leftX : rightX, leftStickActive ? leftY : rightY);
        }
    }

    private void fetchTriggerInput(boolean leftJoycon) {
        //? if sdl {
        Function<Integer, Short> getAxisValue = (axis) -> SDLGamepad.SDL_GetGamepadAxis(instancePointer, axis);
        //?} else if glfw {
//        var buffer = gamepadState.axes();
//        Function<Integer, Float> getAxisValue = (axis) -> buffer.get(axis);
        //?}

        for (int i = GAMEPAD_AXIS_LEFT_TRIGGER; i <= GAMEPAD_AXIS_RIGHT_TRIGGER; i++) {
            int axis = leftJoycon ? ButtonBinding.controller2Button(i) : i;
            float value = getAxisValue.apply(i);

            input.handleTriggerAxis(AxisStorage.of(axis, value, MidnightControlsConfig.triggerDeadZone));
        }
    }

    public String getMapping() {
        return SDLGamepad.SDL_GetGamepadMapping(instancePointer);
    }

    /**
     * Reads the specified resource and returns the raw data as a ByteBuffer.
     *
     * @param resource the resource to read
     * @return the resource data
     * @throws IOException If an IO error occurs.
     */
    private static ByteBuffer ioResourceToBuffer(String resource) throws IOException {
        ByteBuffer buffer = null;

        var path = Paths.get(resource);
        if (Files.isReadable(path)) {
            try (var fc = Files.newByteChannel(path)) {
                buffer = createByteBuffer((int) fc.size() + 2);
                while (fc.read(buffer) != -1) ;
                buffer.put((byte) 0);
            }
        }

        if (buffer != null) buffer.flip(); // Force Java 8 >.<
        return buffer;
    }

    /**
     * Updates the controller mappings.
     */
    public static void updateMappings() {
        CompletableFuture.supplyAsync(Gamepad::updateMappingsSync);
    }

    private static boolean updateMappingsSync() {
        try {
            MidnightControls.log("Updating controller mappings...");
            Optional<File> databaseFile = getDatabaseFile();
            if (databaseFile.isPresent()) {
                var database = ioResourceToBuffer(databaseFile.get().getPath());
                //? sdl
                if (database != null) SDLGamepad.SDL_AddGamepadMappingsFromFile(database);
                //? glfw
                //if (database != null) GLFW.glfwUpdateGamepadMappings(database);
            }
            if (!MidnightControlsClient.MAPPINGS_FILE.exists())
                return false;
            var buffer = ioResourceToBuffer(MidnightControlsClient.MAPPINGS_FILE.getPath());
            //? sdl
            if (buffer != null) SDLGamepad.SDL_AddGamepadMappingsFromFile(buffer);
            SDLGamepad.SDL_ReloadGamepadMappings();
            //? glfw
            //if (buffer != null) GLFW.glfwUpdateGamepadMappings(buffer);
        } catch (IOException e) {
            e.fillInStackTrace();
        }

//        try (var memoryStack = MemoryStack.stackPush()) {
//            var pointerBuffer = memoryStack.mallocPointer(1);
//            int i = GLFW.glfwGetError(pointerBuffer);
//            if (i != 0) {
//                long l = pointerBuffer.get();
//                var string = l == 0L ? "" : MemoryUtil.memUTF8(l);
//                showToastMessage(Component.translatable("midnightcontrols.controller.mappings.error"), Component.literal(string));
//                MidnightControls.log(I18n.get("midnightcontrols.controller.mappings.error")+string);
//            }
//        } catch (Throwable e) {
//            /* Ignored :concern: */
//        }

        if (MidnightControlsConfig.debug) {
            //? sdl {
            var joysticks = SDLJoystick.SDL_GetJoysticks();
            if (joysticks == null) return true;
            while (joysticks.hasRemaining()) {
                int i = joysticks.get();
                //?} else if glfw {
                //for (int i = GLFW.GLFW_JOYSTICK_1; i <= GLFW.GLFW_JOYSTICK_16; i++) {
                //?}
                var joystick = byId(i);

                if (!joystick.isConnected())
                    continue;

                MidnightControls.log(String.format("Controller #%d name: \"%s\"\n GUID: %s\n Gamepad: %s",
                        joystick.getId(),
                        joystick.getName(),
                        joystick.getGuid(),
                        joystick.isGamepad()));
            }
        }
        return true;
    }

    private static Optional<File> getDatabaseFile() {
        File databaseFile = new File("config/gamecontrollerdatabase.txt");
        try {
            BufferedInputStream in = new BufferedInputStream(URI.create("https://raw.githubusercontent.com/gabomdq/SDL_GameControllerDB/master/gamecontrollerdb.txt").toURL().openStream());
            BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(databaseFile));
            byte[] dataBuffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                out.write(dataBuffer, 0, bytesRead);
            }
            out.close();
        } catch (Exception e) {
            return Optional.empty();
        }
        return Optional.of(databaseFile);
    }
}
