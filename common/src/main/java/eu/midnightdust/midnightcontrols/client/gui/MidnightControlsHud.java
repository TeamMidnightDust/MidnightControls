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
import eu.midnightdust.midnightcontrols.client.enums.HudSide;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import eu.midnightdust.midnightcontrols.client.MidnightControlsClient;
import eu.midnightdust.midnightcontrols.client.MidnightControlsConfig;
import eu.midnightdust.midnightcontrols.client.compat.MidnightControlsCompat;
import eu.midnightdust.midnightcontrols.client.controller.ButtonBinding;
import org.joml.Matrix3x2fStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents the midnightcontrols HUD.
 *
 * @author LambdAurora
 * @version 1.7.0
 * @since 1.0.0
 */
public class MidnightControlsHud {
    private Minecraft client = Minecraft.getInstance();
    private int attackWidth = 0;
    private int attackButtonWidth = 0;
    private int dropItemWidth = 0;
    private int dropItemButtonWidth = 0;
    private int inventoryWidth = 0;
    private int inventoryButtonWidth = 0;
    private int swapHandsWidth = 0;
    private int swapHandsButtonWidth = 0;
    private boolean showSwapHandsAction = false;
    private int useWidth = 0;
    private int useButtonWidth = 0;
    private String attackAction = "";
    private String placeAction = "";
    private int ticksDisplayedCrosshair = 0;
    private static boolean isCrammed = false;
    public static boolean isVisible = false;
    private static final MidnightControlsHud INSTANCE = new MidnightControlsHud();

    public static MidnightControlsHud getInstance() {
        return INSTANCE;
    }

