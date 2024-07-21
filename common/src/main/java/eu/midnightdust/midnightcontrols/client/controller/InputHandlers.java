/*
 * Copyright © 2021 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of midnightcontrols.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package eu.midnightdust.midnightcontrols.client.controller;

import com.google.common.collect.Lists;
import eu.midnightdust.midnightcontrols.client.enums.ButtonState;
import eu.midnightdust.midnightcontrols.client.MidnightControlsClient;
import eu.midnightdust.midnightcontrols.client.MidnightInput;
import eu.midnightdust.midnightcontrols.client.compat.InventoryTabsCompat;
import eu.midnightdust.midnightcontrols.client.compat.MidnightControlsCompat;
import eu.midnightdust.midnightcontrols.client.gui.RingScreen;
import eu.midnightdust.midnightcontrols.client.touch.gui.TouchscreenOverlay;
import eu.midnightdust.midnightcontrols.client.mixin.*;
import eu.midnightdust.midnightcontrols.client.util.HandledScreenAccessor;
import eu.midnightdust.midnightcontrols.client.util.InventoryUtil;
import eu.midnightdust.midnightcontrols.client.util.ToggleSneakSprintUtil;
import eu.midnightdust.midnightcontrols.client.util.platform.ItemGroupUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.advancement.AdvancementsScreen;
import net.minecraft.client.gui.screen.ingame.*;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.client.gui.widget.TabNavigationWidget;
import net.minecraft.client.util.ScreenshotRecorder;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

import java.util.Optional;

import static eu.midnightdust.midnightcontrols.client.MidnightControlsClient.client;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_2;

/**
 * Represents some input handlers.
 *
 * @author LambdAurora
 * @version 1.7.0
 * @since 1.1.0
 */
