/*
 * Copyright © 2019 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of LambdaControls.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package me.lambdaurora.lambdacontrols;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

/**
 * Represents the LambdaControls mod.
 */
public class LambdaControls implements ClientModInitializer
{
    private static      LambdaControls       INSTANCE;
    public static final Identifier           CONTROLLER_BUTTONS = new Identifier("lambdacontrols", "textures/gui/controller_buttons.png");
    public final        Logger               logger             = LogManager.getLogger("LambdaControls");
    public final        LambdaControlsConfig config             = new LambdaControlsConfig(this);
    public final        ControllerInput      controller_input   = new ControllerInput(this);

    @Override
    public void onInitializeClient()
    {
        INSTANCE = this;
        this.log("Initializing LambdaControls...");
        this.config.load();
    }

    /**
     * This method is called when Minecraft is initializing.
     */
    public void on_mc_init(@NotNull MinecraftClient client)
    {
        Controller.update_mappings();
        ButtonBinding.init(client.options);
        this.config.init_keybindings(client.options);
        GLFW.glfwSetJoystickCallback((jid, event) -> {
            if (event == GLFW.GLFW_CONNECTED) {
                Controller controller = Controller.by_id(jid);
                client.getToastManager().add(new SystemToast(SystemToast.Type.TUTORIAL_HINT, new TranslatableText("lambdacontrols.controller.connected", jid),
                        new LiteralText(controller.get_name())));
            } else if (event == GLFW.GLFW_DISCONNECTED) {
                client.getToastManager().add(new SystemToast(SystemToast.Type.TUTORIAL_HINT, new TranslatableText("lambdacontrols.controller.disconnected", jid),
                        null));
            }
        });
    }

    /**
     * This method is called every Minecraft tick.
     *
     * @param client The client instance.
     */
    public void on_tick(@NotNull MinecraftClient client)
    {
        if (this.config.get_controls_mode() == ControlsMode.CONTROLLER)
            this.controller_input.on_tick(client);
    }

    public void on_render(MinecraftClient client)
    {
        this.controller_input.on_render(client);
    }

    /**
     * Prints a message to the terminal.
     *
     * @param info The message to print.
     */
    public void log(String info)
    {
        this.logger.info("[LambdaControls] " + info);
    }

    /**
     * Gets the LambdaControls instance.
     *
     * @return The LambdaControls instance.
     */
    public static LambdaControls get()
    {
        return INSTANCE;
    }

    public static int draw_button_tip(int x, int y, @NotNull ButtonBinding button, boolean display, @NotNull MinecraftClient client)
    {
        return draw_button_tip(x, y, button.get_button(), button.get_translation_key(), display, client);
    }

    public static int draw_button_tip(int x, int y, int button, @NotNull String action, boolean display, @NotNull MinecraftClient client)
    {
        int controller_type = get().config.get_controller_type().get_id();
        String translated_action = I18n.translate(action);

        if (display) {
            int button_offset = button * 15;
            switch (button) {
                case GLFW.GLFW_GAMEPAD_BUTTON_LEFT_THUMB:
                    button_offset = 15 * 15;
                    break;
                case GLFW.GLFW_GAMEPAD_BUTTON_RIGHT_THUMB:
                    button_offset = 16 * 15;
                    break;
                case GLFW.GLFW_GAMEPAD_AXIS_LEFT_TRIGGER + 100:
                case GLFW.GLFW_GAMEPAD_AXIS_LEFT_TRIGGER + 200:
                    button_offset = 9 * 15;
                    break;
                case GLFW.GLFW_GAMEPAD_AXIS_RIGHT_TRIGGER + 100:
                case GLFW.GLFW_GAMEPAD_AXIS_RIGHT_TRIGGER + 200:
                    button_offset = 10 * 15;
                    break;
            }

            client.getTextureManager().bindTexture(LambdaControls.CONTROLLER_BUTTONS);
            GlStateManager.disableDepthTest();

            GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            DrawableHelper.blit(x, y, (float) button_offset, (float) (controller_type * 15), 15, 15, 256, 256);
            GlStateManager.enableDepthTest();

            int text_y = (15 - client.textRenderer.fontHeight) / 2;
            client.textRenderer.drawWithShadow(translated_action, (float) (x + 15 + 5), (float) (y + text_y), 14737632);
        }

        return display ? get_button_tip_width(translated_action, client.textRenderer) : -10;
    }

    private static int get_button_tip_width(@NotNull String action, @NotNull TextRenderer text_renderer)
    {
        return 15 + 5 + text_renderer.getStringWidth(action);
    }
}
