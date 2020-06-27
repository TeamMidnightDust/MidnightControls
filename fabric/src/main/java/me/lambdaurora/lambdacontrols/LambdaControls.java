/*
 * Copyright © 2020 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of LambdaControls.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package me.lambdaurora.lambdacontrols;

import io.netty.buffer.Unpooled;
import me.lambdaurora.lambdacontrols.event.PlayerChangeControlsModeCallback;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Optional;

/**
 * Represents the LambdaControls mod.
 *
 * @author LambdAurora
 * @version 1.3.0
 * @since 1.0.0
 */
public class LambdaControls implements ModInitializer
{
    private static      LambdaControls INSTANCE;
    public static final Identifier     CONTROLS_MODE_CHANNEL = new Identifier(LambdaControlsConstants.CONTROLS_MODE_CHANNEL.toString());
    public static final Identifier     FEATURE_CHANNEL       = new Identifier(LambdaControlsConstants.FEATURE_CHANNEL.toString());
    public static final Identifier     HELLO_CHANNEL         = new Identifier(LambdaControlsConstants.HELLO_CHANNEL.toString());

    public static final TranslatableText NOT_BOUND_TEXT = new TranslatableText("lambdacontrols.not_bound");

    public final Logger logger = LogManager.getLogger("LambdaControls");

    @Override
    public void onInitialize()
    {
        INSTANCE = this;
        this.log("Initializing LambdaControls...");

        ServerSidePacketRegistry.INSTANCE.register(HELLO_CHANNEL,
                (context, attachedData) -> {
                    String version = attachedData.readString(16);
                    ControlsMode.byId(attachedData.readString(32))
                            .ifPresent(controlsMode -> context.getTaskQueue()
                                    .execute(() -> PlayerChangeControlsModeCallback.EVENT.invoker().apply(context.getPlayer(), controlsMode)));
                    context.getTaskQueue().execute(() ->
                            ServerSidePacketRegistry.INSTANCE.sendToPlayer(context.getPlayer(), FEATURE_CHANNEL, this.makeFeatureBuffer(LambdaControlsFeature.FRONT_BLOCK_PLACING)));
                });
        ServerSidePacketRegistry.INSTANCE.register(CONTROLS_MODE_CHANNEL,
                (context, attachedData) -> ControlsMode.byId(attachedData.readString(32))
                        .ifPresent(controlsMode -> context.getTaskQueue()
                                .execute(() -> PlayerChangeControlsModeCallback.EVENT.invoker().apply(context.getPlayer(), controlsMode))));
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
     * Returns a packet byte buffer made for the lambdacontrols:controls_mode plugin message.
     *
     * @param controlsMode The controls mode to send.
     * @return The packet byte buffer.
     */
    public PacketByteBuf makeControlsModeBuffer(@NotNull ControlsMode controlsMode)
    {
        Objects.requireNonNull(controlsMode, "Controls mode cannot be null.");
        return new PacketByteBuf(Unpooled.buffer()).writeString(controlsMode.getName(), 32);
    }

    /**
     * Returns a packet byte buffer made for the lambdacontrols:feature plugin message.
     *
     * @param feature The feature data to send.
     * @return The packet byte buffer.
     */
    public PacketByteBuf makeFeatureBuffer(@NotNull LambdaControlsFeature feature)
    {
        Objects.requireNonNull(feature, "Feature cannot be null.");
        PacketByteBuf buffer = new PacketByteBuf(Unpooled.buffer()).writeString(feature.getName(), 64);
        buffer.writeBoolean(feature.isAllowed());
        return buffer;
    }

    public PacketByteBuf makeHello(@NotNull ControlsMode controlsMode)
    {
        String version = "";
        Optional<ModContainer> container;
        if ((container = FabricLoader.getInstance().getModContainer(LambdaControlsConstants.NAMESPACE)).isPresent()) {
            version = container.get().getMetadata().getVersion().getFriendlyString();
        }
        return new PacketByteBuf(Unpooled.buffer()).writeString(version, 16).writeString(controlsMode.getName(), 32);
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
