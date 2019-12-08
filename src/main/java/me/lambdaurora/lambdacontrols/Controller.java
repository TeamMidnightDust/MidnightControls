/*
 * Copyright © 2019 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of LambdaControls.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package me.lambdaurora.lambdacontrols;

import org.aperlambda.lambdacommon.utils.Nameable;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWGamepadState;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.lwjgl.BufferUtils.createByteBuffer;

/**
 * Represents a controller.
 */
public class Controller implements Nameable
{
    private static final Map<Integer, Controller> CONTROLLERS = new HashMap<>();
    private final        int                      id;

    public Controller(int id)
    {
        this.id = id;
    }

    /**
     * Gets the identifier of this controller.
     *
     * @return The identifier of this controller.
     */
    public int get_id()
    {
        return this.id;
    }

    /**
     * Gets the controller's globally unique identifier.
     *
     * @return The controller's GUID.
     */
    public String get_guid()
    {
        String guid = GLFW.glfwGetJoystickGUID(this.id);
        return guid == null ? "" : guid;
    }

    /**
     * Returns whether this controller is connected or not.
     *
     * @return True if this controller is connected, else false.
     */
    public boolean is_connected()
    {
        return GLFW.glfwJoystickPresent(this.id);
    }

    /**
     * Returns whether this controller is a gamepad or not.
     *
     * @return True if this controller is a gamepad, else false.
     */
    public boolean is_gamepad()
    {
        return GLFW.glfwJoystickIsGamepad(this.id);
    }

    /**
     * Gets the name of the controller.
     *
     * @return The controller's name.
     */
    @Override
    public @NotNull String get_name()
    {
        String name = this.is_gamepad() ? GLFW.glfwGetGamepadName(this.id) : GLFW.glfwGetJoystickName(this.id);
        return name == null ? String.valueOf(this.get_id()) : name;
    }

    /**
     * Gets the state of the controller.
     *
     * @return The state of the controller input.
     */
    public GLFWGamepadState get_state()
    {
        GLFWGamepadState state = GLFWGamepadState.create();
        if (this.is_gamepad())
            GLFW.glfwGetGamepadState(this.id, state);
        return state;
    }

    public static @NotNull Controller by_id(int id)
    {
        if (id > GLFW.GLFW_JOYSTICK_LAST) {
            LambdaControls.get().log("Controller '" + id + "' doesn't exist.");
            id = GLFW.GLFW_JOYSTICK_LAST;
        }
        Controller controller;
        if (CONTROLLERS.containsKey(id))
            return CONTROLLERS.get(id);
        else {
            controller = new Controller(id);
            CONTROLLERS.put(id, controller);
            return controller;
        }
    }

    public static @NotNull Optional<Controller> by_guid(@NotNull String guid)
    {
        return CONTROLLERS.values().stream().filter(Controller::is_connected)
                .filter(controller -> controller.get_guid().equals(guid))
                .max(Comparator.comparingInt(Controller::get_id));
    }

    /**
     * Reads the specified resource and returns the raw data as a ByteBuffer.
     *
     * @param resource    The resource to read.
     * @param buffer_size The initial buffer size.
     * @return The resource data.
     * @throws IOException If an IO error occurs.
     */
    private static ByteBuffer io_resource_to_buffer(String resource, int buffer_size) throws IOException
    {
        ByteBuffer buffer = null;

        Path path = Paths.get(resource);
        if (Files.isReadable(path)) {
            try (SeekableByteChannel fc = Files.newByteChannel(path)) {
                buffer = createByteBuffer((int) fc.size() + 2);
                while (fc.read(buffer) != -1) {
                }
                buffer.put((byte) 0);
            }
        }

        buffer.flip();
        return buffer;
    }

    /**
     * Updates the controller mappings.
     */
    public static void update_mappings()
    {
        try {
            File mappings_file = new File("config/gamecontrollerdb.txt");
            if (!mappings_file.exists())
                return;
            ByteBuffer buffer = io_resource_to_buffer(mappings_file.getPath(), 1024);
            GLFW.glfwUpdateGamepadMappings(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
