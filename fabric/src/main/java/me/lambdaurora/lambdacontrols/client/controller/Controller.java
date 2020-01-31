/*
 * Copyright © 2020 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of LambdaControls.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package me.lambdaurora.lambdacontrols.client.controller;

import me.lambdaurora.lambdacontrols.client.LambdaControlsClient;
import org.aperlambda.lambdacommon.utils.Nameable;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWGamepadState;

import java.io.File;
import java.io.IOException;
import java.nio.Buffer;
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
 *
 * @author LambdAurora
 * @version 1.0.0
 * @since 1.0.0
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
    public int getId()
    {
        return this.id;
    }

    /**
     * Gets the controller's globally unique identifier.
     *
     * @return The controller's GUID.
     */
    public String getGuid()
    {
        String guid = GLFW.glfwGetJoystickGUID(this.id);
        return guid == null ? "" : guid;
    }

    /**
     * Returns whether this controller is connected or not.
     *
     * @return True if this controller is connected, else false.
     */
    public boolean isConnected()
    {
        return GLFW.glfwJoystickPresent(this.id);
    }

    /**
     * Returns whether this controller is a gamepad or not.
     *
     * @return True if this controller is a gamepad, else false.
     */
    public boolean isGamepad()
    {
        return this.isConnected() && GLFW.glfwJoystickIsGamepad(this.id);
    }

    /**
     * Gets the name of the controller.
     *
     * @return The controller's name.
     */
    @Override
    public @NotNull String getName()
    {
        String name = this.isGamepad() ? GLFW.glfwGetGamepadName(this.id) : GLFW.glfwGetJoystickName(this.id);
        return name == null ? String.valueOf(this.getId()) : name;
    }

    /**
     * Gets the state of the controller.
     *
     * @return The state of the controller input.
     */
    public GLFWGamepadState getState()
    {
        GLFWGamepadState state = GLFWGamepadState.create();
        if (this.isGamepad())
            GLFW.glfwGetGamepadState(this.id, state);
        return state;
    }

    public static @NotNull Controller byId(int id)
    {
        if (id > GLFW.GLFW_JOYSTICK_LAST) {
            LambdaControlsClient.get().log("Controller '" + id + "' doesn't exist.");
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

    public static @NotNull Optional<Controller> byGuid(@NotNull String guid)
    {
        return CONTROLLERS.values().stream().filter(Controller::isConnected)
                .filter(controller -> controller.getGuid().equals(guid))
                .max(Comparator.comparingInt(Controller::getId));
    }

    /**
     * Reads the specified resource and returns the raw data as a ByteBuffer.
     *
     * @param resource   The resource to read.
     * @param bufferSize The initial buffer size.
     * @return The resource data.
     * @throws IOException If an IO error occurs.
     */
    private static ByteBuffer ioResourceToBuffer(String resource, int bufferSize) throws IOException
    {
        ByteBuffer buffer = null;

        Path path = Paths.get(resource);
        if (Files.isReadable(path)) {
            try (SeekableByteChannel fc = Files.newByteChannel(path)) {
                buffer = createByteBuffer((int) fc.size() + 2);
                while (fc.read(buffer) != -1) ;
                buffer.put((byte) 0);
            }
        }

        ((Buffer) buffer).flip(); // Force Java 8 >.<
        return buffer;
    }

    /**
     * Updates the controller mappings.
     */
    public static void updateMappings()
    {
        try {
            File mappingsFile = new File("config/gamecontrollerdb.txt");
            if (!mappingsFile.exists())
                return;
            LambdaControlsClient.get().log("Updating controller mappings...");
            ByteBuffer buffer = ioResourceToBuffer(mappingsFile.getPath(), 1024);
            GLFW.glfwUpdateGamepadMappings(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
