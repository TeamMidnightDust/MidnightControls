/*
 * Copyright © 2020 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of LambdaControls.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package me.lambdaurora.lambdacontrols.compat;

import me.lambdaurora.lambdacontrols.controller.ButtonBinding;
import me.lambdaurora.lambdacontrols.controller.InputManager;
import net.fabricmc.fabric.api.client.keybinding.FabricKeyBinding;
import net.fabricmc.loader.api.FabricLoader;
import org.aperlambda.lambdacommon.utils.LambdaReflection;
import org.lwjgl.glfw.GLFW;

/**
 * Represents a compatibility handler.
 *
 * @author LambdAurora
 * @version 1.1.0
 */
public class LambdaControlsCompat
{
    private static final String OKZOOMER_CLASS_PATH = "io.github.joaoh1.okzoomer.OkZoomer";

    public static void init()
    {
        if (FabricLoader.getInstance().isModLoaded("okzoomer") && LambdaReflection.does_class_exist(OKZOOMER_CLASS_PATH)) {
            LambdaReflection.get_class(OKZOOMER_CLASS_PATH).map(clazz -> LambdaReflection.get_first_field_of_type(clazz, FabricKeyBinding.class))
                    .ifPresent(field -> field.map(f -> (FabricKeyBinding) LambdaReflection.get_field_value(null, f))
                            .ifPresent(zoom_key_binding -> {
                                ButtonBinding binding = InputManager.register_binding(new ButtonBinding("zoom", new int[]{GLFW.GLFW_GAMEPAD_BUTTON_DPAD_UP, GLFW.GLFW_GAMEPAD_BUTTON_X}, true));
                                binding.set_key_binding(zoom_key_binding);
                                ButtonBinding.MISC_CATEGORY.register_binding(binding);
                            }));
        }
    }
}
