/*
 * Copyright © 2022 Motschen <motschen@midnightdust.eu>
 *
 * This file is part of MidnightControls.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package eu.midnightdust.midnightcontrols.client.compat;

import eu.midnightdust.midnightcontrols.client.MidnightControlsClient;
import eu.midnightdust.midnightcontrols.client.MidnightControlsConfig;
import eu.midnightdust.midnightcontrols.client.controller.ButtonBinding;
import eu.midnightdust.midnightcontrols.client.controller.ButtonCategory;
import eu.midnightdust.midnightcontrols.client.controller.InputManager;
import eu.midnightdust.midnightcontrols.client.mixin.KeyBindingIDAccessor;
import net.minecraft.client.option.KeyBinding;
import org.aperlambda.lambdacommon.Identifier;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

/**
 * Represents a compatibility handler for VoxelMap.
 *
 * @author Motschen
 * @version 1.8.0
 * @since 1.8.0
 */
public class VoxelMapCompat implements CompatHandler {

    private final KeyBinding voxelMapZoomKey = KeyBindingIDAccessor.getKEYS_BY_ID().getOrDefault("key.minimap.zoom", null);
    private final KeyBinding voxelmapFullscreenKey = KeyBindingIDAccessor.getKEYS_BY_ID().getOrDefault("key.minimap.togglefullscreen", null);
    private final KeyBinding voxelmapMenuKey = KeyBindingIDAccessor.getKEYS_BY_ID().getOrDefault("key.minimap.voxelmapmenu", null);;
    private final KeyBinding voxelmapWaypointMenuKey = KeyBindingIDAccessor.getKEYS_BY_ID().getOrDefault("key.minimap.waypointmenu", null);;
    private final KeyBinding voxelmapWaypointKey = KeyBindingIDAccessor.getKEYS_BY_ID().getOrDefault("key.minimap.waypointhotkey", null);
    private final KeyBinding voxelmapMobToggleKey = KeyBindingIDAccessor.getKEYS_BY_ID().getOrDefault("key.minimap.togglemobs", null);
    private final KeyBinding voxelmapWaypointToggleKey = KeyBindingIDAccessor.getKEYS_BY_ID().getOrDefault("key.minimap.toggleingamewaypoints", null);
    private static final ButtonCategory VOXELMAP_CATEGORY = InputManager.registerCategory(new Identifier("minecraft","controls.minimap.title"));


    @Override
    public void handle(@NotNull MidnightControlsClient mod) {
        if (MidnightControlsConfig.debug && KeyBindingIDAccessor.getKEYS_BY_ID() != null) KeyBindingIDAccessor.getKEYS_BY_ID().forEach((a, b) -> System.out.println(a + " - " + b));
        new ButtonBinding.Builder("key.minimap.zoom")
                .buttons(GLFW.GLFW_GAMEPAD_BUTTON_DPAD_DOWN, GLFW.GLFW_GAMEPAD_BUTTON_X)
                .onlyInGame()
                .cooldown(true)
                .category(VOXELMAP_CATEGORY)
                .linkKeybind(voxelMapZoomKey)
                .register();
        new ButtonBinding.Builder("key.minimap.togglefullscreen")
                .buttons(GLFW.GLFW_GAMEPAD_BUTTON_DPAD_DOWN, GLFW.GLFW_GAMEPAD_BUTTON_Y)
                .onlyInGame()
                .cooldown(true)
                .category(VOXELMAP_CATEGORY)
                .linkKeybind(voxelmapFullscreenKey)
                .register();
        new ButtonBinding.Builder("key.minimap.voxelmapmenu")
                .buttons(GLFW.GLFW_GAMEPAD_BUTTON_DPAD_DOWN, GLFW.GLFW_GAMEPAD_BUTTON_START)
                .onlyInGame()
                .cooldown(true)
                .category(VOXELMAP_CATEGORY)
                .linkKeybind(voxelmapMenuKey)
                .register();
        new ButtonBinding.Builder("key.minimap.waypointmenu")
                .buttons(GLFW.GLFW_GAMEPAD_BUTTON_DPAD_DOWN, GLFW.GLFW_GAMEPAD_BUTTON_GUIDE)
                .onlyInGame()
                .cooldown(true)
                .category(VOXELMAP_CATEGORY)
                .linkKeybind(voxelmapWaypointMenuKey)
                .register();
        new ButtonBinding.Builder("key.minimap.waypointhotkey")
                .buttons(GLFW.GLFW_GAMEPAD_BUTTON_DPAD_DOWN, GLFW.GLFW_GAMEPAD_BUTTON_BACK)
                .onlyInGame()
                .cooldown(true)
                .category(VOXELMAP_CATEGORY)
                .linkKeybind(voxelmapWaypointKey)
                .register();
        new ButtonBinding.Builder("key.minimap.togglemobs")
                .buttons(GLFW.GLFW_GAMEPAD_BUTTON_DPAD_DOWN, GLFW.GLFW_GAMEPAD_BUTTON_A)
                .onlyInGame()
                .cooldown(true)
                .category(VOXELMAP_CATEGORY)
                .linkKeybind(voxelmapMobToggleKey)
                .register();
        new ButtonBinding.Builder("key.minimap.toggleingamewaypoints")
                .buttons(GLFW.GLFW_GAMEPAD_BUTTON_DPAD_DOWN, GLFW.GLFW_GAMEPAD_BUTTON_B)
                .onlyInGame()
                .cooldown(true)
                .category(VOXELMAP_CATEGORY)
                .linkKeybind(voxelmapWaypointToggleKey)
                .register();
    }
}
