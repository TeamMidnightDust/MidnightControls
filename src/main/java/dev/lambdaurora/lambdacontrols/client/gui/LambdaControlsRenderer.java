/*
 * Copyright © 2020 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of LambdaControls.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package dev.lambdaurora.lambdacontrols.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.lambdaurora.lambdacontrols.client.LambdaInput;
import dev.lambdaurora.lambdacontrols.client.compat.LambdaControlsCompat;
import dev.lambdaurora.lambdacontrols.client.util.HandledScreenAccessor;
import dev.lambdaurora.lambdacontrols.client.LambdaControlsClient;
import dev.lambdaurora.lambdacontrols.client.controller.ButtonBinding;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.slot.Slot;
import org.aperlambda.lambdacommon.utils.Pair;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

/**
 * Represents the LambdaControls renderer.
 *
 * @author LambdAurora
 * @version 1.5.0
 * @since 1.2.0
 */
public class LambdaControlsRenderer
{
    public static final  int ICON_SIZE   = 20;
    private static final int BUTTON_SIZE = 15;
    private static final int AXIS_SIZE   = 18;

    public static int getButtonSize(int button)
    {
        switch (button) {
            case -1:
                return 0;
            case GLFW.GLFW_GAMEPAD_AXIS_LEFT_X + 100:
            case GLFW.GLFW_GAMEPAD_AXIS_LEFT_X + 200:
            case GLFW.GLFW_GAMEPAD_AXIS_LEFT_Y + 100:
            case GLFW.GLFW_GAMEPAD_AXIS_LEFT_Y + 200:
            case GLFW.GLFW_GAMEPAD_AXIS_RIGHT_X + 100:
            case GLFW.GLFW_GAMEPAD_AXIS_RIGHT_X + 200:
            case GLFW.GLFW_GAMEPAD_AXIS_RIGHT_Y + 100:
            case GLFW.GLFW_GAMEPAD_AXIS_RIGHT_Y + 200:
                return AXIS_SIZE;
            default:
                return BUTTON_SIZE;
        }
    }

    /**
     * Gets the binding icon width.
     *
     * @param binding The binding.
     * @return The width.
     */
    public static int getBindingIconWidth(@NotNull ButtonBinding binding)
    {
        return getBindingIconWidth(binding.getButton());
    }

    /**
     * Gets the binding icon width.
     *
     * @param buttons The buttons.
     * @return The width.
     */
    public static int getBindingIconWidth(int[] buttons)
    {
        int width = 0;
        for (int i = 0; i < buttons.length; i++) {
            width += ICON_SIZE;
            if (i + 1 < buttons.length) {
                width += 2;
            }
        }
        return width;
    }

    public static Pair<Integer, Integer> drawButton(MatrixStack matrices, int x, int y, @NotNull ButtonBinding button, @NotNull MinecraftClient client)
    {
        return drawButton(matrices, x, y, button.getButton(), client);
    }

    public static Pair<Integer, Integer> drawButton(MatrixStack matrices, int x, int y, int[] buttons, @NotNull MinecraftClient client)
    {
        int height = 0;
        int length = 0;
        int currentX = x;
        for (int i = 0; i < buttons.length; i++) {
            int btn = buttons[i];
            int size = drawButton(matrices, currentX, y, btn, client);
            if (size > height)
                height = size;
            length += size;
            if (i + 1 < buttons.length) {
                length += 2;
                currentX = x + length;
            }
        }
        return Pair.of(length, height);
    }

    @SuppressWarnings("deprecated")
    public static int drawButton(MatrixStack matrices, int x, int y, int button, @NotNull MinecraftClient client)
    {
        boolean second = false;
        if (button == -1)
            return 0;
        else if (button >= 500) {
            button -= 1000;
            second = true;
        }

        int controllerType = LambdaControlsClient.get().config.getControllerType().getId();
        boolean axis = false;
        int buttonOffset = button * 15;
        switch (button) {
            case GLFW.GLFW_GAMEPAD_BUTTON_LEFT_BUMPER:
                buttonOffset = 7 * 15;
                break;
            case GLFW.GLFW_GAMEPAD_BUTTON_RIGHT_BUMPER:
                buttonOffset = 8 * 15;
                break;
            case GLFW.GLFW_GAMEPAD_BUTTON_BACK:
                buttonOffset = 4 * 15;
                break;
            case GLFW.GLFW_GAMEPAD_BUTTON_START:
                buttonOffset = 6 * 15;
                break;
            case GLFW.GLFW_GAMEPAD_BUTTON_GUIDE:
                buttonOffset = 5 * 15;
                break;
            case GLFW.GLFW_GAMEPAD_BUTTON_LEFT_THUMB:
                buttonOffset = 15 * 15;
                break;
            case GLFW.GLFW_GAMEPAD_BUTTON_RIGHT_THUMB:
                buttonOffset = 16 * 15;
                break;
            case GLFW.GLFW_GAMEPAD_AXIS_LEFT_X + 100:
                buttonOffset = 0;
                axis = true;
                break;
            case GLFW.GLFW_GAMEPAD_AXIS_LEFT_Y + 100:
                buttonOffset = 18;
                axis = true;
                break;
            case GLFW.GLFW_GAMEPAD_AXIS_RIGHT_X + 100:
                buttonOffset = 2 * 18;
                axis = true;
                break;
            case GLFW.GLFW_GAMEPAD_AXIS_RIGHT_Y + 100:
                buttonOffset = 3 * 18;
                axis = true;
                break;
            case GLFW.GLFW_GAMEPAD_AXIS_LEFT_X + 200:
                buttonOffset = 4 * 18;
                axis = true;
                break;
            case GLFW.GLFW_GAMEPAD_AXIS_LEFT_Y + 200:
                buttonOffset = 5 * 18;
                axis = true;
                break;
            case GLFW.GLFW_GAMEPAD_AXIS_RIGHT_X + 200:
                buttonOffset = 6 * 18;
                axis = true;
                break;
            case GLFW.GLFW_GAMEPAD_AXIS_RIGHT_Y + 200:
                buttonOffset = 7 * 18;
                axis = true;
                break;
            case GLFW.GLFW_GAMEPAD_AXIS_LEFT_TRIGGER + 100:
            case GLFW.GLFW_GAMEPAD_AXIS_LEFT_TRIGGER + 200:
                buttonOffset = 9 * 15;
                break;
            case GLFW.GLFW_GAMEPAD_AXIS_RIGHT_TRIGGER + 100:
            case GLFW.GLFW_GAMEPAD_AXIS_RIGHT_TRIGGER + 200:
                buttonOffset = 10 * 15;
                break;
        }

        client.getTextureManager().bindTexture(axis ? LambdaControlsClient.CONTROLLER_AXIS : LambdaControlsClient.CONTROLLER_BUTTONS);
        RenderSystem.disableDepthTest();

        int assetSize = axis ? AXIS_SIZE : BUTTON_SIZE;

        RenderSystem.color4f(1.0F, second ? 0.0F : 1.0F, 1.0F, 1.0F);
        DrawableHelper.drawTexture(matrices, x + (ICON_SIZE / 2 - assetSize / 2), y + (ICON_SIZE / 2 - assetSize / 2),
                (float) buttonOffset, (float) (controllerType * (axis ? AXIS_SIZE : BUTTON_SIZE)),
                assetSize, assetSize,
                256, 256);
        RenderSystem.enableDepthTest();

        return ICON_SIZE;
    }

