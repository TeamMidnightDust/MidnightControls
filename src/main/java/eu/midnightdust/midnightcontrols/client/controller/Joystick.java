package eu.midnightdust.midnightcontrols.client.controller;

import eu.midnightdust.midnightcontrols.client.MidnightControlsConfig;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.sdl.SDLGUID;
import org.lwjgl.sdl.SDLGamepad;
import org.lwjgl.sdl.SDLJoystick;
import org.lwjgl.sdl.SDL_GUID;
import org.lwjgl.system.MemoryStack;

import java.nio.charset.StandardCharsets;
import java.util.*;

public class Joystick {
    private static final Map<Integer, Joystick> JOYSTICKS = new HashMap<>();
    private final int id;
    //? sdl
    private final long instancePointer;
    public static final Joystick NONE = new Joystick();

    private Joystick() {
        id = -1;
        instancePointer = -1;
    }

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
        boolean exists = false;
        while (sdlJoysticks.hasRemaining()) {
            if (sdlJoysticks.get() == id) exists = true;
        }
        if (!exists) {
            //MidnightControls.log("Controller '" + id + "' doesn't exist.");
            return NONE;
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

    public static int searchNextAvailableJoystick(int newId, boolean allowNone) {
        if ((allowNone && newId == -1) || newId == 0) return newId;

        Joystick candidate = Joystick.byId(newId);
        boolean connected = candidate.isConnected();
        if (MidnightControlsConfig.excludedControllers.stream().anyMatch(exclusion -> candidate.getName().matches(exclusion))) connected = false;
        if (!connected) {
            newId++;
        }

        //? sdl {
        var joysticks = SDLJoystick.SDL_GetJoysticks();
        while (joysticks != null && joysticks.hasRemaining()) {

            if (joysticks.get() == newId && !joysticks.hasRemaining()) newId = joysticks.get(0);
        }
        //?} else if glfw {
//        if (newId > GLFW.GLFW_JOYSTICK_LAST)
//            newId = allowNone ? -1 : GLFW.GLFW_JOYSTICK_1;
        //?}

        return connected ? newId : searchNextAvailableJoystick(newId, allowNone);
    }

    /**
     * Gets the controller's globally unique identifier.
     *
     * @return the controller's GUID
     */
    public String getGuid() {
        if (this == NONE) return "No controller selected";
        //? if >=26.3 {
        String guid = null;
        try (final MemoryStack stack = MemoryStack.stackPush()) {
            final SDL_GUID rawGuid = SDL_GUID.malloc(stack);
            SDLJoystick.SDL_GetJoystickGUID(instancePointer, rawGuid);
            final var buffer = stack.calloc(33);
            SDLGUID.SDL_GUIDToString(rawGuid, buffer);
            guid = StandardCharsets.UTF_8.decode(buffer).toString();
            final int nullPos = guid.indexOf('\u0000');
            if (nullPos != -1) {
                guid = guid.substring(0, nullPos);
            }
        } catch (Exception ignored) {}

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
        if (this == NONE) return false;
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
        if (this == NONE) return false;
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
        if (this == NONE) return "None";
        //? if >=26.3 {
        var name = SDLJoystick.SDL_GetJoystickName(this.instancePointer);
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
