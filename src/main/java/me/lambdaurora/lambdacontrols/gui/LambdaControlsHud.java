/*
 * Copyright © 2019 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of LambdaControls.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package me.lambdaurora.lambdacontrols.gui;

import me.lambdaurora.lambdacontrols.ButtonBinding;
import me.lambdaurora.lambdacontrols.ControlsMode;
import me.lambdaurora.lambdacontrols.LambdaControls;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.util.hit.HitResult;
import org.jetbrains.annotations.NotNull;

/**
 * Represents the LambdaControls HUD.
 */
public class LambdaControlsHud extends DrawableHelper
{
    private final MinecraftClient client;
    private final LambdaControls  mod;

    public LambdaControlsHud(@NotNull MinecraftClient client, @NotNull LambdaControls mod)
    {
        this.client = client;
        this.mod = mod;
    }

    /**
     * Renders the LambdaControls' HUD.
     */
    public void render()
    {
        if (this.mod.config.get_controls_mode() == ControlsMode.CONTROLLER && this.mod.config.is_hud_enabled() && this.client.currentScreen == null) {
            int x = 10, y = bottom(10);
            x += this.draw_button_tip(x, y, ButtonBinding.INVENTORY, true) + 10;
            this.draw_button_tip(x, y, ButtonBinding.SWAP_HANDS, true);
            x = 10;
            x += this.draw_button_tip(x, (y -= 20), ButtonBinding.DROP_ITEM, !client.player.getMainHandStack().isEmpty()) + 10;
            this.draw_button_tip(x, y, ButtonBinding.ATTACK.get_button(),
                    client.crosshairTarget.getType() == HitResult.Type.BLOCK ? "lambdacontrols.action.hit" : ButtonBinding.ATTACK.get_translation_key(),
                    client.crosshairTarget.getType() != HitResult.Type.MISS);
        }
    }

    private int bottom(int y)
    {
        return this.client.getWindow().getScaledHeight() - y - 15;
    }

    private int draw_button_tip(int x, int y, @NotNull ButtonBinding button, boolean display)
    {
        return LambdaControls.draw_button_tip(x, y, button, display, this.client);
    }

    private int draw_button_tip(int x, int y, int button, @NotNull String action, boolean display)
    {
        return LambdaControls.draw_button_tip(x, y, button, action, display, this.client);
    }
}
