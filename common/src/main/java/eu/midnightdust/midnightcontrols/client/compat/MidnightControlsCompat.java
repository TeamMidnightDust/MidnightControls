/*
 * Copyright © 2021 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of midnightcontrols.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package eu.midnightdust.midnightcontrols.client.compat;

import eu.midnightdust.lib.util.PlatformFunctions;
import eu.midnightdust.midnightcontrols.client.controller.InputManager;
import eu.midnightdust.midnightcontrols.client.util.storage.AxisStorage;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.hit.BlockHitResult;
import org.aperlambda.lambdacommon.utils.LambdaReflection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static eu.midnightdust.midnightcontrols.MidnightControls.log;
import static eu.midnightdust.midnightcontrols.client.MidnightControlsClient.client;

/**
 * Represents a compatibility handler.
 *
 * @author LambdAurora, Motschen
 * @version 1.10.0
 * @since 1.1.0
 */
public class MidnightControlsCompat {
    @Deprecated // INTERNAL -> PLEASE USE streamCompatHandlers() INSTEAD
    public static final List<CompatHandler> HANDLERS = new ArrayList<>();

    /**
     * Initializes compatibility with other mods if needed.
     */
    public static void init() {
        if (PlatformFunctions.isModLoaded("emi")) {
            log("Adding EMI compatibility...");
            registerCompatHandler(new EMICompat());
        }
        if (PlatformFunctions.isModLoaded("hardcorequesting") && LambdaReflection.doesClassExist(HQMCompat.GUI_BASE_CLASS_PATH)) {
            log("Adding HQM compatibility...");
            registerCompatHandler(new HQMCompat());
        }
        if (PlatformFunctions.isModLoaded("bedrockify")) {
            log("Adding Bedrockify compatibility...");
            registerCompatHandler(new BedrockifyCompat());
        }
        if (PlatformFunctions.isModLoaded("yet-another-config-lib")) {
            log("Adding YACL compatibility...");
            registerCompatHandler(new YACLCompat());
        }
        if (PlatformFunctions.isModLoaded("sodium")) {
            log("Adding Sodium compatibility...");
            registerCompatHandler(new SodiumCompat());
        }
        if (PlatformFunctions.isModLoaded("inventorytabs")) {
            log("Adding Inventory Tabs compatibility...");
            registerCompatHandler(new InventoryTabsCompat());
        }
        HANDLERS.forEach(CompatHandler::handle);
        InputManager.loadButtonBindings();
    }

    /**
     * Registers a new compatibility handler.
     *
     * @param handler the compatibility handler to register
     */
    public static void registerCompatHandler(@NotNull CompatHandler handler) {
        HANDLERS.add(handler);
    }

    /**
     * Streams through compatibility handlers.
     *
     * @return a stream of compatibility handlers
     */
    public static Stream<CompatHandler> streamCompatHandlers() {
        return HANDLERS.stream();
    }

    /**
     * Returns whether the mouse is required on the specified screen.
     *
     * @param screen the screen
     * @return true if the mouse is requried on the specified screen, else false
     */
    public static boolean requireMouseOnScreen(Screen screen) {
        return streamCompatHandlers().anyMatch(handler -> handler.requireMouseOnScreen(screen));
    }
    /**
     * Handles custom tabs for modded screens
     *
     * @param screen the screen
     * @return true if the handle was fired and succeed, else false
     */
    public static boolean handleTabs(Screen screen, boolean forward) {
        return streamCompatHandlers().anyMatch(handler -> handler.handleTabs(screen, forward));
    }
    /**
     * Handles custom pages for modded screens
     *
     * @param screen the screen
     * @return true if the handle was fired and succeed, else false
     */
    public static boolean handlePages(Screen screen, boolean forward) {
        return streamCompatHandlers().anyMatch(handler -> handler.handlePages(screen, forward));
    }

    /**
     * Returns a slot at the specified location if possible.
     *
     * @param screen the screen
     * @param mouseX the mouse X-coordinate
     * @param mouseY the mouse Y-coordinate
     * @return a slot if present, else null
     */
    public static @Nullable CompatHandler.SlotPos getSlotAt(@NotNull Screen screen, int mouseX, int mouseY) {
        for (var handler : HANDLERS) {
            var slot = handler.getSlotAt(screen, mouseX, mouseY);
            if (slot != null)
                return slot;
        }
        return null;
    }

    /**
     * Returns a custom translation key to make custom attack action strings on the HUD.
     *
     * @param client the client instance
     * @param placeResult the last place block result
     * @return null if untouched, else a translation key
     */
    public static String getAttackActionAt(@NotNull MinecraftClient client, @Nullable BlockHitResult placeResult) {
        for (CompatHandler handler : HANDLERS) {
            String action = handler.getAttackActionAt(client, placeResult);
            if (action != null) {
                return action;
            }
        }
        return null;
    }

    /**
     * Returns a custom translation key to make custom use action strings on the HUD.
     *
     * @param client the client instance
     * @param placeResult the last place block result
     * @return null if untouched, else a translation key
     */
    public static String getUseActionAt(@NotNull MinecraftClient client, @Nullable BlockHitResult placeResult) {
        for (CompatHandler handler : HANDLERS) {
            String action = handler.getUseActionAt(client, placeResult);
            if (action != null) {
                return action;
            }
        }
        return null;
    }

    /**
     * Handles the menu back button.
     *
     * @param client the client instance
     * @param screen the screen
     * @return true if the handle was fired and succeed, else false
     */
    public static boolean handleMenuBack(@NotNull MinecraftClient client, @NotNull Screen screen) {
        for (CompatHandler handler : HANDLERS) {
            if (handler.handleMenuBack(client, screen))
                return true;
        }
        return false;
    }
    /**
     * Handles the camera movement.
     *
     * @param targetYaw the target yaw
     * @param targetPitch the target pitch
     */
    public static void handleCamera(double targetYaw, double targetPitch) {
        MidnightControlsCompat.HANDLERS.forEach(handler -> handler.handleCamera(client, targetYaw, targetPitch));
    }
    /**
     * Handles movement for players as well as vehicles
     *
     * @param storage       the storage containing info about the current axis
     * @param adjustedValue the value of the axis, adjusted for max values and non-analogue movement, recommended for player movement
     */
    public static void handleMovement(AxisStorage storage, float adjustedValue) {
        streamCompatHandlers().forEach(handler -> handler.handleMovement(client, storage, adjustedValue));
    }
}