    public void init() {
        this.client = MidnightControlsClient.client;
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
     * Renders the MidnightControls HUD.
     */
    public void render(GuiGraphics context, DeltaTracker tickCounter) {
        if (this.client == null || !isVisible) return;
        if (MidnightControlsConfig.controlsMode == ControlsMode.CONTROLLER && this.client.screen == null) {
            isCrammed = client.getWindow().getGuiScaledWidth() < 520;
            int y = bottom(2);
            Matrix3x2fStack matrices = context.pose();
            matrices.pushMatrix();
            this.renderFirstIcons(context, MidnightControlsConfig.hudSide == HudSide.LEFT ? 2 : client.getWindow().getGuiScaledWidth() - 2, y);
            this.renderSecondIcons(context, MidnightControlsConfig.hudSide == HudSide.RIGHT ? 2 : client.getWindow().getGuiScaledWidth() - 2, y);
            this.renderFirstSection(context, MidnightControlsConfig.hudSide == HudSide.LEFT ? 2 : client.getWindow().getGuiScaledWidth() - 2, y);
            this.renderSecondSection(context, MidnightControlsConfig.hudSide == HudSide.RIGHT ? 2 : client.getWindow().getGuiScaledWidth() - 2, y);
            matrices.popMatrix();
        }

        if (MidnightControlsClient.reacharound.isLastReacharoundVertical()) {
            // Render crosshair indicator.
            var window = this.client.getWindow();
            var text = "[  ]";

            float scale = Math.min(5, this.ticksDisplayedCrosshair + tickCounter.getGameTimeDeltaPartialTick(true)) / 5F;
            scale *= scale;
            int opacity = ((int) (255 * scale)) << 24;

            context.drawString(client.font, text, (int) (window.getGuiScaledWidth() / 2.f - this.client.font.width(text) / 2.f),
                    (int) (window.getGuiScaledHeight() / 2.f - 4), 0xFFCCCCCC | opacity, false);
        }
    }

    public void renderFirstIcons(GuiGraphics context, int x, int y) {
        int offset = 2 + this.inventoryWidth + this.inventoryButtonWidth + 4;
        int currentX = MidnightControlsConfig.hudSide == HudSide.LEFT ? x : x - this.inventoryButtonWidth;
        if (!ButtonBinding.INVENTORY.isNotBound()) this.drawButton(context, currentX, y, ButtonBinding.INVENTORY, true);
        if (!ButtonBinding.SWAP_HANDS.isNotBound() && !isCrammed && showSwapHandsAction) this.drawButton(context, currentX += (MidnightControlsConfig.hudSide == HudSide.LEFT ? offset : -offset), y, ButtonBinding.SWAP_HANDS, true);
        offset = 2 + this.swapHandsWidth + this.dropItemButtonWidth + 4;
        if (this.client.options.showSubtitles().get() && MidnightControlsConfig.hudSide == HudSide.RIGHT) {
            currentX += -offset;
        } else {
            currentX = MidnightControlsConfig.hudSide == HudSide.LEFT ? x : x - this.dropItemButtonWidth;
            y -= 20;
        }
        if (!ButtonBinding.DROP_ITEM.isNotBound() && client.player != null)
            this.drawButton(context, currentX, y, ButtonBinding.DROP_ITEM, !this.client.player.getMainHandItem().isEmpty());
    }

    public void renderSecondIcons(GuiGraphics context, int x, int y) {
        int offset;
        int currentX = x;
        if (isCrammed && showSwapHandsAction && !this.client.options.showSubtitles().get() && !ButtonBinding.SWAP_HANDS.isNotBound()) {
            if (MidnightControlsConfig.hudSide == HudSide.LEFT)
                currentX -= this.useButtonWidth;
            this.drawButton(context, currentX, y, ButtonBinding.SWAP_HANDS, true);
            currentX = x;
            y -= 20;
        }
        if (!this.placeAction.isEmpty() && (!ButtonBinding.USE.isNotBound()) ) {
            if (MidnightControlsConfig.hudSide == HudSide.LEFT)
                currentX -= this.useButtonWidth;
            this.drawButton(context, currentX, y, ButtonBinding.USE, true);
            offset = 2 + this.useWidth + 4;
            if (this.client.options.showSubtitles().get() && MidnightControlsConfig.hudSide == HudSide.LEFT) {
                currentX -= offset;
            } else {
                currentX = x;
                y -= 20;
            }
        }

        if (MidnightControlsConfig.hudSide == HudSide.LEFT)
            currentX -= this.attackButtonWidth;

        if (!ButtonBinding.ATTACK.isNotBound()) this.drawButton(context, currentX, y, ButtonBinding.ATTACK, this.attackWidth != 0);
    }

    public void renderFirstSection(GuiGraphics context, int x, int y) {
        int currentX = MidnightControlsConfig.hudSide == HudSide.LEFT ? x + this.inventoryButtonWidth + 2 : x - this.inventoryButtonWidth - 2 - this.inventoryWidth;
        if (!ButtonBinding.INVENTORY.isNotBound()) this.drawTip(context, currentX, y, ButtonBinding.INVENTORY, true);
        currentX += MidnightControlsConfig.hudSide == HudSide.LEFT ? this.inventoryWidth + 4 + this.swapHandsButtonWidth + 2
                : -this.swapHandsWidth - 2 - this.swapHandsButtonWidth - 4;
        if (!ButtonBinding.SWAP_HANDS.isNotBound() && !isCrammed && showSwapHandsAction) this.drawTip(context, currentX, y, ButtonBinding.SWAP_HANDS, true);
        if (this.client.options.showSubtitles().get() && MidnightControlsConfig.hudSide == HudSide.RIGHT) {
            currentX += -this.dropItemWidth - 2 - this.dropItemButtonWidth - 4;
        } else {
            y -= 20;
            currentX = MidnightControlsConfig.hudSide == HudSide.LEFT ? x + this.dropItemButtonWidth + 2 : x - this.dropItemButtonWidth - 2 - this.dropItemWidth;
        }
        if (!ButtonBinding.DROP_ITEM.isNotBound() && client.player != null) this.drawTip(context, currentX, y, ButtonBinding.DROP_ITEM, !this.client.player.getMainHandItem().isEmpty());
    }

    public void renderSecondSection(GuiGraphics context, int x, int y) {
        int currentX = x;

        if (isCrammed && showSwapHandsAction && !this.client.options.showSubtitles().get() && !ButtonBinding.SWAP_HANDS.isNotBound()) {
            currentX += MidnightControlsConfig.hudSide == HudSide.RIGHT ? this.swapHandsButtonWidth + 2 : -this.swapHandsButtonWidth - 2 - this.swapHandsWidth;

            this.drawTip(context, currentX, y, ButtonBinding.SWAP_HANDS, true);

            currentX = x;
            y -= 20;
        }
        if (!this.placeAction.isEmpty()) {
            currentX += MidnightControlsConfig.hudSide == HudSide.RIGHT ? this.useButtonWidth + 2 : -this.useButtonWidth - 2 - this.useWidth;

            this.drawTip(context, currentX, y, this.placeAction, true);

            if (this.client.options.showSubtitles().get() && MidnightControlsConfig.hudSide == HudSide.LEFT) {
                currentX -= 4;
            } else {
                currentX = x;
                y -= 20;
            }
        }

        currentX += MidnightControlsConfig.hudSide == HudSide.RIGHT ? this.attackButtonWidth + 2 : -this.attackButtonWidth - 2 - this.attackWidth;

        if (!ButtonBinding.ATTACK.isNotBound()) this.drawTip(context, currentX, y, this.attackAction, this.attackWidth != 0);
    }

    public void tick() {
        if (this.client == null) return;
        if (MidnightControlsConfig.controlsMode == ControlsMode.CONTROLLER) {
            if (this.client.hitResult == null)
                return;

            String placeAction;

            // Update "Use" tip status.
            BlockHitResult placeHitResult;
            if (this.client.hitResult.getType() == HitResult.Type.MISS) {
                placeHitResult = MidnightControlsClient.reacharound.getLastReacharoundResult();
                this.attackAction = "";
                this.attackWidth = 0;
            } else {
                if (this.client.hitResult.getType() == HitResult.Type.BLOCK)
                    placeHitResult = (BlockHitResult) this.client.hitResult;
                else
                    placeHitResult = null;

                this.attackAction = this.client.hitResult.getType() == HitResult.Type.BLOCK ? "midnightcontrols.action.hit" : ButtonBinding.ATTACK.getTranslationKey();
                this.attackWidth = this.width(attackAction);
            }

            if (MidnightControlsClient.reacharound.isLastReacharoundVertical()) {
                if (this.ticksDisplayedCrosshair < 5)
                    this.ticksDisplayedCrosshair++;
            } else {
                this.ticksDisplayedCrosshair = 0;
            }

            var customAttackAction = MidnightControlsCompat.getAttackActionAt(this.client, placeHitResult);
            if (customAttackAction != null) {
                this.attackAction = customAttackAction;
                this.attackWidth = this.width(customAttackAction);
            }

            ItemStack stack = null;
            if (this.client.player != null) {
                stack = this.client.player.getMainHandItem();
                if (stack == null || stack.isEmpty())
                    stack = this.client.player.getOffhandItem();
            }
            if (stack == null || stack.isEmpty()) {
                placeAction = "";
            } else {
                if (placeHitResult != null && stack.getItem() instanceof BlockItem) {
                    placeAction = "midnightcontrols.action.place";
                } else {
                    placeAction = ButtonBinding.USE.getTranslationKey();
                }
            }

            var customUseAction = MidnightControlsCompat.getUseActionAt(this.client, placeHitResult);
            if (customUseAction != null)
                placeAction = customUseAction;

            this.placeAction = placeAction;
            this.showSwapHandsAction = this.client.player != null && (!this.client.player.getMainHandItem().isEmpty() || !this.client.player.getOffhandItem().isEmpty());

            // Cache the "Use" tip width.
            if (this.placeAction.isEmpty())
                this.useWidth = 0;
            else
                this.useWidth = this.width(this.placeAction);
        }
    }

    private int bottom(int y) {
        return (this.client.getWindow().getGuiScaledHeight() - y - MidnightControlsRenderer.ICON_SIZE);
    }

    private int width(@NotNull ButtonBinding binding) {
        return this.width(binding.getTranslationKey());
    }

    private int width(@Nullable String text) {
        if (client == null || text == null || text.isEmpty())
            return 0;
        return this.client.font.width(I18n.get(text));
    }

    private void drawButton(GuiGraphics context, int x, int y, @NotNull ButtonBinding button, boolean display) {
        if (display)
            MidnightControlsRenderer.drawButton(context, x, y, button, this.client);
    }

    private void drawTip(GuiGraphics context, int x, int y, @NotNull ButtonBinding button, boolean display) {
        this.drawTip(context, x, y, button.getTranslationKey(), display);
    }

    private void drawTip(GuiGraphics context, int x, int y, @NotNull String action, boolean display) {
        if (!display)
            return;
        var translatedAction = I18n.get(action);
        int textY = (MidnightControlsRenderer.ICON_SIZE / 2 - this.client.font.lineHeight / 2) + 1;
        context.drawString(this.client.font, translatedAction, x, (y + textY), 0xFFFFFFFF, false);
    }
}
