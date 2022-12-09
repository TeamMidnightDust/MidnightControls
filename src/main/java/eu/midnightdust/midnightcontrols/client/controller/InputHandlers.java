/*
 * Copyright © 2021 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of midnightcontrols.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package eu.midnightdust.midnightcontrols.client.controller;

import eu.midnightdust.midnightcontrols.client.ButtonState;
import eu.midnightdust.midnightcontrols.client.MidnightControlsClient;
import eu.midnightdust.midnightcontrols.client.MidnightControlsConfig;
import eu.midnightdust.midnightcontrols.client.MidnightInput;
import eu.midnightdust.midnightcontrols.client.compat.InventoryTabsCompat;
import eu.midnightdust.midnightcontrols.client.compat.MidnightControlsCompat;
import eu.midnightdust.midnightcontrols.client.compat.SodiumCompat;
import eu.midnightdust.midnightcontrols.client.gui.RingScreen;
import eu.midnightdust.midnightcontrols.client.mixin.AdvancementsScreenAccessor;
import eu.midnightdust.midnightcontrols.client.mixin.CreativeInventoryScreenAccessor;
import eu.midnightdust.midnightcontrols.client.mixin.RecipeBookWidgetAccessor;
import eu.midnightdust.midnightcontrols.client.util.HandledScreenAccessor;
import eu.midnightdust.midnightcontrols.client.util.MouseAccessor;
import net.fabricmc.fabric.impl.client.itemgroup.CreativeGuiExtensions;
import net.fabricmc.fabric.impl.client.itemgroup.FabricCreativeGuiComponents;
import net.fabricmc.fabric.impl.itemgroup.FabricItemGroup;
import net.fabricmc.fabric.impl.itemgroup.ItemGroupHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.advancement.AdvancementsScreen;
import net.minecraft.client.gui.screen.ingame.*;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.client.gui.widget.PressableWidget;
import net.minecraft.client.util.ScreenshotRecorder;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.math.MathHelper;
import org.aperlambda.lambdacommon.utils.Pair;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_2;

/**
 * Represents some input handlers.
 *
 * @author LambdAurora
 * @version 1.7.0
 * @since 1.1.0
 */
@SuppressWarnings("UnstableApiUsage")
public class InputHandlers {
    private InputHandlers() {
    }
    private static List<ItemGroup> getVisibleGroups(CreativeInventoryScreen screen) {
        return ItemGroupHelper.sortedGroups.stream()
            .filter(itemGroup -> {
                if (FabricCreativeGuiComponents.COMMON_GROUPS.contains(itemGroup)) return true;
                return ((CreativeGuiExtensions)screen).fabric_currentPage() == ((FabricItemGroup)itemGroup).getPage() && itemGroup.shouldDisplay() && (!itemGroup.equals(ItemGroups.OPERATOR) || ItemGroups.operatorEnabled);
            }).toList();
    }

