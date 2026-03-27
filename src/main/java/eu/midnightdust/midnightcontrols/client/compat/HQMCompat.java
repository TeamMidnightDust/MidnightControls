/*
 * Copyright © 2021 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of midnightcontrols.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package eu.midnightdust.midnightcontrols.client.compat;

import java.util.Optional;
import net.minecraft.client.gui.screens.Screen;

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

    private Optional<Class<?>> getClassByName(String c) {
        try {
            return Optional.of(Class.forName(c));
        } catch (ClassNotFoundException e) {
            return Optional.empty();
        }
    }

    @Override
    public void handle() {
        this.guiBaseClass = getClassByName(GUI_BASE_CLASS_PATH);
    }

    @Override
    public boolean requireMouseOnScreen(Screen screen) {
        return this.guiBaseClass.map(clazz -> clazz.isInstance(screen)).orElse(false);
    }
}
