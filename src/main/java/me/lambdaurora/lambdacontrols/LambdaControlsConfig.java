/*
 * Copyright © 2019 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of LambdaControls.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package me.lambdaurora.lambdacontrols;

import com.electronwill.nightconfig.core.file.FileConfig;
import org.jetbrains.annotations.NotNull;

/**
 * Represents LambdaControls configuration.
 */
public class LambdaControlsConfig
{
    private final FileConfig     config = FileConfig.builder("config/lambdacontrols.toml").concurrent().defaultResource("/config.toml").build();
    private final LambdaControls mod;

    public LambdaControlsConfig(@NotNull LambdaControls mod)
    {
        this.mod = mod;
    }

    public void load()
    {
        this.config.load();
        this.mod.log("Configuration loaded.");
    }

    public void save()
    {
        this.config.save();
        this.mod.log("Configuration saved.");
    }
}
