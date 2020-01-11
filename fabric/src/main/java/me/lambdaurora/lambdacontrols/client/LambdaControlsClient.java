/*
 * Copyright © 2020 LambdAurora <aurora42lambda@gmail.com>
 *
 * This file is part of LambdaControls.
 *
 * Licensed under the MIT license. For more information,
 * see the LICENSE file.
 */

package me.lambdaurora.lambdacontrols.client;

import com.mojang.blaze3d.platform.GlStateManager;
import me.lambdaurora.lambdacontrols.ControlsMode;
import me.lambdaurora.lambdacontrols.LambdaControls;
import me.lambdaurora.lambdacontrols.LambdaControlsConstants;
import me.lambdaurora.lambdacontrols.client.compat.LambdaControlsCompat;
import me.lambdaurora.lambdacontrols.client.controller.ButtonBinding;
import me.lambdaurora.lambdacontrols.client.controller.Controller;
import me.lambdaurora.lambdacontrols.client.gui.LambdaControlsHud;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.FabricKeyBinding;
import net.fabricmc.fabric.api.client.keybinding.KeyBindingRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.aperlambda.lambdacommon.utils.Pair;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

/**
 * Represents the LambdaControls client mod.
 *
 * @author LambdAurora
 * @version 1.1.0
 * @since 1.1.0
 */
public class LambdaControlsClient extends LambdaControls implements ClientModInitializer
{
    private static      LambdaControlsClient INSTANCE;
    public static final FabricKeyBinding     BINDING_LOOK_UP    = FabricKeyBinding.Builder.create(new Identifier(LambdaControlsConstants.NAMESPACE, "look_up"),
            InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_KP_8, "key.categories.movement").build();
    public static final FabricKeyBinding     BINDING_LOOK_RIGHT = FabricKeyBinding.Builder.create(new Identifier(LambdaControlsConstants.NAMESPACE, "look_right"),
            InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_KP_6, "key.categories.movement").build();
    public static final FabricKeyBinding     BINDING_LOOK_DOWN  = FabricKeyBinding.Builder.create(new Identifier(LambdaControlsConstants.NAMESPACE, "look_down"),
            InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_KP_2, "key.categories.movement").build();
    public static final FabricKeyBinding     BINDING_LOOK_LEFT  = FabricKeyBinding.Builder.create(new Identifier(LambdaControlsConstants.NAMESPACE, "look_left"),
            InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_KP_4, "key.categories.movement").build();
    public static final Identifier           CONTROLLER_BUTTONS = new Identifier(LambdaControlsConstants.NAMESPACE, "textures/gui/controller_buttons.png");
    public static final Identifier           CONTROLLER_AXIS    = new Identifier(LambdaControlsConstants.NAMESPACE, "textures/gui/controller_axis.png");
    public final        LambdaControlsConfig config             = new LambdaControlsConfig(this);
    public final        LambdaInput          input              = new LambdaInput(this);
    private             LambdaControlsHud    hud;
    private             ControlsMode         previous_controls_mode;

    @Override
    public void onInitializeClient()
    {
        INSTANCE = this;
        KeyBindingRegistry.INSTANCE.register(BINDING_LOOK_UP);
        KeyBindingRegistry.INSTANCE.register(BINDING_LOOK_RIGHT);
        KeyBindingRegistry.INSTANCE.register(BINDING_LOOK_DOWN);
        KeyBindingRegistry.INSTANCE.register(BINDING_LOOK_LEFT);

        HudRenderCallback.EVENT.register(delta -> this.hud.render());
    }

    /**
     * This method is called when Minecraft is initializing.
     */
    public void on_mc_init(@NotNull MinecraftClient client)
    {
        ButtonBinding.init(client.options);
        this.config.load();
        Controller.update_mappings();
        GLFW.glfwSetJoystickCallback((jid, event) -> {
            if (event == GLFW.GLFW_CONNECTED) {
                Controller controller = Controller.by_id(jid);
                client.getToastManager().add(new SystemToast(SystemToast.Type.TUTORIAL_HINT, new TranslatableText("lambdacontrols.controller.connected", jid),
                        new LiteralText(controller.get_name())));
            } else if (event == GLFW.GLFW_DISCONNECTED) {
                client.getToastManager().add(new SystemToast(SystemToast.Type.TUTORIAL_HINT, new TranslatableText("lambdacontrols.controller.disconnected", jid),
                        null));
            }

            this.switch_controls_mode();
        });

        this.hud = new LambdaControlsHud(client, this);

        LambdaControlsCompat.init(this);
    }

    /**
     * This method is called every Minecraft tick.
     *
     * @param client The client instance.
     */
    public void on_tick(@NotNull MinecraftClient client)
    {
        this.input.on_tick(client);
        if (this.config.get_controls_mode() == ControlsMode.CONTROLLER)
            this.input.on_controller_tick(client);
    }

    public void on_render(MinecraftClient client)
    {
        this.input.on_render(client);
    }

    /**
     * Switches the controls mode if the auto switch is enabled.
     */
    public void switch_controls_mode()
    {
        if (this.config.has_auto_switch_mode()) {
            if (this.config.get_controller().is_gamepad()) {
                this.previous_controls_mode = this.config.get_controls_mode();
                this.config.set_controls_mode(ControlsMode.CONTROLLER);
            } else {
                if (this.previous_controls_mode == null) {
                    this.previous_controls_mode = ControlsMode.DEFAULT;
                }

                this.config.set_controls_mode(this.previous_controls_mode);
            }
        }
    }

