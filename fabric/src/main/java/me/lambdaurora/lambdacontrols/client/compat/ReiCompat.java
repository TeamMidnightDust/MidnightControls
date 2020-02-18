/*
 * Copyright © 2020 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of LambdaControls.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package me.lambdaurora.lambdacontrols.client.compat;

import me.lambdaurora.lambdacontrols.client.ButtonState;
import me.lambdaurora.lambdacontrols.client.LambdaControlsClient;
import me.lambdaurora.lambdacontrols.client.compat.mixin.RecipeViewingScreenAccessor;
import me.lambdaurora.lambdacontrols.client.controller.ButtonBinding;
import me.lambdaurora.lambdacontrols.client.controller.InputManager;
import me.lambdaurora.lambdacontrols.client.controller.PressAction;
import me.shedaniel.rei.gui.RecipeViewingScreen;
import me.shedaniel.rei.gui.VillagerRecipeViewingScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import static org.lwjgl.glfw.GLFW.GLFW_GAMEPAD_BUTTON_LEFT_BUMPER;
import static org.lwjgl.glfw.GLFW.GLFW_GAMEPAD_BUTTON_RIGHT_BUMPER;

/**
 * Represents a compatibility handler for REI.
 *
 * @author LambdAurora
 * @version 1.2.0
 * @since 1.2.0
 */
public class ReiCompat implements CompatHandler
{
    @Override
    public void handle(@NotNull LambdaControlsClient mod)
    {
        InputManager.registerBinding(new ButtonBinding.Builder(new Identifier("rei", "tab_back"))
                .buttons(GLFW_GAMEPAD_BUTTON_LEFT_BUMPER)
                .filter((client, binding) -> isViewingScreen(client.currentScreen))
                .action(tabAction(false))
                .cooldown(true)
                .build());

        InputManager.registerBinding(new ButtonBinding.Builder(new Identifier("rei", "tab_next"))
                .buttons(GLFW_GAMEPAD_BUTTON_RIGHT_BUMPER)
                .filter((client, binding) -> isViewingScreen(client.currentScreen))
                .action(tabAction(true))
                .cooldown(true)
                .build());
    }

    @Override
    public boolean requireMouseOnScreen(@NotNull Screen screen)
    {
        return isViewingScreen(screen);
    }

    private static boolean isViewingScreen(@NotNull Screen screen)
    {
        return screen instanceof RecipeViewingScreen || screen instanceof VillagerRecipeViewingScreen;
    }

    private static PressAction tabAction(boolean next)
    {
        return (client, button, action) -> {
            if (action == ButtonState.RELEASE)
                return false;

            if (client.currentScreen instanceof RecipeViewingScreen) {
                RecipeViewingScreenAccessor screen = (RecipeViewingScreenAccessor) client.currentScreen;
                if (next)
                    screen.getCategoryNext().onPressed();
                else
                    screen.getCategoryBack().onPressed();
            }
            return false;
        };
    }
}
