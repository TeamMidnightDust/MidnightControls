/*
 * Copyright © 2021 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of midnightcontrols.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package eu.midnightdust.midnightcontrols.client.gui;

import eu.midnightdust.midnightcontrols.ControlsMode;
import eu.midnightdust.midnightcontrols.MidnightControlsConstants;
import eu.midnightdust.midnightcontrols.client.HudSide;
import eu.midnightdust.midnightcontrols.client.MidnightControlsClient;
import eu.midnightdust.midnightcontrols.client.MidnightControlsConfig;
import eu.midnightdust.midnightcontrols.client.MidnightInput;
import eu.midnightdust.midnightcontrols.client.compat.MidnightControlsCompat;
import eu.midnightdust.midnightcontrols.client.controller.ButtonBinding;
import dev.lambdaurora.spruceui.hud.Hud;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents the midnightcontrols HUD.
 *
 * @author LambdAurora
 * @version 1.7.0
 * @since 1.0.0
 */
public class MidnightControlsHud extends Hud {
    private final MidnightControlsClient mod;
    private MinecraftClient client;
    private int attackWidth = 0;
    private int attackButtonWidth = 0;
    private int dropItemWidth = 0;
    private int dropItemButtonWidth = 0;
    private int inventoryWidth = 0;
    private int inventoryButtonWidth = 0;
    private int swapHandsWidth = 0;
    private int swapHandsButtonWidth = 0;
    private int useWidth = 0;
    private int useButtonWidth = 0;
    private BlockHitResult placeHitResult;
    private String attackAction = "";
    private String placeAction = "";
    private int ticksDisplayedCrosshair = 0;

    public MidnightControlsHud(@NotNull MidnightControlsClient mod) {
        super(new Identifier(MidnightControlsConstants.NAMESPACE, "hud/button_indicator"));
        this.mod = mod;
    }

    @Override
    public void init(@NotNull MinecraftClient client, int screenWidth, int screenHeight) {
        super.init(client, screenWidth, screenHeight);
        this.client = client;
        this.inventoryWidth = this.width(ButtonBinding.INVENTORY);
        this.inventoryButtonWidth = MidnightControlsRenderer.getBindingIconWidth(ButtonBinding.INVENTORY);
        this.swapHandsWidth = this.width(ButtonBinding.SWAP_HANDS);
        this.swapHandsButtonWidth = MidnightControlsRenderer.getBindingIconWidth(ButtonBinding.SWAP_HANDS);
        this.dropItemWidth = this.width(ButtonBinding.DROP_ITEM);
        this.dropItemButtonWidth = MidnightControlsRenderer.getBindingIconWidth(ButtonBinding.DROP_ITEM);
        this.attackButtonWidth = MidnightControlsRenderer.getBindingIconWidth(ButtonBinding.ATTACK);
        this.useButtonWidth = MidnightControlsRenderer.getBindingIconWidth(ButtonBinding.USE);
    }

    /**
     * Renders the midnightcontrols' HUD.
     */
    @Override
    public void render(MatrixStack matrices, float tickDelta) {
        if (MidnightControlsConfig.controlsMode == ControlsMode.CONTROLLER && this.client.currentScreen == null) {
            int y = bottom(2);
            this.renderFirstIcons(matrices, MidnightControlsConfig.hudSide == HudSide.LEFT ? 2 : client.getWindow().getScaledWidth() - 2, y);
            this.renderSecondIcons(matrices, MidnightControlsConfig.hudSide == HudSide.RIGHT ? 2 : client.getWindow().getScaledWidth() - 2, y);
            this.renderFirstSection(matrices, MidnightControlsConfig.hudSide == HudSide.LEFT ? 2 : client.getWindow().getScaledWidth() - 2, y);
            this.renderSecondSection(matrices, MidnightControlsConfig.hudSide == HudSide.RIGHT ? 2 : client.getWindow().getScaledWidth() - 2, y);
        }

        if (this.mod.reacharound.isLastReacharoundVertical()) {
            // Render crosshair indicator.
            var window = this.client.getWindow();
            var text = "[  ]";

            float scale = Math.min(5, this.ticksDisplayedCrosshair + tickDelta) / 5F;
            scale *= scale;
            int opacity = ((int) (255 * scale)) << 24;

            this.client.textRenderer.draw(matrices, text, window.getScaledWidth() / 2.f - this.client.textRenderer.getWidth(text) / 2.f,
                    window.getScaledHeight() / 2.f - 4, 0xCCCCCC | opacity);
        }
    }

