/*
 * Copyright © 2021-2022 Karen/あけみ <karen@akemi.ai>, LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of MidnightControls.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package eu.midnightdust.midnightcontrols.client.compat;

import eu.midnightdust.midnightcontrols.MidnightControls;
import eu.midnightdust.midnightcontrols.client.MidnightControlsClient;
import eu.midnightdust.midnightcontrols.client.controller.ButtonBinding;
import net.minecraft.client.option.KeyBinding;
import org.jetbrains.annotations.NotNull;
import org.aperlambda.lambdacommon.utils.LambdaReflection;
import org.lwjgl.glfw.GLFW;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Represents a compatibility handler for Ok Zoomer.
 *
 * @author Karen/あけみ, LambdAurora
 * @version 1.4.3
 * @since 1.1.0
 */
public class OkZoomerCompat implements CompatHandler {
    private boolean didAllReflectionCallsSucceed = false;

    private KeyBinding okZoomerZoomKey;
    private KeyBinding okZoomerIncreaseZoomKey;
    private KeyBinding okZoomerDecreaseZoomKey;
    private KeyBinding okZoomerResetZoomKey;

    private Method okZoomerAreExtraKeyBindsEnabledMethod;

    public OkZoomerCompat() {
        // These strings represent the names of the classes, fields, and methods we use from the Ok Zoomer API
        String okZoomerZoomKeybindsClassString;

        String okZoomerZoomKeyFieldString;
        String okZoomerIncreaseZoomKeyFieldString;
        String okZoomerDecreaseZoomKeyFieldString;
        String okZoomerResetZoomKeyFieldString;

        String okZoomerAreExtraKeyBindsEnabledMethodNameString;

        // These variables represent the actual objects that we reflect to
        Class<?> okZoomerZoomKeybindsClass;

        Field okZoomerZoomKeyField;
        Field okZoomerIncreaseZoomKeyField;
        Field okZoomerDecreaseZoomKeyField;
        Field okZoomerResetZoomKeyField;

        // First, we need to determine which version of the Ok Zoomer API we're dealing with here.
        if (LambdaReflection.doesClassExist("io.github.ennuil.okzoomer.keybinds.ZoomKeybinds")) {
            // https://github.com/EnnuiL/OkZoomer/blob/5.0.0-beta.3+1.17.1/src/main/java/io/github/ennuil/okzoomer/keybinds/ZoomKeybinds.java
            MidnightControls.log("Ok Zoomer version 5.0.0-beta.3 or below detected!");

            okZoomerZoomKeybindsClassString = "io.github.ennuil.okzoomer.keybinds.ZoomKeybinds";

            okZoomerZoomKeyFieldString = "zoomKey";
            okZoomerIncreaseZoomKeyFieldString = "increaseZoomKey";
            okZoomerDecreaseZoomKeyFieldString = "decreaseZoomKey";
            okZoomerResetZoomKeyFieldString = "resetZoomKey";

            okZoomerAreExtraKeyBindsEnabledMethodNameString = "areExtraKeybindsEnabled";
        } else if (LambdaReflection.doesClassExist("io.github.ennuil.okzoomer.key_binds.ZoomKeyBinds")) {
            // https://github.com/EnnuiL/OkZoomer/blob/5.0.0-beta.6+1.18.2/src/main/java/io/github/ennuil/okzoomer/key_binds/ZoomKeyBinds.java
            MidnightControls.log("Ok Zoomer version 5.0.0-beta.6, 5.0.0-beta.5, or 5.0.0-beta.4 detected!");

            okZoomerZoomKeybindsClassString = "io.github.ennuil.okzoomer.key_binds.ZoomKeyBinds";

            okZoomerZoomKeyFieldString = "ZOOM_KEY";
            okZoomerIncreaseZoomKeyFieldString = "INCREASE_ZOOM_KEY";
            okZoomerDecreaseZoomKeyFieldString = "DECREASE_ZOOM_KEY";
            okZoomerResetZoomKeyFieldString = "RESET_ZOOM_KEY";

            okZoomerAreExtraKeyBindsEnabledMethodNameString = "areExtraKeyBindsEnabled";
        } else if (LambdaReflection.doesClassExist("io.github.ennuil.ok_zoomer.key_binds.ZoomKeyBinds")) {
            // https://github.com/EnnuiL/OkZoomer/blob/5.0.0-beta.7+1.18.2/src/main/java/io/github/ennuil/ok_zoomer/key_binds/ZoomKeyBinds.java
            MidnightControls.log("Ok Zoomer version 5.0.0-beta.7 (Quilt) or above detected!");

            okZoomerZoomKeybindsClassString = "io.github.ennuil.ok_zoomer.key_binds.ZoomKeyBinds";

            okZoomerZoomKeyFieldString = "ZOOM_KEY";
            okZoomerIncreaseZoomKeyFieldString = "INCREASE_ZOOM_KEY";
            okZoomerDecreaseZoomKeyFieldString = "DECREASE_ZOOM_KEY";
            okZoomerResetZoomKeyFieldString = "RESET_ZOOM_KEY";

            okZoomerAreExtraKeyBindsEnabledMethodNameString = "areExtraKeyBindsEnabled";
        } else {
            // If all of the above checks fail, then the version of the Ok Zoomer API that the user is trying to use is too new.
            MidnightControls.warn("The version of Ok Zoomer that you are currently using is too new, and is not yet supported by MidnightControls!");
            return;
        }

        // Reflect to the ZoomKeyBinds (>= 5.0.0-beta.4) / ZoomKeybinds (<= 5.0.0-beta.3) class.
        try {
            okZoomerZoomKeybindsClass = Class.forName(okZoomerZoomKeybindsClassString);
        } catch (ClassNotFoundException exception) {
            // This theoretically should never happen.
            MidnightControls.warn("MidnightControls failed to reflect to the Ok Zoomer keybinds class!");
            exception.printStackTrace();
            return;
        }

        // Reflect to all of the keybind fields.
        try {
            okZoomerZoomKeyField = okZoomerZoomKeybindsClass.getField(okZoomerZoomKeyFieldString);
            okZoomerIncreaseZoomKeyField = okZoomerZoomKeybindsClass.getField(okZoomerIncreaseZoomKeyFieldString);
            okZoomerDecreaseZoomKeyField = okZoomerZoomKeybindsClass.getField(okZoomerDecreaseZoomKeyFieldString);
            okZoomerResetZoomKeyField = okZoomerZoomKeybindsClass.getField(okZoomerResetZoomKeyFieldString);
        } catch (NoSuchFieldException exception) {
            MidnightControls.warn("MidnightControls failed to reflect to the Ok Zoomer keybind fields!");
            exception.printStackTrace();
            return;
        }

        // Initialise KeyBinding objects
        try {
            okZoomerZoomKey = (KeyBinding) okZoomerZoomKeyField.get(null);
            okZoomerIncreaseZoomKey = (KeyBinding) okZoomerIncreaseZoomKeyField.get(null);
            okZoomerDecreaseZoomKey = (KeyBinding) okZoomerDecreaseZoomKeyField.get(null);
            okZoomerResetZoomKey = (KeyBinding) okZoomerResetZoomKeyField.get(null);
        } catch (IllegalAccessException exception) {
            MidnightControls.warn("MidnightControls failed to reflect to the Ok Zoomer keybind objects!");
            exception.printStackTrace();
            return;
        }

        // Reflect to the areExtraKeyBindsEnabled (>= 5.0.0-beta.4) / areExtraKeybindsEnabled (<= 5.0.0-beta.3) method.
        // TODO: Consider replacing this entirely with getExtraKeyBind (>= 5.0.0-beta.4) / getExtraKeybind (<= 5.0.0-beta.3) in the future.
        try {
            okZoomerAreExtraKeyBindsEnabledMethod = okZoomerZoomKeybindsClass.getDeclaredMethod(okZoomerAreExtraKeyBindsEnabledMethodNameString);
        } catch (NoSuchMethodException exception) {
            MidnightControls.warn("MidnightControls failed to reflect to an Ok Zoomer method (areExtraKeyBindsEnabled / areExtraKeybindsEnabled)!");
            exception.printStackTrace();
            return;
        }

        didAllReflectionCallsSucceed = true;
    }

