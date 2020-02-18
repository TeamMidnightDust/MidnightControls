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
import me.lambdaurora.lambdacontrols.client.compat.mixin.VillagerRecipeViewingScreenAccessor;
import me.lambdaurora.lambdacontrols.client.controller.ButtonBinding;
import me.lambdaurora.lambdacontrols.client.controller.InputHandlers;
import me.lambdaurora.lambdacontrols.client.controller.InputManager;
import me.lambdaurora.lambdacontrols.client.controller.PressAction;
import me.shedaniel.rei.api.RecipeCategory;
import me.shedaniel.rei.gui.ContainerScreenOverlay;
import me.shedaniel.rei.gui.RecipeViewingScreen;
import me.shedaniel.rei.gui.VillagerRecipeViewingScreen;
import me.shedaniel.rei.gui.widget.EntryListWidget;
import me.shedaniel.rei.impl.ScreenHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.Identifier;
import org.aperlambda.lambdacommon.utils.LambdaReflection;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

import static org.lwjgl.glfw.GLFW.*;

/**
 * Represents a compatibility handler for REI.
 *
 * @author LambdAurora
 * @version 1.2.0
 * @since 1.2.0
 */
public class ReiCompat implements CompatHandler
{
    private static EntryListWidget ENTRY_LIST_WIDGET;
    public static  ButtonBinding   TAB_BACK;

    @Override
    public void handle(@NotNull LambdaControlsClient mod)
    {
        InputManager.registerBinding(new ButtonBinding.Builder(new Identifier("rei", "category_back"))
                .buttons(GLFW_GAMEPAD_BUTTON_LEFT_BUMPER)
                .filter((client, binding) -> isViewingScreen(client.currentScreen))
                .action(handleTab(false))
                .cooldown(true)
                .build());
        InputManager.registerBinding(new ButtonBinding.Builder(new Identifier("rei", "category_next"))
                .buttons(GLFW_GAMEPAD_BUTTON_RIGHT_BUMPER)
                .filter((client, binding) -> isViewingScreen(client.currentScreen))
                .action(handleTab(true))
                .cooldown(true)
                .build());

        InputManager.registerBinding(new ButtonBinding.Builder(new Identifier("rei", "page_back"))
                .buttons(ButtonBinding.axisAsButton(GLFW_GAMEPAD_AXIS_RIGHT_X, false))
                .filter((client, binding) -> InputHandlers.inInventory(client, binding) || isViewingScreen(client.currentScreen))
                .action(handlePage(false))
                .cooldown(true)
                .build());
        InputManager.registerBinding(new ButtonBinding.Builder(new Identifier("rei", "page_next"))
                .buttons(ButtonBinding.axisAsButton(GLFW_GAMEPAD_AXIS_RIGHT_X, true))
                .filter((client, binding) -> InputHandlers.inInventory(client, binding) || isViewingScreen(client.currentScreen))
                .action(handlePage(true))
                .cooldown(true)
                .build());

        // For some reasons this is broken.
        InputManager.registerBinding(new ButtonBinding.Builder(new Identifier("rei", "show_usage"))
                .buttons(GLFW_GAMEPAD_BUTTON_RIGHT_THUMB)
                .filter((client, binding) -> InputHandlers.inInventory(client, binding) || isViewingScreen(client.currentScreen))
                .action((client, button, action) -> {
                    if (action != ButtonState.RELEASE)
                        return false;
                    Optional<ContainerScreenOverlay> overlay = ScreenHelper.getOptionalOverlay();
                    if (!overlay.isPresent())
                        return false;
                    double mouseX = client.mouse.getX();
                    double mouseY = client.mouse.getY();
                    EntryListWidget widget = getEntryListWidget();
                    if (widget == null)
                        return false;
                    return widget.mouseClicked(mouseX, mouseY, GLFW_MOUSE_BUTTON_2);
                })
                .cooldown(true)
                .build());
    }

    @Override
    public boolean requireMouseOnScreen(Screen screen)
    {
        return isViewingScreen(screen);
    }

    private static boolean isViewingScreen(Screen screen)
    {
        return screen instanceof RecipeViewingScreen || screen instanceof VillagerRecipeViewingScreen;
    }

    private static EntryListWidget getEntryListWidget()
    {
        if (ENTRY_LIST_WIDGET == null) {
            ENTRY_LIST_WIDGET = LambdaReflection.getFirstFieldOfType(ContainerScreenOverlay.class, EntryListWidget.class)
                    .map(field -> (EntryListWidget) LambdaReflection.getFieldValue(null, field))
                    .orElse(null);
        }
        return ENTRY_LIST_WIDGET;
    }

    private static PressAction handlePage(boolean next)
    {
        return (client, button, action) -> {
            if (action == ButtonState.RELEASE)
                return false;

            Optional<ContainerScreenOverlay> overlay = ScreenHelper.getOptionalOverlay();
            if (!overlay.isPresent())
                return false;

            EntryListWidget widget = getEntryListWidget();
            if (widget == null)
                return false;

            if (next)
                widget.nextPage();
            else
                widget.previousPage();
            widget.updateEntriesPosition();

            return true;
        };
    }

    /**
     * Returns the handler for category tabs buttons.
     *
     * @param next True if the action is to switch to the next tab.
     * @return The handler.
     */
    private static PressAction handleTab(boolean next)
    {
        return (client, button, action) -> {
            if (action != ButtonState.RELEASE)
                return false;

            if (client.currentScreen instanceof RecipeViewingScreen) {
                RecipeViewingScreenAccessor screen = (RecipeViewingScreenAccessor) client.currentScreen;
                if (next)
                    screen.getCategoryNext().onPressed();
                else
                    screen.getCategoryBack().onPressed();
                return true;
            } else if (client.currentScreen instanceof VillagerRecipeViewingScreen) {
                VillagerRecipeViewingScreenAccessor screen = (VillagerRecipeViewingScreenAccessor) client.currentScreen;
                List<RecipeCategory<?>> categories = screen.getCategories();
                int currentTab = screen.getSelectedCategoryIndex();
                int nextTab = currentTab + (next ? 1 : -1);
                if (nextTab < 0)
                    nextTab = categories.size() - 1;
                else if (nextTab >= categories.size())
                    nextTab = 0;
                screen.setSelectedCategoryIndex(nextTab);
                screen.lambdacontrols_init();
                return true;
            }
            return false;
        };
    }
}
