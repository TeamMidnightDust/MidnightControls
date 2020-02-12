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
import me.lambdaurora.lambdacontrols.client.mixin.AdvancementsScreenAccessor;
import me.lambdaurora.lambdacontrols.client.mixin.CreativeInventoryScreenAccessor;
import me.lambdaurora.lambdacontrols.client.util.ContainerScreenAccessor;
import me.lambdaurora.lambdacontrols.client.util.KeyBindingAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.advancement.AdvancementTab;
import net.minecraft.client.gui.screen.advancement.AdvancementsScreen;
import net.minecraft.client.gui.screen.ingame.ContainerScreen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.util.ScreenshotUtils;
import net.minecraft.container.Slot;
import net.minecraft.item.ItemGroup;
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
 * @version 1.1.1
 * @since 1.1.0
 */
public class InputHandlers
{
    private InputHandlers()
    {
    }

    public static PressAction handleHotbar(boolean right)
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
                CreativeInventoryScreenAccessor inventory = (CreativeInventoryScreenAccessor) client.currentScreen;
                int currentSelectedTab = inventory.getSelectedTab();
                int nextTab = currentSelectedTab + (right ? 1 : -1);
                if (nextTab < 0)
                    nextTab = ItemGroup.GROUPS.length - 1;
                else if (nextTab >= ItemGroup.GROUPS.length)
                    nextTab = 0;
                inventory.lambdacontrols_setSelectedTab(ItemGroup.GROUPS[nextTab]);
                return true;
            } else if (client.currentScreen instanceof AdvancementsScreen) {
                AdvancementsScreenAccessor screen = (AdvancementsScreenAccessor) client.currentScreen;
                List<AdvancementTab> tabs = screen.getTabs().values().stream().distinct().collect(Collectors.toList());
                AdvancementTab tab = screen.getSelectedTab();
                for (int i = 0; i < tabs.size(); i++) {
                    if (tabs.get(i).equals(tab)) {
                        int nextTab = i + (right ? 1 : -1);
                        if (nextTab < 0)
                            nextTab = tabs.size() - 1;
                        else if (nextTab >= tabs.size())
                            nextTab = 0;
                        screen.getAdvancementManager().selectTab(tabs.get(nextTab).getRoot(), true);
                        break;
                    }
                }
            }
            return false;
        };
    }

    public static boolean handlePauseGame(@NotNull MinecraftClient client, @NotNull ButtonBinding binding, @NotNull ButtonState action)
    {
        if (action == ButtonState.PRESS) {
            // If in game, then pause the game.
            if (client.currentScreen == null)
                client.openPauseMenu(false);
            else if (client.currentScreen instanceof ContainerScreen && client.player != null) // If the current screen is a container then close it.
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
    public static boolean handleScreenshot(@NotNull MinecraftClient client, @NotNull ButtonBinding binding, @NotNull ButtonState action)
    {
        if (action == ButtonState.PRESS)
            ScreenshotUtils.saveScreenshot(client.runDirectory, client.getWindow().getFramebufferWidth(), client.getWindow().getFramebufferHeight(), client.getFramebuffer(),
                    text -> client.execute(() -> client.inGameHud.getChatHud().addMessage(text)));
        return true;
    }

    public static boolean handleToggleSneak(@NotNull MinecraftClient client, @NotNull ButtonBinding button, @NotNull ButtonState action)
    {
        if (client.player != null && !client.player.abilities.flying) {
            button.asKeyBinding().filter(binding -> action == ButtonState.PRESS).ifPresent(binding -> ((KeyBindingAccessor) binding).lambdacontrols_handlePressState(!binding.isPressed()));
            return true;
        }
        return false;
    }

    public static PressAction handleInventorySlotPad(int direction)
    {
        return (client, binding, action) -> {
            if (!(client.currentScreen instanceof ContainerScreen && action != ButtonState.RELEASE))
                return false;

            ContainerScreen inventory = (ContainerScreen) client.currentScreen;
            ContainerScreenAccessor accessor = (ContainerScreenAccessor) inventory;
            int guiLeft = accessor.getX();
            int guiTop = accessor.getY();
            double mouseX = client.mouse.getX() * (double) client.getWindow().getScaledWidth() / (double) client.getWindow().getWidth();
            double mouseY = client.mouse.getY() * (double) client.getWindow().getScaledHeight() / (double) client.getWindow().getHeight();

            // Finds the hovered slot.
            Slot mouseSlot = accessor.lambdacontrols_getSlotAt(mouseX, mouseY);

            // Finds the closest slot in the GUI within 14 pixels.
            Optional<Slot> closestSlot = inventory.getContainer().slots.parallelStream()
                    .filter(Predicate.isEqual(mouseSlot).negate())
                    .map(slot -> {
                        int posX = guiLeft + slot.xPosition + 8;
                        int posY = guiTop + slot.yPosition + 8;

                        int otherPosX = (int) mouseX;
                        int otherPosY = (int) mouseY;
                        if (mouseSlot != null) {
                            otherPosX = guiLeft + mouseSlot.xPosition + 8;
                            otherPosY = guiTop + mouseSlot.yPosition + 8;
                        }

                        // Distance between the slot and the cursor.
                        double distance = Math.sqrt(Math.pow(posX - otherPosX, 2) + Math.pow(posY - otherPosY, 2));
                        return Pair.of(slot, distance);
                    }).filter(entry -> {
                        Slot slot = entry.key;
                        int posX = guiLeft + slot.xPosition + 8;
                        int posY = guiTop + slot.yPosition + 8;
                        int otherPosX = (int) mouseX;
                        int otherPosY = (int) mouseY;
                        if (mouseSlot != null) {
                            otherPosX = guiLeft + mouseSlot.xPosition + 8;
                            otherPosY = guiTop + mouseSlot.yPosition + 8;
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
                int x = guiLeft + slot.xPosition + 8;
                int y = guiTop + slot.yPosition + 8;
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
     * Returns whether the client is in an inventory or not.
     *
     * @param client  The client instance.
     * @param binding The affected binding.
     * @return True if the client is in an inventory, else false.
     */
    public static boolean inInventory(@NotNull MinecraftClient client, @NotNull ButtonBinding binding)
    {
        return client.currentScreen instanceof ContainerScreen;
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
