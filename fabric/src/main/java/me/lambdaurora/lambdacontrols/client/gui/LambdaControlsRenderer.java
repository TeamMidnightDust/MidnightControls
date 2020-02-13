/*
 * Copyright © 2020 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of LambdaControls.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package me.lambdaurora.lambdacontrols.client.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import me.lambdaurora.lambdacontrols.client.LambdaControlsClient;
import me.lambdaurora.lambdacontrols.client.controller.ButtonBinding;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.render.*;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.Matrix4f;
import net.minecraft.client.util.math.Rotation3;
import org.aperlambda.lambdacommon.utils.Pair;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

public class LambdaControlsRenderer
{
    public static Pair<Integer, Integer> drawButton(int x, int y, @NotNull ButtonBinding button, @NotNull MinecraftClient client)
    {
        return drawButton(x, y, button.getButton(), client);
    }

    public static Pair<Integer, Integer> drawButton(int x, int y, int[] buttons, @NotNull MinecraftClient client)
    {
        int height = 0;
        int length = 0;
        int currentX = x;
        for (int i = 0; i < buttons.length; i++) {
            int btn = buttons[i];
            Pair<Integer, Integer> size = drawButton(currentX, y, btn, client);
            if (size.key > height)
                height = size.key;
            length += size.key;
            if (i + 1 < buttons.length) {
                length += 2;
                currentX = x + length;
            }
        }
        return Pair.of(length, height);
    }

    @SuppressWarnings("deprecated")
    public static Pair<Integer, Integer> drawButton(int x, int y, int button, @NotNull MinecraftClient client)
    {
        boolean second = false;
        if (button == -1)
            return Pair.of(0, 0);
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
        GlStateManager.disableDepthTest();

        GlStateManager.color4f(1.0F, second ? 0.0F : 1.0F, 1.0F, 1.0F);
        DrawableHelper.blit(x, y, (float) buttonOffset, (float) (controllerType * (axis ? 18 : 15)), axis ? 18 : 15, axis ? 18 : 15, 256, 256);
        GlStateManager.enableDepthTest();

        return axis ? Pair.of(18, 18) : Pair.of(15, 15);
    }

    public static int drawButtonTip(int x, int y, @NotNull ButtonBinding button, boolean display, @NotNull MinecraftClient client)
    {
        return drawButtonTip(x, y, button.getButton(), button.getTranslationKey(), display, client);
    }

    public static int drawButtonTip(int x, int y, int[] button, @NotNull String action, boolean display, @NotNull MinecraftClient client)
    {
        VertexConsumerProvider.Immediate immediate = VertexConsumerProvider.immediate(Tessellator.getInstance().getBuffer());
        int width = drawButtonTip(x, y, button, action, display, client, immediate, Rotation3.identity().getMatrix());
        immediate.draw();
        return width;
    }

    public static int drawButtonTip(int x, int y, @NotNull ButtonBinding button, boolean display, @NotNull MinecraftClient client, @NotNull VertexConsumerProvider.Immediate immediate, @NotNull Matrix4f matrix4f)
    {
        return drawButtonTip(x, y, button.getButton(), button.getTranslationKey(), display, client, immediate, matrix4f);
    }

    public static int drawButtonTip(int x, int y, int[] button, @NotNull String action, boolean display, @NotNull MinecraftClient client, @NotNull VertexConsumerProvider.Immediate immediate, @NotNull Matrix4f matrix4f)
    {
        if (display) {
            RenderSystem.enableAlphaTest();
            int buttonWidth = drawButton(x, y, button, client).key;

            String translatedAction = I18n.translate(action);
            int textY = (15 - client.textRenderer.fontHeight) / 2;
            int width = client.textRenderer.draw(translatedAction, (float) (x + buttonWidth + 5), (float) (y + textY), 14737632, true, matrix4f, immediate,
                    false, 0, 15728880);

            return width;
        }

        return -10;
    }

    private static int getButtonTipWidth(@NotNull String action, @NotNull TextRenderer textRenderer)
    {
        return 15 + 5 + textRenderer.getStringWidth(action);
    }
}
