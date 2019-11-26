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
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.GameOptions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.glfw.GLFW.GLFW_JOYSTICK_1;


/**
 * Represents the LambdaControls mod.
 */
public class LambdaControls implements ClientModInitializer
{
    private static LambdaControls        INSTANCE;
    public final   Logger                logger        = LogManager.getLogger("LambdaControls");
    public final   LambdaControlsConfig  config        = new LambdaControlsConfig(this);
    private final  Map<Integer, Integer> BUTTON_STATES = new HashMap<>();
    private        int                   cid           = GLFW_JOYSTICK_1;

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
        this.config.init_keybindings(client.options);
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
        GameOptions options = client.options;
        ByteBuffer btn_buffer = GLFW.glfwGetJoystickButtons(GLFW.GLFW_JOYSTICK_3);
        if (btn_buffer != null) {
            for (int i = 0; i < btn_buffer.limit(); i++) {
                boolean btn_state = btn_buffer.get() == (byte) 1;

                int current_state = BUTTON_STATES.getOrDefault(i, 0);
                if (current_state == 0 && btn_state) {
                    BUTTON_STATES.put(i, 1);

                    int f_i = i;
                    this.config.get_keybind("button_" + i).ifPresent(key_binding -> {
                        ((LambdaKeyBinding) key_binding).lambdacontrols_press();
                        if (key_binding == options.keyInventory && client.player != null && client.player.container != client.player.playerContainer) {
                            BUTTON_STATES.put(f_i, 2);
                        }
                    });
                    if (this.config.is_hotbar_left_button(i)) {
                        client.player.inventory.selectedSlot = client.player.inventory.selectedSlot == 0 ? 8 : client.player.inventory.selectedSlot - 1;
                    } else if (this.config.is_hotbar_right_button(i)) {
                        client.player.inventory.selectedSlot = client.player.inventory.selectedSlot == 8 ? 0 : client.player.inventory.selectedSlot + 1;
                    }
                } else if (current_state != 0 && !btn_state) {
                    this.config.get_keybind("button_" + i).ifPresent(key_binding -> {
                        if (key_binding == options.keyInventory && current_state == 2 && client.player != null && client.player.container != client.player.playerContainer) {
                            client.player.closeContainer();
                        }
                        ((LambdaKeyBinding) key_binding).lambdacontrols_unpress();
                    });

                    BUTTON_STATES.put(i, 0);
                }
            }
        }
        FloatBuffer axes_buffer = GLFW.glfwGetJoystickAxes(GLFW.GLFW_JOYSTICK_3);
        if (axes_buffer != null) {
            for (int i = 0; i < axes_buffer.limit(); i++) {
                float value = axes_buffer.get();
                {
                    int state = value > 0.5F ? 1 : (value < -0.5F ? 2 : 0);
                    this.config.get_keybind("axe_" + i + "+").ifPresent(key_binding -> ((LambdaKeyBinding) key_binding).handle_press_state(state == 1));
                    this.config.get_keybind("axe_" + i + "-").ifPresent(key_binding -> ((LambdaKeyBinding) key_binding).handle_press_state(state == 2));
                }
                if (this.config.is_look_axis(i) && (value > 0.25F || value < -0.25F)) {
                    int state = value > 0.25F ? 1 : (value < -0.25F ? 2 : 0);
                    float multiplier = 50.f;
                    double x = 0.0D;
                    double y = 0.0D;
                    if (this.config.is_view_down_control(i, state)) {
                        if (this.config.get_view_down_control().endsWith("+"))
                            y = Math.abs(value * multiplier);
                        else
                            y = -Math.abs(value * multiplier);
                    } else if (this.config.is_view_up_control(i, state)) {
                        if (this.config.get_view_up_control().endsWith("+"))
                            y = Math.abs(value * multiplier);
                        else
                            y = -Math.abs(value * multiplier);
                    }
                    if (this.config.is_view_left_control(i, state)) {
                        if (this.config.get_view_left_control().endsWith("+"))
                            x = Math.abs(value * multiplier);
                        else
                            x = -Math.abs(value * multiplier);
                    } else if (this.config.is_view_right_control(i, state)) {
                        if (this.config.get_view_right_control().endsWith("+"))
                            x = Math.abs(value * multiplier);
                        else
                            x = -Math.abs(value * multiplier);
                    }
                    client.player.changeLookDirection(x, y);
                }
            }
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