    public static PressAction handleHotbar(boolean next) {
        return (client, button, value, action) -> {
            if (action == ButtonState.RELEASE)
                return false;

            // When in-game
            if (client.currentScreen == null && client.player != null) {
                if (!client.player.isSpectator()) {
                    if (next)
                        client.player.getInventory().scrollInHotbar(-1.0);
                    else
                        client.player.getInventory().scrollInHotbar(1.0);
                }
                else {
                    if (client.inGameHud.getSpectatorHud().isOpen()) {
                        client.inGameHud.getSpectatorHud().cycleSlot(next ? -1 : 1);
                    } else {
                        float g = MathHelper.clamp(client.player.getAbilities().getFlySpeed() + (next ? 1 : -1) * 0.005F, 0.0F, 0.2F);
                        client.player.getAbilities().setFlySpeed(g);
                    }
                }
                return true;
            } else if (client.currentScreen instanceof RingScreen) {
                MidnightControlsClient.get().ring.cyclePage(next);
            } else if (client.currentScreen instanceof CreativeInventoryScreenAccessor inventory) {
                ItemGroup currentTab = CreativeInventoryScreenAccessor.getSelectedTab();
                int currentColumn = currentTab.getColumn();
                ItemGroup.Row currentRow = currentTab.getRow();
                ItemGroup newTab = null;
                List<ItemGroup> visibleTabs = getVisibleGroups((CreativeInventoryScreen) client.currentScreen);
                for (ItemGroup tab : visibleTabs) {
                    if (tab.getRow().equals(currentRow) && ((newTab == null && ((next && tab.getColumn() > currentColumn) ||
                            (!next && tab.getColumn() < currentColumn))) || (newTab != null && ((next && tab.getColumn() > currentColumn && tab.getColumn() < newTab.getColumn()) ||
                            (!next && tab.getColumn() < currentColumn && tab.getColumn() > newTab.getColumn())))))
                        newTab = tab;
                }
                if (newTab == null)
                    for (ItemGroup tab : visibleTabs) {
                        if ((tab.getRow().compareTo(currentRow)) != 0 && ((next && newTab == null || next && newTab.getColumn() > tab.getColumn()) || (!next && newTab == null) || (!next && newTab.getColumn() < tab.getColumn())))
                            newTab = tab;
                    }
                if (newTab == null) {
                    for (ItemGroup tab : visibleTabs) {
                        if ((next && tab.getRow() == ItemGroup.Row.TOP && tab.getColumn() == 0) ||
                                !next && tab.getRow() == ItemGroup.Row.BOTTOM && (newTab == null || tab.getColumn() > newTab.getColumn()))
                            newTab = tab;
                    }
                }
                if (newTab == null || newTab.equals(currentTab)) newTab = ItemGroups.getDefaultTab();
                inventory.midnightcontrols$setSelectedTab(newTab);
                return true;
            } else if (client.currentScreen instanceof InventoryScreen || client.currentScreen instanceof CraftingScreen || client.currentScreen instanceof AbstractFurnaceScreen<?>) {
                RecipeBookWidget recipeBook;
                if (client.currentScreen instanceof InventoryScreen inventoryScreen) recipeBook = inventoryScreen.getRecipeBookWidget();
                else if (client.currentScreen instanceof CraftingScreen craftingScreen) recipeBook = craftingScreen.getRecipeBookWidget();
                else recipeBook = ((AbstractFurnaceScreen<?>)client.currentScreen).getRecipeBookWidget();
                var recipeBookAccessor = (RecipeBookWidgetAccessor) recipeBook;
                var tabs = recipeBookAccessor.getTabButtons();
                var currentTab = recipeBookAccessor.getCurrentTab();
                if (currentTab == null || !recipeBook.isOpen()) {
                    if (MidnightControlsCompat.isInventoryTabsPresent()) InventoryTabsCompat.handleInventoryTabs(client.currentScreen, next);
                    return false;
                }
                int nextTab = tabs.indexOf(currentTab) + (next ? 1 : -1);
                if (nextTab < 0)
                    nextTab = tabs.size() - 1;
                else if (nextTab >= tabs.size())
                    nextTab = 0;
                currentTab.setToggled(false);
                recipeBookAccessor.setCurrentTab(currentTab = tabs.get(nextTab));
                currentTab.setToggled(true);
                recipeBookAccessor.midnightcontrols$refreshResults(true);
                return true;
            } else if (client.currentScreen instanceof AdvancementsScreenAccessor screen) {
                var tabs = screen.getTabs().values().stream().distinct().toList();
                var tab = screen.getSelectedTab();
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
            } else {
                if (FabricLoader.getInstance().isModLoaded("sodium")) SodiumCompat.handleTabs(client.currentScreen, next);
            }
            if (MidnightControlsCompat.isInventoryTabsPresent()) InventoryTabsCompat.handleInventoryTabs(client.currentScreen, next);
            return false;
        };
    }
    public static PressAction handlePage(boolean next) {
        return (client, button, value, action) -> {
            if (client.currentScreen instanceof CreativeInventoryScreen) {
                var screen = (HandledScreenAccessor) client.currentScreen;
                try {
                    if (next) {
                        return client.currentScreen.children().stream().filter(element -> element instanceof PressableWidget)
                                .map(element -> (PressableWidget) element)
                                .filter(element -> element.getMessage() != null && element.getMessage().getContent() != null)
                                .anyMatch(element -> {
                                    if (element.getMessage().getString().equals(">")) {
                                        element.onPress();
                                        return true;
                                    }
                                    return false;
                                });
                    } else {
                        return client.currentScreen.children().stream().filter(element -> element instanceof PressableWidget)
                                .map(element -> (PressableWidget) element)
                                .filter(element -> element.getMessage() != null && element.getMessage().getContent() != null)
                                .anyMatch(element -> {
                                    if (element.getMessage().getString().equals("<")) {
                                        element.onPress();
                                        return true;
                                    }
                                    return false;
                                });
                    }
                } catch (Exception ignored) {}
            }
            if (MidnightControlsCompat.isInventoryTabsPresent()) InventoryTabsCompat.handleInventoryPage(client.currentScreen, next);
            return false;
        };
    }
    public static PressAction handleExit() {
        return (client, button, value, action) -> {
            if (client.currentScreen != null && client.currentScreen.getClass() != TitleScreen.class) {
                if (!MidnightControlsCompat.handleMenuBack(client, client.currentScreen))
                    if (!MidnightControlsClient.get().input.tryGoBack(client.currentScreen))
                        client.currentScreen.close();
                return true;
            }
            return false;
        };
    }
    public static PressAction handleActions() {
        return (client, button, value, action) -> {
            if (!(client.currentScreen instanceof HandledScreen<?> screen)) return false;
            if (client.interactionManager == null || client.player == null)
                return false;

            if (MidnightControlsClient.get().input.inventoryInteractionCooldown > 0)
                return true;
            double x = client.mouse.getX() * (double) client.getWindow().getScaledWidth() / (double) client.getWindow().getWidth();
            double y = client.mouse.getY() * (double) client.getWindow().getScaledHeight() / (double) client.getWindow().getHeight();

            var accessor = (HandledScreenAccessor) screen;
            Slot slot = ((HandledScreenAccessor) screen).midnightcontrols$getSlotAt(x, y);

            int slotId;
            if (slot == null) {
                if (button.getName().equals("take_all")) {
                    ((MouseAccessor) client.mouse).setLeftButtonClicked(true);
                    return false;
                }
                slotId = accessor.midnightcontrols$isClickOutsideBounds(x, y, accessor.getX(), accessor.getY(), GLFW_MOUSE_BUTTON_1) ? -999 : -1;
            } else {
                slotId = slot.id;
            }
            var actionType = SlotActionType.PICKUP;
            int clickData = GLFW.GLFW_MOUSE_BUTTON_1;

            MidnightControlsClient.get().input.inventoryInteractionCooldown = 5;
            switch (button.getName()) {
            case "take_all": {
                if (screen instanceof CreativeInventoryScreen) {
                    if (slot != null && (((CreativeInventoryScreenAccessor) accessor).midnightcontrols$isCreativeInventorySlot(slot) || MidnightControlsCompat.streamCompatHandlers().anyMatch(handler -> handler.isCreativeSlot(screen, slot))))
                        actionType = SlotActionType.CLONE;
                }
                break;
            }
            case "take": {
                clickData = GLFW_MOUSE_BUTTON_2;
                break;
            }
            case "quick_move": {
                actionType = SlotActionType.QUICK_MOVE;
                break;
            }
            default:
                return false;
            }
            accessor.midnightcontrols$onMouseClick(slot, slotId, clickData, actionType);
            return true;
        };
    }

