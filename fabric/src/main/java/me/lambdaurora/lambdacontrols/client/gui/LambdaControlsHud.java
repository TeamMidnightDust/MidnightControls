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
import me.lambdaurora.lambdacontrols.LambdaControlsConstants;
import me.lambdaurora.lambdacontrols.client.HudSide;
import me.lambdaurora.lambdacontrols.client.LambdaControlsClient;
import me.lambdaurora.lambdacontrols.client.controller.ButtonBinding;
import me.lambdaurora.spruceui.hud.Hud;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.HitResult;
import org.jetbrains.annotations.NotNull;

/**
 * Represents the LambdaControls HUD.
 *
 * @author LambdAurora
 * @version 1.1.0
 * @since 1.0.0
 */
public class LambdaControlsHud extends Hud
{
    private final LambdaControlsClient mod;
    private       MinecraftClient      client;
    private       int                  widthBottom = 0;
    private       int                  widthTop    = 0;

    public LambdaControlsHud(@NotNull LambdaControlsClient mod)
    {
        super(new Identifier(LambdaControlsConstants.NAMESPACE, "hud/button_indicator"));
        this.mod = mod;
    }

    @Override
    public void init(@NotNull MinecraftClient client, int screenWidth, int screenHeight)
    {
        super.init(client, screenWidth, screenHeight);
        this.client = client;
    }

    /**
     * Renders the LambdaControls' HUD.
     */
    public void render(float tickDelta)
    {
        if (this.mod.config.getControlsMode() == ControlsMode.CONTROLLER && this.client.currentScreen == null) {
            int x = this.mod.config.getHudSide() == HudSide.LEFT ? 10 : client.getWindow().getScaledWidth() - 10 - this.widthBottom, y = bottom(10);
            x += (this.widthBottom = this.drawButtonTip(x, y, ButtonBinding.INVENTORY, true) + 10);
            this.widthBottom += this.drawButtonTip(x, y, ButtonBinding.SWAP_HANDS, true);
            x = this.mod.config.getHudSide() == HudSide.LEFT ? 10 : client.getWindow().getScaledWidth() - 10 - this.widthTop;
            x += (this.widthTop = this.drawButtonTip(x, (y -= 20), ButtonBinding.DROP_ITEM, !this.client.player.getMainHandStack().isEmpty()) + 10);
            this.widthTop += this.drawButtonTip(x, y, ButtonBinding.ATTACK.getButton(),
                    this.client.crosshairTarget.getType() == HitResult.Type.BLOCK ? "lambdacontrols.action.hit" : ButtonBinding.ATTACK.getTranslationKey(),
                    this.client.crosshairTarget.getType() != HitResult.Type.MISS);
        }
    }

    private int bottom(int y)
    {
        return this.client.getWindow().getScaledHeight() - y - 15;
    }

    private int drawButtonTip(int x, int y, @NotNull ButtonBinding button, boolean display)
    {
        return LambdaControlsClient.drawButtonTip(x, y, button, display, this.client);
    }

    private int drawButtonTip(int x, int y, int[] button, @NotNull String action, boolean display)
    {
        return LambdaControlsClient.drawButtonTip(x, y, button, action, display, this.client);
    }
}
