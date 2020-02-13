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
import me.lambdaurora.lambdacontrols.client.LambdaInput;
import me.lambdaurora.lambdacontrols.client.controller.ButtonBinding;
import me.lambdaurora.spruceui.hud.Hud;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.Matrix4f;
import net.minecraft.client.util.math.Rotation3;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
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
    private       int                  useWidth    = 0;
    private       int                  attackWidth = 0;
    private       BlockHitResult       placeHitResult;
    private       String               placeAction = "";

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
            VertexConsumerProvider.Immediate immediate = VertexConsumerProvider.immediate(Tessellator.getInstance().getBuffer());
            Matrix4f matrix4f = Rotation3.identity().getMatrix();
            this.renderFirstSection(this.mod.config.getHudSide() == HudSide.LEFT ? 10 : client.getWindow().getScaledWidth() - 10 - this.widthBottom, immediate, matrix4f);
            this.renderSecondSection(this.mod.config.getHudSide() == HudSide.RIGHT ? 10 : client.getWindow().getScaledWidth() - 10, immediate, matrix4f);
            immediate.draw();
        }
    }

    public void renderFirstSection(int x, @NotNull VertexConsumerProvider.Immediate immediate, @NotNull Matrix4f matrix4f)
    {
        int y = bottom(10);
        x += (this.widthBottom = this.drawButtonTip(x, y, ButtonBinding.INVENTORY, true, immediate, matrix4f) + 10);
        this.widthBottom += this.drawButtonTip(x, y, ButtonBinding.SWAP_HANDS, true, immediate, matrix4f);
        this.widthTop = this.drawButtonTip(x, (y - 24), ButtonBinding.DROP_ITEM, !this.client.player.getMainHandStack().isEmpty(), immediate, matrix4f) + 10;
    }

    public void renderSecondSection(int x, @NotNull VertexConsumerProvider.Immediate immediate, @NotNull Matrix4f matrix4f)
    {
        int y = bottom(10);
        if (this.placeHitResult != null) {
            int firstX = this.mod.config.getHudSide() == HudSide.RIGHT ? x : x - this.useWidth;
            this.useWidth = this.drawButtonTip(firstX, y, ButtonBinding.USE.getButton(), this.placeAction, true, immediate, matrix4f);
            y -= 24;
        }

        int secondX = this.mod.config.getHudSide() == HudSide.RIGHT ? x : x - this.attackWidth;
        this.attackWidth = this.drawButtonTip(secondX, y, ButtonBinding.ATTACK.getButton(),
                this.client.crosshairTarget.getType() == HitResult.Type.BLOCK ? "lambdacontrols.action.hit" : ButtonBinding.ATTACK.getTranslationKey(),
                this.client.crosshairTarget.getType() != HitResult.Type.MISS, immediate, matrix4f);
    }

    @Override
    public void tick()
    {
        super.tick();
        if (this.mod.config.getControlsMode() == ControlsMode.CONTROLLER) {
            if (this.client.crosshairTarget == null)
                return;

            if (this.client.crosshairTarget.getType() == HitResult.Type.MISS) {
                this.placeHitResult = LambdaInput.tryFrontPlace(this.client);
            } else {
                if (this.client.crosshairTarget.getType() == HitResult.Type.BLOCK)
                    this.placeHitResult = (BlockHitResult) this.client.crosshairTarget;
                else
                    this.placeHitResult = null;
            }

            if (this.placeHitResult != null) {
                ItemStack stack = this.client.player.getActiveItem();
                if (stack != null && stack.getItem() instanceof BlockItem) {
                    this.placeAction = "lambdacontrols.action.place";
                }
            }
        }
    }

    @Override
    public boolean hasTicks()
    {
        return true;
    }

    private int bottom(int y)
    {
        return this.client.getWindow().getScaledHeight() - y - 15;
    }

    private int drawButtonTip(int x, int y, @NotNull ButtonBinding button, boolean display, @NotNull VertexConsumerProvider.Immediate immediate, @NotNull Matrix4f matrix4f)
    {
        return LambdaControlsRenderer.drawButtonTip(x, y, button, display, this.client, immediate, matrix4f);
    }

    private int drawButtonTip(int x, int y, int[] button, @NotNull String action, boolean display, @NotNull VertexConsumerProvider.Immediate immediate, @NotNull Matrix4f matrix4f)
    {
        return LambdaControlsRenderer.drawButtonTip(x, y, button, action, display, this.client, immediate, matrix4f);
    }
}