    @Override
    public void handle() {
        if (didAllReflectionCallsSucceed) {
            new ButtonBinding.Builder("zoom")
                    .buttons(GLFW.GLFW_GAMEPAD_BUTTON_DPAD_UP, GLFW.GLFW_GAMEPAD_BUTTON_X)
                    .onlyInGame()
                    .cooldown(true)
                    .category(ButtonBinding.MISC_CATEGORY)
                    .linkKeybind(okZoomerZoomKey)
                    .register();

            boolean okZoomerAreExtraKeyBindsEnabled = false;
            try {
                okZoomerAreExtraKeyBindsEnabled = (boolean) okZoomerAreExtraKeyBindsEnabledMethod.invoke(null);
            } catch (IllegalAccessException exception) {
                MidnightControls.warn("MidnightControls encountered an IllegalAccessException while attempting to invoke a reflected Ok Zoomer method (areExtraKeyBindsEnabled / areExtraKeybindsEnabled)!");
                exception.printStackTrace();
            } catch (InvocationTargetException exception) {
                MidnightControls.warn("MidnightControls encountered an InvocationTargetException while attempting to invoke a reflected Ok Zoomer method (areExtraKeyBindsEnabled / areExtraKeybindsEnabled)!");
                exception.printStackTrace();
            }

            if (okZoomerAreExtraKeyBindsEnabled) {
                new ButtonBinding.Builder("zoom_in")
                        .buttons(GLFW.GLFW_GAMEPAD_BUTTON_DPAD_UP, ButtonBinding.axisAsButton(GLFW.GLFW_GAMEPAD_AXIS_RIGHT_TRIGGER, true))
                        .onlyInGame()
                        .cooldown(true)
                        .category(ButtonBinding.MISC_CATEGORY)
                        .linkKeybind(okZoomerIncreaseZoomKey)
                        .register();
                new ButtonBinding.Builder("zoom_out")
                        .buttons(GLFW.GLFW_GAMEPAD_BUTTON_DPAD_UP, ButtonBinding.axisAsButton(GLFW.GLFW_GAMEPAD_AXIS_LEFT_TRIGGER, true))
                        .onlyInGame()
                        .cooldown(true)
                        .category(ButtonBinding.MISC_CATEGORY)
                        .linkKeybind(okZoomerDecreaseZoomKey)
                        .register();
                new ButtonBinding.Builder("zoom_reset")
                        .onlyInGame()
                        .cooldown(true)
                        .category(ButtonBinding.MISC_CATEGORY)
                        .linkKeybind(okZoomerResetZoomKey)
                        .register();
            }
        }
    }
}
