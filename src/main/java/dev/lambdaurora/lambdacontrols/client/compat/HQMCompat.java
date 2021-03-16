/*
 * Copyright © 2020 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of LambdaControls.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package dev.lambdaurora.lambdacontrols.client.compat;

import dev.lambdaurora.lambdacontrols.client.LambdaControlsClient;
import net.minecraft.client.gui.screen.Screen;
import org.aperlambda.lambdacommon.utils.LambdaReflection;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Represents HQM compatibility handler.
 * <p>
 * This is bad.
 *
 * @author LambdAurora
 * @version 1.3.2
 * @since 1.3.2
 */
public class HQMCompat implements CompatHandler {
    public static final String GUI_BASE_CLASS_PATH = "hardcorequesting.client.interfaces.GuiBase";
    private Optional<Class<?>> guiBaseClass;

    @Override
    public void handle(@NotNull LambdaControlsClient mod) {
        this.guiBaseClass = LambdaReflection.getClass(GUI_BASE_CLASS_PATH);
    }

    @Override
    public boolean requireMouseOnScreen(Screen screen) {
        return this.guiBaseClass.map(clazz -> clazz.isInstance(screen)).orElse(false);
    }
}
