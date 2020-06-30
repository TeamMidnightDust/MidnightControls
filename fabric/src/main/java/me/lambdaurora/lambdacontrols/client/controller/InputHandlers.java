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
import me.lambdaurora.lambdacontrols.client.LambdaInput;
import me.lambdaurora.lambdacontrols.client.mixin.AdvancementsScreenAccessor;
import me.lambdaurora.lambdacontrols.client.mixin.CreativeInventoryScreenAccessor;
import me.lambdaurora.lambdacontrols.client.mixin.RecipeBookWidgetAccessor;
import me.lambdaurora.lambdacontrols.client.util.HandledScreenAccessor;
import me.lambdaurora.lambdacontrols.client.util.KeyBindingAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.advancement.AdvancementTab;
import net.minecraft.client.gui.screen.advancement.AdvancementsScreen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.screen.recipebook.RecipeGroupButtonWidget;
import net.minecraft.client.util.ScreenshotUtils;
import net.minecraft.item.ItemGroup;
import net.minecraft.screen.slot.Slot;
import org.aperlambda.lambdacommon.utils.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Represents some input handlers.
 *
 * @author LambdAurora
 * @version 1.4.0
 * @since 1.1.0
 */
public class InputHandlers
{
    private InputHandlers()
    {
    }

    public static PressAction handleHotbar(boolean next)
    {
        return (client, button, value, action) -> {
            if (action == ButtonState.RELEASE)
                return false;

            // When ingame
            if (client.currentScreen == null && client.player != null) {
                if (next)
                    client.player.inventory.selectedSlot = client.player.inventory.selectedSlot == 8 ? 0 : client.player.inventory.selectedSlot + 1;
                else
                    client.player.inventory.selectedSlot = client.player.inventory.selectedSlot == 0 ? 8 : client.player.inventory.selectedSlot - 1;
                return true;
            } else if (client.currentScreen instanceof CreativeInventoryScreen) {
                CreativeInventoryScreenAccessor inventory = (CreativeInventoryScreenAccessor) client.currentScreen;
                int currentTab = inventory.getSelectedTab();
                int nextTab = currentTab + (next ? 1 : -1);
                if (nextTab < 0)
                    nextTab = ItemGroup.GROUPS.length - 1;
                else if (nextTab >= ItemGroup.GROUPS.length)
                    nextTab = 0;
                inventory.lambdacontrols_setSelectedTab(ItemGroup.GROUPS[nextTab]);
                return true;
            } else if (client.currentScreen instanceof InventoryScreen) {
                RecipeBookWidgetAccessor recipeBook = (RecipeBookWidgetAccessor) ((InventoryScreen) client.currentScreen).getRecipeBookWidget();
                List<RecipeGroupButtonWidget> tabs = recipeBook.getTabButtons();
                RecipeGroupButtonWidget currentTab = recipeBook.getCurrentTab();
                if (currentTab == null)
                    return false;
                int nextTab = tabs.indexOf(currentTab) + (next ? 1 : -1);
                if (nextTab < 0)
                    nextTab = tabs.size() - 1;
                else if (nextTab >= tabs.size())
                    nextTab = 0;
                currentTab.setToggled(false);
                recipeBook.setCurrentTab(currentTab = tabs.get(nextTab));
                currentTab.setToggled(true);
                recipeBook.lambdacontrols_refreshResults(true);
                return true;
            } else if (client.currentScreen instanceof AdvancementsScreen) {
                AdvancementsScreenAccessor screen = (AdvancementsScreenAccessor) client.currentScreen;
                List<AdvancementTab> tabs = screen.getTabs().values().stream().distinct().collect(Collectors.toList());
                AdvancementTab tab = screen.getSelectedTab();
                if (tab == null)
                    return false;
                for (int i = 0; i < tabs.size(); i++) {
                    if (tabs.get(i).equals(tab)) {
                        int nextTab = i + (next ? 1 : -1);
                        if (nextTab < 0)
                            nextTab = tabs.size() - 1;
                        else if (nextTab >= tabs.size())
                            nextTab = 0;
                        screen.getAdvancementManager().selectTab(tabs.get(nextTab).getRoot(), true);
                        break;
                    }
                }
                return true;
            }
            return false;
        };
    }

