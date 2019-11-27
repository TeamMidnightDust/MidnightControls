/*
 * Copyright © 2019 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of LambdaControls.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package me.lambdaurora.lambdacontrols.gui;

import me.lambdaurora.lambdacontrols.ControlsMode;
import me.lambdaurora.lambdacontrols.LambdaControls;
import me.lambdaurora.lambdacontrols.util.LambdaKeyBinding;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

/**
 * Represents the LambdaControls HUD.
 */
public class LambdaControlsHud
{
    private final MinecraftClient client;
    private final LambdaControls  mod;
    private       ButtonWidget    jump_button;

    public LambdaControlsHud(@NotNull MinecraftClient client, @NotNull LambdaControls mod)
    {
        this.client = client;
        this.mod = mod;
        this.jump_button = new ButtonWidget(50, 50, 20, 20, "J", button-> {});
    }

    public void render(float delta)
    {
        if (this.mod.config.get_controls_mode() == ControlsMode.TOUCHSCREEN)
            this.render_touchscreen(delta);
    }

    public void render_touchscreen(float delta)
    {
        //this.jump_button.render((int) this.client.mouse.getX(), (int) this.client.mouse.getY(), delta);
    }

    public void on_input(double x, double y, int button, int action)
    {
        if (this.jump_button.mouseClicked(x, y, button)) {
            ((LambdaKeyBinding) this.client.options.keyJump).handle_press_state(action != GLFW.GLFW_RELEASE);
        }
    }
}
