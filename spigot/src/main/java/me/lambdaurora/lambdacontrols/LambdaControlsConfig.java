/*
 * Copyright © 2020 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of LambdaControls.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package me.lambdaurora.lambdacontrols;

import com.electronwill.nightconfig.core.file.FileConfig;
import org.jetbrains.annotations.NotNull;

import java.io.File;

/**
 * Represents the LambdaControls Spigot configuration.
 */
public class LambdaControlsConfig
{
    private static final boolean DEFAULT_FRONT_BLOCK_PLACING = true;

    protected final FileConfig           config = FileConfig.builder("config/lambdacontrols.toml").concurrent().defaultResource("/server_config.toml").build();
    private final   LambdaControlsSpigot plugin;

    public LambdaControlsConfig(@NotNull LambdaControlsSpigot plugin)
    {
        this.plugin = plugin;
    }

    public void load()
    {
        File configDir = new File("config/");
        if (!configDir.exists())
            configDir.mkdirs();
        this.config.load();
        this.plugin.log("Configuration loaded.");
        LambdaControlsFeature.FRONT_BLOCK_PLACING.setAllowed(this.config.getOrElse("gameplay.front_block_placing", DEFAULT_FRONT_BLOCK_PLACING));
    }
}
