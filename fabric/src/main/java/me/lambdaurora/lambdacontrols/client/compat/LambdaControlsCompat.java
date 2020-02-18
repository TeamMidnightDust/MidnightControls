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
import net.minecraft.client.gui.screen.Screen;
import org.aperlambda.lambdacommon.utils.LambdaReflection;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a compatibility handler.
 *
 * @author LambdAurora
 * @version 1.1.0
 * @since 1.1.0
 */
public class LambdaControlsCompat
{
    private static final List<CompatHandler> HANDLERS = new ArrayList<>();

    /**
     * Initializes compatibility with other mods if needed.
     *
     * @param mod The mod instance.
     */
    public static void init(@NotNull LambdaControlsClient mod)
    {
        if (FabricLoader.getInstance().isModLoaded("okzoomer") && LambdaReflection.doesClassExist(OkZoomerCompat.OKZOOMER_CLASS_PATH)) {
            mod.log("Adding okzoomer compatibility...");
            HANDLERS.add(new OkZoomerCompat());
        }
        if (FabricLoader.getInstance().isModLoaded("roughlyenoughitems")) {
            mod.log("Adding REI compatiblity...");
            HANDLERS.add(new ReiCompat());
        }
        HANDLERS.forEach(handler -> handler.handle(mod));
        InputManager.loadButtonBindings(mod.config);
    }

    /**
     * Returns whether the mouse is required on the specified screen.
     *
     * @param screen The screen.
     * @return True if the mouse is requried on the specified screen, else false.
     */
    public static boolean requireMouseOnScreen(@NotNull Screen screen)
    {
        return HANDLERS.stream().anyMatch(handler -> handler.requireMouseOnScreen(screen));
    }
}
