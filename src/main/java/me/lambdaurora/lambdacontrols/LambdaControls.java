/*
 * Copyright © 2019 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of LambdaControls.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package me.lambdaurora.lambdacontrols;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;


/**
 * Represents the LambdaControls mod.
 */
public class LambdaControls implements ClientModInitializer
{
    private static LambdaControls       INSTANCE;
    public final   Logger               logger           = LogManager.getLogger("LambdaControls");
    public final   LambdaControlsConfig config           = new LambdaControlsConfig(this);
    public final   ControllerInput      controller_input = new ControllerInput(this);

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
    public void on_mc_init(@NotNull MinecraftClient client)
    {
        Controller.update_mappings();
        this.config.init_keybindings(client.options);
        GLFW.glfwSetJoystickCallback((jid, event) -> {
            if (event == GLFW.GLFW_CONNECTED) {
                Controller controller = Controller.by_id(jid);
                client.getToastManager().add(new SystemToast(SystemToast.Type.TUTORIAL_HINT, new TranslatableText("lambdacontrols.controller.connected", jid),
                        new LiteralText(controller.get_name())));
            } else if (event == GLFW.GLFW_DISCONNECTED) {
                client.getToastManager().add(new SystemToast(SystemToast.Type.TUTORIAL_HINT, new TranslatableText("lambdacontrols.controller.disconnected", jid),
                        null));
            }
        });
    }

    /**
     * This method is called every Minecraft tick.
     *
     * @param client The client instance.
     */
    public void on_tick(@NotNull MinecraftClient client)
    {
        if (this.config.get_controls_mode() == ControlsMode.CONTROLLER)
            this.controller_input.on_tick(client);
    }

    public void on_render(MinecraftClient client)
    {
        this.controller_input.on_render(client);
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
