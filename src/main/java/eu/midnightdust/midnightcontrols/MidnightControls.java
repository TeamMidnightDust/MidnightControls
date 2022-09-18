/*
 * Copyright © 2021 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of midnightcontrols.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package eu.midnightdust.midnightcontrols;

import eu.midnightdust.midnightcontrols.client.MidnightControlsConfig;
import eu.midnightdust.midnightcontrols.event.PlayerChangeControlsModeCallback;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Optional;

/**
 * Represents the MidnightControls mod.
 *
 * @author LambdAurora & Motschen
 * @version 1.8.0
 * @since 1.0.0
 */
public class MidnightControls implements ModInitializer {
    private static MidnightControls INSTANCE;
    public static final Identifier CONTROLS_MODE_CHANNEL = new Identifier(MidnightControlsConstants.CONTROLS_MODE_CHANNEL.toString());
    public static final Identifier FEATURE_CHANNEL = new Identifier(MidnightControlsConstants.FEATURE_CHANNEL.toString());
    public static final Identifier HELLO_CHANNEL = new Identifier(MidnightControlsConstants.HELLO_CHANNEL.toString());
    public static boolean isExtrasLoaded;

    public final Logger logger = LogManager.getLogger("MidnightControls");

    @Override
    public void onInitialize() {
        INSTANCE = this;
        isExtrasLoaded = FabricLoader.getInstance().isModLoaded("midnightcontrols-extra");
        this.log("Initializing MidnightControls...");

        ServerPlayNetworking.registerGlobalReceiver(HELLO_CHANNEL, (server, player, handler, buf, responseSender) -> {
            String version = buf.readString(32);
            ControlsMode.byId(buf.readString(32))
                    .ifPresent(controlsMode -> server
                            .execute(() -> PlayerChangeControlsModeCallback.EVENT.invoker().apply(player, controlsMode)));
            server.execute(() -> {
                ServerPlayNetworking.send(player, FEATURE_CHANNEL, this.makeFeatureBuffer(MidnightControlsFeature.HORIZONTAL_REACHAROUND));
            });
        });
        ServerPlayNetworking.registerGlobalReceiver(CONTROLS_MODE_CHANNEL,
                (server, player, handler, buf, responseSender) -> ControlsMode.byId(buf.readString(32))
                        .ifPresent(controlsMode -> server
                                .execute(() -> PlayerChangeControlsModeCallback.EVENT.invoker().apply(player, controlsMode))));
    }

    /**
     * Prints a message to the terminal.
     *
     * @param info the message to print
     */
    public void log(String info) {
        this.logger.info("[MidnightControls] " + info);
    }

    /**
     * Prints a warning to the terminal.
     *
     * @param warning the warning to print
     */
    public void warn(String warning) {
        this.logger.info("[MidnightControls] " + warning);
    }

    /**
     * Returns a packet byte buffer made for the midnightcontrols:controls_mode plugin message.
     *
     * @param controlsMode the controls mode to send
     * @return the packet byte buffer
     */
    public PacketByteBuf makeControlsModeBuffer(@NotNull ControlsMode controlsMode) {
        Objects.requireNonNull(controlsMode, "Controls mode cannot be null.");
        return new PacketByteBuf(Unpooled.buffer()).writeString(controlsMode.getName(), 32);
    }

    /**
     * Returns a packet byte buffer made for the midnightcontrols:feature plugin message.
     *
     * @param features the features data to send
     * @return the packet byte buffer
     */
    public PacketByteBuf makeFeatureBuffer(MidnightControlsFeature... features) {
        if (features.length == 0)
            throw new IllegalArgumentException("At least one feature must be provided.");
        var buffer = new PacketByteBuf(Unpooled.buffer());
        buffer.writeVarInt(features.length);
        for (var feature : features) {
            buffer.writeString(feature.getName(), 64);
            buffer.writeBoolean(feature.isAllowed());
        }
        return buffer;
    }

    public PacketByteBuf makeHello(@NotNull ControlsMode controlsMode) {
        var version = "";
        Optional<ModContainer> container;
        if ((container = FabricLoader.getInstance().getModContainer(MidnightControlsConstants.NAMESPACE)).isPresent()) {
            version = container.get().getMetadata().getVersion().getFriendlyString();
        }
        return new PacketByteBuf(Unpooled.buffer()).writeString(version, 32).writeString(controlsMode.getName(), 32);
    }

    /**
     * Gets the MidnightControls instance.
     *
     * @return the MidnightControls instance
     */
    public static MidnightControls get() {
        return INSTANCE;
    }
}
