/*
 * Copyright © 2020 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of LambdaControls.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package me.lambdaurora.lambdacontrols.compat;

import me.lambdaurora.lambdacontrols.LambdaControls;
import me.lambdaurora.lambdacontrols.controller.ButtonBinding;
import me.lambdaurora.lambdacontrols.controller.InputManager;
import net.fabricmc.fabric.api.client.keybinding.FabricKeyBinding;
import org.aperlambda.lambdacommon.utils.LambdaReflection;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

/**
 * Represents a compatibility handler for OkZoomer.
 *
 * @author LambdAurora
 * @version 1.1.0
 * @since 1.1.0
 */
public class OkZoomerCompat implements CompatHandler
{
    public static final String OKZOOMER_CLASS_PATH = "io.github.joaoh1.okzoomer.OkZoomer";

    @Override
    public void handle(@NotNull LambdaControls mod)
    {
        LambdaReflection.get_first_field_of_type(io.github.joaoh1.okzoomer.OkZoomer.class, FabricKeyBinding.class)
                .map(field -> (FabricKeyBinding) LambdaReflection.get_field_value(null, field))
                .ifPresent(zoom_key_binding -> {
                    ButtonBinding binding = InputManager.register_binding(new ButtonBinding("zoom", new int[]{GLFW.GLFW_GAMEPAD_BUTTON_DPAD_UP, GLFW.GLFW_GAMEPAD_BUTTON_X}, true));
                    binding.set_key_binding(zoom_key_binding);
                    ButtonBinding.MISC_CATEGORY.register_binding(binding);
                });
    }
}