    public void renderFirstIcons(MatrixStack matrices, int x, int y) {
        int offset = 2 + this.inventoryWidth + this.inventoryButtonWidth + 4;
        int currentX = MidnightControlsConfig.hudSide == HudSide.LEFT ? x : x - this.inventoryButtonWidth;
        this.drawButton(matrices, currentX, y, ButtonBinding.INVENTORY, true);
        this.drawButton(matrices, currentX += (MidnightControlsConfig.hudSide == HudSide.LEFT ? offset : -offset), y, ButtonBinding.SWAP_HANDS, true);
        offset = 2 + this.swapHandsWidth + this.dropItemButtonWidth + 4;
        if (this.client.options.getShowSubtitles().getValue() && MidnightControlsConfig.hudSide == HudSide.RIGHT) {
            currentX += -offset;
        } else {
            currentX = MidnightControlsConfig.hudSide == HudSide.LEFT ? x : x - this.dropItemButtonWidth;
            y -= 24;
        }
        this.drawButton(matrices, currentX, y, ButtonBinding.DROP_ITEM, !this.client.player.getMainHandStack().isEmpty());
    }

    public void renderSecondIcons(MatrixStack matrices, int x, int y) {
        int offset;
        int currentX = x;
        if (!this.placeAction.isEmpty()) {
            if (MidnightControlsConfig.hudSide == HudSide.LEFT)
                currentX -= this.useButtonWidth;
            this.drawButton(matrices, currentX, y, ButtonBinding.USE, true);
            offset = 2 + this.useWidth + 4;
            if (this.client.options.getShowSubtitles().getValue() && MidnightControlsConfig.hudSide == HudSide.LEFT) {
                currentX -= offset;
            } else {
                currentX = x;
                y -= 24;
            }
        }

        if (MidnightControlsConfig.hudSide == HudSide.LEFT)
            currentX -= this.attackButtonWidth;

        this.drawButton(matrices, currentX, y, ButtonBinding.ATTACK, this.attackWidth != 0);
    }

    public void renderFirstSection(MatrixStack matrices, int x, int y) {
        int currentX = MidnightControlsConfig.hudSide == HudSide.LEFT ? x + this.inventoryButtonWidth + 2 : x - this.inventoryButtonWidth - 2 - this.inventoryWidth;
        this.drawTip(matrices, currentX, y, ButtonBinding.INVENTORY, true);
        currentX += MidnightControlsConfig.hudSide == HudSide.LEFT ? this.inventoryWidth + 4 + this.swapHandsButtonWidth + 2
                : -this.swapHandsWidth - 2 - this.swapHandsButtonWidth - 4;
        this.drawTip(matrices, currentX, y, ButtonBinding.SWAP_HANDS, true);
        if (this.client.options.getShowSubtitles().getValue() && MidnightControlsConfig.hudSide == HudSide.RIGHT) {
            currentX += -this.dropItemWidth - 2 - this.dropItemButtonWidth - 4;
        } else {
            y -= 24;
            currentX = MidnightControlsConfig.hudSide == HudSide.LEFT ? x + this.dropItemButtonWidth + 2 : x - this.dropItemButtonWidth - 2 - this.dropItemWidth;
        }
        this.drawTip(matrices, currentX, y, ButtonBinding.DROP_ITEM, !this.client.player.getMainHandStack().isEmpty());
    }

