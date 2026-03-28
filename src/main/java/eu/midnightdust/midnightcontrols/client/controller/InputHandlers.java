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
import eu.midnightdust.midnightcontrols.client.compat.MidnightControlsCompat;
import eu.midnightdust.midnightcontrols.client.gui.RingScreen;
import eu.midnightdust.midnightcontrols.client.touch.gui.TouchscreenOverlay;
import eu.midnightdust.midnightcontrols.client.mixin.*;
import eu.midnightdust.midnightcontrols.client.util.HandledScreenAccessor;
import eu.midnightdust.midnightcontrols.client.util.InventoryUtil;
import eu.midnightdust.midnightcontrols.client.util.ToggleSneakSprintUtil;
import eu.midnightdust.midnightcontrols.client.util.platform.ItemGroupUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Screenshot;
import net.minecraft.client.gui.components.tabs.TabNavigationBar;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.advancements.AdvancementsScreen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.AbstractRecipeBookScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.Slot;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

import java.util.Optional;

import static eu.midnightdust.midnightcontrols.client.MidnightControlsClient.client;
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
            if (client.screen == null && client.player != null) {
                if (!client.player.isSpectator()) {
                    var inv = client.player.getInventory();
                    if (next)
                        inv.setSelectedSlot(inv.getSelectedSlot() < 8 ? inv.getSelectedSlot() + 1 : inv.getSelectedSlot() - 8);
                    else
                        inv.setSelectedSlot(inv.getSelectedSlot() > 0 ? inv.getSelectedSlot() - 1 : inv.getSelectedSlot() + 8);
                }
                else {
                    if (client.gui.getSpectatorGui().isMenuActive()) {
                        client.gui.getSpectatorGui().onMouseScrolled(next ? -1 : 1);
                    } else {
                        float g = Mth.clamp(client.player.getAbilities().getFlyingSpeed() + (next ? 1 : -1) * 0.005F, 0.0F, 0.2F);
                        client.player.getAbilities().setFlyingSpeed(g);
                    }
                }
                return true;
            } else if (client.screen instanceof RingScreen) {
                MidnightControlsClient.ring.cyclePage(next);
            } else if (client.screen instanceof CreativeInventoryScreenAccessor inventory) {
                inventory.midnightcontrols$setSelectedTab(ItemGroupUtil.cycleTab(next, client));
                return true;
            } else if (client.screen instanceof AbstractRecipeBookScreen<?> recipeBookScreen) {
                RecipeBookComponent<?> recipeBook = ((RecipeBookScreenAccessor) recipeBookScreen).getRecipeBook();

                var recipeBookAccessor = (RecipeBookWidgetAccessor) recipeBook;
                var tabs = recipeBookAccessor.getTabButtons();
                var currentTab = recipeBookAccessor.getCurrentTab();
                if (currentTab == null || !recipeBook.isVisible()) {
                    return MidnightControlsCompat.handleTabs(client.screen, next);
                }
                int nextTab = tabs.indexOf(currentTab) + (next ? 1 : -1);
                if (nextTab < 0)
                    nextTab = tabs.size() - 1;
                else if (nextTab >= tabs.size())
                    nextTab = 0;
                currentTab.active = false;
                recipeBookAccessor.setCurrentTab(currentTab = tabs.get(nextTab));
                currentTab.active = true;
                recipeBookScreen.recipesUpdated();
                return true;
            } else if (client.screen instanceof AdvancementsScreenAccessor screen) {
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
                        screen.getAdvancementManager().setSelectedTab(tabs.get(nextTab).getRootNode().holder(), true);
                        break;
                    }
                }
                return true;
            } else if (client.screen != null && client.screen.children().stream().anyMatch(e -> e instanceof TabNavigationBar)) {
                return Lists.newCopyOnWriteArrayList(client.screen.children()).stream().anyMatch(e -> {
                    if (e instanceof TabNavigationBar tabs) {
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
            } else return MidnightControlsCompat.handleTabs(client.screen, next);

            return false;
        };
    }

    public static PressAction handlePage(boolean next) {
        return (client, button, value, action) -> {
            if (action == ButtonState.RELEASE)
                return false;
            if (client.screen instanceof CreativeModeInventoryScreen creativeScreen) {
                return ItemGroupUtil.cyclePage(next, creativeScreen);
            }


            return MidnightControlsCompat.handlePages(client.screen, next);
        };
    }
    public static PressAction handleExit() {
        return (client, button, value, action) -> {
            if (client.screen != null && client.screen.getClass() != TitleScreen.class) {
                if (!MidnightControlsCompat.handleMenuBack(client, client.screen))
                    if (!MidnightControlsClient.input.tryGoBack(client.screen))
                        client.screen.onClose();
                return true;
            }
            return false;
        };
    }
    public static PressAction handleActions() {
        return (client, button, value, action) -> {
            if (!(client.screen instanceof AbstractContainerScreen<?> screen)) return false;
            if (client.gameMode == null || client.player == null)
                return false;

            if (MidnightControlsClient.input.inventoryInteractionCooldown > 0)
                return true;
            double x = client.mouseHandler.xpos() * (double) client.getWindow().getGuiScaledWidth() / (double) client.getWindow().getScreenWidth();
            double y = client.mouseHandler.ypos() * (double) client.getWindow().getGuiScaledHeight() / (double) client.getWindow().getScreenHeight();

            var accessor = (HandledScreenAccessor) screen;
            Slot slot = accessor.midnightcontrols$getSlotAt(x, y);

            int slotId;
            if (slot == null) {
                if (button.getName().equals("take_all")) {
                    return false;
                }
                slotId = accessor.midnightcontrols$isClickOutsideBounds(x, y, accessor.getX(), accessor.getY()) ? -999 : -1;
            } else {
                slotId = slot.index;
            }
            var actionType = ContainerInput.PICKUP;
            int clickData = GLFW.GLFW_MOUSE_BUTTON_1;

            MidnightControlsClient.input.inventoryInteractionCooldown = 5;
            switch (button.getName()) {
                case "take_all" -> {
                    if (screen instanceof CreativeModeInventoryScreen) {
                        if (slot != null && (((CreativeInventoryScreenAccessor) accessor).midnightcontrols$isCreativeInventorySlot(slot) || MidnightControlsCompat.streamCompatHandlers().anyMatch(handler -> handler.isCreativeSlot(screen, slot))))
                            actionType = ContainerInput.CLONE;
                    }
                }
                case "take" -> {
                    clickData = GLFW_MOUSE_BUTTON_2;
                }
                case "quick_move" -> {
                    actionType = ContainerInput.QUICK_MOVE;
                }
                default -> {
                    return false;
                }
            }
            accessor.midnightcontrols$onMouseClick(slot, slotId, clickData, actionType);
            return true;
        };
    }

    public static boolean handlePauseGame(@NotNull Minecraft client, @NotNull ButtonBinding binding, float value, @NotNull ButtonState action) {
        if (action == ButtonState.PRESS) {
            // If in game, then pause the game.
            if (client.screen == null || client.screen instanceof RingScreen)
                client.pauseGame(false);
            else if (client.screen instanceof AbstractContainerScreen && client.player != null) // If the current screen is a container then close it.
                client.player.closeContainer();
            else // Else just close the current screen.
                client.screen.onClose();
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
    public static boolean handleScreenshot(@NotNull Minecraft client, @NotNull ButtonBinding binding, float value, @NotNull ButtonState action) {
        if (action == ButtonState.RELEASE)
            //~ if >=26.1 '.addMessage' -> '.addClientSystemMessage'
            Screenshot.grab(client.gameDirectory, client.getMainRenderTarget(), text -> client.execute(() -> client.gui.getChat().addClientSystemMessage(text)));
        return true;
    }

    public static boolean handleToggleSneak(@NotNull Minecraft client, @NotNull ButtonBinding button, float value, @NotNull ButtonState action) {
        return ToggleSneakSprintUtil.toggleSneak(button);
    }
    public static boolean handleToggleSprint(@NotNull Minecraft client, @NotNull ButtonBinding button, float value, @NotNull ButtonState action) {
        return ToggleSneakSprintUtil.toggleSprint(button);
    }

    public static PressAction handleInventorySlotPad(int direction) {
        return (client, binding, value, action) -> {
            if (!(client.screen instanceof AbstractContainerScreen<?> inventory && action != ButtonState.RELEASE))
                return false;

            var accessor = (HandledScreenAccessor) inventory;

            Optional<Slot> closestSlot = InventoryUtil.findClosestSlot(inventory, direction);

            if (closestSlot.isPresent()) {
                var slot = closestSlot.get();
                int x = accessor.getX() + slot.x + 8;
                int y = accessor.getY() + slot.y + 8;
                InputManager.queueMousePosition(x * (double) client.getWindow().getScreenWidth() / (double) client.getWindow().getGuiScaledWidth(),
                        y * (double) client.getWindow().getScreenHeight() / (double) client.getWindow().getGuiScaledHeight());
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
        return (client.screen == null && MidnightControlsClient.input.screenCloseCooldown <= 0) || client.screen instanceof TouchscreenOverlay || client.screen instanceof RingScreen;
    }

    /**
     * Returns whether the client is in a non-interactive screen (which means require mouse input) or not.
     *
     * @param binding the affected binding
     * @return true if the client is in a non-interactive screen, else false
     */
    public static boolean inNonInteractiveScreens(@NotNull ButtonBinding binding) {
        if (client.screen == null)
            return false;
        return !MidnightInput.isScreenInteractive(client.screen);
    }

    /**
     * Returns whether the client is in an inventory or not.
     *
     * @param binding the affected binding
     * @return true if the client is in an inventory, else false
     */
    public static boolean inInventory(@NotNull ButtonBinding binding) {
        return client.screen instanceof AbstractContainerScreen;
    }

    /**
     * Returns whether the client is in the advancements screen or not.
     *
     * @param binding the affected binding
     * @return true if the client is in the advancements screen, else false
     */
    public static boolean inAdvancements(@NotNull ButtonBinding binding) {
        return client.screen instanceof AdvancementsScreen;
    }
}
