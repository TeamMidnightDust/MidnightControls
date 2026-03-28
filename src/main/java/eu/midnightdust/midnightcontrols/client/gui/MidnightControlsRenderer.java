/*
 * Copyright © 2021 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of midnightcontrols.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package eu.midnightdust.midnightcontrols.client.gui;

import eu.midnightdust.midnightcontrols.client.enums.ControllerType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.resources.language.I18n;
import eu.midnightdust.midnightcontrols.client.MidnightControlsClient;
import eu.midnightdust.midnightcontrols.client.MidnightControlsConfig;
import eu.midnightdust.midnightcontrols.client.controller.ButtonBinding;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

/**
 * Represents the midnightcontrols renderer.
 *
 * @author LambdAurora
 * @version 1.7.0
 * @since 1.2.0
 */
//~ if >= 26.1 'GuiGraphics' -> 'GuiGraphicsExtractor' {
public class MidnightControlsRenderer {
    public static final int ICON_SIZE = 20;
    private static final int BUTTON_SIZE = 15;
    private static final int AXIS_SIZE = 18;

    public static int getButtonSize(int button) {
        return switch (button) {
            case -1 -> 0;
            case GLFW.GLFW_GAMEPAD_AXIS_LEFT_X + 100, GLFW.GLFW_GAMEPAD_AXIS_LEFT_X + 200,
                    GLFW.GLFW_GAMEPAD_AXIS_LEFT_Y + 100, GLFW.GLFW_GAMEPAD_AXIS_LEFT_Y + 200,
                    GLFW.GLFW_GAMEPAD_AXIS_RIGHT_X + 100, GLFW.GLFW_GAMEPAD_AXIS_RIGHT_X + 200,
                    GLFW.GLFW_GAMEPAD_AXIS_RIGHT_Y + 100, GLFW.GLFW_GAMEPAD_AXIS_RIGHT_Y + 200 -> AXIS_SIZE;
            default -> BUTTON_SIZE;
        };
    }

    /**
     * Gets the binding icon width.
     *
     * @param binding the binding
     * @return the width
     */
    public static int getBindingIconWidth(@NotNull ButtonBinding binding) {
        return getBindingIconWidth(binding.getButton());
    }

    /**
     * Gets the binding icon width.
     *
     * @param buttons the buttons
     * @return the width
     */
    public static int getBindingIconWidth(int[] buttons) {
        int width = 0;
        for (int i = 0; i < buttons.length; i++) {
            width += ICON_SIZE;
            if (i + 1 < buttons.length) {
                width += 2;
            }
        }
        return width;
    }

    public static ButtonSize drawButton(GuiGraphicsExtractor context, int x, int y, @NotNull ButtonBinding button, @NotNull Minecraft client) {
        return drawButton(context, x, y, button.getButton(), client);
    }

    public static ButtonSize drawButton(GuiGraphicsExtractor context, int x, int y, int[] buttons, @NotNull Minecraft client) {
        int height = 0;
        int length = 0;
        int currentX = x;
        for (int i = 0; i < buttons.length; i++) {
            int btn = buttons[i];
            int size = drawButton(context, currentX, y, btn, client);
            if (size > height)
                height = size;
            length += size;
            if (i + 1 < buttons.length) {
                length += 2;
                currentX = x + length;
            }
        }
        return new ButtonSize(length, height);
    }

