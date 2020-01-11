/*
 * Copyright © 2020 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of LambdaControls.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package me.lambdaurora.lambdacontrols.client.controller;

import me.lambdaurora.lambdacontrols.client.ButtonState;
import me.lambdaurora.lambdacontrols.client.mixin.CreativeInventoryScreenAccessor;
import me.lambdaurora.lambdacontrols.client.util.KeyBindingAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.AbstractContainerScreen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.util.ScreenshotUtils;
import net.minecraft.item.ItemGroup;
import org.jetbrains.annotations.NotNull;

/**
 * Represents some input handlers.
 *
 * @author LambdAurora
 * @version 1.1.0
 * @since 1.1.0
 */
public class InputHandlers
{
    private static int hotbar_cooldown = 0;

    private InputHandlers()
    {
    }

    public static PressAction handle_hotbar(boolean right)
    {
        return (client, button, action) -> {
            if (action == ButtonState.RELEASE)
                return false;

            // When ingame
            if (client.currentScreen == null && client.player != null) {
                if (right)
                    client.player.inventory.selectedSlot = client.player.inventory.selectedSlot == 8 ? 0 : client.player.inventory.selectedSlot + 1;
                else
                    client.player.inventory.selectedSlot = client.player.inventory.selectedSlot == 0 ? 8 : client.player.inventory.selectedSlot - 1;
                return true;
            } else if (client.currentScreen instanceof CreativeInventoryScreen) {
                CreativeInventoryScreenAccessor creative_inventory = (CreativeInventoryScreenAccessor) client.currentScreen;
                int current_selected_tab = creative_inventory.lambdacontrols_get_selected_tab();
                int next_tab = current_selected_tab + (right ? 1 : -1);
                if (next_tab < 0)
                    next_tab = ItemGroup.GROUPS.length - 1;
                else if (next_tab >= ItemGroup.GROUPS.length)
                    next_tab = 0;
                creative_inventory.lambdacontrols_set_selected_tab(ItemGroup.GROUPS[next_tab]);
                return true;
            }
            return false;
        };
    }

    public static boolean handle_pause_game(@NotNull MinecraftClient client, @NotNull ButtonBinding binding, @NotNull ButtonState action)
    {
        if (action == ButtonState.PRESS) {
            // If in game, then pause the game.
            if (client.currentScreen == null)
                client.openPauseMenu(false);
            else if (client.currentScreen instanceof AbstractContainerScreen && client.player != null) // If the current screen is a container then close it.
                client.player.closeContainer();
            else // Else just close the current screen.
                client.currentScreen.onClose();
        }
        return true;
    }

    /**
     * Handles the screenshot action.
     *
     * @param client  The client instance.
     * @param binding The binding which fired the action.
     * @param action  The action done on the binding.
     * @return True if handled, else false.
     */
    public static boolean handle_screenshot(@NotNull MinecraftClient client, @NotNull ButtonBinding binding, @NotNull ButtonState action)
    {
        if (action == ButtonState.PRESS)
            ScreenshotUtils.saveScreenshot(client.runDirectory, client.getWindow().getFramebufferWidth(), client.getWindow().getFramebufferHeight(), client.getFramebuffer(),
                    text -> client.execute(() -> client.inGameHud.getChatHud().addMessage(text)));
        return true;
    }

    public static boolean handle_toggle_sneak(@NotNull MinecraftClient client, @NotNull ButtonBinding button, @NotNull ButtonState action)
    {
        if (client.player != null && !client.player.abilities.flying) {
            button.as_key_binding().filter(binding -> action == ButtonState.PRESS).ifPresent(binding -> ((KeyBindingAccessor) binding).handle_press_state(!binding.isPressed()));
            return true;
        }
        return false;
    }
}
