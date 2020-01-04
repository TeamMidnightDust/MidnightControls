/*
 * Copyright © 2020 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of LambdaControls.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package me.lambdaurora.lambdacontrols;

import me.lambdaurora.lambdacontrols.event.PlayerChangeControlsModeCallback;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Represents the LambdaControls mod.
 *
 * @author LambdAurora
 * @version 1.1.0
 * @since 1.0.0
 */
public class LambdaControls implements ModInitializer
{
    private static      LambdaControls INSTANCE;
    public static final Identifier     CONTROLS_MODE_CHANNEL = new Identifier(LambdaControlsConstants.NAMESPACE, "controls_mode");

    public final Logger logger = LogManager.getLogger("LambdaControls");

    @Override
    public void onInitialize()
    {
        INSTANCE = this;
        this.log("Initializing LambdaControls...");

        ServerSidePacketRegistry.INSTANCE.register(CONTROLS_MODE_CHANNEL,
                (context, attached_data) -> ControlsMode.by_id(attached_data.readString(32))
                        .ifPresent(controls_mode -> context.getTaskQueue()
                                .execute(() -> PlayerChangeControlsModeCallback.EVENT.invoker().apply(context.getPlayer(), controls_mode))));
    }

    /**
     * Prints a message to the terminal.
     *
     * @param info The message to print.
     */
    public void log(String info)
    {
        this.logger.info("[LambdaControls] " + info);
    }

    /**
     * Prints a warning to the terminal.
     *
     * @param warning The warning to print.
     */
    public void warn(String warning)
    {
        this.logger.info("[LambdaControls] " + warning);
    }

    /**
     * Gets the LambdaControls instance.
     *
     * @return The LambdaControls instance.
     */
    public static LambdaControls get()
    {
        return INSTANCE;
    }
}
