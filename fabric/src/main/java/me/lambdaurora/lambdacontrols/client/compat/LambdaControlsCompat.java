/*
 * Copyright © 2020 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of LambdaControls.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package me.lambdaurora.lambdacontrols.client.compat;

import me.lambdaurora.lambdacontrols.client.LambdaControlsClient;
import me.lambdaurora.lambdacontrols.client.controller.InputManager;
import net.fabricmc.loader.api.FabricLoader;
import org.aperlambda.lambdacommon.utils.LambdaReflection;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a compatibility handler.
 *
 * @author LambdAurora
 * @version 1.1.0
 * @since 1.1.0
 */
public class LambdaControlsCompat
{
    /**
     * Initializes compatibility with other mods if needed.
     *
     * @param mod The mod instance.
     */
    public static void init(@NotNull LambdaControlsClient mod)
    {
        if (FabricLoader.getInstance().isModLoaded("okzoomer") && LambdaReflection.does_class_exist(OkZoomerCompat.OKZOOMER_CLASS_PATH)) {
            mod.log("Adding okzoomer compatibility...");
            new OkZoomerCompat().handle(mod);
        }
        InputManager.load_button_bindings(mod.config);
    }
}
