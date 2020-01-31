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
import me.lambdaurora.lambdacontrols.LambdaControlsFeature;
import me.lambdaurora.lambdacontrols.client.compat.LambdaControlsCompat;
import me.lambdaurora.lambdacontrols.client.controller.ButtonBinding;
import me.lambdaurora.lambdacontrols.client.controller.Controller;
import me.lambdaurora.lambdacontrols.client.gui.LambdaControlsHud;
import me.lambdaurora.lambdacontrols.client.gui.TouchscreenOverlay;
import me.lambdaurora.spruceui.event.OpenScreenCallback;
import me.lambdaurora.spruceui.hud.HudManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.FabricKeyBinding;
import net.fabricmc.fabric.api.client.keybinding.KeyBindingRegistry;
import net.fabricmc.fabric.api.event.client.ClientTickCallback;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
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
    private             ControlsMode         previousControlsMode;

    @Override
    public void onInitializeClient()
    {
        INSTANCE = this;
        KeyBindingRegistry.INSTANCE.register(BINDING_LOOK_UP);
        KeyBindingRegistry.INSTANCE.register(BINDING_LOOK_RIGHT);
        KeyBindingRegistry.INSTANCE.register(BINDING_LOOK_DOWN);
        KeyBindingRegistry.INSTANCE.register(BINDING_LOOK_LEFT);

        ClientSidePacketRegistry.INSTANCE.register(CONTROLS_MODE_CHANNEL, (context, attachedData) -> context.getTaskQueue()
                .execute(() -> ClientSidePacketRegistry.INSTANCE.sendToServer(CONTROLS_MODE_CHANNEL, this.makeControlsModeBuffer(this.config.getControlsMode()))));
        ClientSidePacketRegistry.INSTANCE.register(FEATURE_CHANNEL, (context, attachedData) -> {
            String name = attachedData.readString(64);
            boolean allowed = attachedData.readBoolean();
            LambdaControlsFeature.fromName(name).ifPresent(feature -> context.getTaskQueue().execute(() -> feature.setAllowed(allowed)));
        });

        ClientTickCallback.EVENT.register(this::onTick);

        OpenScreenCallback.EVENT.register((client, screen) -> {
            if (screen == null && this.config.getControlsMode() == ControlsMode.TOUCHSCREEN) {
                screen = new TouchscreenOverlay(this);
                screen.init(client, client.getWindow().getScaledWidth(), client.getWindow().getScaledHeight());
                client.skipGameRender = false;
                client.currentScreen = screen;
            } else if (screen != null) {
                this.input.onScreenOpen(client, client.getWindow().getWidth(), client.getWindow().getHeight());
            }
        });

        HudManager.register(this.hud = new LambdaControlsHud(this));
    }

    /**
     * This method is called when Minecraft is initializing.
     */
    public void onMcInit(@NotNull MinecraftClient client)
    {
        ButtonBinding.init(client.options);
        this.config.load();
        this.hud.setVisible(this.config.isHudEnabled());
        Controller.updateMappings();
        GLFW.glfwSetJoystickCallback((jid, event) -> {
            if (event == GLFW.GLFW_CONNECTED) {
                Controller controller = Controller.byId(jid);
                client.getToastManager().add(new SystemToast(SystemToast.Type.TUTORIAL_HINT, new TranslatableText("lambdacontrols.controller.connected", jid),
                        new LiteralText(controller.getName())));
            } else if (event == GLFW.GLFW_DISCONNECTED) {
                client.getToastManager().add(new SystemToast(SystemToast.Type.TUTORIAL_HINT, new TranslatableText("lambdacontrols.controller.disconnected", jid),
                        null));
            }

            this.switchControlsMode();
        });

        LambdaControlsCompat.init(this);
    }

    /**
     * This method is called every Minecraft tick.
     *
     * @param client The client instance.
     */
    public void onTick(@NotNull MinecraftClient client)
    {
        this.input.onTick(client);
        if (this.config.getControlsMode() == ControlsMode.CONTROLLER && (client.isWindowFocused() || this.config.hasUnfocusedInput()))
            this.input.onControllerTick(client);
    }

    public void onRender(MinecraftClient client)
    {
        this.input.onRender(client);
    }

    /**
     * Called when leaving a server.
     */
    public void onLeave()
    {
        LambdaControlsFeature.resetAllAllowed();
    }

    /**
     * Switches the controls mode if the auto switch is enabled.
     */
    public void switchControlsMode()
    {
        if (this.config.hasAutoSwitchMode()) {
            if (this.config.getController().isGamepad()) {
                this.previousControlsMode = this.config.getControlsMode();
                this.config.setControlsMode(ControlsMode.CONTROLLER);
            } else {
                if (this.previousControlsMode == null) {
                    this.previousControlsMode = ControlsMode.DEFAULT;
                }

                this.config.setControlsMode(this.previousControlsMode);
            }
        }
    }

    /**
     * Sets whether the HUD is enabled or not.
     *
     * @param enabled True if the HUD is enabled, else false.
     */
    public void setHudEnabled(boolean enabled)
    {
        this.config.setHudEnabled(enabled);
        this.hud.setVisible(enabled);
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

        int controllerType = get().config.getControllerType().getId();
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
        if (display) {
            int buttonWidth = drawButton(x, y, button, client).key;

            String translatedAction = I18n.translate(action);
            int textY = (15 - client.textRenderer.fontHeight) / 2;
            client.textRenderer.drawWithShadow(translatedAction, (float) (x + buttonWidth + 5), (float) (y + textY), 14737632);

            return getButtonTipWidth(translatedAction, client.textRenderer);
        }

        return -10;
    }

    private static int getButtonTipWidth(@NotNull String action, @NotNull TextRenderer textRenderer)
    {
        return 15 + 5 + textRenderer.getStringWidth(action);
    }
}
