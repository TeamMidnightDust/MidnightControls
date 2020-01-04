/*
 * Copyright © 2020 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of LambdaControls.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package me.lambdaurora.lambdacontrols.client.gui;

import me.lambdaurora.lambdacontrols.client.LambdaControlsClient;
import me.lambdaurora.lambdacontrols.client.controller.ButtonBinding;
import me.lambdaurora.spruceui.AbstractIconButtonWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.language.I18n;
import org.aperlambda.lambdacommon.utils.Pair;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a controller button widget.
 */
public class ControllerButtonWidget extends AbstractIconButtonWidget
{
    private ButtonBinding binding;
    private int           icon_width;

    public ControllerButtonWidget(int x, int y, int width, @NotNull ButtonBinding button_binding, @NotNull PressAction on_press)
    {
        super(x, y, width, 20, ButtonBinding.get_localized_button_name(button_binding.get_button()[0]), on_press);
        this.binding = button_binding;
    }

    public void update()
    {
        int length = binding.get_button().length;
        this.setMessage(this.binding.is_not_bound() ? I18n.translate("lambdacontrols.not_bound") :
                (length > 0 ? ButtonBinding.get_localized_button_name(binding.get_button()[0]) : "<>"));
    }

    @Override
    public String getMessage()
    {
        if (this.binding.get_button().length > 1)
            return "";
        return super.getMessage();
    }

    @Override
    protected int render_icon(int mouse_x, int mouse_y, float delta, int x, int y)
    {
        if (this.binding.get_button().length > 1) {
            x += (this.width / 2 - this.icon_width / 2) - 4;
        }
        Pair<Integer, Integer> size = LambdaControlsClient.draw_button(x, y, this.binding, MinecraftClient.getInstance());
        this.icon_width = size.get_key();
        return size.get_value();
    }
}