    public void renderSecondSection(MatrixStack matrices, int x, int y) {
        int currentX = x;

        if (!this.placeAction.isEmpty()) {
            currentX += MidnightControlsConfig.hudSide == HudSide.RIGHT ? this.useButtonWidth + 2 : -this.useButtonWidth - 2 - this.useWidth;

            this.drawTip(matrices, currentX, y, this.placeAction, true);

            if (this.client.options.getShowSubtitles().getValue() && MidnightControlsConfig.hudSide == HudSide.LEFT) {
                currentX -= 4;
            } else {
                currentX = x;
                y -= 24;
            }
        }

        currentX += MidnightControlsConfig.hudSide == HudSide.RIGHT ? this.attackButtonWidth + 2 : -this.attackButtonWidth - 2 - this.attackWidth;

        this.drawTip(matrices, currentX, y, this.attackAction, this.attackWidth != 0);
    }

    @Override
    public void tick() {
        super.tick();
        if (MidnightControlsConfig.controlsMode == ControlsMode.CONTROLLER) {
            if (this.client.crosshairTarget == null)
                return;

            String placeAction;

            // Update "Use" tip status.
            if (this.client.crosshairTarget.getType() == HitResult.Type.MISS) {
                this.placeHitResult = this.mod.reacharound.getLastReacharoundResult();
                this.attackAction = "";
                this.attackWidth = 0;
            } else {
                if (this.client.crosshairTarget.getType() == HitResult.Type.BLOCK)
                    this.placeHitResult = (BlockHitResult) this.client.crosshairTarget;
                else
                    this.placeHitResult = null;

                this.attackAction = this.client.crosshairTarget.getType() == HitResult.Type.BLOCK ? "midnightcontrols.action.hit" : ButtonBinding.ATTACK.getTranslationKey();
                this.attackWidth = this.width(attackAction);
            }

            if (this.mod.reacharound.isLastReacharoundVertical()) {
                if (this.ticksDisplayedCrosshair < 5)
                    this.ticksDisplayedCrosshair++;
            } else {
                this.ticksDisplayedCrosshair = 0;
            }

            var customAttackAction = MidnightControlsCompat.getAttackActionAt(this.client, this.placeHitResult);
            if (customAttackAction != null) {
                this.attackAction = customAttackAction;
                this.attackWidth = this.width(customAttackAction);
            }

            ItemStack stack = null;
            if (this.client.player != null) {
                stack = this.client.player.getMainHandStack();
                if (stack == null || stack.isEmpty())
                    stack = this.client.player.getOffHandStack();
            }
            if (stack == null || stack.isEmpty()) {
                placeAction = "";
            } else {
                if (this.placeHitResult != null && stack.getItem() instanceof BlockItem) {
                    placeAction = "midnightcontrols.action.place";
                } else {
                    placeAction = ButtonBinding.USE.getTranslationKey();
                }
            }

            var customUseAction = MidnightControlsCompat.getUseActionAt(this.client, this.placeHitResult);
            if (customUseAction != null)
                placeAction = customUseAction;

            this.placeAction = placeAction;

            // Cache the "Use" tip width.
            if (this.placeAction.isEmpty())
                this.useWidth = 0;
            else
                this.useWidth = this.width(this.placeAction);
        }
    }

    @Override
    public boolean hasTicks() {
        return true;
    }

    private int bottom(int y) {
        return this.client.getWindow().getScaledHeight() - y - MidnightControlsRenderer.ICON_SIZE;
    }

    private int width(@NotNull ButtonBinding binding) {
        return this.width(binding.getTranslationKey());
    }

    private int width(@Nullable String text) {
        if (text == null || text.isEmpty())
            return 0;
        return this.client.textRenderer.getWidth(I18n.translate(text));
    }

    private void drawButton(MatrixStack matrices, int x, int y, @NotNull ButtonBinding button, boolean display) {
        if (display)
            MidnightControlsRenderer.drawButton(matrices, x, y, button, this.client);
    }

    private void drawTip(MatrixStack matrices, int x, int y, @NotNull ButtonBinding button, boolean display) {
        this.drawTip(matrices, x, y, button.getTranslationKey(), display);
    }

    private void drawTip(MatrixStack matrices, int x, int y, @NotNull String action, boolean display) {
        if (!display)
            return;
        var translatedAction = I18n.translate(action);
        int textY = (MidnightControlsRenderer.ICON_SIZE / 2 - this.client.textRenderer.fontHeight / 2) + 1;
        this.client.textRenderer.draw(matrices, translatedAction, (float) x, (float) (y + textY), 14737632);
    }
}