    public static int drawButton(GuiGraphicsExtractor context, int x, int y, int button, @NotNull Minecraft client) {
        boolean second = false;
        if (button == -1)
            return 0;
        else if (button >= 500) {
            button -= 1000;
            second = true;
        }

        int controllerType = MidnightControlsConfig.controllerType == ControllerType.DEFAULT ? MidnightControlsConfig.matchControllerToType().getId() : MidnightControlsConfig.controllerType.getId();
        boolean axis = false;
        int buttonOffset = button * 15;
        switch (button) {
            case 15 -> buttonOffset = 0;
            case 16 -> buttonOffset = 18;
            case 17 -> buttonOffset = 36;
            case 18 -> buttonOffset = 54;
            case GLFW.GLFW_GAMEPAD_BUTTON_LEFT_BUMPER -> buttonOffset = 7 * 15;
            case GLFW.GLFW_GAMEPAD_BUTTON_RIGHT_BUMPER -> buttonOffset = 8 * 15;
            case GLFW.GLFW_GAMEPAD_BUTTON_BACK -> buttonOffset = 4 * 15;
            case GLFW.GLFW_GAMEPAD_BUTTON_START -> buttonOffset = 6 * 15;
            case GLFW.GLFW_GAMEPAD_BUTTON_GUIDE -> buttonOffset = 5 * 15;
            case GLFW.GLFW_GAMEPAD_BUTTON_LEFT_THUMB -> buttonOffset = 15 * 15;
            case GLFW.GLFW_GAMEPAD_BUTTON_RIGHT_THUMB -> buttonOffset = 16 * 15;
            case GLFW.GLFW_GAMEPAD_AXIS_LEFT_X + 100 -> {
                buttonOffset = 0;
                axis = true;
            }
            case GLFW.GLFW_GAMEPAD_AXIS_LEFT_Y + 100 -> {
                buttonOffset = 18;
                axis = true;
            }
            case GLFW.GLFW_GAMEPAD_AXIS_RIGHT_X + 100 -> {
                buttonOffset = 2 * 18;
                axis = true;
            }
            case GLFW.GLFW_GAMEPAD_AXIS_RIGHT_Y + 100 -> {
                buttonOffset = 3 * 18;
                axis = true;
            }
            case GLFW.GLFW_GAMEPAD_AXIS_LEFT_X + 200 -> {
                buttonOffset = 4 * 18;
                axis = true;
            }
            case GLFW.GLFW_GAMEPAD_AXIS_LEFT_Y + 200 -> {
                buttonOffset = 5 * 18;
                axis = true;
            }
            case GLFW.GLFW_GAMEPAD_AXIS_RIGHT_X + 200 -> {
                buttonOffset = 6 * 18;
                axis = true;
            }
            case GLFW.GLFW_GAMEPAD_AXIS_RIGHT_Y + 200 -> {
                buttonOffset = 7 * 18;
                axis = true;
            }
            case GLFW.GLFW_GAMEPAD_AXIS_LEFT_TRIGGER + 100, GLFW.GLFW_GAMEPAD_AXIS_LEFT_TRIGGER + 200 -> buttonOffset = 9 * 15;
            case GLFW.GLFW_GAMEPAD_AXIS_RIGHT_TRIGGER + 100, GLFW.GLFW_GAMEPAD_AXIS_RIGHT_TRIGGER + 200 -> buttonOffset = 10 * 15;
        }

        int assetSize = axis || (button >= 15 && button <= 18) ? AXIS_SIZE : BUTTON_SIZE;

        //RenderSystem.setShaderColor(1.f, second ? 0.f : 1.f, 1.f, 1.f);
        context.blit(RenderPipelines.GUI_TEXTURED, axis ? MidnightControlsClient.CONTROLLER_AXIS : button >= 15 && button <= 19 ? MidnightControlsClient.CONTROLLER_EXPANDED :MidnightControlsClient.CONTROLLER_BUTTONS
                , x + (ICON_SIZE / 2 - assetSize / 2), y + (ICON_SIZE / 2 - assetSize / 2),
                (float) buttonOffset, (float) (controllerType * assetSize),
                assetSize, assetSize,
                256, 256);

        return ICON_SIZE;
    }

    public static int drawButtonTip(GuiGraphicsExtractor context, int x, int y, @NotNull ButtonBinding button, boolean display, @NotNull Minecraft client) {
        return drawButtonTip(context, x, y, button.getButton(), button.getTranslationKey(), display, client);
    }

    public static int drawButtonTip(GuiGraphicsExtractor context, int x, int y, int[] button, @NotNull String action, boolean display, @NotNull Minecraft client) {
        if (display) {
            int buttonWidth = drawButton(context, x, y, button, client).length();

            var translatedAction = I18n.get(action);
            int textY = (MidnightControlsRenderer.ICON_SIZE / 2 - client.font.lineHeight / 2) + 1;

            //~ if >= 26.1 '.drawString(' -> '.text('
            context.text(client.font, translatedAction, (x + buttonWidth + 2), (y + textY), 0xFFFFFFFF);
            return (x + buttonWidth + 2) + client.font.width(translatedAction);
        }

        return -10;
    }

    private static int getButtonTipWidth(@NotNull String action, @NotNull Font textRenderer) {
        return 15 + 5 + textRenderer.width(action);
    }

    public record ButtonSize(int length, int height) {
    }
}
//~}