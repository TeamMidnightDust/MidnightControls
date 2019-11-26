/*
 * Copyright © 2019 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of LambdaControls.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package me.lambdaurora.lambdacontrols;

import me.lambdaurora.lambdacontrols.util.LambdaKeyBinding;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.event.client.ClientTickCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.KeyBinding;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;

import java.nio.ByteBuffer;

import static org.lwjgl.glfw.GLFW.GLFW_JOYSTICK_1;


/**
 * Represents the LambdaControls mod.
 */
public class LambdaControls implements ClientModInitializer
{
    private static LambdaControls       INSTANCE;
    public final   Logger               logger = LogManager.getLogger("LambdaControls");
    public final   LambdaControlsConfig config = new LambdaControlsConfig(this);
    private        int                  cid    = GLFW_JOYSTICK_1;

    @Override
    public void onInitializeClient()
    {
        INSTANCE = this;
        this.log("Initializing LambdaControls...");
        this.config.load();
    }

    /**
     * This method is called when Minecraft is initializing.
     */
    public void on_mc_init()
    {
        GLFW.glfwSetJoystickCallback((jid, event) -> {
            if (event == GLFW.GLFW_CONNECTED) {
                this.log("CONNECTED " + jid);
                cid = jid;
            }
        });
    }

    /**
     * This method is called every Minecraft tick.
     *
     * @param client The client instance.
     */
    public void on_tick(MinecraftClient client)
    {
        ByteBuffer buffer = GLFW.glfwGetJoystickButtons(GLFW.GLFW_JOYSTICK_3);
        if (buffer == null)
            return;
        //this.log(String.valueOf(buffer.get()));
        if (buffer.get() == (byte) 1) {
            this.log("uwu");
            ((LambdaKeyBinding) client.options.keyJump).lambdacontrols_press();
        }
    }

    /**
     * Prints a message to the terminal.
     *
     * @param info The message to print.
     */
    public void log(String info)
    {
        this.logger.info("[LambdaControls] " + info);
    }

    /**
     * Gets the LambdaControls instance.
     *
     * @return The LambdaControls instance.
     */
    public static LambdaControls get()
    {
        return INSTANCE;
    }
}
