/*
 * Copyright © 2021 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of midnightcontrols.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package eu.midnightdust.midnightcontrols.client;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import eu.midnightdust.midnightcontrols.client.gui.MidnightControlsSettingsScreen;

/**
 * Represents the API implementation of ModMenu for midnightcontrols.
 *
 * @author LambdAurora
 * @version 1.7.0
 * @since 1.1.0
 */
public class MidnightControlsModMenu implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> new MidnightControlsSettingsScreen(parent, false);
    }
}