    public static boolean handlePauseGame(@NotNull MinecraftClient client, @NotNull ButtonBinding binding, float value, @NotNull ButtonState action) {
        if (action == ButtonState.PRESS) {
            // If in game, then pause the game.
            if (client.currentScreen == null || client.currentScreen instanceof RingScreen)
                client.openPauseMenu(false);
            else if (client.currentScreen instanceof HandledScreen && client.player != null) // If the current screen is a container then close it.
                client.player.closeHandledScreen();
            else // Else just close the current screen.
                client.currentScreen.close();
        }
        return true;
    }

    /**
     * Handles the screenshot action.
     *
     * @param client the client instance
     * @param binding the binding which fired the action
     * @param action the action done on the binding
     * @return true if handled, else false
     */
    public static boolean handleScreenshot(@NotNull MinecraftClient client, @NotNull ButtonBinding binding, float value, @NotNull ButtonState action) {
        if (action == ButtonState.RELEASE)
            ScreenshotRecorder.saveScreenshot(client.runDirectory, client.getFramebuffer(),
                    text -> client.execute(() -> client.inGameHud.getChatHud().addMessage(text)));
        return true;
    }

    public static boolean handleToggleSneak(@NotNull MinecraftClient client, @NotNull ButtonBinding button, float value, @NotNull ButtonState action) {
        button.asKeyBinding().ifPresent(binding -> {
            boolean sneakToggled = client.options.getSneakToggled().getValue();
            if (client.player.getAbilities().flying && sneakToggled)
                client.options.getSneakToggled().setValue(false);
            else if (MidnightControlsConfig.controllerToggleSneak != sneakToggled)
                client.options.getSneakToggled().setValue(!sneakToggled);
            binding.setPressed(button.pressed);
            if (client.player.getAbilities().flying && sneakToggled)
                client.options.getSneakToggled().setValue(true);
            else if (MidnightControlsConfig.controllerToggleSneak != sneakToggled)
                client.options.getSneakToggled().setValue(sneakToggled);
        });
        return true;
    }
    public static boolean handleToggleSprint(@NotNull MinecraftClient client, @NotNull ButtonBinding button, float value, @NotNull ButtonState action) {
        button.asKeyBinding().ifPresent(binding -> {
            boolean sprintToggled = client.options.getSprintToggled().getValue();
            if (client.player.getAbilities().flying && sprintToggled)
                client.options.getSprintToggled().setValue(false);
            else if (MidnightControlsConfig.controllerToggleSneak != sprintToggled)
                client.options.getSprintToggled().setValue(!sprintToggled);
            binding.setPressed(button.pressed);
            if (client.player.getAbilities().flying && sprintToggled)
                client.options.getSprintToggled().setValue(true);
            else if (MidnightControlsConfig.controllerToggleSneak != sprintToggled)
                client.options.getSprintToggled().setValue(sprintToggled);
        });
        return true;
    }

