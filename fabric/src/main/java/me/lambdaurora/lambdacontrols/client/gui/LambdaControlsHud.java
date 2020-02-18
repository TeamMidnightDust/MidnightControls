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
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.Matrix4f;
import net.minecraft.client.util.math.Rotation3;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents the LambdaControls HUD.
 *
 * @author LambdAurora
 * @version 1.2.0
 * @since 1.0.0
 */
public class LambdaControlsHud extends Hud
{
    private final LambdaControlsClient mod;
    private       MinecraftClient      client;
    private       int                  attackWidth          = 0;
    private       int                  attackButtonWidth    = 0;
    private       int                  dropItemWidth        = 0;
    private       int                  dropItemButtonWidth  = 0;
    private       int                  inventoryWidth       = 0;
    private       int                  inventoryButtonWidth = 0;
    private       int                  swapHandsWidth       = 0;
    private       int                  swapHandsButtonWidth = 0;
    private       int                  useWidth             = 0;
    private       int                  useButtonWidth       = 0;
    private       BlockHitResult       placeHitResult;
    private       String               attackAction         = "";
    private       String               placeAction          = "";

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
        this.inventoryWidth = this.width(ButtonBinding.INVENTORY);
        this.inventoryButtonWidth = LambdaControlsRenderer.getBindingIconWidth(ButtonBinding.INVENTORY);
        this.swapHandsWidth = this.width(ButtonBinding.SWAP_HANDS);
        this.swapHandsButtonWidth = LambdaControlsRenderer.getBindingIconWidth(ButtonBinding.SWAP_HANDS);
        this.dropItemWidth = this.width(ButtonBinding.DROP_ITEM);
        this.dropItemButtonWidth = LambdaControlsRenderer.getBindingIconWidth(ButtonBinding.DROP_ITEM);
        this.attackButtonWidth = LambdaControlsRenderer.getBindingIconWidth(ButtonBinding.ATTACK);
        this.useButtonWidth = LambdaControlsRenderer.getBindingIconWidth(ButtonBinding.USE);
    }

    /**
     * Renders the LambdaControls' HUD.
     */
    public void render(float tickDelta)
    {
        if (this.mod.config.getControlsMode() == ControlsMode.CONTROLLER && this.client.currentScreen == null) {
            int y = bottom(2);
            this.renderFirstIcons(this.mod.config.getHudSide() == HudSide.LEFT ? 2 : client.getWindow().getScaledWidth() - 2, y);
            this.renderSecondIcons(this.mod.config.getHudSide() == HudSide.RIGHT ? 2 : client.getWindow().getScaledWidth() - 2, y);
            VertexConsumerProvider.Immediate immediate = VertexConsumerProvider.immediate(Tessellator.getInstance().getBuffer());
            Matrix4f matrix4f = Rotation3.identity().getMatrix();
            this.renderFirstSection(this.mod.config.getHudSide() == HudSide.LEFT ? 2 : client.getWindow().getScaledWidth() - 2, y, immediate, matrix4f);
            this.renderSecondSection(this.mod.config.getHudSide() == HudSide.RIGHT ? 2 : client.getWindow().getScaledWidth() - 2, y, immediate, matrix4f);
            immediate.draw();
        }
    }

    public void renderFirstIcons(int x, int y)
    {
        int offset = 2 + this.inventoryWidth + this.inventoryButtonWidth + 4;
        int currentX = this.mod.config.getHudSide() == HudSide.LEFT ? x : x - this.inventoryButtonWidth;
        this.drawButton(currentX, y, ButtonBinding.INVENTORY, true);
        this.drawButton(currentX += (this.mod.config.getHudSide() == HudSide.LEFT ? offset : -offset), y, ButtonBinding.SWAP_HANDS, true);
        offset = 2 + this.swapHandsWidth + this.dropItemButtonWidth + 4;
        if (this.client.options.showSubtitles && this.mod.config.getHudSide() == HudSide.RIGHT) {
            currentX += -offset;
        } else {
            currentX = this.mod.config.getHudSide() == HudSide.LEFT ? x : x - this.dropItemButtonWidth;
            y -= 24;
        }
        this.drawButton(currentX, y, ButtonBinding.DROP_ITEM, !this.client.player.getMainHandStack().isEmpty());
    }

    public void renderSecondIcons(int x, int y)
    {
        int offset;
        int currentX = x;
        if (!this.placeAction.isEmpty()) {
            if (this.mod.config.getHudSide() == HudSide.LEFT)
                currentX -= this.useButtonWidth;
            this.drawButton(currentX, y, ButtonBinding.USE, true);
            offset = 2 + this.useWidth + 4;
            if (this.client.options.showSubtitles && this.mod.config.getHudSide() == HudSide.LEFT) {
                currentX -= offset;
            } else {
                currentX = x;
                y -= 24;
            }
        }

        if (this.mod.config.getHudSide() == HudSide.LEFT)
            currentX -= this.attackButtonWidth;

        this.drawButton(currentX, y, ButtonBinding.ATTACK, this.attackWidth != 0);
    }

    public void renderFirstSection(int x, int y, @NotNull VertexConsumerProvider.Immediate immediate, @NotNull Matrix4f matrix4f)
    {
        int currentX = this.mod.config.getHudSide() == HudSide.LEFT ? x + this.inventoryButtonWidth + 2 : x - this.inventoryButtonWidth - 2 - this.inventoryWidth;
        this.drawTip(currentX, y, ButtonBinding.INVENTORY, true, immediate, matrix4f);
        currentX += this.mod.config.getHudSide() == HudSide.LEFT ? this.inventoryWidth + 4 + this.swapHandsButtonWidth + 2
                : -this.swapHandsWidth - 2 - this.swapHandsButtonWidth - 4;
        this.drawTip(currentX, y, ButtonBinding.SWAP_HANDS, true, immediate, matrix4f);
        if (this.client.options.showSubtitles && this.mod.config.getHudSide() == HudSide.RIGHT) {
            currentX += -this.dropItemWidth - 2 - this.dropItemButtonWidth - 4;
        } else {
            y -= 24;
            currentX = this.mod.config.getHudSide() == HudSide.LEFT ? x + this.dropItemButtonWidth + 2 : x - this.dropItemButtonWidth - 2 - this.dropItemWidth;
        }
        this.drawTip(currentX, y, ButtonBinding.DROP_ITEM, !this.client.player.getMainHandStack().isEmpty(), immediate, matrix4f);
    }

    public void renderSecondSection(int x, int y, @NotNull VertexConsumerProvider.Immediate immediate, @NotNull Matrix4f matrix4f)
    {
        int currentX = x;

        if (!this.placeAction.isEmpty()) {
            currentX += this.mod.config.getHudSide() == HudSide.RIGHT ? this.useButtonWidth + 2 : -this.useButtonWidth - 2 - this.useWidth;

            this.drawTip(currentX, y, this.placeAction, true, immediate, matrix4f);

            if (this.client.options.showSubtitles && this.mod.config.getHudSide() == HudSide.LEFT) {
                currentX -= 4;
            } else {
                currentX = x;
                y -= 24;
            }
        }

        currentX += this.mod.config.getHudSide() == HudSide.RIGHT ? this.attackButtonWidth + 2 : -this.attackButtonWidth - 2 - this.attackWidth;

        this.drawTip(currentX, y, this.attackAction, this.attackWidth != 0, immediate, matrix4f);
    }

    @Override
    public void tick()
    {
        super.tick();
        if (this.mod.config.getControlsMode() == ControlsMode.CONTROLLER) {
            if (this.client.crosshairTarget == null)
                return;

            String placeAction;

            // Update "Use" tip status.
            if (this.client.crosshairTarget.getType() == HitResult.Type.MISS) {
                this.placeHitResult = LambdaInput.tryFrontPlace(this.client);
                this.attackAction = "";
                this.attackWidth = 0;
            } else {
                if (this.client.crosshairTarget.getType() == HitResult.Type.BLOCK)
                    this.placeHitResult = (BlockHitResult) this.client.crosshairTarget;
                else
                    this.placeHitResult = null;

                this.attackAction = this.client.crosshairTarget.getType() == HitResult.Type.BLOCK ? "lambdacontrols.action.hit" : ButtonBinding.ATTACK.getTranslationKey();
                this.attackWidth = this.width(attackAction);
            }

            ItemStack stack = this.client.player.getMainHandStack();
            if ((stack == null || stack.isEmpty()) && ((stack = this.client.player.getOffHandStack()) == null || stack.isEmpty())) {
                placeAction = "";
            } else {
                if (this.placeHitResult != null && stack.getItem() instanceof BlockItem) {
                    placeAction = "lambdacontrols.action.place";
                } else {
                    placeAction = ButtonBinding.USE.getTranslationKey();
                }
            }

            this.placeAction = placeAction;

            // Cache the "Use" tip width.
            if (this.placeAction.isEmpty())
                this.useWidth = 0;
            else
                this.useWidth = this.width(this.placeAction);
        }
    }

    @Override
    public boolean hasTicks()
    {
        return true;
    }

    private int bottom(int y)
    {
        return this.client.getWindow().getScaledHeight() - y - LambdaControlsRenderer.ICON_SIZE;
    }

    private int width(@NotNull ButtonBinding binding)
    {
        return this.width(binding.getTranslationKey());
    }

    private int width(@Nullable String text)
    {
        if (text == null || text.isEmpty())
            return 0;
        return this.client.textRenderer.getStringWidth(I18n.translate(text));
    }

    private void drawButton(int x, int y, @NotNull ButtonBinding button, boolean display)
    {
        if (display)
            LambdaControlsRenderer.drawButton(x, y, button, this.client);
    }

    private void drawTip(int x, int y, @NotNull ButtonBinding button, boolean display, @NotNull VertexConsumerProvider.Immediate immediate, @NotNull Matrix4f matrix4f)
    {
        this.drawTip(x, y, button.getTranslationKey(), display, immediate, matrix4f);
    }

    private void drawTip(int x, int y, @NotNull String action, boolean display, @NotNull VertexConsumerProvider.Immediate immediate, @NotNull Matrix4f matrix4f)
    {
        if (!display)
            return;
        String translatedAction = I18n.translate(action);
        int textY = (LambdaControlsRenderer.ICON_SIZE / 2 - this.client.textRenderer.fontHeight / 2) + 1;
        client.textRenderer.draw(translatedAction, (float) x, (float) (y + textY), 14737632, true, matrix4f, immediate,
                false, 0, 15728880);
    }
}