public class InputHandlers {
    private InputHandlers() {
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
                MidnightControlsClient.ring.cyclePage(next);
            } else if (client.currentScreen instanceof CreativeInventoryScreenAccessor inventory) {
                inventory.midnightcontrols$setSelectedTab(ItemGroupUtil.cycleTab(next, client));
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
                        screen.getAdvancementManager().selectTab(tabs.get(nextTab).getRoot().getAdvancementEntry(), true);
                        break;
                    }
                }
                return true;
            } else if (client.currentScreen != null && client.currentScreen.children().stream().anyMatch(e -> e instanceof TabNavigationWidget)) {
                return Lists.newCopyOnWriteArrayList(client.currentScreen.children()).stream().anyMatch(e -> {
                    if (e instanceof TabNavigationWidget tabs) {
                        TabNavigationWidgetAccessor accessor = (TabNavigationWidgetAccessor) tabs;
                        int tabIndex = accessor.getTabs().indexOf(accessor.getTabManager().getCurrentTab());
                        if (next ? tabIndex+1 < accessor.getTabs().size() : tabIndex > 0) {
                            if (next) tabs.selectTab(tabIndex + 1, true);
                            else tabs.selectTab(tabIndex - 1, true);
                            return true;
                        }
                    }
                    return false;
                });
            } else return MidnightControlsCompat.handleTabs(client.currentScreen, next);

            return false;
        };
    }

    public static PressAction handlePage(boolean next) {
        return (client, button, value, action) -> {
            if (action == ButtonState.RELEASE)
                return false;
            if (client.currentScreen instanceof CreativeInventoryScreen creativeScreen) {
                return ItemGroupUtil.cyclePage(next, creativeScreen);
            }
            if (MidnightControlsCompat.isInventoryTabsPresent()) InventoryTabsCompat.handleInventoryPage(client.currentScreen, next);

            return false;
        };
    }
    public static PressAction handleExit() {
        return (client, button, value, action) -> {
            if (client.currentScreen != null && client.currentScreen.getClass() != TitleScreen.class) {
                if (!MidnightControlsCompat.handleMenuBack(client, client.currentScreen))
                    if (!MidnightControlsClient.input.tryGoBack(client.currentScreen))
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

            if (MidnightControlsClient.input.inventoryInteractionCooldown > 0)
                return true;
            double x = client.mouse.getX() * (double) client.getWindow().getScaledWidth() / (double) client.getWindow().getWidth();
            double y = client.mouse.getY() * (double) client.getWindow().getScaledHeight() / (double) client.getWindow().getHeight();

            var accessor = (HandledScreenAccessor) screen;
            Slot slot = accessor.midnightcontrols$getSlotAt(x, y);

            int slotId;
            if (slot == null) {
                if (button.getName().equals("take_all")) {
                    return false;
                }
                slotId = accessor.midnightcontrols$isClickOutsideBounds(x, y, accessor.getX(), accessor.getY(), GLFW_MOUSE_BUTTON_1) ? -999 : -1;
            } else {
                slotId = slot.id;
            }
            var actionType = SlotActionType.PICKUP;
            int clickData = GLFW.GLFW_MOUSE_BUTTON_1;

            MidnightControlsClient.input.inventoryInteractionCooldown = 5;
            switch (button.getName()) {
                case "take_all" -> {
                    if (screen instanceof CreativeInventoryScreen) {
                        if (slot != null && (((CreativeInventoryScreenAccessor) accessor).midnightcontrols$isCreativeInventorySlot(slot) || MidnightControlsCompat.streamCompatHandlers().anyMatch(handler -> handler.isCreativeSlot(screen, slot))))
                            actionType = SlotActionType.CLONE;
                    }
                }
                case "take" -> {
                    clickData = GLFW_MOUSE_BUTTON_2;
                }
                case "quick_move" -> {
                    actionType = SlotActionType.QUICK_MOVE;
                }
                default -> {
                    return false;
                }
            }
            accessor.midnightcontrols$onMouseClick(slot, slotId, clickData, actionType);
            return true;
        };
    }

    public static boolean handlePauseGame(@NotNull MinecraftClient client, @NotNull ButtonBinding binding, float value, @NotNull ButtonState action) {
        if (action == ButtonState.PRESS) {
            // If in game, then pause the game.
            if (client.currentScreen == null || client.currentScreen instanceof RingScreen)
                client.openGameMenu(false);
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
        return ToggleSneakSprintUtil.toggleSneak(button);
    }
    public static boolean handleToggleSprint(@NotNull MinecraftClient client, @NotNull ButtonBinding button, float value, @NotNull ButtonState action) {
        return ToggleSneakSprintUtil.toggleSprint(button);
    }

    public static PressAction handleInventorySlotPad(int direction) {
        return (client, binding, value, action) -> {
            if (!(client.currentScreen instanceof HandledScreen<?> inventory && action != ButtonState.RELEASE))
                return false;

            var accessor = (HandledScreenAccessor) inventory;

            Optional<Slot> closestSlot = InventoryUtil.findClosestSlot(inventory, direction);

            if (closestSlot.isPresent()) {
                var slot = closestSlot.get();
                int x = accessor.getX() + slot.x + 8;
                int y = accessor.getY() + slot.y + 8;
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
     * @param binding the affected binding
     * @return true
     */
    public static boolean always(@NotNull ButtonBinding binding) {
        return true;
    }

    /**
     * Returns whether the client is in game or not.
     *
     * @param binding the affected binding
     * @return true if the client is in game, else false
     */
    public static boolean inGame(@NotNull ButtonBinding binding) {
        return (client.currentScreen == null && MidnightControlsClient.input.screenCloseCooldown <= 0) || client.currentScreen instanceof TouchscreenOverlay || client.currentScreen instanceof RingScreen;
    }

    /**
     * Returns whether the client is in a non-interactive screen (which means require mouse input) or not.
     *
     * @param binding the affected binding
     * @return true if the client is in a non-interactive screen, else false
     */
    public static boolean inNonInteractiveScreens(@NotNull ButtonBinding binding) {
        if (client.currentScreen == null)
            return false;
        return !MidnightInput.isScreenInteractive(client.currentScreen);
    }

    /**
     * Returns whether the client is in an inventory or not.
     *
     * @param binding the affected binding
     * @return true if the client is in an inventory, else false
     */
    public static boolean inInventory(@NotNull ButtonBinding binding) {
        return client.currentScreen instanceof HandledScreen;
    }

    /**
     * Returns whether the client is in the advancements screen or not.
     *
     * @param binding the affected binding
     * @return true if the client is in the advancements screen, else false
     */
    public static boolean inAdvancements(@NotNull ButtonBinding binding) {
        return client.currentScreen instanceof AdvancementsScreen;
    }
}