    public static PressAction handleInventorySlotPad(int direction) {
        return (client, binding, value, action) -> {
            if (!(client.currentScreen instanceof HandledScreen inventory && action != ButtonState.RELEASE))
                return false;

            var accessor = (HandledScreenAccessor) inventory;
            int guiLeft = accessor.getX();
            int guiTop = accessor.getY();
            double mouseX = client.mouse.getX() * (double) client.getWindow().getScaledWidth() / (double) client.getWindow().getWidth();
            double mouseY = client.mouse.getY() * (double) client.getWindow().getScaledHeight() / (double) client.getWindow().getHeight();

            // Finds the hovered slot.
            var mouseSlot = accessor.midnightcontrols$getSlotAt(mouseX, mouseY);

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
                        var slot = entry.key;
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
                var slot = closestSlot.get();
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
     * @param client the client instance
     * @param binding the affected binding
     * @return true
     */
    public static boolean always(@NotNull MinecraftClient client, @NotNull ButtonBinding binding) {
        return true;
    }

    /**
     * Returns whether the client is in game or not.
     *
     * @param client the client instance
     * @param binding the affected binding
     * @return true if the client is in game, else false
     */
    public static boolean inGame(@NotNull MinecraftClient client, @NotNull ButtonBinding binding) {
        return (client.currentScreen == null && MidnightControlsClient.get().input.screenCloseCooldown <= 0) || client.currentScreen instanceof RingScreen;
    }

    /**
     * Returns whether the client is in a non-interactive screen (which means require mouse input) or not.
     *
     * @param client the client instance
     * @param binding the affected binding
     * @return true if the client is in a non-interactive screen, else false
     */
    public static boolean inNonInteractiveScreens(@NotNull MinecraftClient client, @NotNull ButtonBinding binding) {
        if (client.currentScreen == null)
            return false;
        return !MidnightInput.isScreenInteractive(client.currentScreen);
    }

    /**
     * Returns whether the client is in an inventory or not.
     *
     * @param client the client instance
     * @param binding the affected binding
     * @return true if the client is in an inventory, else false
     */
    public static boolean inInventory(@NotNull MinecraftClient client, @NotNull ButtonBinding binding) {
        return client.currentScreen instanceof HandledScreen;
    }

    /**
     * Returns whether the client is in the advancements screen or not.
     *
     * @param client the client instance
     * @param binding the affected binding
     * @return true if the client is in the advancements screen, else false
     */
    public static boolean inAdvancements(@NotNull MinecraftClient client, @NotNull ButtonBinding binding) {
        return client.currentScreen instanceof AdvancementsScreen;
    }
}
