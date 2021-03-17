/*
 * Copyright © 2021 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of LambdaControls.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package dev.lambdaurora.lambdacontrols.client.compat;

import dev.lambdaurora.lambdacontrols.client.LambdaControlsClient;
import dev.lambdaurora.lambdacontrols.client.controller.InputManager;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.hit.BlockHitResult;
import org.aperlambda.lambdacommon.utils.LambdaReflection;
import org.aperlambda.lambdacommon.utils.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * Represents a compatibility handler.
 *
 * @author LambdAurora
 * @version 1.5.0
 * @since 1.1.0
 */
public class LambdaControlsCompat {
    private static final List<CompatHandler> HANDLERS = new ArrayList<>();

    /**
     * Initializes compatibility with other mods if needed.
     *
     * @param mod The mod instance.
     */
    public static void init(@NotNull LambdaControlsClient mod) {
        if (FabricLoader.getInstance().isModLoaded("okzoomer")) {
            mod.log("Adding okzoomer compatibility...");
            HANDLERS.add(new OkZoomerCompat());
        }
        if (isReiPresent()) {
            mod.log("Adding REI compatiblity...");
            HANDLERS.add(new ReiCompat());
        }
        if (FabricLoader.getInstance().isModLoaded("hardcorequesting") && LambdaReflection.doesClassExist(HQMCompat.GUI_BASE_CLASS_PATH)) {
            mod.log("Adding HQM compatibility...");
            HANDLERS.add(new HQMCompat());
        }
        HANDLERS.forEach(handler -> handler.handle(mod));
        InputManager.loadButtonBindings(mod.config);
    }

    /**
     * Registers a new compatibility handler.
     *
     * @param handler The compatibility handler to register.
     */
    public static void registerCompatHandler(@NotNull CompatHandler handler) {
        HANDLERS.add(handler);
    }

    /**
     * Streams through compatibility handlers.
     *
     * @return A stream of compatibility handlers.
     */
    public static Stream<CompatHandler> streamCompatHandlers() {
        return HANDLERS.stream();
    }

    /**
     * Returns whether the mouse is required on the specified screen.
     *
     * @param screen The screen.
     * @return True if the mouse is requried on the specified screen, else false.
     */
    public static boolean requireMouseOnScreen(Screen screen) {
        return HANDLERS.stream().anyMatch(handler -> handler.requireMouseOnScreen(screen));
    }

    /**
     * Returns a slot at the specified location if possible.
     *
     * @param screen The screen.
     * @param mouseX The mouse X-coordinate.
     * @param mouseY The mouse Y-coordinate.
     * @return A slot if present, else null.
     */
    public static @Nullable Pair<Integer, Integer> getSlotAt(@NotNull Screen screen, int mouseX, int mouseY) {
        for (CompatHandler handler : HANDLERS) {
            Pair<Integer, Integer> slot = handler.getSlotAt(screen, mouseX, mouseY);
            if (slot != null)
                return slot;
        }
        return null;
    }

    /**
     * Returns a custom translation key to make custom attack action strings on the HUD.
     *
     * @param client The client instance.
     * @param placeResult The last place block result.
     * @return Null if untouched, else a translation key.
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
     * @param client The client instance.
     * @param placeResult The last place block result.
     * @return Null if untouched, else a translation key.
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
     * @param client The client instance.
     * @param screen The screen.
     * @return True if the handle was fired and succeed, else false.
     */
    public static boolean handleMenuBack(@NotNull MinecraftClient client, @NotNull Screen screen) {
        for (CompatHandler handler : HANDLERS) {
            if (handler.handleMenuBack(client, screen))
                return true;
        }
        return false;
    }

    /**
     * Returns whether Roughly Enough Items is present.
     *
     * @return True if Roughly Enough Items is present, else false.
     */
    public static boolean isReiPresent() {
        return FabricLoader.getInstance().isModLoaded("roughlyenoughitems");
    }
}
