/*
 * Copyright © 2019 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of LambdaControls.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package me.lambdaurora.lambdacontrols;

import me.lambdaurora.lambdacontrols.mixin.AbstractContainerScreenAccessor;
import me.lambdaurora.lambdacontrols.util.CreativeInventoryScreenAccessor;
import me.lambdaurora.lambdacontrols.util.LambdaKeyBinding;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.AbstractContainerScreen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.screen.world.WorldListWidget;
import net.minecraft.client.gui.widget.AbstractPressableButtonWidget;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.client.options.GameOptions;
import net.minecraft.container.Slot;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.math.MathHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aperlambda.lambdacommon.utils.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.lwjgl.glfw.GLFW.GLFW_JOYSTICK_1;


/**
 * Represents the LambdaControls mod.
 */
public class LambdaControls implements ClientModInitializer
{
    private static LambdaControls        INSTANCE;
    public final   Logger                logger                 = LogManager.getLogger("LambdaControls");
    public final   LambdaControlsConfig  config                 = new LambdaControlsConfig(this);
    public final   ControllerInput       controller_input       = new ControllerInput(this);
    private final  Map<Integer, Integer> BUTTON_STATES          = new HashMap<>();
    private        float                 prev_x_axis            = 0.F;
    private        float                 prev_y_axis            = 0.F;
    private        int                   prev_target_mouse_x    = 0;
    private        int                   prev_target_mouse_y    = 0;
    private        int                   target_mouse_x         = 0;
    private        int                   target_mouse_y         = 0;
    private        float                 mouse_speed_x          = 0.F;
    private        float                 mouse_speed_y          = 0.F;
    private        boolean               last_a_state           = false;
    private        int                   cid                    = GLFW_JOYSTICK_1;
    private        boolean               allow_controller_mouse = true;
    private        int                   action_gui_cooldown    = 0;

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
    public void on_tick(@NotNull MinecraftClient client)
    {
        if (this.config.get_controls_mode() == ControlsMode.CONTROLLER)
            this.controller_input.on_tick(client);
        /* Decreases the cooldown for the screen focus change.
        if (this.action_gui_cooldown > 0)
            --this.action_gui_cooldown;
        if (this.action_gui_cooldown == 0)
            this.allow_controller_mouse = true;

        this.prev_target_mouse_x = this.target_mouse_x;
        this.prev_target_mouse_y = this.target_mouse_y;
        if (LambdaControls.get().config.get_controls_mode() == ControlsMode.CONTROLLER)
            this.on_controller_tick(client);*/
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