    /**
     * Gets the LambdaControls client instance.
     *
     * @return The LambdaControls client instance.
     */
    public static LambdaControlsClient get()
    {
        return INSTANCE;
    }

    public static Pair<Integer, Integer> draw_button(int x, int y, @NotNull ButtonBinding button, @NotNull MinecraftClient client)
    {
        return draw_button(x, y, button.get_button(), client);
    }

    public static Pair<Integer, Integer> draw_button(int x, int y, int[] buttons, @NotNull MinecraftClient client)
    {
        int height = 0;
        int length = 0;
        int current_x = x;
        for (int i = 0; i < buttons.length; i++) {
            int btn = buttons[i];
            Pair<Integer, Integer> size = draw_button(current_x, y, btn, client);
            if (size.get_key() > height)
                height = size.get_value();
            length += size.get_key();
            if (i + 1 < buttons.length) {
                length += 2;
                current_x = x + length;
            }
        }
        return Pair.of(length, height);
    }

    public static Pair<Integer, Integer> draw_button(int x, int y, int button, @NotNull MinecraftClient client)
    {
        boolean second = false;
        if (button == -1)
            return Pair.of(0, 0);
        else if (button >= 500) {
            button -= 1000;
            second = true;
        }

        int controller_type = get().config.get_controller_type().get_id();
        boolean axis = false;
        int button_offset = button * 15;
        switch (button) {
            case GLFW.GLFW_GAMEPAD_BUTTON_LEFT_BUMPER:
                button_offset = 7 * 15;
                break;
            case GLFW.GLFW_GAMEPAD_BUTTON_RIGHT_BUMPER:
                button_offset = 8 * 15;
                break;
            case GLFW.GLFW_GAMEPAD_BUTTON_BACK:
                button_offset = 4 * 15;
                break;
            case GLFW.GLFW_GAMEPAD_BUTTON_START:
                button_offset = 6 * 15;
                break;
            case GLFW.GLFW_GAMEPAD_BUTTON_GUIDE:
                button_offset = 5 * 15;
                break;
            case GLFW.GLFW_GAMEPAD_BUTTON_LEFT_THUMB:
                button_offset = 15 * 15;
                break;
            case GLFW.GLFW_GAMEPAD_BUTTON_RIGHT_THUMB:
                button_offset = 16 * 15;
                break;
            case GLFW.GLFW_GAMEPAD_AXIS_LEFT_X + 100:
                button_offset = 0;
                axis = true;
                break;
            case GLFW.GLFW_GAMEPAD_AXIS_LEFT_Y + 100:
                button_offset = 18;
                axis = true;
                break;
            case GLFW.GLFW_GAMEPAD_AXIS_RIGHT_X + 100:
                button_offset = 2 * 18;
                axis = true;
                break;
            case GLFW.GLFW_GAMEPAD_AXIS_RIGHT_Y + 100:
                button_offset = 3 * 18;
                axis = true;
                break;
            case GLFW.GLFW_GAMEPAD_AXIS_LEFT_X + 200:
                button_offset = 4 * 18;
                axis = true;
                break;
            case GLFW.GLFW_GAMEPAD_AXIS_LEFT_Y + 200:
                button_offset = 5 * 18;
                axis = true;
                break;
            case GLFW.GLFW_GAMEPAD_AXIS_RIGHT_X + 200:
                button_offset = 6 * 18;
                axis = true;
                break;
            case GLFW.GLFW_GAMEPAD_AXIS_RIGHT_Y + 200:
                button_offset = 7 * 18;
                axis = true;
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

        client.getTextureManager().bindTexture(axis ? LambdaControlsClient.CONTROLLER_AXIS : LambdaControlsClient.CONTROLLER_BUTTONS);
        GlStateManager.disableDepthTest();

        GlStateManager.color4f(1.0F, second ? 0.0F : 1.0F, 1.0F, 1.0F);
        DrawableHelper.blit(x, y, (float) button_offset, (float) (controller_type * (axis ? 18 : 15)), axis ? 18 : 15, axis ? 18 : 15, 256, 256);
        GlStateManager.enableDepthTest();

        return axis ? Pair.of(18, 18) : Pair.of(15, 15);
    }

    public static int draw_button_tip(int x, int y, @NotNull ButtonBinding button, boolean display, @NotNull MinecraftClient client)
    {
        return draw_button_tip(x, y, button.get_button(), button.get_translation_key(), display, client);
    }

    public static int draw_button_tip(int x, int y, int[] button, @NotNull String action, boolean display, @NotNull MinecraftClient client)
    {
        if (display) {
            int button_width = draw_button(x, y, button, client).get_key();

            String translated_action = I18n.translate(action);
            int text_y = (15 - client.textRenderer.fontHeight) / 2;
            client.textRenderer.drawWithShadow(translated_action, (float) (x + button_width + 5), (float) (y + text_y), 14737632);

            return get_button_tip_width(translated_action, client.textRenderer);
        }

        return -10;
    }

    private static int get_button_tip_width(@NotNull String action, @NotNull TextRenderer text_renderer)
    {
        return 15 + 5 + text_renderer.getStringWidth(action);
    }
}
