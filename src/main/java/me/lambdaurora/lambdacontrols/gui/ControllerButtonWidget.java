/*
 * Copyright © 2020 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of LambdaControls.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package me.lambdaurora.lambdacontrols.gui;

import me.lambdaurora.lambdacontrols.controller.ButtonBinding;
import me.lambdaurora.lambdacontrols.LambdaControls;
import me.lambdaurora.spruceui.AbstractIconButtonWidget;
import net.minecraft.client.MinecraftClient;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a controller button widget.
 */
public class ControllerButtonWidget extends AbstractIconButtonWidget
{
    private ButtonBinding binding;

    public ControllerButtonWidget(int x, int y, int width, @NotNull ButtonBinding button_binding, @NotNull PressAction on_press)
    {
        super(x, y, width, 20, ButtonBinding.get_localized_button_name(button_binding.get_button()[0]), on_press);
        this.binding = button_binding;
    }

    public void update()
    {
        this.setMessage(ButtonBinding.get_localized_button_name(binding.get_button()[0]));
    }

    @Override
    protected int render_icon(int mouse_x, int mouse_y, float delta, int x, int y)
    {
        return LambdaControls.draw_button(x, y, this.binding, MinecraftClient.getInstance()).get_value();
    }
}
