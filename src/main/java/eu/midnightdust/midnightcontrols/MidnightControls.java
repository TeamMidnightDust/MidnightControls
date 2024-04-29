/*
 * Copyright © 2021 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of midnightcontrols.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package eu.midnightdust.midnightcontrols;

import eu.midnightdust.midnightcontrols.event.PlayerChangeControlsModeCallback;
import eu.midnightdust.midnightcontrols.packet.ControlsModePacket;
import eu.midnightdust.midnightcontrols.packet.FeaturePacket;
import eu.midnightdust.midnightcontrols.packet.HelloPacket;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.CustomPayload;
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
    public static final CustomPayload.Id<CustomPayload> CONTROLS_MODE_CHANNEL = CustomPayload.id(MidnightControlsConstants.CONTROLS_MODE_CHANNEL.toString());
    public static final CustomPayload.Id<CustomPayload> FEATURE_CHANNEL = CustomPayload.id(MidnightControlsConstants.FEATURE_CHANNEL.toString());
    public static final CustomPayload.Id<CustomPayload> HELLO_CHANNEL = CustomPayload.id(MidnightControlsConstants.HELLO_CHANNEL.toString());
    public static boolean isExtrasLoaded;

    public final Logger logger = LogManager.getLogger("MidnightControls");

    @Override
    public void onInitialize() {
        INSTANCE = this;
        isExtrasLoaded = FabricLoader.getInstance().isModLoaded("midnightcontrols-extra");
        this.log("Initializing MidnightControls...");

        PayloadTypeRegistry.playC2S().register(HelloPacket.PACKET_ID, HelloPacket.codec);
        PayloadTypeRegistry.playC2S().register(ControlsModePacket.PACKET_ID, ControlsModePacket.codec);
        PayloadTypeRegistry.playS2C().register(ControlsModePacket.PACKET_ID, ControlsModePacket.codec);
        PayloadTypeRegistry.playS2C().register(FeaturePacket.PACKET_ID, FeaturePacket.codec);

        ServerPlayNetworking.registerGlobalReceiver(HelloPacket.PACKET_ID, (payload, context) -> {
            ControlsMode.byId(payload.controlsMode())
                    .ifPresent(controlsMode -> PlayerChangeControlsModeCallback.EVENT.invoker().apply(context.player(), controlsMode));
            context.responseSender().sendPacket(new FeaturePacket(MidnightControlsFeature.HORIZONTAL_REACHAROUND));
        });
        ServerPlayNetworking.registerGlobalReceiver(ControlsModePacket.PACKET_ID,
                (payload, context) -> ControlsMode.byId(payload.controlsMode())
                        .ifPresent(controlsMode -> PlayerChangeControlsModeCallback.EVENT.invoker().apply(context.player(), controlsMode)));
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
     * Gets the MidnightControls instance.
     *
     * @return the MidnightControls instance
     */
    public static MidnightControls get() {
        return INSTANCE;
    }
}