    public static boolean handlePauseGame(@NotNull MinecraftClient client, @NotNull ButtonBinding binding, float value, @NotNull ButtonState action)
    {
        if (action == ButtonState.PRESS) {
            // If in game, then pause the game.
            if (client.currentScreen == null)
                client.openPauseMenu(false);
            else if (client.currentScreen instanceof HandledScreen && client.player != null) // If the current screen is a container then close it.
                client.player.closeHandledScreen();
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
    public static boolean handleScreenshot(@NotNull MinecraftClient client, @NotNull ButtonBinding binding, float value, @NotNull ButtonState action)
    {
        if (action == ButtonState.RELEASE)
            ScreenshotUtils.saveScreenshot(client.runDirectory, client.getWindow().getFramebufferWidth(), client.getWindow().getFramebufferHeight(), client.getFramebuffer(),
                    text -> client.execute(() -> client.inGameHud.getChatHud().addMessage(text)));
        return true;
    }

    public static boolean handleToggleSneak(@NotNull MinecraftClient client, @NotNull ButtonBinding button, float value, @NotNull ButtonState action)
    {
        if (client.player != null && !client.player.abilities.flying) {
            button.asKeyBinding().filter(binding -> action == ButtonState.PRESS).ifPresent(binding -> ((KeyBindingAccessor) binding).lambdacontrols_handlePressState(!binding.isPressed()));
            return true;
        }
        return false;
    }

    public static PressAction handleInventorySlotPad(int direction)
    {
        return (client, binding, value, action) -> {
            if (!(client.currentScreen instanceof HandledScreen && action != ButtonState.RELEASE))
                return false;

            HandledScreen inventory = (HandledScreen) client.currentScreen;
            HandledScreenAccessor accessor = (HandledScreenAccessor) inventory;
            int guiLeft = accessor.getX();
            int guiTop = accessor.getY();
            double mouseX = client.mouse.getX() * (double) client.getWindow().getScaledWidth() / (double) client.getWindow().getWidth();
            double mouseY = client.mouse.getY() * (double) client.getWindow().getScaledHeight() / (double) client.getWindow().getHeight();

            // Finds the hovered slot.
            Slot mouseSlot = accessor.lambdacontrols_getSlotAt(mouseX, mouseY);

            // Finds the closest slot in the GUI within 14 pixels.
            Optional<Slot> closestSlot = inventory.getScreenHandler().slots.parallelStream()
                    .filter(Predicate.isEqual(mouseSlot).negate())
                    .map(slot -> {
                        int posX = guiLeft + slot.x + 8;
                        int posY = guiTop + slot.y + 8;

                        int otherPosX = (int) mouseX;
                        int otherPosY = (int) mouseY;
                        if (mouseSlot != null) {
                            otherPosX = guiLeft + mouseSlot.x + 8;
                            otherPosY = guiTop + mouseSlot.y + 8;
                        }

                        // Distance between the slot and the cursor.
                        double distance = Math.sqrt(Math.pow(posX - otherPosX, 2) + Math.pow(posY - otherPosY, 2));
                        return Pair.of(slot, distance);
                    }).filter(entry -> {
                        Slot slot = entry.key;
                        int posX = guiLeft + slot.x + 8;
                        int posY = guiTop + slot.y + 8;
                        int otherPosX = (int) mouseX;
                        int otherPosY = (int) mouseY;
                        if (mouseSlot != null) {
                            otherPosX = guiLeft + mouseSlot.x + 8;
                            otherPosY = guiTop + mouseSlot.y + 8;
                        }
                        if (direction == 0)
                            return posY < otherPosY;
                        else if (direction == 1)
                            return posY > otherPosY;
                        else if (direction == 2)
                            return posX > otherPosX;
                        else if (direction == 3)
                            return posX < otherPosX;
                        else
                            return false;
                    })
                    .min(Comparator.comparingDouble(p -> p.value))
                    .map(p -> p.key);

            if (closestSlot.isPresent()) {
                Slot slot = closestSlot.get();
                int x = guiLeft + slot.x + 8;
                int y = guiTop + slot.y + 8;
                InputManager.queueMousePosition(x * (double) client.getWindow().getWidth() / (double) client.getWindow().getScaledWidth(),
                        y * (double) client.getWindow().getHeight() / (double) client.getWindow().getScaledHeight());
                return true;
            }
            return false;
        };
    }

    /**
     * Returns always true to the filter.
     *
     * @param client  The client instance.
     * @param binding The affected binding.
     * @return True.
     */
    public static boolean always(@NotNull MinecraftClient client, @NotNull ButtonBinding binding)
    {
        return true;
    }

    /**
     * Returns whether the client is in game or not.
     *
     * @param client  The client instance.
     * @param binding The affected binding.
     * @return True if the client is in game, else false.
     */
    public static boolean inGame(@NotNull MinecraftClient client, @NotNull ButtonBinding binding)
    {
        return client.currentScreen == null;
    }

    /**
     * Returns whether the client is in a non-interactive screen (which means require mouse input) or not.
     *
     * @param client  The client instance.
     * @param binding The affected binding.
     * @return True if the client is in a non-interactive screen, else false.
     */
    public static boolean inNonInteractiveScreens(@NotNull MinecraftClient client, @NotNull ButtonBinding binding)
    {
        if (client.currentScreen == null)
            return false;
        return !LambdaInput.isScreenInteractive(client.currentScreen);
    }

    /**
     * Returns whether the client is in an inventory or not.
     *
     * @param client  The client instance.
     * @param binding The affected binding.
     * @return True if the client is in an inventory, else false.
     */
    public static boolean inInventory(@NotNull MinecraftClient client, @NotNull ButtonBinding binding)
    {
        return client.currentScreen instanceof HandledScreen;
    }

    /**
     * Returns whether the client is in the advancements screen or not.
     *
     * @param client  The client instance.
     * @param binding The affected binding.
     * @return True if the client is in the advancements screen, else false.
     */
    public static boolean inAdvancements(@NotNull MinecraftClient client, @NotNull ButtonBinding binding)
    {
        return client.currentScreen instanceof AdvancementsScreen;
    }
}
