/*
 * Copyright © 2020 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of LambdaControls.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package me.lambdaurora.lambdacontrols.client.gui;

import me.lambdaurora.lambdacontrols.ControlsMode;
import me.lambdaurora.lambdacontrols.client.HudSide;
import me.lambdaurora.lambdacontrols.client.LambdaControlsClient;
import me.lambdaurora.lambdacontrols.client.controller.ButtonBinding;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.util.hit.HitResult;
import org.jetbrains.annotations.NotNull;

/**
 * Represents the LambdaControls HUD.
 *
 * @author LambdAurora
 * @version 1.1.0
 * @since 1.0.0
 */
public class LambdaControlsHud extends DrawableHelper
{
    private final MinecraftClient      client;
    private final LambdaControlsClient mod;
    private       int                  width_bottom = 0;
    private       int                  width_top    = 0;

    public LambdaControlsHud(@NotNull MinecraftClient client, @NotNull LambdaControlsClient mod)
    {
        this.client = client;
        this.mod = mod;
    }

    /**
     * Renders the LambdaControls' HUD.
     */
    public void render()
    {
        if (this.mod.config.get_controls_mode() == ControlsMode.CONTROLLER && this.mod.config.is_hud_enabled() && this.client.currentScreen == null && !this.client.options.hudHidden) {
            int x = this.mod.config.get_hud_side() == HudSide.LEFT ? 10 : client.getWindow().getScaledWidth() - 10 - this.width_bottom, y = bottom(10);
            x += (this.width_bottom = this.draw_button_tip(x, y, ButtonBinding.INVENTORY, true) + 10);
            this.width_bottom += this.draw_button_tip(x, y, ButtonBinding.SWAP_HANDS, true);
            x = this.mod.config.get_hud_side() == HudSide.LEFT ? 10 : client.getWindow().getScaledWidth() - 10 - this.width_top;
            x += (this.width_top = this.draw_button_tip(x, (y -= 20), ButtonBinding.DROP_ITEM, !this.client.player.getMainHandStack().isEmpty()) + 10);
            this.width_top += this.draw_button_tip(x, y, ButtonBinding.ATTACK.get_button(),
                    this.client.crosshairTarget.getType() == HitResult.Type.BLOCK ? "lambdacontrols.action.hit" : ButtonBinding.ATTACK.get_translation_key(),
                    this.client.crosshairTarget.getType() != HitResult.Type.MISS);
        }
    }

    private int bottom(int y)
    {
        return this.client.getWindow().getScaledHeight() - y - 15;
    }

    private int draw_button_tip(int x, int y, @NotNull ButtonBinding button, boolean display)
    {
        return LambdaControlsClient.draw_button_tip(x, y, button, display, this.client);
    }

    private int draw_button_tip(int x, int y, int[] button, @NotNull String action, boolean display)
    {
        return LambdaControlsClient.draw_button_tip(x, y, button, action, display, this.client);
    }
}
