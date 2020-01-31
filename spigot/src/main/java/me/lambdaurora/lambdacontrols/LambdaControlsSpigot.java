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
import me.lambdaurora.lambdacontrols.event.PlayerChangeControlsModeEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static me.lambdaurora.lambdacontrols.LambdaControlsConstants.*;

/**
 * Represents the LambdaControls spigot plugin which provides extra features for servers.
 *
 * @author LambdAurora
 * @version 1.1.0
 * @since 1.1.0
 */
public class LambdaControlsSpigot extends JavaPlugin implements PluginMessageListener, Listener
{
    private static final Map<Player, ControlsMode> PLAYERS_CONTROLS_MODE = new HashMap<>();
    public final         LambdaControlsConfig      config                = new LambdaControlsConfig(this);

    @Override
    public void onEnable()
    {
        super.onEnable();

        this.config.load();

        // Note that Spigot has a bullshit channel size restriction as Minecraft SUPPORTS UP TO 32767 AS CHANNEL SIZE.
        // Please stop using that bad server software, move over Sponge or idk other things. REALLY.
        this.getServer().getMessenger().registerIncomingPluginChannel(this, CONTROLS_MODE_CHANNEL.toString(), this);
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, CONTROLS_MODE_CHANNEL.toString());
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, FEATURE_CHANNEL.toString());
        this.getServer().getMessenger().registerIncomingPluginChannel(this, HELLO_CHANNEL.toString(), this);
        this.getServer().getPluginManager().registerEvents(this, this);

        this.getServer().getOnlinePlayers().forEach(player -> {
            PLAYERS_CONTROLS_MODE.put(player, ControlsMode.DEFAULT);

            this.requestPlayerControlsMode(player);
            this.updatePlayerFeature(player, LambdaControlsFeature.FRONT_BLOCK_PLACING);
        });
    }

    @Override
    public void onDisable()
    {
        super.onDisable();

        this.getServer().getMessenger().unregisterIncomingPluginChannel(this, CONTROLS_MODE_CHANNEL.toString());
        this.getServer().getMessenger().unregisterOutgoingPluginChannel(this, CONTROLS_MODE_CHANNEL.toString());
        this.getServer().getMessenger().unregisterOutgoingPluginChannel(this, FEATURE_CHANNEL.toString());
        this.getServer().getMessenger().unregisterIncomingPluginChannel(this, HELLO_CHANNEL.toString());

        PLAYERS_CONTROLS_MODE.clear();
    }

    public void requestPlayerControlsMode(@NotNull Player player)
    {
        player.sendPluginMessage(this, CONTROLS_MODE_CHANNEL.toString(), new byte[0]);
    }

    public void updatePlayerFeature(@NotNull Player player, @NotNull LambdaControlsFeature feature)
    {
        Objects.requireNonNull(player);
        Objects.requireNonNull(feature);

        player.sendPluginMessage(this, FEATURE_CHANNEL.toString(), this.makeFeatureMessage(feature));
    }

    /**
     * Prints a message to the terminal.
     *
     * @param info The message to print.
     */
    public void log(String info)
    {
        this.getLogger().info(info);
    }

    @Override
    public void onPluginMessageReceived(@NotNull String channel, @NotNull Player player, @NotNull byte[] message)
    {
        if (channel.equals(HELLO_CHANNEL.toString())) {
            NettyPacketBuffer buffer = new NettyPacketBuffer(Unpooled.copiedBuffer(message));
            String version = buffer.readString(16);
            ControlsMode.byId(buffer.readString(32)).ifPresent(controlsMode -> {
                PLAYERS_CONTROLS_MODE.put(player, controlsMode);
                PlayerChangeControlsModeEvent event = new PlayerChangeControlsModeEvent(player, controlsMode);
                this.getServer().getPluginManager().callEvent(event);
            });
            this.updatePlayerFeature(player, LambdaControlsFeature.FRONT_BLOCK_PLACING);
        } else if (channel.equals(CONTROLS_MODE_CHANNEL.toString())) {
            NettyPacketBuffer buffer = new NettyPacketBuffer(Unpooled.copiedBuffer(message));
            ControlsMode.byId(buffer.readString(32)).ifPresent(controlsMode -> {
                PLAYERS_CONTROLS_MODE.put(player, controlsMode);
                PlayerChangeControlsModeEvent event = new PlayerChangeControlsModeEvent(player, controlsMode);
                this.getServer().getPluginManager().callEvent(event);
            });
        }
    }

    /**
     * Returns a packet byte buffer made for the lambdacontrols:feature plugin message.
     *
     * @param feature The feature data to send.
     * @return The packet byte buffer.
     */
    public byte[] makeFeatureMessage(@NotNull LambdaControlsFeature feature)
    {
        Objects.requireNonNull(feature, "Feature cannot be null.");
        NettyPacketBuffer buffer = new NettyPacketBuffer(Unpooled.buffer());
        buffer.writeString(feature.getName());
        buffer.writeBoolean(feature.isAllowed());
        return buffer.array();
    }

    @EventHandler
    public void onPlayerJoin(@NotNull PlayerJoinEvent event)
    {
        PLAYERS_CONTROLS_MODE.put(event.getPlayer(), ControlsMode.DEFAULT);
    }

    @EventHandler
    public void onPlayerLeave(@NotNull PlayerQuitEvent event)
    {
        PLAYERS_CONTROLS_MODE.remove(event.getPlayer());
    }
}
