package eu.midnightdust.midnightcontrols.client.controller;

import eu.midnightdust.midnightcontrols.MidnightControls;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.sdl.SDLGamepad;
import org.lwjgl.sdl.SDLJoystick;

import java.util.*;

public class Joystick {
    private static final Map<Integer, Joystick> JOYSTICKS = new HashMap<>();
    private final int id;
    //? sdl
    private final long instancePointer;

    Joystick(int id) {
        this.id = id;
        //? sdl
        this.instancePointer = SDLJoystick.SDL_OpenJoystick(id);
    }

    //? sdl {
    Joystick(int id, long instancePointer) {
        this.id = id;
        this.instancePointer = instancePointer;
    }
    //?}

    public Optional<Gamepad> asGamepad() {
        if (JOYSTICKS.get(id) instanceof Gamepad gamepad) return Optional.of(gamepad);
        if (isGamepad()) {
            Gamepad gamepad = new Gamepad(this.getId(), instancePointer);
            JOYSTICKS.put(id, gamepad);
            return Optional.of(gamepad);
        }
        return Optional.empty();
    }

    public static Joystick byId(int id) {
        //? if sdl {
        var sdlJoysticks = SDLJoystick.SDL_GetJoysticks();
        if (sdlJoysticks == null) {
            throw new RuntimeException("No joystick connected");
        }
        int finalId = id;
        if (Arrays.stream(sdlJoysticks.array()).noneMatch(j -> j == finalId)) {
            MidnightControls.log("Controller '" + id + "' doesn't exist.");
            id = sdlJoysticks.get();
        }
        //?} else if glfw {
//        if (id > GLFW.GLFW_JOYSTICK_LAST) {
//            MidnightControls.log("Controller '" + id + "' doesn't exist.");
//            id = GLFW.GLFW_JOYSTICK_LAST;
//        }
        //?}
        Joystick joystick;
        if (JOYSTICKS.containsKey(id))
            return JOYSTICKS.get(id);
        else {
            joystick = new Joystick(id);
            JOYSTICKS.put(id, joystick);
            return joystick;
        }
    }

    public static Optional<Joystick> byGuid(@NotNull String guid) {
        return JOYSTICKS.values().stream().filter(Joystick::isConnected)
                .filter(controller -> controller.getGuid().equals(guid))
                .max(Comparator.comparingInt(Joystick::getId));
    }

    /**
     * Gets the controller's globally unique identifier.
     *
     * @return the controller's GUID
     */
    public String getGuid() {
        //? if >=26.3 {
        String guid = SDLGamepad.SDL_GetGamepadSerial(this.instancePointer);
        //?} else {
        //String guid = GLFW.glfwGetJoystickGUID(this.id);
        //?}
        return guid == null ? "" : guid;
    }

    /**
     * Returns whether this controller is connected or not.
     *
     * @return true if this controller is connected, else false
     */
    public boolean isConnected() {
        //? if >=26.3 {
        return SDLJoystick.SDL_JoystickConnected(this.instancePointer);
        //?} else {
        //return GLFW.glfwJoystickPresent(this.id);
        //?}
    }

    /**
     * Returns whether this controller is a gamepad or not.
     *
     * @return true if this controller is a gamepad, else false
     */
    public boolean isGamepad() {
        //? if >=26.3 {
        return this.isConnected() && SDLGamepad.SDL_IsGamepad(this.getId());
        //?} else {
        //return this.isConnected() && GLFW.glfwJoystickIsGamepad(this.id);
        //?}
    }

    /**
     * Gets the name of the controller.
     *
     * @return the controller's name
     */
    public @NotNull String getName() {
        //? if >=26.3 {
        var name = this.isGamepad() ? SDLGamepad.SDL_GetGamepadName(this.instancePointer) : SDLJoystick.SDL_GetJoystickName(this.instancePointer);
        //?} else {
        //var name = this.isGamepad() ? GLFW.glfwGetGamepadName(this.id) : GLFW.glfwGetJoystickName(this.id);
        //?}
        return name == null ? String.valueOf(this.getId()) : name;
    }

    public int getId() {
        return id;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Joystick) obj;
        return this.id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Joystick[" +
                "id=" + id + ']';
    }

}