    public static int drawButtonTip(MatrixStack matrices, int x, int y, @NotNull ButtonBinding button, boolean display, @NotNull MinecraftClient client)
    {
        return drawButtonTip(matrices, x, y, button.getButton(), button.getTranslationKey(), display, client);
    }

    public static int drawButtonTip(MatrixStack matrices, int x, int y, int[] button, @NotNull String action, boolean display, @NotNull MinecraftClient client)
    {
        if (display) {
            int buttonWidth = drawButton(matrices, x, y, button, client).key;

            String translatedAction = I18n.translate(action);
            int textY = (LambdaControlsRenderer.ICON_SIZE / 2 - client.textRenderer.fontHeight / 2) + 1;

            return client.textRenderer.drawWithShadow(matrices, translatedAction, (float) (x + buttonWidth + 2), (float) (y + textY), 14737632);
        }

        return -10;
    }

    private static int getButtonTipWidth(@NotNull String action, @NotNull TextRenderer textRenderer)
    {
        return 15 + 5 + textRenderer.getWidth(action);
    }

    public static void renderVirtualCursor(@NotNull MatrixStack matrices, @NotNull MinecraftClient client)
    {
        if (!LambdaControlsClient.get().config.hasVirtualMouse() || (client.currentScreen == null || LambdaInput.isScreenInteractive(client.currentScreen)))
            return;

        int mouseX = (int) (client.mouse.getX() * (double) client.getWindow().getScaledWidth() / (double) client.getWindow().getWidth());
        int mouseY = (int) (client.mouse.getY() * (double) client.getWindow().getScaledHeight() / (double) client.getWindow().getHeight());

        boolean hoverSlot = false;

        if (client.currentScreen instanceof HandledScreen) {
            HandledScreenAccessor inventoryScreen = (HandledScreenAccessor) client.currentScreen;
            int guiLeft = inventoryScreen.getX();
            int guiTop = inventoryScreen.getY();

            Slot slot = inventoryScreen.lambdacontrols_getSlotAt(mouseX, mouseY);

            if (slot != null) {
                mouseX = guiLeft + slot.x;
                mouseY = guiTop + slot.y;
                hoverSlot = true;
            }
        }

        if (!hoverSlot) {
            Pair<Integer, Integer> slot = LambdaControlsCompat.getSlotAt(client.currentScreen, mouseX, mouseY);

            if (slot != null) {
                mouseX = slot.getFirst();
                mouseY = slot.getSecond();
                hoverSlot = true;
            }
        }

        if (!hoverSlot) {
            mouseX -= 8;
            mouseY -= 8;
        }

        drawCursor(matrices, mouseX, mouseY, hoverSlot, client);
    }

    /**
     * Draws the virtual cursor.
     *
     * @param matrices  The matrix stack.
     * @param x         X coordinate.
     * @param y         Y coordinate.
     * @param hoverSlot True if hovering a slot, else false.
     * @param client    The client instance.
     */
    public static void drawCursor(@NotNull MatrixStack matrices, int x, int y, boolean hoverSlot, @NotNull MinecraftClient client)
    {
        client.getTextureManager().bindTexture(LambdaControlsClient.CURSOR_TEXTURE);

        RenderSystem.disableDepthTest();

        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        DrawableHelper.drawTexture(matrices, x, y, hoverSlot ? 16.F : 0.F, LambdaControlsClient.get().config.getVirtualMouseSkin().ordinal() * 16.F, 16, 16, 32, 64);
        RenderSystem.enableDepthTest();
    }
}
